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
package de.tweerlei.spring.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * HTTP entity
 * 
 * @author Robert Wruck
 */
public interface HttpEntity
	{
	/**
	 * Set a header field
	 * @param name Header name
	 * @param value Value
	 */
	public void setHeader(String name, String value);
	
	/**
	 * Get the headers (read-only)
	 * @return the headers
	 */
	public Map<String, String> getHeaders();
	
	/**
	 * Get the status code
	 * @return the status
	 */
	public int getStatus();
	
	/**
	 * Check whether the status code indicates success
	 * @return true for success
	 */
	public boolean isSuccessful();
	
	/**
	 * Get the content length (RFC 1945)
	 * @return Content length
	 */
	public long getContentLength();
	
	/**
	 * Get the content type (RFC 1945 and 2045)
	 * @return Content type (never null)
	 */
	public MimeType getContentType();
	
	/**
	 * Write the content
	 * @param stream OutputStream
	 * @throws IOException on error
	 */
	public void writeContent(OutputStream stream) throws IOException;
	
	/**
	 * Writes the content to a byte array and returns it
	 * @return Byte array
	 */
	public byte[] getRawContent();
	}
