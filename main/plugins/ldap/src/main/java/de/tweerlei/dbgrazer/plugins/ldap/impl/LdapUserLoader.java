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
package de.tweerlei.dbgrazer.plugins.ldap.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.ldap.LdapAccessService;
import de.tweerlei.dbgrazer.plugins.ldap.ConfigKeys;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;

/**
 * Perform user authentication against an LDAP server
 * 
 * @author Robert Wruck
 */
@Service("ldapUserLoader")
public class LdapUserLoader implements UserAuthenticator, ConfigListener
	{
	private final ConfigService configService;
	private final LdapAccessService ldapAccessService;
	private final Logger logger;
	
	private String link;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param ldapAccessService LdapAccessService
	 */
	@Autowired
	public LdapUserLoader(ConfigService configService, LdapAccessService ldapAccessService)
		{
		this.configService = configService;
		this.ldapAccessService = ldapAccessService;
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
		link = configService.get(ConfigKeys.LDAP_LINK);
		}
	
	@Override
	public boolean authenticate(String username, String password)
		{
		if (link == null)
			{
			logger.log(Level.SEVERE, "No valid link for authentication");
			return (false);
			}
		
		final LdapTemplate ldap = ldapAccessService.getLdapTemplate(link);
		if (ldap == null)
			{
			logger.log(Level.SEVERE, "Unknown link for authentication: " + link);
			return (false);
			}
		
		final String base = configService.get(ConfigKeys.LDAP_USER_DN);
		final String filter = configService.get(ConfigKeys.LDAP_USER_FILTER);
		
		final String userFilter = filter.replace("?", username);
		
		logger.log(Level.INFO, "Trying to authenticate: [" + base + "] [" + userFilter + "]");
		
		return (ldap.authenticate(base, userFilter, password));
		}
	}
