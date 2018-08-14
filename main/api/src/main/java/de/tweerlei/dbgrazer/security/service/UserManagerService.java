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
package de.tweerlei.dbgrazer.security.service;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import org.springframework.validation.BindException;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.file.ObjectPersister;
import de.tweerlei.dbgrazer.security.model.User;

/**
 * Service for managing users
 * 
 * @author Robert Wruck
 */
public interface UserManagerService
	{
	/**
	 * Get all known user names
	 * @return User names
	 */
	public SortedSet<String> findAllUsers();
	
	/**
	 * Find a user by name
	 * @param name User name
	 * @return User or null
	 */
	public User findUserByName(String name);
	
	/**
	 * Create a user
	 * @param user User name
	 * @param u User definition
	 * @return Created user name
	 * @throws BindException on validation errors
	 */
	public String createUser(String user, User u) throws BindException;
	
	/**
	 * Update a query
	 * @param user User name
	 * @param name User name
	 * @param u User definition
	 * @return Updated user name
	 * @throws BindException on validation errors
	 */
	public String updateUser(String user, String name, User u) throws BindException;
	
	/**
	 * Remove a user
	 * @param user User name
	 * @param name User name
	 * @return true on success
	 */
	public boolean removeUser(String user, String name);
	
	/**
	 * List extension objects
	 * @param user User name
	 * @param schema Schema name
	 * @param extensionName Extension name
	 * @return Design names
	 */
	public SortedSet<String> listExtensionObjects(String user, String schema, String extensionName);
	
	/**
	 * Load an extension object
	 * @param <T> Object type
	 * @param user User name
	 * @param schema Schema name
	 * @param extensionName Extension name
	 * @param name Object name
	 * @param persister Persister impl.
	 * @return Loaded object
	 * @throws IOException on error
	 */
	public <T> T loadExtensionObject(String user, String schema, String extensionName, String name, ObjectPersister<T> persister) throws IOException;
	
	/**
	 * Save an extension object
	 * @param <T> Object type
	 * @param user User name
	 * @param owner User name
	 * @param schema Schema name
	 * @param extensionName Extension name
	 * @param name Object name
	 * @param object Object to save
	 * @param persister Persister impl.
	 * @return Actual object name that was saved
	 * @throws IOException on error
	 */
	public <T> String saveExtensionObject(String user, String owner, String schema, String extensionName, String name, T object, ObjectPersister<T> persister) throws IOException;
	
	/**
	 * Remove a user design
	 * @param user User name
	 * @param owner User name
	 * @param schema Schema name
	 * @param extensionName Extension name
	 * @param name Object name
	 * @throws IOException on error
	 */
	public void removeExtensionObject(String user, String owner, String schema, String extensionName, String name) throws IOException;
	
	/**
	 * Get a user's modification history
	 * @param name User name
	 * @param limit Limit number of returned entries
	 * @return History
	 */
	public List<HistoryEntry> getHistory(String name, int limit);
	}
