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
package de.tweerlei.dbgrazer.web.service;

import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.RowTransferer;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.web.model.QueryParameters;

/**
 * Perform queries
 * 
 * @author Robert Wruck
 */
public interface QueryPerformerService
	{
	/**
	 * Perform a recursive query, returning a separate Result per direct subquery
	 * @param link Link name
	 * @param query Query
	 * @return A single result if the query is not recursive, otherwise a result per immediate subquery but at least a single empty Result
	 * @throws PerformQueryException on error
	 */
	public Map<String, Result> performRecursiveQuery(String link, QueryParameters query) throws PerformQueryException;
	
	/**
	 * Perform a non-recursive query, returning a single Result
	 * @param link Link name
	 * @param query Query
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result performQuery(String link, QueryParameters query) throws PerformQueryException;
	
	/**
	 * Create a RowProducer for a non-recursive query
	 * @param link Link name
	 * @param query Query
	 * @return Result
	 */
	public RowProducer createRowProducer(String link, QueryParameters query);
	
	/**
	 * Create a custom query
	 * @param type Query type name
	 * @param statement Statement text
	 * @param paramDefs Parameter definitions
	 * @param label Query name
	 * @return Query
	 */
	public Query createCustomQuery(String type, String statement, List<ParameterDef> paramDefs, String label);
	
	/**
	 * Perform a custom query
	 * @param link Link name
	 * @param type Query type name
	 * @param statement Statement text
	 * @param paramDefs Parameter definitions
	 * @param params Parameters
	 * @param label Query name
	 * @param export Whether to return ALL result rows
	 * @param monitor CancelableProgressMonitor (may be null)
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result performCustomQuery(String link, String type, String statement, List<ParameterDef> paramDefs, List<Object> params, String label, boolean export, CancelableProgressMonitor monitor) throws PerformQueryException;
	
	/**
	 * Perform a custom query, passing results to a RowHandler
	 * @param link Link name
	 * @param type Query type name
	 * @param statement Statement text
	 * @param label Query name
	 * @param handler RowHandler
	 * @return Processed row count
	 * @throws PerformQueryException on error
	 */
	public int performCustomQuery(String link, String type, String statement, String label, RowHandler handler) throws PerformQueryException;
	
	/**
	 * Perform a custom query, passing results to a RowHandler
	 * @param link Link name
	 * @param statements Statements
	 * @param type Query type name
	 * @param monitor DMLProgressMonitor
	 * @return Processed row count
	 * @throws PerformQueryException on error
	 */
	public Result performCustomQueries(String link, StatementProducer statements, String type, DMLProgressMonitor monitor) throws PerformQueryException;
	
	/**
	 * Perform modifications in a single transaction
	 * @param link Link name
	 * @param query Source query statement
	 * @param transferer RowTransferer
	 * @param type DML query type
	 * @param monitor DMLProgressMonitor
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result transferRows(String link, String query, RowTransferer transferer, String type, DMLProgressMonitor monitor) throws PerformQueryException;
	
	/**
	 * Perform modifications in a single transaction
	 * @param link Link name
	 * @param query Source query statement
	 * @param transferer RowTransferer
	 * @param handler Destination StatementHandler
	 * @param type DML query type
	 * @param monitor DMLProgressMonitor
	 * @param export Whether to return ALL result rows
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result transferRows(String link, String query, RowTransferer transferer, StatementHandler handler, String type, DMLProgressMonitor monitor, boolean export) throws PerformQueryException;
	
	/**
	 * Perform a custom chart query
	 * @param link Link name
	 * @param type Query type name
	 * @param statement Statement text
	 * @param label Query name
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result performCustomChartQuery(String link, String type, String statement, String label) throws PerformQueryException;
	}
