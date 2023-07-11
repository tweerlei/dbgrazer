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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import de.tweerlei.common.codec.Base64OutputStream;
import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.http.HttpHeaders;
import de.tweerlei.spring.http.MimeType;

/**
 * Base64 encoded HTTP entity
 * 
 * @author Robert Wruck
 */
public class Base64HttpEntity implements HttpEntity
	{
	private final HttpEntity entity;
	
	/**
	 * Constructor
	 * @param entity Wrapped entity
	 */
	public Base64HttpEntity(HttpEntity entity)
		{
		this.entity = entity;
		}
	
	public void setHeader(String name, String value)
		{
		entity.setHeader(name, value);
		}
	
	public Map<String, String> getHeaders()
		{
		final Map<String, String> ret = entity.getHeaders();
		
		// Override the required headers with the determined values
//		ret.put(HttpHeaders.CONTENT_TYPE, getContentType().toString());
//		ret.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(getContentLength()));
		ret.put(HttpHeaders.CONTENT_TRANSFER_ENCODING, "base64");
		
		return (ret);
		}
	
	public int getStatus()
		{
		return (entity.getStatus());
		}
	
	public boolean isSuccessful()
		{
		return (entity.isSuccessful());
		}
	
	public long getContentLength()
		{
		final long l = entity.getContentLength();
		
		// 3-byte blocks, encoded in 4 chars each, rounded up
		final long blocks = (l + 2) / 3;
		
		// 18-block lines (72 chars), rounded up
		final long lines = (blocks + 17) / 18;
		
		// delimited by CRLF each
		return (4 * blocks + 2 * lines);
		}
	
	public MimeType getContentType()
		{
		return (entity.getContentType());
		}
	
	public void writeContent(OutputStream stream) throws IOException
		{
		final Base64OutputStream os = new Base64OutputStream(new OutputStreamWriter(stream, "US-ASCII"), 72, "\r\n");
		
		entity.writeContent(os);
		
		os.flush();
		}
	
	public byte[] getRawContent()
		{
		return (entity.getRawContent());
		}
	
	@Override
	public String toString()
		{
		return (entity.toString());
		}
	}
