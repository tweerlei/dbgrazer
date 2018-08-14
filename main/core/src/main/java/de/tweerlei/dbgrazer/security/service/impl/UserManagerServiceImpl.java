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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.file.ObjectPersister;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.security.backend.UserLoader;
import de.tweerlei.dbgrazer.security.backend.impl.DummyUserLoader;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.UserErrorKeys;
import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class UserManagerServiceImpl implements UserManagerService, ConfigListener
	{
	private final ConfigService configService;
	private final KeywordService keywordService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	
	private UserLoader loader;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param keywordService KeywordService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public UserManagerServiceImpl(ConfigService configService, KeywordService keywordService, ModuleLookupService moduleService)
		{
		this.configService = configService;
		this.keywordService = keywordService;
		this.moduleService = moduleService;
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
		final String loaderPrefix = configService.get(ConfigKeys.USER_LOADER);
		
		logger.log(Level.INFO, "Using edit UserLoader: " + loaderPrefix);
		try	{
			loader = moduleService.findModuleInstance(loaderPrefix + "UserLoader", UserLoader.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			loader = new DummyUserLoader(configService);
			}
		}
	
	@Override
	public SortedSet<String> findAllUsers()
		{
		return (loader.listUsers());
		}
	
	@Override
	public User findUserByName(String name)
		{
		final String qn = keywordService.normalizeName(name);
		
		return (loader.loadUser(qn));
		}
	
	@Override
	public String createUser(String user, User u) throws BindException
		{
		validateUser(u);
		
		final String qn = keywordService.normalizeName(u.getLogin());
		if (StringUtils.empty(qn))
			{
			logger.log(Level.INFO, "createUser: Name is empty");
			return (null);
			}
		if (findUserByName(qn) != null)
			{
			logger.log(Level.INFO, "createUser: Not overwriting " + qn);
			return (null);
			}
		
		try	{
			loader.createUser(user, qn, u);
			logger.log(Level.INFO, "createUser: Successfully created " + qn);
			return (qn);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createUser: createUser failed " + qn, e);
			return (null);
			}
		}
	
	@Override
	public String updateUser(String user, String name, User u) throws BindException
		{
		validateUser(u);
		
		final String qnOld = keywordService.normalizeName(name);
		final User qOld = findUserByName(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "updateUser: Not found " + qnOld);
			return (null);
			}
		
		final String qnNew = keywordService.normalizeName(u.getLogin());
		if (StringUtils.empty(qnNew))
			{
			logger.log(Level.INFO, "updateUser: Name is empty");
			return (null);
			}
		
		final User qNew = findUserByName(qnNew);
		if ((qNew != null) && !qOld.getLogin().equals(qNew.getLogin()))
			{
			logger.log(Level.INFO, "updateUser: Not overwriting " + qnNew);
			return (null);
			}
		
		try	{
			loader.updateUser(user, qOld.getLogin(), qnNew, u);
			logger.log(Level.INFO, "updateUser: Successfully changed " + qOld.getLogin() + "/" + qnNew);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "updateUser: updateUser failed " + qOld.getLogin() + "/" + qnNew, e);
			return (null);
			}
		
		return (qnNew);
		}
	
	private void validateUser(User user) throws BindException
		{
		final BindException errors = new BindException(user, "model");
		
		if (StringUtils.empty(user.getName()))
			errors.rejectValue("name", UserErrorKeys.EMPTY_NAME);
		
		if (errors.hasErrors())
			throw errors;
		}
	
	@Override
	public boolean removeUser(String user, String name)
		{
		final String qnOld = keywordService.normalizeName(name);
		final User qOld = findUserByName(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "removeUser: Not found " + qnOld);
			return (false);
			}
		
		try	{
			loader.removeUser(user, qOld.getLogin());
			logger.log(Level.INFO, "removeUser: Successfully removed " + qOld.getLogin());
			return (true);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "removeUser: removeUser failed", e);
			return (false);
			}
		}
	
	@Override
	public SortedSet<String> listExtensionObjects(String user, String schema, String extensionName)
		{
		final String qn = keywordService.normalizeName(user);
		final String sn = keywordService.normalizeName(schema);
		final String en = keywordService.normalizeName(extensionName);
		
		return (loader.listExtensionObjects(qn, sn, en));
		}
	
	@Override
	public <T> T loadExtensionObject(String user, String schema, String extensionName, String name, ObjectPersister<T> pers) throws IOException
		{
		final String qn = keywordService.normalizeName(user);
		final String sn = keywordService.normalizeName(schema);
		final String en = keywordService.normalizeName(extensionName);
		final String dn = keywordService.normalizeName(name);
		
		return (loader.loadExtensionObject(qn, sn, en, dn, pers));
		}
	
	@Override
	public <T> String saveExtensionObject(String user, String owner, String schema, String extensionName, String name, T object, ObjectPersister<T> pers) throws IOException
		{
		final String qn = keywordService.normalizeName(owner);
		final String sn = keywordService.normalizeName(schema);
		final String en = keywordService.normalizeName(extensionName);
		final String dn = keywordService.normalizeName(name);
		
		loader.saveExtensionObject(user, qn, sn, en, dn, object, pers);
		
		return (dn);
		}
	
	@Override
	public void removeExtensionObject(String user, String owner, String schema, String extensionName, String name) throws IOException
		{
		final String qn = keywordService.normalizeName(owner);
		final String sn = keywordService.normalizeName(schema);
		final String en = keywordService.normalizeName(extensionName);
		final String dn = keywordService.normalizeName(name);
		
		loader.removeExtensionObject(user, qn, sn, en, dn);
		}
	
	@Override
	public List<HistoryEntry> getHistory(String name, int limit)
		{
		final String qn = keywordService.normalizeName(name);
		
		try	{
			final List<HistoryEntry> l = loader.getHistory(qn, limit);
			if (l.size() > 1)
				Collections.sort(l);
			return (l);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "getHistory: getHistory failed", e);
			return (Collections.emptyList());
			}
		}
	}
