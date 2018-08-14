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
package de.tweerlei.dbgrazer.security.backend;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.file.ObjectPersister;
import de.tweerlei.dbgrazer.security.model.User;

/**
 * Load user data
 * 
 * @author Robert Wruck
 */
public interface UserLoader
	{
	/**
	 * Load user data
	 * @param username Username
	 * @return User object
	 */
	public User loadUser(String username);
	
	/**
	 * List available user names
	 * @return User names
	 */
	public SortedSet<String> listUsers();
	
	/**
	 * Create a user definition
	 * @param user User name
	 * @param name User name
	 * @param u User definition
	 * @throws IOException on error
	 */
	public void createUser(String user, String name, User u) throws IOException;
	
	/**
	 * Update a user definition
	 * @param user User name
	 * @param name User name
	 * @param newName New user name
	 * @param u User definition
	 * @throws IOException on error
	 */
	public void updateUser(String user, String name, String newName, User u) throws IOException;
	
	/**
	 * Remove a user definition
	 * @param user User name
	 * @param name User name
	 * @throws IOException on error
	 */
	public void removeUser(String user, String name) throws IOException;
	
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
	 * @throws IOException on error
	 */
	public <T> void saveExtensionObject(String user, String owner, String schema, String extensionName, String name, T object, ObjectPersister<T> persister) throws IOException;
	
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
	 * Get the modification history for a user
	 * @param name User name
	 * @param limit List at most this number of newest entries
	 * @return History
	 * @throws IOException on error
	 */
	public List<HistoryEntry> getHistory(String name, int limit) throws IOException;
	}
