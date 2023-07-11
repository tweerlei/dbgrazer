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
 * HTTP status code constants
 * 
 * @author Robert Wruck
 */
public final class HttpStatusCodes
	{
	/** HTTP status: Continue (lowest informational code) */
	public static final int CONTINUE = 100;
	
	/** HTTP status: OK (lowest success code) */
	public static final int OK = 200;
	
	/** HTTP status: Multiple choices (lowest redirection code) */
	public static final int MULTIPLE_CHOICES = 300;
	
	/** HTTP status: Bad request (lowest client error code) */
	public static final int BAD_REQUEST = 400;
	
	/** HTTP status: Internal server error (lowest server error code) */
	public static final int INTERNAL_SERVER_ERROR = 500;
	
	
	private HttpStatusCodes()
		{
		}
	}
