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
import java.util.SortedSet;

import de.tweerlei.dbgrazer.web.model.CustomQuery;
import de.tweerlei.dbgrazer.web.model.QueryHistoryEntry;
import de.tweerlei.dbgrazer.web.model.TableFilterEntry;
import de.tweerlei.dbgrazer.web.model.TableSet;

/**
 * Per-schema settings for the current user
 * 
 * @author Robert Wruck
 */
public interface SchemaSettings
	{
	/**
	 * Get the catalog
	 * @return catalog
	 */
	public String getCatalog();
	
	/**
	 * Set the catalog
	 * @param catalog The catalog
	 */
	public void setCatalog(String catalog);
	
	/**
	 * Get the schema
	 * @return schema
	 */
	public String getSchema();
	
	/**
	 * Set the schema
	 * @param schema The schema
	 */
	public void setSchema(String schema);
	
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
	 * Check for expand other schemas
	 * @return expand other schemas
	 */
	public boolean isExpandOtherSchemas();
	
	/**
	 * Set expand other schemas
	 * @param b expand other schemas
	 */
	public void setExpandOtherSchemas(boolean b);
	
	/**
	 * Check for sort columns
	 * @return sort columns
	 */
	public boolean isSortColumns();
	
	/**
	 * Set sort columns
	 * @param b sort columns
	 */
	public void setSortColumns(boolean b);
	
	/**
	 * Check for designer preview mode
	 * @return preview mode
	 */
	public boolean isDesignerPreviewMode();
	
	/**
	 * Set designer preview mode
	 * @param b preview mode
	 */
	public void setDesignerPreviewMode(boolean b);
	
	/**
	 * Check for designer compact mode
	 * @return compact mode
	 */
	public boolean isDesignerCompactMode();
	
	/**
	 * Set designer compact mode
	 * @param b compact mode
	 */
	public void setDesignerCompactMode(boolean b);
	
	/**
	 * Get the queryHistory
	 * @return the queryHistory
	 */
	public List<QueryHistoryEntry> getQueryHistory();

	/**
	 * Get the tableFilters
	 * @return the tableFilters
	 */
	public Map<String, TableFilterEntry> getTableFilters();

	/**
	 * Get the querySettings
	 * @return the querySettings
	 */
	public Map<String, Map<String, String>> getQuerySettings();
	
	/**
	 * Get the favorite queries
	 * @return Query names
	 */
	public SortedSet<String> getFavorites();
	
	/**
	 * Set the favorite queries
	 * @param favorites Query names
	 */
	public void setFavorites(SortedSet<String> favorites);

	/**
	 * Get the schema table names
	 * @return Table names
	 */
	public TableSet getDesign();
	
	/**
	 * Get recently used parameter values
	 * @return Map: name -> value
	 */
	public Map<String, String> getParameterHistory();
	}
