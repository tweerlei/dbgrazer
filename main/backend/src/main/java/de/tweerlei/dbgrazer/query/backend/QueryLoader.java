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
package de.tweerlei.dbgrazer.query.backend;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.Query;

/**
 * Load query definitions
 * 
 * @author Robert Wruck
 */
public interface QueryLoader
	{
	/**
	 * Load query definitions
	 * @param schema Schema name
	 * @return Loaded queries
	 */
	public SortedMap<String, Query> loadQueries(SchemaDef schema);
	
	/**
	 * Create a query definition
	 * @param schema Schema name
	 * @param user User name
	 * @param name Query name
	 * @param query Query definition
	 * @throws IOException on error
	 */
	public void createQuery(SchemaDef schema, String user, String name, Query query) throws IOException;
	
	/**
	 * Update a query definition
	 * @param schema Schema name
	 * @param user User name
	 * @param name Query name
	 * @param newName New query name
	 * @param query Query definition
	 * @throws IOException on error
	 */
	public void updateQuery(SchemaDef schema, String user, String name, String newName, Query query) throws IOException;
	
	/**
	 * Remove a query definition
	 * @param schema Schema name
	 * @param user User name
	 * @param name Query name
	 * @throws IOException on error
	 */
	public void removeQuery(SchemaDef schema, String user, String name) throws IOException;
	
	/**
	 * Get the modification history for a query
	 * @param schema Schema name
	 * @param name Query name
	 * @param limit List at most this number of newest entries
	 * @return History
	 * @throws IOException on error
	 */
	public List<HistoryEntry> getHistory(SchemaDef schema, String name, int limit) throws IOException;
	
	/**
	 * Get a previous version of a query
	 * @param schema Schema name
	 * @param name Query name
	 * @param version Version
	 * @return Query or null
	 * @throws IOException on error
	 */
	public Query getQueryVersion(SchemaDef schema, String name, String version) throws IOException;
	
	/**
	 * Get all existing subSchemas for a given schema
	 * @param schema Schema name
	 * @return SubSchemas
	 */
	public List<SchemaDef> getSubSchemas(SchemaDef schema);
	
	/**
	 * Rename a schema
	 * @param user User name
	 * @param oldName Old schema name
	 * @param newName New schema name
	 * @throws IOException on error
	 */
	public void renameSchema(String user, SchemaDef oldName, SchemaDef newName) throws IOException;
	}
