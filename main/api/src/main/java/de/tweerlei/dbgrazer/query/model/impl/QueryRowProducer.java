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
package de.tweerlei.dbgrazer.query.model.impl;

import java.util.List;
import java.util.TimeZone;

import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.service.QueryRunnerService;

/**
 * Perform a query and pass ResultRows to a RowHandler
 * 
 * @author Robert Wruck
 */
public class QueryRowProducer implements RowProducer
	{
	private final QueryRunnerService runner;
	private final String link;
	private final Query query;
	private final List<Object> params;
	private final TimeZone timeZone;
	
	/**
	 * Constructor
	 * @param link Link name
	 * @param query Query
	 * @param params Query parameters
	 * @param timeZone TimeZone to use for temporal results
	 * @param runner QueryRunnerService
	 */
	public QueryRowProducer(String link, Query query, List<Object> params, TimeZone timeZone,
			QueryRunnerService runner)
		{
		this.link = link;
		this.query = query;
		this.params = params;
		this.timeZone = timeZone;
		this.runner = runner;
		}
	
	@Override
	public void produceRows(RowHandler h)
		{
		try	{
			runner.performStreamedQuery(link, query, params, timeZone, Integer.MAX_VALUE, h);
			}
		catch (PerformQueryException e)
			{
			throw e.getCause();
			}
		}
	}
