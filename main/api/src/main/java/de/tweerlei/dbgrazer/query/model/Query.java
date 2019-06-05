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
package de.tweerlei.dbgrazer.query.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.link.model.SchemaDef;

/**
 * DB query abstraction
 * 
 * @author Robert Wruck
 */
public interface Query extends Named, Serializable
	{
	/**
	 * Get the query scope
	 * @return Query scope
	 */
	public SchemaDef getSourceSchema();
	
	/**
	 * Get the group name
	 * @return Group name
	 */
	public String getGroupName();
	
	/**
	 * Get the statement text
	 * @return Statement text
	 */
	public String getStatement();
	
	/**
	 * Get the parameters
	 * @return Parameters
	 */
	public List<ParameterDef> getParameters();
	
	/**
	 * Get possible target queries by column
	 * @return Map: Column index -> Query name
	 */
	public Map<Integer, TargetDef> getTargetQueries();
	
	/**
	 * Get whether this query returns a single row (or none)
	 * @return QueryType
	 */
	public QueryType getType();
	
	/**
	 * Get subqueries, if any
	 * @return Subqueries
	 */
	public List<SubQueryDef> getSubQueries();
	
	/**
	 * Get additional attributes
	 * @return Map: Key -> Value
	 */
	public Map<String, String> getAttributes();
	}
