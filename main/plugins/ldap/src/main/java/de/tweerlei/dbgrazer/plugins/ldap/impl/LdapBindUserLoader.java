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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.LdapEntryIdentification;
import org.springframework.ldap.core.LdapEntryIdentificationContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.ldap.LdapAccessService;
import de.tweerlei.dbgrazer.plugins.ldap.ConfigKeys;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;

/**
 * Perform user authentication against an LDAP server via simple bind
 * 
 * @author Robert Wruck
 */
@Service("ldapbindUserLoader")
public class LdapBindUserLoader implements UserAuthenticator, ConfigListener
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
	public LdapBindUserLoader(ConfigService configService, LdapAccessService ldapAccessService)
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
		
		final String base = configService.get(ConfigKeys.LDAP_USER_DN);
		final String pattern = configService.get(ConfigKeys.LDAP_USER_PATTERN);
		final String ldapUsername = pattern.replace("?", username);
		final String ldapUserDN;
		if (StringUtils.empty(base))
			ldapUserDN = ldapUsername;
		else
			ldapUserDN = ldapUsername + "," + base;
		
		final LdapTemplate ldap = ldapAccessService.getLdapTemplate(link, ldapUserDN, password);
		if (ldap == null)
			{
			logger.log(Level.SEVERE, "Unknown link for authentication: " + link);
			return (false);
			}
		
		final String filter = configService.get(ConfigKeys.LDAP_USER_FILTER);
		final String userFilter = filter.replace("?", username);
		
		logger.log(Level.INFO, "Trying to authenticate: [" + ldapUserDN + "] [" + base + "] [" + userFilter + "]");
		
		try	{
			@SuppressWarnings("unchecked")
			final List<LdapEntryIdentification> l = ldap.search(base, userFilter, new LdapEntryIdentificationContextMapper());
			if (l.size() != 1)
				{
				logger.log(Level.WARNING, "Found " + l.size() + " results");
				return (false);
				}
			
			final LdapEntryIdentification entryIdentification = l.get(0);
			logger.log(Level.INFO, "Found " + entryIdentification.getAbsoluteDn());
			return (true);
			}
		catch (NamingException e)
			{
			logger.log(Level.SEVERE, "LDAP search failed for bind user DN: " + ldapUserDN, e);
			return (false);
			}
		}
	}
