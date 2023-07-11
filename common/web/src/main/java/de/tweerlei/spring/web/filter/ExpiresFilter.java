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
package de.tweerlei.spring.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Add an HTTP Expires header to affected resources.
 * Time to live for resources is taken from the init-param "ttl":
 * negative value = Never expires
 * 0 = expire immediately
 * positive value = Expire after the given number of seconds
 * 
 * @author Robert Wruck
 */
public class ExpiresFilter implements Filter
	{
	private static final String PARAM_TTL = "ttl";
	
	private int expireTime;
	
	/**
	 * Constructor
	 */
	public ExpiresFilter()
		{
		}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
		{
		// Do our processing
		addCacheHeaders(request, response);
		
		// Pass the request down the chain
		chain.doFilter(request, response);
		}
	
	public void destroy()
		{
		}
	
	public void init(FilterConfig config) throws ServletException
		{
		final String v = config.getInitParameter(PARAM_TTL);
		if (v == null)
			expireTime = 0;
		else
			expireTime = Integer.parseInt(v);
		}
	
	private void addCacheHeaders(ServletRequest request, ServletResponse response)
		{
		final HttpServletResponse sr = (HttpServletResponse) response;
		if (expireTime < 0)
			{
			sr.setDateHeader("Expires", System.currentTimeMillis() + 365 * 86400 * 1000);
			sr.setHeader("Cache-Control", "max-age=" + String.valueOf(365 * 86400) + ",public,must-revalidate");
			}
		else
			{
			sr.setDateHeader("Expires", System.currentTimeMillis() + expireTime * 1000);
			sr.setHeader("Cache-Control", "max-age=" + String.valueOf(expireTime) + ",public,must-revalidate");
			}
		}
	
	@Override
	public String toString()
		{
		return ("ExpiresFilter(" + expireTime + ")");
		}
	}
