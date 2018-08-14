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
package de.tweerlei.dbgrazer.extension.http.impl;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.http.ConfigKeys;

/**
 * Configure global networking settings
 * 
 * @author Robert Wruck
 */
@Service
public class NetworkConfigurator implements ConfigListener
	{
	private final ConfigService configService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 */
	@Autowired
	public NetworkConfigurator(ConfigService configService)
		{
		this.configService = configService;
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
		final String proxyHost = configService.get(ConfigKeys.HTTP_PROXY_HOST);
		final int proxyPort = configService.get(ConfigKeys.HTTP_PROXY_PORT);
		final String proxyUsername = configService.get(ConfigKeys.HTTP_PROXY_USERNAME);
		final String proxyPassword = configService.get(ConfigKeys.HTTP_PROXY_PASSWORD);
		
		if (!StringUtils.empty(proxyHost) && !StringUtils.empty(proxyUsername))
			{
			logger.log(Level.INFO, "Registering proxy authenticator for " + proxyHost + ":" + proxyPort);
			
			final PasswordAuthentication auth = new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
			Authenticator.setDefault(new Authenticator()
				{
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
					{
					if (getRequestorType() == Authenticator.RequestorType.PROXY)
						{
						if (getRequestingHost().toLowerCase().equals(proxyHost.toLowerCase())
								&& (getRequestingPort() == proxyPort))
							return (auth);
						}
					return (null);
					}
				});
			}
		else
			Authenticator.setDefault(null);
		}
	}
