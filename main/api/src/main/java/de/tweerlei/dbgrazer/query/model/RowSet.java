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

import java.util.List;
import java.util.Map;

/**
 * A RowSet returned by a database query
 * 
 * @author Robert Wruck
 */
public interface RowSet extends RowContainer<ResultRow>
	{
	/**
	 * Get the query that produced the RowSet
	 * @return Query
	 */
	public Query getQuery();
	
	/**
	 * Get the fixed parameter values
	 * @return Parameter values
	 */
	public List<String> getParameterValues();
	
	/**
	 * Get the index of the subquery that produced this RowSet
	 * @return Index
	 */
	public int getSubQueryIndex();
	
	/**
	 * Get the time taken for the query
	 * @return Milliseconds
	 */
	public long getQueryTime();
	
	/**
	 * Get the number of rows affected by this query
	 * @return Affected row count
	 */
	public int getAffectedRows();
	
	/**
	 * Check whether more rows were available but the fetch limit was reached
	 * @return true whether more rows are available
	 */
	public boolean isMoreAvailable();
	
	/**
	 * Get the first row
	 * @return Values
	 */
	public ResultRow getFirstRow();
	
	/**
	 * Get the first column
	 * @return Values
	 */
	public List<Object> getFirstColumn();
	
	/**
	 * Get the first value of the first row
	 * @return Object
	 */
	public Object getFirstValue();
	
	/**
	 * Get additional attributes
	 * @return additional attributes
	 */
	public Map<String, Object> getAttributes();
	
	/**
	 * Clone this RowSet
	 * @return New RowSet
	 */
	@Override
	public RowSet clone();
	}
