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

import de.tweerlei.spring.http.MimeType;

/**
 * HttpEntity with in-memory content
 * 
 * @author Robert Wruck
 */
public class StringHttpEntity extends ByteArrayHttpEntity
	{
	/**
	 * Constructor
	 * @param contentType Content type
	 * @param str String
	 * @throws IOException If the charset is unsupported
	 */
	public StringHttpEntity(MimeType contentType, String str) throws IOException
		{
		super(contentType, str.getBytes(getCharset(contentType)));
		}
	}
