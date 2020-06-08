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

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowInterpreter;
import de.tweerlei.dbgrazer.query.model.RowTransferer;
import de.tweerlei.dbgrazer.query.model.StatementProducer;

/**
 * Backend for executing queries
 * 
 * @author Robert Wruck
 */
public abstract class BaseQueryRunner extends NamedBase implements QueryRunner
	{
	/**
	 * Constructor
	 * @param name Name
	 */
	protected BaseQueryRunner(String name)
		{
		super(name);
		}
	
	@Override
	public int performStreamedQuery(String link, Query query, List<Object> params, TimeZone timeZone, int limit, RowHandler handler) throws PerformQueryException
		{
		throw new PerformQueryException("performQuery", new IllegalStateException("Operation not supported for " + getName()));
		}
	
	@Override
	public Result performQueries(String link, Query query, StatementProducer statements, TimeZone timeZone, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		throw new PerformQueryException("performQueries", new IllegalStateException("Operation not supported for " + getName()));
		}
	
	@Override
	public Result transferRows(String link, Query query, TimeZone timeZone, RowTransferer transferer, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		throw new PerformQueryException("transferRows", new IllegalStateException("Operation not supported for " + getName()));
		}
	
	@Override
	public Result transferRows(String link, Query query, TimeZone timeZone, RowInterpreter interpreter, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		throw new PerformQueryException("transferRows", new IllegalStateException("Operation not supported for " + getName()));
		}
	}
