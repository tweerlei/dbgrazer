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

import de.tweerlei.spring.http.MimeType;

/**
 * HttpEntity with in-memory content
 * 
 * @author Robert Wruck
 */
public class ByteArrayHttpEntity extends AbstractHttpEntity
	{
	private final byte[] content;
	private final int offset;
	private final int length;
	
	/**
	 * Constructor
	 * @param contentType Content type
	 * @param data Data bytes
	 * @param off First byte offset
	 * @param len Number of bytes
	 */
	public ByteArrayHttpEntity(MimeType contentType, byte[] data, int off, int len)
		{
		super(contentType);
		this.content = data;
		this.offset = off;
		this.length = len;
		}
	
	/**
	 * Constructor
	 * @param contentType Content type
	 * @param data Data bytes
	 */
	public ByteArrayHttpEntity(MimeType contentType, byte[] data)
		{
		this(contentType, data, 0, data.length);
		}
	
	public long getContentLength()
		{
		return (length);
		}
	
	public void writeContent(OutputStream stream) throws IOException
		{
		stream.write(content, offset, length);
		}
	
	@Override
	public byte[] getRawContent()
		{
		if ((offset == 0) && (length == content.length))
			return (content);
		
		final byte[] ret = new byte[length];
		System.arraycopy(content, offset, ret, 0, length);
		return (ret);
		}
	}
