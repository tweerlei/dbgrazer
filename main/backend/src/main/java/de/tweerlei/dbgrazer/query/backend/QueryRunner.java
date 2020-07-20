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

import java.util.List;
import java.util.TimeZone;

import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowInterpreter;
import de.tweerlei.dbgrazer.query.model.StatementProducer;

/**
 * Backend for executing queries
 * 
 * @author Robert Wruck
 */
public interface QueryRunner extends Named
	{
	/**
	 * Check whether this QueryRunner supports a given QueryType
	 * @param t QueryType
	 * @return true if supported
	 */
	public boolean supports(QueryType t);
	
	/**
	 * Perform a query
	 * @param link Link name
	 * @param query Query
	 * @param subQueryIndex Base subquery index
	 * @param params Parameters
	 * @param timeZone TimeZone to use for temporal results
	 * @param limit Limit the number of rows to fetch per result RowSet
	 * @param monitor CancelableProgressMonitor (may be null)
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException;
	
	/**
	 * Perform a query, passing results to a RowHandler
	 * @param link Link name
	 * @param query Query
	 * @param params Parameters
	 * @param timeZone TimeZone to use for temporal results
	 * @param limit Limit the number of rows to fetch per result RowSet
	 * @param handler RowHandler
	 * @return Processed row count
	 * @throws PerformQueryException on error
	 */
	public int performStreamedQuery(String link, Query query, List<Object> params, TimeZone timeZone, int limit, RowHandler handler) throws PerformQueryException;
	
	/**
	 * Perform DML queries in a single transaction
	 * @param link Link name
	 * @param query Query (statement is ignored in favor of queries)
	 * @param statements Queries
	 * @param timeZone TimeZone to use for temporal results
	 * @param commitSize Perform a COMMIT after this number of rows
	 * @param monitor DMLProgressMonitor
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result performQueries(String link, Query query, StatementProducer statements, TimeZone timeZone, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException;
	
	/**
	 * Perform DML queries in a single transaction
	 * @param link Link name
	 * @param query Query statement
	 * @param timeZone TimeZone to use for temporal results
	 * @param interpreter RowInterpreter
	 * @param commitSize Perform a COMMIT after this number of rows
	 * @param monitor DMLProgressMonitor
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result transferRows(String link, Query query, TimeZone timeZone, RowInterpreter interpreter, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException;
	}
