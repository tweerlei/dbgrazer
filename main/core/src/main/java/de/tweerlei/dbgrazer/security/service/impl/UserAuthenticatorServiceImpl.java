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
package de.tweerlei.dbgrazer.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;
import de.tweerlei.dbgrazer.security.backend.UserLoader;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.service.UserAuthenticatorService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class UserAuthenticatorServiceImpl implements UserAuthenticatorService, ConfigListener
	{
	private final ConfigService configService;
	private final KeywordService keywordService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	
	private List<UserAuthenticator> authorizers;
	private List<UserLoader> loaders;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param keywordService KeywordService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public UserAuthenticatorServiceImpl(ConfigService configService, KeywordService keywordService,
			ModuleLookupService moduleService)
		{
		this.configService = configService;
		this.keywordService = keywordService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Register for config changess
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
		final String authPrefixes = configService.get(ConfigKeys.USER_AUTHN);
		final List<UserAuthenticator> a = new ArrayList<UserAuthenticator>();
		for (String s : keywordService.extractValues(authPrefixes))
			{
			logger.log(Level.INFO, "Using authn UserLoader: " + s);
			try	{
				a.add(moduleService.findModuleInstance(s + "UserLoader", UserAuthenticator.class));
				}
			catch (RuntimeException e)
				{
				logger.log(Level.SEVERE, "findModuleInstance", e);
				}
			}
		
		final String loaderPrefixes = configService.get(ConfigKeys.USER_AUTHZ);
		final List<UserLoader> l = new ArrayList<UserLoader>();
		for (String s : keywordService.extractValues(loaderPrefixes))
			{
			logger.log(Level.INFO, "Using authz UserLoader: " + s);
			try	{
				l.add(moduleService.findModuleInstance(s + "UserLoader", UserLoader.class));
				}
			catch (RuntimeException e)
				{
				logger.log(Level.SEVERE, "findModuleInstance", e);
				}
			}
		
		authorizers = a;
		loaders = l;
		}
	
	@Override
	public boolean login(String username, String password)
		{
		for (UserAuthenticator u : authorizers)
			{
			if (u.authenticate(username, password))
				{
				logger.log(Level.INFO, "Authenticated user: " + username);
				return (true);
				}
			}
		
		logger.log(Level.INFO, "Authentication failed: " + username);
		return (false);
		}
	
	@Override
	public User loadUser(String username)
		{
		for (UserLoader u : loaders)
			{
			final User user = u.loadUser(username);
			if (user != null)
				{
				logger.log(Level.INFO, "Authorized user: " + username);
				return (user);
				}
			}
		
		logger.log(Level.INFO, "Authorization failed: " + username);
		return (null);
		}
	}
