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
package de.tweerlei.dbgrazer.query.service;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.springframework.validation.BindException;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryCheckResult;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.model.QueryType;

/**
 * Service for accessing queries
 * 
 * @author Robert Wruck
 */
public interface QueryService
	{
	/**
	 * Find a query by name
	 * @param link Link name
	 * @param name Name
	 * @return Query or null
	 */
	public Query findQueryByName(String link, String name);
	
	/**
	 * Find queries that accept a given set of parameters
	 * @param link Link name
	 * @param params Parameter names
	 * @param views Whether to return views or subqueries
	 * @return Queries
	 */
	public List<Query> findQueriesByParameters(String link, List<String> params, boolean views);
	
	/**
	 * Get a QueryGroup for all queries
	 * @param link Link name
	 * @param listviews Separate listviews and views
	 * @param valuequeries Treat referenced value queries as subqueries
	 * @return QueryGroup
	 */
	public QueryGroup groupAllQueries(String link, boolean listviews, boolean valuequeries);
	
	/**
	 * Group a set of queries
	 * @param link Link name
	 * @param names Query names
	 * @param listviews Separate listviews and views
	 * @param valuequeries Treat referenced value queries as subqueries
	 * @return QueryGroup
	 */
	public QueryGroup groupQueries(String link, Set<String> names, boolean listviews, boolean valuequeries);
	
	/**
	 * Find all known query types that are supported for a given link type,
	 * NOT including view types
	 * @param linkType link type
	 * @return Query types
	 */
	public Set<QueryType> findSimpleQueryTypes(LinkType linkType);
	
	/**
	 * Find all known script query types that are supported for a given link type,
	 * @param linkType link type
	 * @return Query types
	 */
	public Set<QueryType> findScriptQueryTypes(LinkType linkType);
	
	/**
	 * Find all known view query types
	 * @return Query types
	 */
	public Set<QueryType> findViewQueryTypes();
	
	/**
	 * Find all known query types that are supported for a given link type,
	 * including view types
	 * @param linkType link type
	 * @return Query types
	 */
	public Set<QueryType> findAllQueryTypes(LinkType linkType);
	
	/**
	 * Find a query type
	 * @param name Type name
	 * @return Query type or null
	 */
	public QueryType findQueryType(String name);
	
	/**
	 * Find all query group names used in all queries
	 * @param link Link name
	 * @return Group names
	 */
	public SortedSet<String> findAllGroupNames(String link);
	
	/**
	 * Find the parameter names for all queries that take one parameter
	 * @param link Link name
	 * @return Parameter names
	 */
	public SortedSet<String> findSingleParameterNames(String link);
	
	/**
	 * Find all parameter names used in all queries
	 * @param link Link name
	 * @return Parameter names
	 */
	public SortedSet<String> findAllParameterNames(String link);
	
	/**
	 * Find queries that accept the given parameter
	 * @param link Link name
	 * @param name Parameter
	 * @return QueryGroup
	 */
	public QueryGroup groupQueriesByParameter(String link, String name);
	
	/**
	 * Get a QueryGroup for matching queries
	 * @param link Link name
	 * @param term Search term
	 * @param stmt Use query statement for matching in addition to the name
	 * @return QueryGroup
	 */
	public QueryGroup groupMatchingQueries(String link, String term, boolean stmt);
	
	/**
	 * Find queries that reference a given query as subquery or parameter value query
	 * @param link Link name
	 * @param name Query name
	 * @return Queries
	 */
	public List<Query> findReferencingQueries(String link, String name);
	
	/**
	 * Find queries that are (maybe indirectly) referenced by a given query
	 * @param link Link name
	 * @param name Query name
	 * @param deep Include indirect references
	 * @return Queries
	 */
	public List<Query> findReferencedQueries(String link, String name, boolean deep);
	
	/**
	 * Find queries that accept the same set of parameters
	 * @param link Link name
	 * @param name Query name
	 * @return QueryGroup
	 */
	public QueryGroup groupRelatedQueries(String link, String name);
	
	/**
	 * Perform a consistency check on all queries
	 * @param link Link name
	 * @return Map: Query name -> Error messages
	 */
	public SortedMap<String, List<QueryCheckResult>> checkQueries(String link);
	
	/**
	 * Get the allowed schema names for creating a query
	 * @param link Link name
	 * @return Schema names
	 */
	public Set<SchemaDef> getPossibleSchemaNames(String link);
	
	/**
	 * Create a query
	 * @param link Link name
	 * @param user User name
	 * @param query Query definition
	 * @return Created query name
	 * @throws BindException on validation errors
	 */
	public String createQuery(String link, String user, Query query) throws BindException;
	
	/**
	 * Update a query
	 * @param link Link name
	 * @param user User name
	 * @param name Query name
	 * @param query New query definition
	 * @return Updated query name
	 * @throws BindException on validation errors
	 */
	public String updateQuery(String link, String user, String name, Query query) throws BindException;
	
	/**
	 * Rename a query group for all matching queries
	 * @param link Link name
	 * @param user User name
	 * @param name Group name
	 * @param newName New group name
	 * @return Updated group name
	 */
	public String renameGroup(String link, String user, String name, String newName);
	
	/**
	 * Rename a parameter for all matching queries
	 * @param link Link name
	 * @param user User name
	 * @param name Parameter name
	 * @param newName New parameter name
	 * @return Updated parameter name
	 */
	public String renameParameter(String link, String user, String name, String newName);
	
	/**
	 * Rename a schema
	 * @param user User name
	 * @param oldName Old schema name
	 * @param newName New schema name
	 * @throws BindException on validation errors
	 */
	public void renameSchema(String user, SchemaDef oldName, SchemaDef newName) throws BindException;
	
	/**
	 * Remove a query
	 * @param link Link name
	 * @param user User name
	 * @param name Query name
	 * @return true on success
	 */
	public boolean removeQuery(String link, String user, String name);
	
	/**
	 * Get a query's modification history
	 * @param link Link name
	 * @param name Query name
	 * @param limit Limit number of returned entries
	 * @return History
	 */
	public List<HistoryEntry> getHistory(String link, String name, int limit);
	
	/**
	 * Get a previous version of a query
	 * @param link Link name
	 * @param name Query name
	 * @param version Version
	 * @return Query or null
	 */
	public Query getQueryVersion(String link, String name, String version);
	
	/**
	 * Get query statistics
	 * @return Map: Schema name -> loaded queries
	 */
	public SortedMap<SchemaDef, Integer> getQueryStats();
	
	/**
	 * Reload all query definitions
	 */
	public void reloadQueries();
	}
