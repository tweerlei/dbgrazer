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

/**
 * HTTP header names
 * 
 * @author Robert Wruck
 */
public final class HttpHeaders
	{
	/** The header key for the authorization (RFC 1945 10.2) */
	public static final String AUTHORIZATION = "Authorization";
	
	/** The header key for the content ID (RFC 2045 8) */
	public static final String CONTENT_DESCRIPTION = "Content-Description";
	
	/** The header key for the content disposition (RFC 2183) */
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	
	/** The header key for the content encoding (RFC 1945 10.3) */
	public static final String CONTENT_ENCODING = "Content-Encoding";
	
	/** The header key for the content ID (RFC 2045 7) */
	public static final String CONTENT_ID = "Content-ID";
	
	/** The header key for the content length (RFC 1945 10.4) */
	public static final String CONTENT_LENGTH = "Content-Length";
	
	/** The header key for the content transfer encoding (RFC 2045 6) */
	public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	
	/** The header key for the content type (RFC 1945 10.5, 2045 5) */
	public static final String CONTENT_TYPE = "Content-Type";
	
	/** The header key for the modification date (RFC 1945 10.10) */
	public static final String LAST_MODIFIED = "Last-Modified";
	
	/** The header key for the MIME version (RFC 2045 4) */
	public static final String MIME_VERSION = "MIME-Version";
	
	/** The virtual header key for the response status line */
	public static final String STATUS = "Status";
	
	
	private HttpHeaders()
		{
		}
	}
