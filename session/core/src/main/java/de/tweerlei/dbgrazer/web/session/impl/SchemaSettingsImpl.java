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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import de.tweerlei.dbgrazer.web.model.CustomQuery;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.TableFilterEntry;
import de.tweerlei.dbgrazer.web.model.TableSet;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;

/**
 * Per-schema settings for the current user
 * 
 * @author Robert Wruck
 */
public class SchemaSettingsImpl implements SchemaSettings, Serializable
	{
	private String catalog;
	private String schema;
	private String queryGroup;
	private String search;
	private boolean expandOtherSchemas;
	private boolean sortColumns;
	private boolean previewMode;
	private boolean compactMode;
	private SortedSet<String> favorites;
	private final Map<String, String> parameterHistory;
	private final List<QueryHistoryEntry> queryHistory;
	private final List<String> customQueryHistory;
	private final Map<String, TableFilterEntry> tableFilters;
	private final Map<String, Map<String, String>> querySettings;
	private final TableSet tableNames;
	private final CustomQuery customQuery;
	
	/**
	 * Constructor
	 */
	public SchemaSettingsImpl()
		{
		this.parameterHistory = new HashMap<String, String>();
		this.queryHistory = new LinkedList<QueryHistoryEntry>();
		this.customQueryHistory = new LinkedList<String>();
		this.tableFilters = new HashMap<String, TableFilterEntry>();
		this.querySettings = new HashMap<String, Map<String, String>>();
		this.tableNames = new TableSet();
		this.customQuery = new CustomQuery();
		}
	
	@Override
	public String getCatalog()
		{
		return catalog;
		}
	
	@Override
	public void setCatalog(String catalog)
		{
		this.catalog = catalog;
		}
	
	@Override
	public String getSchema()
		{
		return schema;
		}
	
	@Override
	public void setSchema(String schema)
		{
		this.schema = schema;
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
	public boolean isDesignerPreviewMode()
		{
		return previewMode;
		}

	@Override
	public void setDesignerPreviewMode(boolean b)
		{
		this.previewMode = b;
		}

	@Override
	public boolean isDesignerCompactMode()
		{
		return compactMode;
		}

	@Override
	public void setDesignerCompactMode(boolean b)
		{
		this.compactMode = b;
		}

	@Override
	public boolean isExpandOtherSchemas()
		{
		return expandOtherSchemas;
		}

	@Override
	public void setExpandOtherSchemas(boolean b)
		{
		this.expandOtherSchemas = b;
		}
	
	@Override
	public boolean isSortColumns()
		{
		return sortColumns;
		}

	@Override
	public void setSortColumns(boolean b)
		{
		this.sortColumns = b;
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
	public Map<String, TableFilterEntry> getTableFilters()
		{
		return tableFilters;
		}

	@Override
	public Map<String, Map<String, String>> getQuerySettings()
		{
		return querySettings;
		}
	
	@Override
	public SortedSet<String> getFavorites()
		{
		return favorites;
		}

	@Override
	public void setFavorites(SortedSet<String> favorites)
		{
		this.favorites = favorites;
		}
	
	@Override
	public TableSet getDesign()
		{
		return (tableNames);
		}
	}
