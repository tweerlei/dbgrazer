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
package de.tweerlei.dbgrazer.security.model.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;

/**
 * User details
 * 
 * @author Robert Wruck
 */
public class UserImpl extends NamedBase implements User, Serializable
	{
	private final String login;
	private final String password;
	private final Set<Authority> authorities;
	private final Map<String, Set<Authority>> groups;
	private final Map<String, String> attributes;
	
	/**
	 * Constructor
	 * @param login Login name
	 * @param name Display name
	 * @param password Password
	 * @param authorities Granted authorities
	 * @param groups Groups
	 * @param attributes Attributes
	 */
	public UserImpl(String login, String name, String password, Set<Authority> authorities, Map<String, Set<Authority>> groups, Map<String, String> attributes)
		{
		super(name);
		this.login = login;
		this.password = password;
		// EnumSet.copyOf doesn't work for empty sets
		this.authorities = ((authorities == null) || authorities.isEmpty()) ? Collections.<Authority>emptySet() : Collections.unmodifiableSet(EnumSet.copyOf(authorities));
		this.groups = (groups == null) ? Collections.<String, Set<Authority>>emptyMap() : Collections.unmodifiableMap(new TreeMap<String, Set<Authority>>(groups));
		this.attributes = (attributes == null) ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<String, String>(attributes));
		}
	
	@Override
	public String getLogin()
		{
		return login;
		}

	@Override
	public String getPassword()
		{
		return password;
		}
	
	@Override
	public Set<Authority> getGrantedAuthorities()
		{
		return authorities;
		}

	@Override
	public Map<String, Set<Authority>> getGroups()
		{
		return (groups);
		}
	
	@Override
	public boolean isGranted(Authority authority)
		{
		return (authorities.contains(authority));
		}
	
	@Override
	public boolean isGranted(Authority authority, String groupName)
		{
		if (isGranted(authority))
			return (true);
		
		final Set<Authority> auth = groups.get(groupName);
		return ((auth != null) && auth.contains(authority));
		}
	
	@Override
	public final Map<String, String> getAttributes()
		{
		return (attributes);
		}
	}
