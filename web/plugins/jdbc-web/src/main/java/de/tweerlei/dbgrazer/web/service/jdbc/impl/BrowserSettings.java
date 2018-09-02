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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.util.HashMap;
import java.util.Map;

import de.tweerlei.dbgrazer.web.model.UserObject;

/**
 * Browser settings
 * 
 * @author Robert Wruck
 */
public class BrowserSettings extends UserObject
	{
	private String catalog;
	private String schema;
	private boolean expandOtherSchemas;
	private boolean sortColumns;
	private boolean previewMode;
	private boolean compactMode;
	private final Map<String, TableFilterEntry> tableFilters;
	
	/**
	 * Constructor
	 */
	public BrowserSettings()
		{
		this.tableFilters = new HashMap<String, TableFilterEntry>();
		}
	
	/**
	 * Get the catalog
	 * @return catalog
	 */
	public String getCatalog()
		{
		return catalog;
		}
	
	/**
	 * Set the catalog
	 * @param catalog The catalog
	 */
	public void setCatalog(String catalog)
		{
		this.catalog = catalog;
		}
	
	/**
	 * Get the schema
	 * @return schema
	 */
	public String getSchema()
		{
		return schema;
		}
	
	/**
	 * Set the schema
	 * @param schema The schema
	 */
	public void setSchema(String schema)
		{
		this.schema = schema;
		}
	
	/**
	 * Check for expand other schemas
	 * @return expand other schemas
	 */
	public boolean isExpandOtherSchemas()
		{
		return expandOtherSchemas;
		}
	
	/**
	 * Set expand other schemas
	 * @param b expand other schemas
	 */
	public void setExpandOtherSchemas(boolean b)
		{
		this.expandOtherSchemas = b;
		}
	
	/**
	 * Check for sort columns
	 * @return sort columns
	 */
	public boolean isSortColumns()
		{
		return sortColumns;
		}
	
	/**
	 * Set sort columns
	 * @param b sort columns
	 */
	public void setSortColumns(boolean b)
		{
		this.sortColumns = b;
		}
	
	/**
	 * Check for designer preview mode
	 * @return preview mode
	 */
	public boolean isDesignerPreviewMode()
		{
		return previewMode;
		}
	
	/**
	 * Set designer preview mode
	 * @param b preview mode
	 */
	public void setDesignerPreviewMode(boolean b)
		{
		this.previewMode = b;
		}
	
	/**
	 * Check for designer compact mode
	 * @return compact mode
	 */
	public boolean isDesignerCompactMode()
		{
		return compactMode;
		}
	
	/**
	 * Set designer compact mode
	 * @param b compact mode
	 */
	public void setDesignerCompactMode(boolean b)
		{
		this.compactMode = b;
		}

	/**
	 * Get the tableFilters
	 * @return the tableFilters
	 */
	public Map<String, TableFilterEntry> getTableFilters()
		{
		return tableFilters;
		}
	}
