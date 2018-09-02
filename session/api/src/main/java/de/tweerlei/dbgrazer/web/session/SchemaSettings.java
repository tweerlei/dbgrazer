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
package de.tweerlei.dbgrazer.web.session;

import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.web.model.CustomQuery;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.UserObject;
import de.tweerlei.dbgrazer.web.model.UserObjectKey;

/**
 * Per-schema settings for the current user
 * 
 * @author Robert Wruck
 */
public interface SchemaSettings
	{
	/**
	 * Get the queryGroup
	 * @return queryGroup
	 */
	public String getQueryGroup();
	
	/**
	 * Set the queryGroup
	 * @param queryGroup The queryGroup
	 */
	public void setQueryGroup(String queryGroup);
	
	/**
	 * Get the search
	 * @return search
	 */
	public String getSearch();
	
	/**
	 * Set the search
	 * @param search The search
	 */
	public void setSearch(String search);
	
	/**
	 * Get the customQuery
	 * @return customQuery
	 */
	public CustomQuery getCustomQuery();
	
	/**
	 * Get the customQuery history
	 * @return customQuery history
	 */
	public List<String> getCustomQueryHistory();
	
	/**
	 * Get the queryHistory
	 * @return the queryHistory
	 */
	public List<QueryHistoryEntry> getQueryHistory();
	
	/**
	 * Get the querySettings
	 * @return the querySettings
	 */
	public Map<String, Map<String, String>> getQuerySettings();
	
	/**
	 * Get a user object
	 * @param <T> User object type
	 * @param key User object key
	 * @return User object or null
	 */
	public <T extends UserObject> T getUserObject(UserObjectKey<T> key);
	
	/**
	 * Set a user object
	 * @param <T> User object type
	 * @param key User object key
	 * @param value User object
	 */
	public <T extends UserObject> void setUserObject(UserObjectKey<T> key, T value);
	
	/**
	 * Clear all user objects that are not persistent
	 */
	public void clearUserObjects();
	
	/**
	 * Get recently used parameter values
	 * @return Map: name -> value
	 */
	public Map<String, String> getParameterHistory();
	}
