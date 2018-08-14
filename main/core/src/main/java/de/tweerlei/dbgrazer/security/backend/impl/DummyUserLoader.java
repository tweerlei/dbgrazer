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
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.file.ObjectPersister;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;
import de.tweerlei.dbgrazer.security.backend.UserLoader;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.impl.UserImpl;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Perform user authentication against the configured adminUser and adminPassword
 * 
 * @author Robert Wruck
 */
@Service("dummyUserLoader")
public class DummyUserLoader implements UserLoader, UserAuthenticator
	{
	private final ConfigAccessor configService;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 */
	@Autowired
	public DummyUserLoader(ConfigAccessor configService)
		{
		this.configService = configService;
		}
	
	@Override
	public boolean authenticate(String username, String password)
		{
		final String user = configService.get(ConfigKeys.ADMIN_USERNAME);
		if (StringUtils.empty(user) || !username.equals(user))
			return (false);
		
		final String pass = configService.get(ConfigKeys.ADMIN_PASSWORD);
		if (StringUtils.empty(pass) || !password.equals(pass))
			return (false);
		
		return (true);
		}
	
	@Override
	public User loadUser(String username)
		{
		final String user = configService.get(ConfigKeys.ADMIN_USERNAME);
		if (StringUtils.empty(user) || !username.equals(user))
			return (null);
		
		return (new UserImpl(username, username, "", EnumSet.allOf(Authority.class), Collections.<String, Set<Authority>>emptyMap(), Collections.<String, String>emptyMap()));
		}

	@Override
	public SortedSet<String> listUsers()
		{
		final SortedSet<String> ret = new TreeSet<String>();
		ret.add(configService.get(ConfigKeys.ADMIN_USERNAME));
		return (ret);
		}
	
	@Override
	public void createUser(String user, String name, User u) throws IOException
		{
		throw new IOException("Not implemented");
		}

	@Override
	public void updateUser(String user, String name, String newName, User u) throws IOException
		{
		throw new IOException("Not implemented");
		}

	@Override
	public void removeUser(String user, String name) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public List<HistoryEntry> getHistory(String name, int limit) throws IOException
		{
		return (new ArrayList<HistoryEntry>());
		}
	
	@Override
	public SortedSet<String> listExtensionObjects(String user, String schema, String extensionName)
		{
		return (new TreeSet<String>());
		}
	
	@Override
	public <T> T loadExtensionObject(String user, String schema, String extensionName, String name, ObjectPersister<T> persister) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public <T> void saveExtensionObject(String user, String owner, String schema, String extensionName, String name, T object, ObjectPersister<T> persister) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public void removeExtensionObject(String user, String owner, String schema, String extensionName, String name) throws IOException
		{
		throw new IOException("Not implemented");
		}
	}
