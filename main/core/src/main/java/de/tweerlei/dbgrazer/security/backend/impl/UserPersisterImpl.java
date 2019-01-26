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
package de.tweerlei.dbgrazer.security.backend.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.security.backend.UserPersister;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.impl.UserImpl;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class UserPersisterImpl implements UserPersister
	{
	private static final String PROP_NAME = "name";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_AUTHORITIES = "authorities";
	private static final String PROP_GROUPS = "groups";
	
	private static final String PART_SEPARATOR = ":";
	
	private final KeywordService keywordService;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 */
	@Autowired
	public UserPersisterImpl(KeywordService keywordService)
		{
		this.keywordService = keywordService;
		}
	
	@Override
	public User readUser(Reader reader, String name) throws IOException
		{
		final Properties props = new Properties();
		props.load(reader);
		
		final String displayName = props.getProperty(PROP_NAME);
		final String pass = props.getProperty(PROP_PASSWORD);
		
		final String auth = props.getProperty(PROP_AUTHORITIES);
		final Set<Authority> authorities = parseAuthorities(auth);
		
		final String groups = props.getProperty(PROP_GROUPS);
		final Map<String, Set<Authority>> groupAuth = parseGroups(groups);
		
		final Map<String, String> attributes = new HashMap<String, String>();
		for (String prop : props.stringPropertyNames())
			{
			if (!prop.equals(PROP_NAME) && !prop.equals(PROP_PASSWORD) && !prop.equals(PROP_AUTHORITIES) && !prop.equals(PROP_GROUPS))
				attributes.put(prop, props.getProperty(prop));
			}
		
		return (new UserImpl(name, StringUtils.empty(displayName) ? name : displayName, StringUtils.notNull(pass), authorities, groupAuth, attributes));
		}
	
	private Set<Authority> parseAuthorities(String auth)
		{
		final Set<Authority> authorities = EnumSet.noneOf(Authority.class);
		for (String s : keywordService.extractValues(auth))
			{
			final Authority a = Authority.forShortName(s.trim());
			if (a != null)
				authorities.add(a);
			}
		return (authorities);
		}
	
	private Map<String, Set<Authority>> parseGroups(String groups)
		{
		final Set<String> groupNames = new HashSet<String>(keywordService.extractValues(groups));
		final Map<String, Set<Authority>> groupAuth = new HashMap<String, Set<Authority>>();
		for (String s : groupNames)
			{
			final String[] parts = StringUtils.split(s, PART_SEPARATOR);
			final Set<Authority> ga = EnumSet.noneOf(Authority.class);
			
			for (int i = 1; i < parts.length; i++)
				{
				final Authority a = Authority.forShortName(parts[i].trim());
				if (a != null)
					ga.add(a);
				}
			if (ga.isEmpty())
				ga.add(Authority.ROLE_LOGIN);
			
			groupAuth.put(parts[0], ga);
			}
		return (groupAuth);
		}
	
	@Override
	public void writeUser(Writer writer, User user) throws IOException
		{
		final Properties props = new Properties();
		
		for (Map.Entry<String, String> ent : user.getAttributes().entrySet())
			props.setProperty(sanitizeParam(ent.getKey()), sanitizeValue(ent.getValue()));
		
		props.setProperty(PROP_NAME, sanitizeName(user.getName(), true));
		props.setProperty(PROP_PASSWORD, user.getPassword());
		
		if (!user.getGroups().isEmpty())
			{
			final Set<String> groupNames = new HashSet<String>();
			for (Map.Entry<String, Set<Authority>> ent : user.getGroups().entrySet())
				{
				final StringBuilder sb = new StringBuilder();
				sb.append(sanitizeGroup(ent.getKey()));
				for (Authority a : ent.getValue())
					{
					sb.append(PART_SEPARATOR);
					sb.append(a.getShortName());
					}
				groupNames.add(sb.toString());
				}
			
			props.setProperty(PROP_GROUPS, keywordService.combineValues(groupNames));
			}
		
		if (!user.getGrantedAuthorities().isEmpty())
			{
			final Set<String> auth = new HashSet<String>();
			for (Authority a : user.getGrantedAuthorities())
				auth.add(a.getShortName());
			
			props.setProperty(PROP_AUTHORITIES, keywordService.combineValues(auth));
			}
		
		props.store(writer, null);
		}
	
	private String sanitizeName(String name, boolean allowEmpty) throws IOException
		{
		final String s = keywordService.normalizeName(name);
		if (StringUtils.empty(s))
			{
			if (!allowEmpty)
				throw new IOException("Invalid name: " + name);
			return ("");
			}
		return (s);
		}
	
	private String sanitizeParam(String name) throws IOException
		{
		final String s = keywordService.normalizeParam(name);
		if (StringUtils.empty(s))
			throw new IOException("Invalid name: " + name);
		return (s);
		}
	
	private String sanitizeValue(String name)
		{
		final String s = keywordService.normalizeValue(name);
		if (StringUtils.empty(s))
			return ("");
		return (s);
		}
	
	private String sanitizeGroup(String name) throws IOException
		{
		final String s = keywordService.normalizeGroup(name);
		if (StringUtils.empty(s))
			throw new IOException("Invalid group name: " + name);
		return (s);
		}
	}
