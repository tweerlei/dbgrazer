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
package de.tweerlei.dbgrazer.extension.http;

import java.io.IOException;
import java.util.List;

import de.tweerlei.spring.http.HttpEntity;

/**
 * Perform HTTP requests
 * 
 * @author Robert Wruck
 */
public interface HttpClientService
	{
	/**
	 * Perform a GET request
	 * @param c Link name
	 * @param endpoint Endpoint path
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity get(String c, String endpoint) throws IOException;
	
	/**
	 * Perform a GET request for an external URL that might not be relative to any link's URL.
	 * If it's not, the request will be performed using an anonymous request.
	 * @param url URL
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity getExternal(String url) throws IOException;
	
	/**
	 * Perform a POST request
	 * @param c Link name
	 * @param endpoint Endpoint path
	 * @param request Request entity
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity post(String c, String endpoint, HttpEntity request) throws IOException;
	
	/**
	 * Perform a multipart POST request
	 * @param c Link name
	 * @param endpoint Endpoint path
	 * @param request Request entity
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity post(String c, String endpoint, List<HttpEntity> request) throws IOException;
	}
