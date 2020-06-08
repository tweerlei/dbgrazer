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
package de.tweerlei.dbgrazer.query.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedSet;
import de.tweerlei.dbgrazer.query.backend.QueryRunner;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowInterpreter;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowTransferer;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.QueryRunnerService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class QueryRunnerServiceImpl implements QueryRunnerService
	{
	private final Logger logger;
	private final Set<QueryRunner> runners;
	
	/**
	 * Constructor
	 * @param runners Available QueryRunners
	 */
	@Autowired(required = false)
	public QueryRunnerServiceImpl(List<QueryRunner> runners)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.runners = Collections.unmodifiableSet(new NamedSet<QueryRunner>(runners));
		
		this.logger.log(Level.INFO, "Query runners: " + this.runners);
		}
	
	/**
	 * Constructor
	 */
	public QueryRunnerServiceImpl()
		{
		this(Collections.<QueryRunner>emptyList());
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res;
		
		final QueryRunner r = findRunner(query.getType());
		if (r == null)
			res = new ResultImpl(query);
		else
			{
			res = r.performQuery(link, query, subQueryIndex, params, timeZone, limit, monitor);
			}
		
		prepareResult(res);
		
		return (res);
		}
	
	@Override
	public int performStreamedQuery(String link, Query query, List<Object> params, TimeZone timeZone, int limit, RowHandler handler) throws PerformQueryException
		{
		try	{
			final QueryRunner r = findRunner(query.getType());
			if (r == null)
				return (0);
			else
				{
				return (r.performStreamedQuery(link, query, params, timeZone, limit, handler));
				}
			}
		catch (PerformQueryException e)
			{
			handler.error(e.getCause());
			throw e;
			}
		finally
			{
			handler.endRows();
			}
		}
	
	@Override
	public Result performQueries(String link, StatementProducer statements, TimeZone timeZone, QueryType type, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final Result res;
		
		final Query q = new QueryImpl(type.getName(), null, null, null, type, null, null, null);
		
		final QueryRunner r = findRunner(type);
		if (r == null)
			res = new ResultImpl(q);
		else
			{
			res = r.performQueries(link, q, statements, timeZone, commitSize, monitor);
			}
		
		prepareResult(res);
		
		return (res);
		}
	
	@Override
	public Result transferRows(String link, String query, TimeZone timeZone, RowTransferer transferer, QueryType type, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final Result res;
		
		final Query q = new QueryImpl(type.getName(), null, null, query, type, null, null, null);
		
		final QueryRunner r = findRunner(type);
		if (r == null)
			res = new ResultImpl(q);
		else
			{
			res = r.transferRows(link, q, timeZone, transferer, commitSize, monitor);
			}
		
		prepareResult(res);
		
		return (res);
		}
	
	@Override
	public Result transferRows(String link, String query, TimeZone timeZone, RowInterpreter interpreter, QueryType type, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final Result res;
		
		final Query q = new QueryImpl(type.getName(), null, null, query, type, null, null, null);
		
		final QueryRunner r = findRunner(type);
		if (r == null)
			res = new ResultImpl(q);
		else
			{
			res = r.transferRows(link, q, timeZone, interpreter, commitSize, monitor);
			}
		
		prepareResult(res);
		
		return (res);
		}
	
	private QueryRunner findRunner(QueryType t)
		{
		for (QueryRunner r : runners)
			{
			if (r.supports(t))
				return (r);
			}
		
		return (null);
		}
	
	private void prepareResult(Result res)
		{
		if (res.getRowSets().isEmpty())
			res.getRowSets().put(res.getQuery().getName(), new RowSetImpl(res.getQuery(), 0, null));
		else
			{
			for (RowSet rs : res.getRowSets().values())
				{
				final ResultVisitor v = rs.getQuery().getType().getPostProcessor();
				if (v != null)
					rs.accept(v, 0);
				}
			}
		}
	}
