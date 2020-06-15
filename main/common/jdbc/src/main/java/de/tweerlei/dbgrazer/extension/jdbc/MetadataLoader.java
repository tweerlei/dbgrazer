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
public interface MetadataLoader
	{
	/**
	 * Set the link name
	 * @param link Link name
	 */
	public void setLink(String link);
	
	/**
	 * Get the link name
	 * @return Link name
	 */
	public String getLink();
	
	/**
	 * Get database info
	 * @return Properties
	 */
	public Map<String, String> getDBInfo();
	
	/**
	 * Get all catalogs
	 * @return Catalogs
	 */
	public SortedSet<String> getCatalogs();
	
	/**
	 * Get all schemas
	 * @param catalog Catalog name
	 * @return Schemas
	 */
	public SortedSet<String> getSchemas(String catalog);
	
	/**
	 * Get tables
	 * @param catalog Catalog
	 * @param schema Schema
	 * @return Map: Table name -> object type
	 */
	public SortedMap<QualifiedName, String> getTables(String catalog, String schema);
	
	/**
	 * Get table info
	 * @param table Table
	 * @return Table description
	 */
	public TableDescription getTableInfo(QualifiedName table);
	
	/**
	 * Get table info
	 * @param tables Tables
	 * @param missing Set to receive all tables that could not be found (may be null)
	 * @param p ProgressMonitor
	 * @return Table description
	 */
	public Set<TableDescription> getTableInfos(Set<QualifiedName> tables, Set<QualifiedName> missing, ProgressMonitor p);
	
	/**
	 * Get recursive table info
	 * @param table Table
	 * @param depth Recursion depth, 0 means starting table only
	 * @param all Include tables from other schemas
	 * @param toplevel true for top level calls
	 * @param p ProgressMonitor
	 * @return Table descriptions
	 */
	public Set<TableDescription> getTableInfoRecursive(QualifiedName table, int depth, boolean all, boolean toplevel, ProgressMonitor p);
	
	/**
	 * Analyze an SQL statement
	 * @param stmt SQL statement
	 * @param params Statement parameter values
	 * @return Execution plan
	 */
	public SQLExecutionPlan analyzeStatement(String stmt, List<Object> params);
	}
