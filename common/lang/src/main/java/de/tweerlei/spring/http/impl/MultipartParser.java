/*
 * Copyright 2018 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tweerlei.spring.http.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.spring.http.HttpClientOptions;
import de.tweerlei.spring.http.HttpEntity;

/**
 * Parse multipart entities
 * 
 * @author Robert Wruck
 */
public class MultipartParser
	{
	private static final byte[] MARKER = { '\r', '\n', '-', '-' };
	private static final byte[] SEPARATOR = { '\r', '\n', '\r', '\n' };
	private static final String CHARSET = "ISO-8859-1";
	
	private final Logger logger;
	
	/**
	 * Constructor
	 */
	public MultipartParser()
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Parse an entity
	 * @param is InputStream
	 * @param contentType Content type
	 * @return Parsed HttpEntity
	 * @throws IOException on error
	 */
	public HttpEntity parse(InputStream is, String contentType) throws IOException
		{
		final MimeTypeBuilder mb = MimeTypeBuilder.parse(contentType);
		final boolean multipart = "multipart".equals(mb.getType());
		
		if (multipart)
			{
			logger.log(Level.INFO, "Parsing multipart entity for type " + contentType);
			return (parseMultipart(is, mb));
			}
		else
			{
			logger.log(Level.INFO, "Parsing simple entity for type " + contentType);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.copy(is, baos);
			return (new ByteArrayHttpEntity(mb.build(), baos.toByteArray()));
			}
		}
	
	private MultipartHttpEntity parseMultipart(InputStream is, MimeTypeBuilder mb) throws IOException
		{
		final MultipartHttpEntity ret = new MultipartHttpEntity(mb.getSubtype());
		ret.setOption(HttpClientOptions.SKIP_MULTIPART_TYPE, true);
		ret.setOption(HttpClientOptions.REPEAT_MULTIPART_HEADERS, true);
		
		final BufferedInputStream stream = new BufferedInputStream(is);
		byte[] boundaryBytes = null;
		
		final String boundary = mb.getParam("boundary");
		if (boundary == null)
			{
			// Try to extract headers from the preamble
			final byte[] preamble = readToBoundary(stream, MARKER);
			if (preamble.length > 0)
				{
				logger.log(Level.INFO, "Multipart headers: " + preamble.length + " bytes");
				final Map<String, String> headers = parseHeaders(preamble);
				for (Map.Entry<String, String> header : headers.entrySet())
					{
					if (header.getKey().equals("content-type"))
						{
						final MimeTypeBuilder mb2 = MimeTypeBuilder.parse(header.getValue());
						final String b = mb2.getParam("boundary");
						if (b != null)
							boundaryBytes = getBoundary(b);
						}
					else
						ret.setHeader(header.getKey(), header.getValue());
					}
				}
			
			if (boundaryBytes == null)
				throw new IllegalArgumentException("No boundary for multipart content");
			
			final byte[] nextBoundary = new byte[boundaryBytes.length - 4];
			System.arraycopy(boundaryBytes, 4, nextBoundary, 0, nextBoundary.length);
			readToBoundary(stream, nextBoundary);
			}
		else
			{
			boundaryBytes = getBoundary(boundary);
			// Discard preamble
			final byte[] nextBoundary = new byte[boundaryBytes.length - 2];
			System.arraycopy(boundaryBytes, 2, nextBoundary, 0, nextBoundary.length);
			final byte[] preamble = readToBoundary(stream, nextBoundary);
			if (preamble.length > 0)
				logger.log(Level.INFO, "Multipart preamble: " + preamble.length + " bytes");
			}
		
		for (;;)
			{
			final byte[] preamble = readHeaders(stream);
			if (preamble == null)
				{
				final byte[] trailer = readTrailer(stream);
				if (trailer.length > 0)
					logger.log(Level.INFO, "Multipart trailer: " + trailer.length + " bytes");
				break;
				}
			final Map<String, String> headers = parseHeaders(preamble);
			final byte[] content = readToBoundary(stream, boundaryBytes);
			
			final String contentType = headers.get("content-type");
			final MimeTypeBuilder mb2 = (contentType == null) ? new MimeTypeBuilder("application", "octet-stream") : MimeTypeBuilder.parse(contentType);
			final ByteArrayHttpEntity part = new ByteArrayHttpEntity(mb2.build(), content);
			
			for (Map.Entry<String, String> header : headers.entrySet())
				{
				if (!header.getKey().equals("content-type"))
					part.setHeader(header.getKey(), header.getValue());
				}
			
			ret.add(part);
			}
		
		return (ret);
		}
	
	private byte[] getBoundary(String boundary) throws IOException
		{
		final byte[] bytes = boundary.getBytes(CHARSET);
		final byte[] ret = new byte[MARKER.length + bytes.length];
		
		System.arraycopy(MARKER, 0, ret, 0, MARKER.length);
		System.arraycopy(bytes, 0, ret, MARKER.length, bytes.length);
		
		return (ret);
		}
	
	private byte[] readHeaders(InputStream is) throws IOException
		{
		int c0 = is.read();
		int c1 = is.read();
		if ((c0 == '-') && (c1 == '-'))
			{
			is.read();
			is.read();
			return (null);
			}
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int state = 0;
		if ((c0 == '\r') && (c1 == '\n'))
			state = 2;
		
		for (;;)
			{
			final int c = is.read();
			if (c < 0)
				throw new EOFException("Unexpected end of input");
			if (c == SEPARATOR[state])
				{
				state++;
				if (state == SEPARATOR.length)
					break;
				}
			else if (state > 0)
				{
				baos.write(SEPARATOR, 0, state);
				state = 0;
				if (c == SEPARATOR[state])
					state++;
				}
			if (state == 0)
				baos.write(c);
			}
		
		return (baos.toByteArray());
		}
	
	private Map<String, String> parseHeaders(byte[] data) throws IOException
		{
		final Map<String, String> ret = new HashMap<String, String>();
		
		final String headers = new String(data, CHARSET);
		final String[] lines = headers.split("\\r\\n", -1);
		for (String line : lines)
			{
			final String[] header = line.split(":", 2);
			if (header.length == 2)
				ret.put(header[0].trim().toLowerCase(), header[1].trim());
			}
		
		return (ret);
		}
	
	private byte[] readToBoundary(InputStream is, byte[] boundary) throws IOException
		{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int state = 0;
		for (;;)
			{
			final int c = is.read();
			if (c < 0)
				throw new EOFException("Unexpected end of input");
			if (c == boundary[state])
				{
				state++;
				if (state == boundary.length)
					break;
				}
			else if (state > 0)
				{
				baos.write(boundary, 0, state);
				state = 0;
				if (c == boundary[state])
					state++;
				}
			if (state == 0)
				baos.write(c);
			}
		
		return (baos.toByteArray());
		}
	
	private byte[] readTrailer(InputStream is) throws IOException
		{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (;;)
			{
			final int c = is.read();
			if (c < 0)
				return (baos.toByteArray());
			baos.write(c);
			}
		}
	}
