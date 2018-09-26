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
package de.tweerlei.dbgrazer.extension.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import de.tweerlei.common.util.ProgressMonitor;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;

/**
 * Service for accessing database metadata
 * 
 * @author Robert Wruck
 */
public interface MetadataService
	{
	/** Which columns to return in a TableDescription */
	public static enum ColumnMode
		{
		/** Return all columns */
		ALL,
		/** Return only PK/FK columns */
		PK_FK,
		/** Return all columns, sorted by PK, FK and nullability */
		SORTED
		}
	
	/**
	 * Get active link statistics
	 * @return Map: Link name -> Cache size
	 */
	public Map<String, Integer> getLinkStats();
	
	/**
	 * Flush any cached metadata for the given link
	 * @param link Link name
	 */
	public void flushCache(String link);
	
	/**
	 * Get database info
	 * @param link Link name
	 * @return Properties
	 */
	public Map<String, String> getDBInfo(String link);
	
	/**
	 * Get all catalogs
	 * @param link Link name
	 * @return Catalogs
	 */
	public SortedSet<String> getCatalogs(String link);
	
	/**
	 * Get all schemas
	 * @param link Link name
	 * @return Schemas
	 */
	public SortedSet<String> getSchemas(String link);
	
	/**
	 * Get tables
	 * @param link Link name
	 * @param catalog Catalog
	 * @param schema Schema
	 * @return Map: Table name -> object type
	 */
	public SortedMap<QualifiedName, String> getTables(String link, String catalog, String schema);
	
	/**
	 * Get tables
	 * @param link Link name
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param type Return only tables of this type
	 * @return Map: Table name -> object type
	 */
	public SortedMap<QualifiedName, String> getTables(String link, String catalog, String schema, String type);
	
	/**
	 * Get tables
	 * @param link Link name
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param type Table type
	 * @param filter Filter expression
	 * @return Map: Table name -> object type
	 */
	public SortedMap<QualifiedName, String> getTables(String link, String catalog, String schema, String type, String filter);
	
	/**
	 * Get table info
	 * @param link Link name
	 * @param table Table
	 * @param mode ColumnMode
	 * @return Table description
	 */
	public TableDescription getTableInfo(String link, QualifiedName table, ColumnMode mode);
	
	/**
	 * Get table info
	 * @param link Link name
	 * @param tables Tables
	 * @param missing Set to receive all tables that could not be found (may be null)
	 * @param mode ColumnMode
	 * @param p ProgressMonitor
	 * @return Table description
	 */
	public Set<TableDescription> getTableInfos(String link, Set<QualifiedName> tables, Set<QualifiedName> missing, ColumnMode mode, ProgressMonitor p);
	
	/**
	 * Get recursive table info
	 * @param link Link name
	 * @param table Table
	 * @param depth Recursion depth, 0 means starting table only
	 * @param all Include tables from other schemas
	 * @param mode ColumnMode
	 * @param p ProgressMonitor
	 * @return Table descriptions
	 */
	public Set<TableDescription> getTableInfoRecursive(String link, QualifiedName table, int depth, boolean all, ColumnMode mode, ProgressMonitor p);
	
	/**
	 * Analyze an SQL statement
	 * @param link Link name
	 * @param stmt SQL statement
	 * @param params Statement parameter values
	 * @return Execution plan
	 */
	public SQLExecutionPlan analyzeStatement(String link, String stmt, List<Object> params);
	}
