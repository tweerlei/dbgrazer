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
import java.util.Map;
import java.util.TreeMap;

import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.http.HttpHeaders;
import de.tweerlei.spring.http.HttpStatusCodes;
import de.tweerlei.spring.http.MimeType;

/**
 * HTTP entity
 * 
 * @author Robert Wruck
 */
public abstract class AbstractHttpEntity implements HttpEntity
	{
	/** HTTP line terminator */
	protected static final String EOL = "\r\n";
	/** HTTP header charset */
	protected static final String HEADER_CHARSET = "US-ASCII";
	/** Default content charset */
	protected static final String DEFAULT_CHARSET = "ISO-8859-1";
	
	private int status;
	private final MimeType contentType;
	private final Map<String, String> headers;
	
	/**
	 * Get the effective content charset
	 * @param contentType MimeType
	 * @return Charset name
	 */
	protected static String getCharset(MimeType contentType)
		{
		final String ret = contentType.getParam("charset");
		if (ret == null)
			return (DEFAULT_CHARSET);
		
		return (ret);
		}
	
	/**
	 * Constructor
	 * @param contentType Content type
	 */
	public AbstractHttpEntity(MimeType contentType)
		{
		this.status = HttpStatusCodes.OK;
		this.contentType = contentType;
		this.headers = new TreeMap<String, String>();
		}
	
	/**
	 * Set the status code
	 * @param status Status code
	 */
	public void setStatus(int status)
		{
		this.status = status;
		}
	
	public int getStatus()
		{
		return (status);
		}
	
	public boolean isSuccessful()
		{
		final int st = getStatus();
		
		return ((st >= HttpStatusCodes.OK) && (st < HttpStatusCodes.MULTIPLE_CHOICES));
		}
	
	public void setHeader(String name, String value)
		{
		if (value == null)
			headers.remove(name);
		else
			headers.put(name, value);
		}
	
	public Map<String, String> getHeaders()
		{
		final Map<String, String> ret = new TreeMap<String, String>(headers);
		
		// Override the required headers with the determined values
		ret.put(HttpHeaders.CONTENT_TYPE, getContentType().toString());
//		ret.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(getContentLength()));
		
		return (ret);
		}
	
	public MimeType getContentType()
		{
		return (contentType);
		}
	
	public byte[] getRawContent()
		{
		try	{
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writeContent(baos);
			return (baos.toByteArray());
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public String toString()
		{
		try	{
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writeContent(baos);
			return (baos.toString(getCharset(getContentType())));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	}
