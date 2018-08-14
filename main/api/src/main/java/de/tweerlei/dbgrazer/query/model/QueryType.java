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

import java.util.Map;

import org.springframework.validation.Errors;

import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.link.model.LinkType;

/**
 * Query type
 * 
 * @author Robert Wruck
 */
public interface QueryType extends Named
	{
	/**
	 * Get the LinkType this QueryType is applicable to or null if applicable to all types
	 * @return LinkType
	 */
	public LinkType getLinkType();
	
	/**
	 * Get the ResultType of this QueryType
	 * @return ResultType
	 */
	public ResultType getResultType();
	
	/**
	 * Get the ResultMapMode of this QueryType
	 * @return ResultMapMode
	 */
	public ResultMapMode getMapMode();
	
	/**
	 * Get the supported query attributes
	 * @return Map: Attribute name -> value type
	 */
	public Map<String, Class<?>> getSupportedAttributes();
	
	/**
	 * Get a SubQueryResolver to resolve subqueries
	 * @return SubQueryResolver or null
	 */
	public SubQueryResolver getSubQueryResolver();
	
	/**
	 * Get a ResultVisitor to be applied to results
	 * @return ResultVisitor or null
	 */
	public ResultVisitor getPostProcessor();
	
	/**
	 * Check whether this QueryType supports performing multiple "statements"
	 * @return true for script query types
	 */
	public boolean isScript();
	
	/**
	 * Check whether this QueryType manipulates data (i.e. requires write access)
	 * @return true for manipulating query types
	 */
	public boolean isManipulation();
	
	/**
	 * Get the orientation of results
	 * @return ResultOrientation
	 */
	public ResultOrientation getOrientation();
	
	/**
	 * Check whether this QueryType is a view that uses a navigation pane
	 * @return true for explorer views
	 */
	public boolean isExplorer();
	
	/**
	 * Check whether this QueryType has a synthetic prefix column
	 * @return true if prefix column is needed
	 */
	public boolean isColumnPrefixed();
	
	/**
	 * Check whether all hierarchy levels have the same set of columns
	 * @return true for same columns
	 */
	public boolean isSingleColumnSet();
	
	/**
	 * Check whether this QueryType accumulates results from successive exeutions
	 * @return true if results are accumulated
	 */
	public boolean isAccumulatingResults();
	
	/**
	 * Validate a Query
	 * @param query Query
	 * @param errors Receives any validation errors
	 */
	public void validate(Query query, Errors errors);
	}
