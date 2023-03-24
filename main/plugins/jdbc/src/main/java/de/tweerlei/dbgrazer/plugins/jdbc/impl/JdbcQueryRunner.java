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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.extension.jdbc.DataAccessService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.ColumnSplitMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.GroupingRowSetMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.RowHandlerMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.RowSetMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.SingleRowSetMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.support.ExecuteCallback;
import de.tweerlei.dbgrazer.plugins.jdbc.support.LimitedResultSetExtractor;
import de.tweerlei.dbgrazer.plugins.jdbc.support.PreparedExecuteCallback;
import de.tweerlei.dbgrazer.plugins.jdbc.types.CustomQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.ReportQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.service.TimeService;

/**
 * Run JDBC queries
 * 
 * @author Robert Wruck
 */
@Service
public class JdbcQueryRunner extends BaseQueryRunner
	{
	private static class SimpleQueryCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final ResultSetExtractor extractor;
		
		public SimpleQueryCallback(JdbcTemplate template, String statement, List<Object> args, ResultSetExtractor extractor)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.extractor = extractor;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			if (args.isEmpty())
				{
				// Use Statement.executeQuery
				return (template.query(statement, extractor));
				}
			else
				{
				// Use PreparedStatement.executeQuery
				return (template.query(statement, args.toArray(), extractor));
				}
			}
		}
	
	private static class CustomQueryCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final List<ParameterDef> params;
		private final ResultSetExtractor extractor;
		
		public CustomQueryCallback(JdbcTemplate template, String statement, List<Object> args, List<ParameterDef> params, ResultSetExtractor extractor)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.params = params;
			this.extractor = extractor;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			if (params.isEmpty())
				{
				// Use Statement.executeQuery
				return (template.query(statement, extractor));
				}
			else
				{
				// Use PreparedStatement.executeQuery
				final int n = params.size();
				final int[] types = new int[n];
				for (int i = 0; i < n; i++)
					types[i] = params.get(i).getType().getDefaultSQLType();
				
				final PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(statement, types);
				final PreparedStatementCreator psc = factory.newPreparedStatementCreator(args);
				return (template.query(psc, extractor));
				}
			}
		}
	
	private static class CustomExecuteCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final List<ParameterDef> params;
		private final ResultSetExtractor extractor;
		
		public CustomExecuteCallback(JdbcTemplate template, String statement, List<Object> args, List<ParameterDef> params, ResultSetExtractor extractor)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.params = params;
			this.extractor = extractor;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			if (params.isEmpty())
				{
				// Use Statement.execute
				final StatementCallback cb = new ExecuteCallback(statement, extractor, template.getNativeJdbcExtractor());
				return (template.execute(cb));
				}
			else
				{
				// Use PreparedStatement.execute
				final int n = params.size();
				final int[] types = new int[n];
				for (int i = 0; i < n; i++)
					types[i] = params.get(i).getType().getDefaultSQLType();
				
				final PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(statement, types);
				final PreparedStatementCreator psc = factory.newPreparedStatementCreator(args);
				final PreparedStatementCallback cb = new PreparedExecuteCallback(extractor, template.getNativeJdbcExtractor());
				return (template.execute(psc, cb));
				}
			}
		}
	
	private final KeywordService keywordService;
	private final DataAccessService dataAccessService;
	private final ResultBuilderService resultBuilder;
	private final SQLGeneratorService sqlGenerator;
	private final TimeService timeService;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 * @param dataAccessService DataAccessService
	 * @param resultBuilder ResultBuilderService
	 * @param sqlGenerator SQLGeneratorService
	 * @param timeService TimeService
	 */
	@Autowired
	public JdbcQueryRunner(KeywordService keywordService, DataAccessService dataAccessService,
			ResultBuilderService resultBuilder, SQLGeneratorService sqlGenerator, TimeService timeService)
		{
		super("JDBC");
		this.keywordService = keywordService;
		this.dataAccessService = dataAccessService;
		this.resultBuilder = resultBuilder;
		this.sqlGenerator = sqlGenerator;
		this.timeService = timeService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return ((t.getLinkType() instanceof JdbcLinkType) && !t.isScript() && (!t.isManipulation() || (t instanceof CustomQueryType)));
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = dataAccessService.getTransactionTemplate(link);
			final JdbcTemplate template = dataAccessService.getJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			if (template != null)
				{
				try	{
					res.getRowSets().putAll(performQuery(tx, template, dialect, query, subQueryIndex, params, timeZone, limit));
					}
				catch (TransactionException e)
					{
					throw new PerformQueryException(query.getName(), e);
					}
				catch (DataAccessException e)
					{
					throw new PerformQueryException(query.getName(), e);
					}
				}
			}
		
		return (res);
		}
	
	private Map<String, RowSetImpl> performQuery(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit)
		{
		final int maxRows = Math.min(limit, template.getMaxRows());
		
		final RowSetMapper mapper = getRowSetMapper(dialect, timeZone, query, subQueryIndex);
		
		final LimitedResultSetExtractor extractor = new LimitedResultSetExtractor(mapper, maxRows);
		final Query runQuery;
		if (query.getType() instanceof ReportQueryType)
			{
			final String countStmt = sqlGenerator.createRowCountQuery(query.getStatement());
			runQuery = new QueryImpl(query.getName(), query.getSourceSchema(), query.getGroupName(), countStmt, null, query.getType(), query.getParameters(), null, query.getAttributes());
			}
		else
			runQuery = query;
		final TransactionCallback cb = getTransactionCallback(runQuery, params, template, extractor, tx.isReadOnly());
		
		final long start = timeService.getCurrentTime();
		final Object updateCount = tx.execute(cb);
		final long end = timeService.getCurrentTime();
		
		final Map<String, RowSetImpl> ret = mapper.getRowSets();
		if (ret.isEmpty() && (updateCount != null))
			{
			// Create a RowSet with the updateCount as single value
			final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, subQueryIndex, "Rows", updateCount, end - start);
			rs.setAffectedRows(((Number) updateCount).intValue());
			ret.put(query.getName(), rs);
			}
		else
			{
			for (RowSetImpl rs : ret.values())
				{
				rs.setMoreAvailable(extractor.isMoreAvailable());
				rs.setQueryTime(end - start);
				}
			}
		
		return (ret);
		}
	
	private RowSetMapper getRowSetMapper(SQLDialect dialect, TimeZone timeZone, Query query, int subQueryIndex)
		{
		switch (query.getType().getMapMode())
			{
			case GROUPED:
				return (new GroupingRowSetMapper(sqlGenerator, dialect, timeZone, query, subQueryIndex));
			case SPLIT:
				return (new ColumnSplitMapper(sqlGenerator, dialect, timeZone, query, subQueryIndex));
			default:
				return (new SingleRowSetMapper(sqlGenerator, dialect, timeZone, query, subQueryIndex));
			}
		}
	
	private TransactionCallback getTransactionCallback(Query query, List<Object> params, JdbcTemplate template, ResultSetExtractor extractor, boolean readOnly)
		{
		final JdbcParamReplacer rep = new JdbcParamReplacer(params, keywordService);
		final String statement = rep.replaceAll(query.getStatement());
		final List<Object> args = rep.getRemainingParams();
		
		if (query.getType() instanceof CustomQueryType)
			{
			if (readOnly)
				return (new CustomQueryCallback(template, statement, args, query.getParameters(), extractor));
			else
				return (new CustomExecuteCallback(template, statement, args, query.getParameters(), extractor));
			}
		else
			return (new SimpleQueryCallback(template, statement, args, extractor));
		}
	
	@Override
	public int performStreamedQuery(String link, Query query, List<Object> params, TimeZone timeZone, int limit, RowHandler handler) throws PerformQueryException
		{
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = dataAccessService.getTransactionTemplate(link);
			final JdbcTemplate template = dataAccessService.getUnlimitedJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			if (template != null)
				{
				try	{
					return (performQuery(tx, template, dialect, query, params, timeZone, limit, handler));
					}
				catch (TransactionException e)
					{
					throw new PerformQueryException(query.getName(), e);
					}
				catch (DataAccessException e)
					{
					throw new PerformQueryException(query.getName(), e);
					}
				}
			}
		
		return (0);
		}
	
	private int performQuery(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, List<Object> params, TimeZone timeZone, int limit, RowHandler handler)
		{
		final RowHandlerMapper mapper = new RowHandlerMapper(sqlGenerator, dialect, timeZone, handler);
		final LimitedResultSetExtractor extractor = new LimitedResultSetExtractor(mapper, limit);
		final TransactionCallback cb = getTransactionCallback(query, params, template, extractor, tx.isReadOnly());
		
		tx.execute(cb);
		
		return (extractor.getRowCount());
		}
	}
