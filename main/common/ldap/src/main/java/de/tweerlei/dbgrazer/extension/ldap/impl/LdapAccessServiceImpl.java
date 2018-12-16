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
package de.tweerlei.dbgrazer.extension.ldap.impl;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.ldap.ConfigKeys;
import de.tweerlei.dbgrazer.extension.ldap.LdapAccessService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;

/**
 * Manage LDAP connections
 * 
 * @author Robert Wruck
 */
@Service
public class LdapAccessServiceImpl implements LdapAccessService, LinkListener, LinkManager
	{
	private final ConfigService configService;
	private final LinkService linkService;
	private final Map<String, LdapTemplate> activeConnections;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param linkService LinkService
	 */
	@Autowired
	public LdapAccessServiceImpl(ConfigService configService, LinkService linkService)
		{
		this.configService = configService;
		this.linkService = linkService;
		this.activeConnections = new ConcurrentHashMap<String, LdapTemplate>();
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		linkService.addListener(this);
		linkService.addManager(this);
		}
	
	/**
	 * Close all connections
	 */
	@PreDestroy
	public synchronized void closeConnections()
		{
		logger.log(Level.INFO, "Closing " + activeConnections.size() + " connections");
		activeConnections.clear();
		}
	
	private synchronized void closeConnection(String link)
		{
		final LdapTemplate ldap = activeConnections.remove(link);
		if (ldap != null)
			{
			logger.log(Level.INFO, "Closing connection " + link);
			}
		}
	
	@Override
	public void linksChanged()
		{
		closeConnections();
		}

	@Override
	public void linkChanged(String link)
		{
		closeConnection(link);
		}
	
	@Override
	public LdapTemplate getLdapTemplate(String c)
		{
		final LdapTemplate ldapTemplate = activeConnections.get(c);
		if (ldapTemplate != null)
			return (ldapTemplate);
		
		return (createConnection(c));
		}
	
	@Override
	public LdapTemplate getLdapTemplate(String c, String username, String password)
		{
		final LinkDef def = linkService.getLink(c, null);
		if ((def == null) /*|| !(def.getType() instanceof LdapLinkType)*/)
			throw new RuntimeException("Unknown link " + c);
		
		return (createLdapTemplate(def.getUrl(), def.getDriver(), username, password));
		}
	
	private synchronized LdapTemplate createConnection(String c)
		{
		LdapTemplate ldapTemplate = activeConnections.get(c);
		if (ldapTemplate != null)
			return (ldapTemplate);
		
		final LinkDef def = linkService.getLink(c, null);
		if ((def == null) /*|| !(def.getType() instanceof LdapLinkType)*/)
			throw new RuntimeException("Unknown link " + c);
		
		ldapTemplate = createLdapTemplate(def.getUrl(), def.getDriver(), def.getUsername(), def.getPassword());
		activeConnections.put(c, ldapTemplate);
		
		return (ldapTemplate);
		}
	
	private LdapTemplate createLdapTemplate(String url, String base, String user, String pass)
		{
		if (url == null)
			throw new RuntimeException("Missing LDAP url");
		
		logger.log(Level.INFO, "Creating LDAP connection: [" + url + "] [" + base + "] [" + user + "]");
		
		final LdapContextSource src = new LdapContextSource();
		src.setUrl(url);
		src.setBase(base);
		src.setUserDn(user);
		src.setPassword(pass);
//		src.setPooled(true);
		
		final String binaryAttributes = configService.get(ConfigKeys.LDAP_BINARY_ATTRIBUTES);
		if (!StringUtils.empty(binaryAttributes))
			src.setBaseEnvironmentProperties(Collections.singletonMap("java.naming.ldap.attributes.binary", binaryAttributes));
		
		try	{
			src.afterPropertiesSet();
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "createLdapTemplate", e);
			}
		
		final LdapTemplate tmp = new LdapTemplate(src);
		// Workaround for ActiveDirectory servers
		tmp.setIgnorePartialResultException(true);
		
		try	{
			tmp.afterPropertiesSet();
			}
		catch (Exception e)
			{
			logger.log(Level.WARNING, "createLdapTemplate", e);
			}
		
		return (tmp);
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, LdapTemplate> ent : activeConnections.entrySet())
			ret.put(ent.getKey(), 1);
		
		return (ret);
		}
	}
