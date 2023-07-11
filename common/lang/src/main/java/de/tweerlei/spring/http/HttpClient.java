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
import java.util.List;
import java.util.Map;

/**
 * Perform HTTP requests
 * 
 * @author Robert Wruck
 */
public interface HttpClient
	{
	/**
	 * Set the connect timeout
	 * @param seconds Timeout
	 */
	public void setConnectTimeout(int seconds);
	
	/**
	 * Set the read timeout
	 * @param seconds Timeout
	 */
	public void setReadTimeout(int seconds);
	
	/**
	 * Set the proxy to use
	 * @param host Hostname
	 * @param port Port
	 */
	public void setProxy(String host, int port);
	
	/**
	 * Set an option
	 * @param option Option name (see HttpClientOptions)
	 * @param b Strict mode
	 */
	public void setOption(String option, boolean b);
	
	/**
	 * Close any resources
	 */
	public void close();
	
	/**
	 * Perform a GET request
	 * @param url URL
	 * @param headers Additional headers (may be null)
	 * @param username Username
	 * @param password Password
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity get(String url, Map<String, String> headers, String username, String password) throws IOException;
	
	/**
	 * Perform a POST request
	 * @param url URL
	 * @param request Request entity
	 * @param username Username
	 * @param password Password
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity post(String url, HttpEntity request, String username, String password) throws IOException;
	
	/**
	 * Perform a multipart POST request
	 * @param url URL
	 * @param request Request entities
	 * @param username Username
	 * @param password Password
	 * @return Response entity
	 * @throws IOException on error
	 */
	public HttpEntity post(String url, List<HttpEntity> request, String username, String password) throws IOException;
	}
