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
package de.tweerlei.dbgrazer.plugins.wiki.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.plugins.wiki.types.PlaintextQueryType;
import de.tweerlei.dbgrazer.plugins.wiki.types.WikiQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.backend.ParamReplacer;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.spring.service.TimeService;

/**
 * Run filesystem queries
 * 
 * @author Robert Wruck
 */
@Service
public class WikiQueryRunner extends BaseQueryRunner
	{
	private final TimeService timeService;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 */
	@Autowired
	public WikiQueryRunner(TimeService timeService)
		{
		super("Wiki");
		this.timeService = timeService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return ((t instanceof WikiQueryType) || (t instanceof PlaintextQueryType));
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>();
		columns.add(new ColumnDefImpl("Content", ColumnType.STRING, null, query.getTargetQueries().get(0), null, null));
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		
		final String stmt = new ParamReplacer(params).replaceAll(query.getStatement());
		
		final long start = timeService.getCurrentTime();
		
		rs.getRows().add(new DefaultResultRow(stmt));
		
		final long end = timeService.getCurrentTime();
		rs.setQueryTime(end - start);
		
		res.getRowSets().put(query.getName(), rs);
		
		return (res);
		}
	}
