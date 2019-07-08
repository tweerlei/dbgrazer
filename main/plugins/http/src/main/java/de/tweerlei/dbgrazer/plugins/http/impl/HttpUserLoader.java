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
package de.tweerlei.dbgrazer.plugins.http.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.http.HttpClientService;
import de.tweerlei.dbgrazer.plugins.http.ConfigKeys;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;
import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.http.impl.MimeTypeBuilder;
import de.tweerlei.spring.http.impl.StringHttpEntity;

/**
 * Perform user authentication via HTTP
 * 
 * @author Robert Wruck
 */
@Service("httpUserLoader")
public class HttpUserLoader implements UserAuthenticator, ConfigListener
	{
	private static final String SOAPACTION_HEADER = "SOAPAction";
	
	private final ConfigService configService;
	private final HttpClientService httpClient;
	private final Logger logger;
	
	private String link;
	private String url;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param httpClient HttpClientService
	 */
	@Autowired
	public HttpUserLoader(ConfigService configService, HttpClientService httpClient)
		{
		this.configService = configService;
		this.httpClient = httpClient;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		link = configService.get(ConfigKeys.HTTP_LINK);
		url = configService.get(ConfigKeys.HTTP_URL);
		}
	
	@Override
	public boolean authenticate(String username, String password)
		{
		if (link == null)
			{
			logger.log(Level.SEVERE, "No valid link for authentication");
			return (false);
			}
		if (url == null)
			{
			logger.log(Level.SEVERE, "No valid URL for authentication");
			return (false);
			}
		
		if (configService.get(ConfigKeys.HTTP_POST))
			{
			return authenticatePOST(username, password);
			}
		else
			{
			return authenticateGET(username, password);
			}
		}
	
	private boolean authenticateGET(String username, String password)
		{
		try	{
			final String pat = configService.get(ConfigKeys.HTTP_PATTERN);
			final Pattern pattern;
			if (pat == null)
				pattern = null;
			else
				pattern = Pattern.compile(pat);
			final String request = url.replace("%username%", username).replace("%password%", password);
			
			final HttpEntity response = httpClient.get(link, request, null);
			
			if (!response.isSuccessful())
				{
				logger.log(Level.INFO, "HTTP status " + response.getStatus());
				return (false);
				}
			
			final String body = response.toString();
			if ((pattern != null) && !pattern.matcher(body).matches())
				{
				logger.log(Level.INFO, body);
				return (false);
				}
			
			return (true);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "GET failed", e);
			return (false);
			}
		}
	
	private boolean authenticatePOST(String username, String password)
		{
		try	{
			final String type = configService.get(ConfigKeys.HTTP_POST_CONTENT_TYPE);
			final String tmpl = configService.get(ConfigKeys.HTTP_POST_BODY);
			final String action = configService.get(ConfigKeys.HTTP_POST_ACTION);
			final String body;
			if (tmpl == null)
				body = "";
			else
				body = tmpl.replace("&username;", username).replace("&password;", password);
			
			final String pat = configService.get(ConfigKeys.HTTP_PATTERN);
			final Pattern pattern;
			if (pat == null)
				pattern = null;
			else
				pattern = Pattern.compile(pat);
			
			final StringHttpEntity request = new StringHttpEntity(MimeTypeBuilder.parse(type), body);
			if (!StringUtils.empty(action))
				request.setHeader(SOAPACTION_HEADER, action);
			
			final HttpEntity response = httpClient.post(link, url, request);
			
			if (!response.isSuccessful())
				{
				logger.log(Level.INFO, "HTTP status " + response.getStatus());
				return (false);
				}
			
			final String respBody = response.toString();
			if ((pattern != null) && !pattern.matcher(respBody).matches())
				{
				logger.log(Level.INFO, respBody);
				return (false);
				}
			
			return (true);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "POST failed", e);
			return (false);
			}
		}
	}
