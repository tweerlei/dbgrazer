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
package de.tweerlei.dbgrazer.web.session.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.web.model.CustomQuery;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.UserObject;
import de.tweerlei.dbgrazer.web.model.UserObjectKey;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;

/**
 * Per-schema settings for the current user
 * 
 * @author Robert Wruck
 */
public class SchemaSettingsImpl implements SchemaSettings, Serializable
	{
	private String queryGroup;
	private String search;
	private Map<UserObjectKey<?>, UserObject> userObjects;
	private final Map<String, String> parameterHistory;
	private final List<QueryHistoryEntry> queryHistory;
	private final List<String> customQueryHistory;
	private final Map<String, Map<String, String>> querySettings;
	private final CustomQuery customQuery;
	
	/**
	 * Constructor
	 */
	public SchemaSettingsImpl()
		{
		this.userObjects = new HashMap<UserObjectKey<?>, UserObject>();
		this.parameterHistory = new HashMap<String, String>();
		this.queryHistory = new LinkedList<QueryHistoryEntry>();
		this.customQueryHistory = new LinkedList<String>();
		this.querySettings = new HashMap<String, Map<String, String>>();
		this.customQuery = new CustomQuery();
		}
	
	@Override
	public Map<String, String> getParameterHistory()
		{
		return (parameterHistory);
		}
	
	@Override
	public String getQueryGroup()
		{
		return queryGroup;
		}
	
	@Override
	public void setQueryGroup(String queryGroup)
		{
		this.queryGroup = queryGroup;
		}
	
	@Override
	public String getSearch()
		{
		return search;
		}
	
	@Override
	public void setSearch(String search)
		{
		this.search = search;
		}
	
	@Override
	public CustomQuery getCustomQuery()
		{
		return (customQuery);
		}
	
	@Override
	public List<QueryHistoryEntry> getQueryHistory()
		{
		return queryHistory;
		}

	@Override
	public List<String> getCustomQueryHistory()
		{
		return customQueryHistory;
		}

	@Override
	public Map<String, Map<String, String>> getQuerySettings()
		{
		return querySettings;
		}
	
	@Override
	public <T extends UserObject> T getUserObject(UserObjectKey<T> key)
		{
		@SuppressWarnings("unchecked")
		final T ret = (T) userObjects.get(key);
		return ret;
		}

	@Override
	public <T extends UserObject> void setUserObject(UserObjectKey<T> key, T value)
		{
		userObjects.put(key, value);
		}
	
	@Override
	public void clearUserObjects()
		{
		for (Iterator<UserObjectKey<?>> it = userObjects.keySet().iterator(); it.hasNext(); )
			{
			if (!it.next().isPersistent())
				it.remove();
			}
		}
	}
