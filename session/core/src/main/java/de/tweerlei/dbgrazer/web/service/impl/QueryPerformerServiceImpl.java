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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowInterpreter;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryRowProducer;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.service.QueryRunnerService;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.query.service.RecursiveQueryRunnerService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.service.TimeService;

/**
 * Perform queries
 * 
 * @author Robert Wruck
 */
@Service
public class QueryPerformerServiceImpl implements QueryPerformerService
	{
	private static final class LimitingStatementHandler implements StatementHandler
		{
		private final StatementHandler h;
		private final int limit;
		private int rowCount;
		
		public LimitingStatementHandler(StatementHandler h, int limit)
			{
			this.h = h;
			this.limit = limit;
			}
		
		@Override
		public void startStatements()
			{
			h.startStatements();
			}
		
		@Override
		public void statement(String stmt)
			{
			rowCount++;
			if (rowCount < limit)
				h.statement(stmt);
			}
		
		@Override
		public void comment(String comment)
			{
			h.comment(comment);
			}

		@Override
		public void endStatements()
			{
			h.endStatements();
			}
		
		@Override
		public void error(RuntimeException e)
			{
			h.error(e);
			}
		}
	
	private static final class LimitingStatementProducer implements StatementProducer
		{
		private final StatementProducer t;
		private final StatementHandler h;
		private final int limit;
		
		public LimitingStatementProducer(StatementProducer t, StatementHandler h, int limit)
			{
			this.t = t;
			this.h = h;
			this.limit = limit;
			}
		
		@Override
		public void produceStatements(StatementHandler handler)
			{
			final LimitingStatementHandler lh = new LimitingStatementHandler((h == null) ? handler : h, limit);
			t.produceStatements(lh);
			}
		
		@Override
		public String getPrepareStatement()
			{
			return (t.getPrepareStatement());
			}
		
		@Override
		public String getCleanupStatement()
			{
			return (t.getCleanupStatement());
			}
		}
	
	private final TimeService timeService;
	private final ConfigAccessor configService;
	private final QueryService queryService;
	private final QueryRunnerService runner;
	private final RecursiveQueryRunnerService recursiveRunner;
	private final ResultTransformerService resultTransformer;
	private final DataFormatterFactory factory;
	private final QuerySettingsManager querySettingsManager;
	private final ResultCache resultCache;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param configService ConfigAccessor
	 * @param queryService QueryService
	 * @param runner QueryRunnerService
	 * @param recursiveRunner RecursiveQueryRunnerService
	 * @param resultTransformer ResultTransformerService
	 * @param factory DataFormatterFactory
	 * @param querySettingsManager QuerySettingsManager
	 * @param resultCache ResultCache
	 */
	@Autowired
	public QueryPerformerServiceImpl(TimeService timeService, ConfigAccessor configService,
			QueryService queryService,
			QueryRunnerService runner, RecursiveQueryRunnerService recursiveRunner, ResultTransformerService resultTransformer,
			DataFormatterFactory factory, QuerySettingsManager querySettingsManager, ResultCache resultCache)
		{
		this.timeService = timeService;
		this.configService = configService;
		this.queryService = queryService;
		this.runner = runner;
		this.recursiveRunner = recursiveRunner;
		this.resultTransformer = resultTransformer;
		this.factory = factory;
		this.querySettingsManager = querySettingsManager;
		this.resultCache = resultCache;
		}
	
	@Override
	public Map<String, Result> performRecursiveQuery(String link, QueryParameters query) throws PerformQueryException
		{
		final DataFormatter fmt = factory.getWebFormatter();
		final TimeZone timeZone = factory.getTimeZone();
		final List<Object> qParams = getQueryParameters(link, query, fmt);
		
		final Map<String, Result> ret;
		
		if (query.getQuery().getType().getName().equals(VisualizationSettings.DASHBOARD_QUERY_TYPE))
			{
			// Allow 2 levels of recursion, limit results to DASHBOARD_ROWS
			ret = recursiveRunner.performRecursiveQuery(link, query.getQuery(), qParams, timeZone, 2, configService.get(ConfigKeys.DASHBOARD_ROWS), true);
			}
		else if (query.getQuery().getType().getName().equals(VisualizationSettings.PANELS_QUERY_TYPE))
			{
			// Allow 2 levels of recursion, limit results to PANEL_ROWS
			ret = recursiveRunner.performRecursiveQuery(link, query.getQuery(), qParams, timeZone, 2, configService.get(ConfigKeys.PANEL_ROWS), true);
			}
		else if (query.getQuery().getType().getName().equals(VisualizationSettings.NAVIGATOR_QUERY_TYPE))
			{
			// Allow 3 levels of recursion
			ret = recursiveRunner.performRecursiveQuery(link, query.getQuery(), qParams, timeZone, 3, Integer.MAX_VALUE, configService.get(ConfigKeys.SHOW_EMPTY_SUBQUERIES));
			}
		else if (query.getQuery().getType().getResultType() == ResultType.RECURSIVE)
			{
			// Allow 2 levels of recursion
			ret = recursiveRunner.performRecursiveQuery(link, query.getQuery(), qParams, timeZone, 2, Integer.MAX_VALUE, configService.get(ConfigKeys.SHOW_EMPTY_SUBQUERIES));
			}
		else
			{
			// Allow 1 level of recursion
			ret = recursiveRunner.performRecursiveQuery(link, query.getQuery(), qParams, timeZone, 1, Integer.MAX_VALUE, configService.get(ConfigKeys.SHOW_EMPTY_SUBQUERIES));
			}
		
		for (Map.Entry<String, Result> ent : ret.entrySet())
			{
			if (ent.getValue().getQuery().getType().isAccumulatingResults())
				ent.setValue(addTimechartResult(link, ent.getValue(), query.getActualParameters()));
			}
		
		return (ret);
		}
	
	@Override
	public Result performQuery(String link, QueryParameters query) throws PerformQueryException
		{
		final DataFormatter fmt = factory.getWebFormatter();
		final TimeZone timeZone = factory.getTimeZone();
		final List<Object> qParams = getQueryParameters(link, query, fmt);
		
		if (query.getQuery().getType().isAccumulatingResults())
			{
			// Return cached result if available
			final Result cached = resultCache.getCachedObject(CacheClass.RESULT, link, query.getQuery().getName(), query.getActualParameters(), Result.class);
			if (cached != null)
				return (cached.clone());
			}
		
		if (query.getQuery().getType().getResultType() == ResultType.MULTILEVEL)
			{
			// Return empty subqueries for MULTILEVEL queries
			return (recursiveRunner.performQuery(link, query.getQuery(), qParams, timeZone, Integer.MAX_VALUE, true));
			}
		else
			{
			// Don't return empty subqueries
			return (recursiveRunner.performQuery(link, query.getQuery(), qParams, timeZone, Integer.MAX_VALUE, false));
			}
		}
	
	@Override
	public RowProducer createRowProducer(String link, QueryParameters query)
		{
		final DataFormatter fmt = factory.getWebFormatter();
		final TimeZone timeZone = factory.getTimeZone();
		final List<Object> qParams = getQueryParameters(link, query, fmt);
		
		return (new QueryRowProducer(link, query.getQuery(), qParams, timeZone, runner));
		}
	
	private List<Object> getQueryParameters(String link, QueryParameters query, DataFormatter fmt)
		{
		final List<Object> qParams = querySettingsManager.translateParameters(query.getQuery(), query.getEffectiveParameters(), fmt);
		
		// Pass any additional parameters to the SubQueryResolver
		if (query.getQuery().getType().getSubQueryResolver() != null)
			{
			if (!CollectionUtils.empty(query.getAdditionalParameters()))
				{
				// Hack: Translate additional parameters for first target query if fully specified
				for (TargetDef t : query.getQuery().getTargetQueries().values())
					{
					if (!t.isParameter())
						{
						final Query tq = queryService.findQueryByName(link, t.getQueryName());
						if (tq != null)
							{
							final List<Object> translated = querySettingsManager.translatePartialParameters(tq, query.getAllParameters(), fmt);
							if (translated != null)
								return (translated);
							}
						}
					}
				
				qParams.addAll(query.getAdditionalParameters());
				}
			}
		
		return (qParams);
		}
	
	private Result addTimechartResult(String link, Result r, Map<Integer, String> params)
		{
		Result cached = resultCache.getCachedObject(CacheClass.RESULT, link, r.getQuery().getName(), params, Result.class);
		if (cached == null)
			cached = new ResultImpl(r.getQuery());
		
		final String label = factory.getMessage(MessageKeys.TIMESTAMP);
		
		resultTransformer.addRowsWithPrefix(cached, r, label, ColumnType.DATE, timeService.getCurrentDate(), configService.get(ConfigKeys.TIMECHART_ROWS));
		
		resultCache.clearCachedObjects(CacheClass.RESULT);
		resultCache.addCachedObject(CacheClass.RESULT, link, r.getQuery().getName(), params, cached);
		
		return (cached.clone());
		}
	
	@Override
	public Query createCustomQuery(String type, String statement, List<ParameterDef> paramDefs, String label)
		{
		final QueryType t = queryService.findQueryType(type);
		if (t == null)
			throw new IllegalArgumentException("Unknown query type: " + type);
		
		final Query q = new QueryImpl(label, new SchemaDef(null, null), null, statement, null, t, paramDefs, null, null);
		
		return (q);
		}
	
	@Override
	public Result performCustomQuery(String link, String type, String statement, List<ParameterDef> paramDefs, List<Object> params, String label, boolean export, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final TimeZone timeZone = factory.getTimeZone();
		final Query q = createCustomQuery(type, statement, paramDefs, label);
		
		final Result r = runner.performQuery(link, q, 0, params == null ? Collections.emptyList() : params, timeZone, export ? Integer.MAX_VALUE : configService.get(ConfigKeys.BROWSER_ROWS), monitor);
		return (r);
		}
	
	@Override
	public void performCustomQuery(String link, String type, String statement, String label, TimeZone timeZone, RowHandler handler) throws PerformQueryException
		{
		try	{
			final Query q = createCustomQuery(type, statement, null, label);
			
			runner.performStreamedQuery(link, q, Collections.emptyList(), timeZone, Integer.MAX_VALUE, handler);
			}
		finally
			{
			handler.endRows();
			}
		}
	
	@Override
	public Result performCustomQueries(String link, StatementProducer statements, String type, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final TimeZone timeZone = factory.getTimeZone();
		final QueryType t = queryService.findQueryType(type);
		if (t == null)
			throw new IllegalArgumentException("Unknown query type: " + type);
		
		return (runner.performQueries(link, statements, timeZone, t, configService.get(ConfigKeys.COMMIT_ROWS), monitor));
		}
	
	@Override
	public Result performCustomQueries(String link, StatementProducer statements, StatementHandler handler, String type, DMLProgressMonitor monitor, boolean export) throws PerformQueryException
		{
		final TimeZone timeZone = factory.getTimeZone();
		final QueryType t = queryService.findQueryType(type);
		if (t == null)
			throw new IllegalArgumentException("Unknown query type: " + type);
		
		final StatementProducer lrt = new LimitingStatementProducer(statements, handler, export ? Integer.MAX_VALUE : configService.get(ConfigKeys.BROWSER_ROWS));
		
		return (runner.performQueries(link, lrt, timeZone, t, configService.get(ConfigKeys.COMMIT_ROWS), monitor));
		}
	
	@Override
	public Result transferRows(String link, String query, RowInterpreter interpreter, String type, DMLProgressMonitor monitor, boolean export) throws PerformQueryException
		{
		final TimeZone timeZone = factory.getTimeZone();
		final QueryType t = queryService.findQueryType(type);
		if (t == null)
			throw new IllegalArgumentException("Unknown query type: " + type);
		
		return (runner.transferRows(link, query, timeZone, interpreter, t, configService.get(ConfigKeys.COMMIT_ROWS), monitor));
		}
	
	@Override
	public Result performCustomChartQuery(String link, String type, String statement, String label) throws PerformQueryException
		{
		final TimeZone timeZone = factory.getTimeZone();
		final Query q = createCustomQuery(type, statement, null, label);
		
		final Result r = runner.performQuery(link, q, 0, Collections.emptyList(), timeZone, Integer.MAX_VALUE, null);
		
		final Query qc = new QueryImpl(label, new SchemaDef(null, null), null, null, null, queryService.findQueryType(VisualizationSettings.CHART_QUERY_TYPE), null, null, null);
		final Result rc = new ResultImpl(qc);
		rc.getRowSets().putAll(r.getRowSets());
		return (rc);
		}
	}
