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

import java.util.Collection;
import java.util.Map;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;

/**
 * Service for constructing Result objects
 * 
 * @author Robert Wruck
 */
public interface ResultBuilderService
	{
	/**
	 * Create an empty RowSet
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param time Query time
	 * @return RowSet
	 */
	public RowSetImpl createEmptyRowSet(Query query, int subQueryIndex, long time);
	
	/**
	 * Create a RowSet that contains a single value
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param columnName Column name
	 * @param value The value
	 * @param time Query time
	 * @return RowSet
	 */
	public RowSetImpl createSingletonRowSet(Query query, int subQueryIndex, String columnName, Object value, long time);
	
	/**
	 * Create a Result that contains a single RowSet with a single value
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param columnName Column name
	 * @param value The value
	 * @param time Query time
	 * @return Result
	 */
	public ResultImpl createSingletonResult(Query query, int subQueryIndex, String columnName, Object value, long time);
	
	/**
	 * Create a RowSet that contains a single column populated from a collection
	 * @param <T> Value type
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param columnName Column name
	 * @param values The values
	 * @param time Query time
	 * @return RowSet
	 */
	public <T> RowSetImpl createRowSet(Query query, int subQueryIndex, String columnName, Collection<T> values, long time);
	
	/**
	 * Create a RowSet that contains two columns populated from a map
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param keyColumnName Key column name
	 * @param valueColumnName Value column name
	 * @param values The values
	 * @param time Query time
	 * @return RowSet
	 */
	public <K, V> RowSetImpl createMapRowSet(Query query, int subQueryIndex, String keyColumnName, String valueColumnName, Map<K, V> values, long time);
	
	/**
	 * Create a RowSet that contains a single row with the map keys as column names and column types derived from the map values
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param values The values
	 * @param time Query time
	 * @return RowSet
	 */
	public RowSetImpl createMapRowSet(Query query, int subQueryIndex, Map<String, Object> values, long time);
	
	/**
	 * Collect all statements from a StatementProducer and return a single script
	 * @param producer StatementProducer
	 * @param header Header comment
	 * @param separator Statement separator
	 * @return Script
	 */
	public String writeScript(StatementProducer producer, String header, String separator);
	}
