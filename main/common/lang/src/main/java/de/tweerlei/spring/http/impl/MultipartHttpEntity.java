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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.tweerlei.spring.http.HttpClientOptions;
import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.http.HttpHeaders;

/**
 * Multipart HttpEntity
 * 
 * @author Robert Wruck
 */
public class MultipartHttpEntity extends AbstractHttpEntity
	{
	private static final String MIME_VERSION = "1.0";
	
	private final List<HttpEntity> parts;
	private final Set<String> options;
	
	/**
	 * Constructor
	 * @param subtype MIME subtype
	 * @param options HttpClientOptions
	 */
	public MultipartHttpEntity(String subtype, Set<String> options)
		{
		super(new MimeTypeBuilder("multipart", subtype, Collections.singletonMap("boundary", UUID.randomUUID().toString())));
		this.parts = new ArrayList<HttpEntity>();
		this.options = (options == null) ? new HashSet<String>() : options;
		}
	
	/**
	 * Constructor
	 * @param subtype MIME subtype
	 */
	public MultipartHttpEntity(String subtype)
		{
		this(subtype, null);
		}
	
	/**
	 * Set an option
	 * @param option Option name (see HttpClientOptions)
	 * @param b Strict mode
	 */
	public void setOption(String option, boolean b)
		{
		if (b)
			options.add(option);
		else
			options.remove(option);
		}
	
	/**
	 * Add an entity
	 * @param entity HttpEntity
	 */
	public void add(HttpEntity entity)
		{
		if (parts.isEmpty() && !options.contains(HttpClientOptions.SKIP_MULTIPART_TYPE))
			{
			// Set the required type parameter in the Content-Type
			((MimeTypeBuilder) getContentType()).setParam("type", entity.getContentType().getMediaType());
			}
		
		parts.add(entity);
		}
	
	/**
	 * Get the number of parts
	 * @return Number of parts
	 */
	public int getPartCount()
		{
		return (parts.size());
		}
	
	/**
	 * Get a part
	 * @param i Index
	 * @return Part HttpEntity
	 */
	public HttpEntity getPart(int i)
		{
		return (parts.get(i));
		}
	
	@Override
	public Map<String, String> getHeaders()
		{
		final Map<String, String> ret = super.getHeaders();
		
		// Override the required headers with the determined values
		ret.put(HttpHeaders.MIME_VERSION, MIME_VERSION);
		
		return (ret);
		}
	
	public long getContentLength()
		{
		long ret = 0;
		
		// Sum up the content lengths of all parts
		for (HttpEntity part : parts)
			ret += part.getContentLength();
		
		// Render multipart boundaries and headers to determine their size
		try	{
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			writeContent(stream, false);
			ret += stream.size();
			}
		catch (IOException e)
			{
			// unlikely for ByteArrayOutputStream
			}
		
		return (ret);
		}
	
	public void writeContent(OutputStream stream) throws IOException
		{
		writeContent(stream, true);
		}
	
	private void writeContent(OutputStream stream, boolean includeContent) throws IOException
		{
		final String boundary = getContentType().getParam("boundary");
		
		if (options.contains(HttpClientOptions.REPEAT_MULTIPART_HEADERS))
			{
			final StringBuilder rootPart = new StringBuilder();
			// Repeat the MIME headers as root content for broken MIME implementations
			rootPart.append(HttpHeaders.MIME_VERSION).append(": ").append(MIME_VERSION).append(EOL);
			rootPart.append(HttpHeaders.CONTENT_TYPE).append(": ").append(getContentType().toString()).append(EOL);
			
			stream.write(rootPart.toString().getBytes(HEADER_CHARSET));
			}
		
		for (HttpEntity part : parts)
			{
			final StringBuilder partHeaders = new StringBuilder();
			partHeaders.append(EOL).append("--").append(boundary).append(EOL);
			for (Map.Entry<String, String> ent : part.getHeaders().entrySet())
				partHeaders.append(ent.getKey()).append(": ").append(ent.getValue()).append(EOL);
			partHeaders.append(EOL);
			stream.write(partHeaders.toString().getBytes(HEADER_CHARSET));
			if (includeContent)
				part.writeContent(stream);
			}
		
		stream.write((EOL + "--" + boundary + "--" + EOL).getBytes(HEADER_CHARSET));
		}
	}
