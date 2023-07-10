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
package de.tweerlei.dbgrazer.plugins.mongodb.impl;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoClient;

import de.tweerlei.dbgrazer.extension.mongodb.MongoDBClientService;
import de.tweerlei.dbgrazer.plugins.mongodb.types.QueryTypeAttributes;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
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
public class MongoDBCommandRunner extends BaseQueryRunner
	{
	private final TimeService timeService;
	private final MongoDBClientService clientService;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param clientService MongoDBClientService
	 */
	@Autowired
	public MongoDBCommandRunner(TimeService timeService, MongoDBClientService clientService)
		{
		super("MONGODBCommand");
		this.timeService = timeService;
		this.clientService = clientService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return ((t.getLinkType() instanceof MongoDBLinkType) && t.isManipulation());
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final String database = query.getAttributes().get(QueryTypeAttributes.ATTR_DATABASE);
		if (database == null)
			throw new PerformQueryException(query.getName(), new RuntimeException("No database specified"));
		
		final MongoClient client = clientService.getMongoClient(link);
		if (client == null)
			throw new PerformQueryException(query.getName(), new RuntimeException("Unknown link " + link));
		
		final Result res = new ResultImpl(query);
		
		try	{
//			final String stmt = new ParamReplacer(params).replaceAll(query.getStatement());
			
			performMongoCommand(client, database, query.getStatement(), params, query, subQueryIndex, res);
			}
		catch (RuntimeException e)
			{
			throw new PerformQueryException(query.getName(), e);
			}
		
		return (res);
		}
	
	private void performMongoCommand(MongoClient client, String database, String statement, List<Object> params, Query query, int subQueryIndex, Result res)
		{
		final Document q = Document.parse(statement);
		new MongoDBParamReplacer(params).visit(q);
		
		final long start = timeService.getCurrentTime();
		final Document r = client.getDatabase(database).runCommand(q);
		final long end = timeService.getCurrentTime();
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, Collections.singletonList(new ColumnDefImpl(
				"result", ColumnType.STRING, null, null, null, null
				)));
		
		rs.getRows().add(new DefaultResultRow(r.toJson()));
		rs.setMoreAvailable(false);
		rs.setQueryTime(end - start);
		
		res.getRowSets().put(res.getQuery().getName(), rs);
		}
	}
