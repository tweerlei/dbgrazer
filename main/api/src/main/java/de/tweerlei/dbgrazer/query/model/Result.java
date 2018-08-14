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

/**
 * A query result that may consist of one or more named sets of rows
 * 
 * @author Robert Wruck
 */
public interface Result extends Serializable, Cloneable
	{
	/**
	 * Get the query that produced the Result
	 * @return Query
	 */
	public Query getQuery();
	
	/**
	 * Get the fixed parameter values
	 * @return Parameter values
	 */
	public List<String> getParameterValues();
	
	/**
	 * Get the row sets
	 * @return Map: Title -> RowSet
	 */
	public Map<String, RowSet> getRowSets();
	
	/**
	 * Get the first RowSet
	 * @return RowSet
	 */
	public RowSet getFirstRowSet();
	
	/**
	 * Accept a ResultVisitor
	 * @param v ResultVisitor
	 */
	public void accept(ResultVisitor v);
	
	/**
	 * Clone this Result
	 * @return New result
	 */
	public Result clone();
	}
