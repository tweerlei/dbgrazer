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
package de.tweerlei.spring.web.service;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods for HttpServletRequest objects
 * 
 * @author Robert Wruck
 */
public interface RequestHelperService
	{
	/**
	 * Get the credentials passed for HTTP basic authentication
	 * @param request HttpServletRequest
	 * @return Array with two elements (username, password) or null
	 */
	public String[] getBasicAuthentication(HttpServletRequest request);
	
	/**
	 * Get a URI object for the requested address
	 * @param request HttpServletRequest
	 * @return URI
	 */
	public URI getRequestURI(HttpServletRequest request);
	
	/**
	 * Get a URI object for the referrer address, if any
	 * @param request HttpServletRequest
	 * @return URI or null
	 */
	public URI getReferrerURI(HttpServletRequest request);
	}
