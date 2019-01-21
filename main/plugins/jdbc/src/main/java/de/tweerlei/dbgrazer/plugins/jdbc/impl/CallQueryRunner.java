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

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.extension.jdbc.DataAccessService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.RowSetMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.mapper.SingleRowSetMapper;
import de.tweerlei.dbgrazer.plugins.jdbc.support.GenerateKeysCallback;
import de.tweerlei.dbgrazer.plugins.jdbc.support.LimitedResultSetExtractor;
import de.tweerlei.dbgrazer.plugins.jdbc.types.CustomQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.DMLKeyQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.DMLQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.FunctionQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.ProcedureQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.SQLScriptOutputReader;
import de.tweerlei.spring.service.TimeService;

/**
 * Run JDBC queries
 * 
 * @author Robert Wruck
 */
@Service
public class CallQueryRunner extends BaseQueryRunner
	{
	private static final String RESULT_COLUMN_NAME = "Output";
	
	private static class DMLQueryCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final String preDMLStatement;
		private final String postDMLStatement;
		
		public DMLQueryCallback(JdbcTemplate template, String statement, List<Object> args, String preDMLStatement, String postDMLStatement)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.preDMLStatement = preDMLStatement;
			this.postDMLStatement = postDMLStatement;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			if (!StringUtils.empty(preDMLStatement))
				template.update(preDMLStatement);
			
			try	{
				return (template.update(statement, args.toArray()));
				}
			finally
				{
				if (!StringUtils.empty(postDMLStatement))
					template.update(postDMLStatement);
				}
			}
		}
	
	private static class DMLKeyQueryCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final List<ParameterDef> params;
		private final String preDMLStatement;
		private final String postDMLStatement;
		private final ResultSetExtractor extractor;
		
		public DMLKeyQueryCallback(JdbcTemplate template, String statement, List<Object> args, List<ParameterDef> params, String preDMLStatement, String postDMLStatement, ResultSetExtractor extractor)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.params = params;
			this.preDMLStatement = preDMLStatement;
			this.postDMLStatement = postDMLStatement;
			this.extractor = extractor;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			if (!StringUtils.empty(preDMLStatement))
				template.update(preDMLStatement);
			
			try	{
				final int n = args.size();
				final List<SqlParameter> sqlParams = new ArrayList<SqlParameter>(n);
				final Map<String, Object> sqlValues = new HashMap<String, Object>(n);
				for (int i = 0; i < n; i++)
					{
					final ParameterDef def = params.get(i);
					sqlParams.add(new SqlParameter(def.getName(), def.getType().getDefaultSQLType()));
					sqlValues.put(def.getName(), args.get(i));
					}
				
				final PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(statement, sqlParams);
				factory.setReturnGeneratedKeys(true);
				final PreparedStatementCreator psc = factory.newPreparedStatementCreator(args);
				final PreparedStatementCallback cb = new GenerateKeysCallback(extractor, template.getNativeJdbcExtractor());
				return (template.execute(psc, cb));
				}
			finally
				{
				if (!StringUtils.empty(postDMLStatement))
					template.update(postDMLStatement);
				}
			}
		}
	
	private static class ProcedureCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final List<ParameterDef> params;
		private final TimeService timeService;
		private final SQLDialect dialect;
		
		public ProcedureCallback(JdbcTemplate template, String statement, List<Object> args, List<ParameterDef> params, TimeService timeService, SQLDialect dialect)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.params = params;
			this.timeService = timeService;
			this.dialect = dialect;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			final int n = args.size();
			final List<SqlParameter> sqlParams = new ArrayList<SqlParameter>(n);
			final Map<String, Object> sqlValues = new HashMap<String, Object>(n);
			for (int i = 0; i < n; i++)
				{
				final ParameterDef def = params.get(i);
				sqlParams.add(new SqlParameter(def.getName(), def.getType().getDefaultSQLType()));
				sqlValues.put(def.getName(), args.get(i));
				}
			
			final CallableStatementCreatorFactory factory = new CallableStatementCreatorFactory(statement, sqlParams);
			final CallableStatementCreator csc = factory.newCallableStatementCreator(sqlValues);
			final CallableStatementCallback cb = new CallProcedureCallback(timeService, dialect);
			return (template.execute(csc, cb));
			}
		}
	
	private static class FunctionCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final String statement;
		private final List<Object> args;
		private final List<ParameterDef> params;
		
		public FunctionCallback(JdbcTemplate template, String statement, List<Object> args, List<ParameterDef> params)
			{
			this.template = template;
			this.statement = statement;
			this.args = args;
			this.params = params;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			final int n = args.size();
			final List<SqlParameter> sqlParams = new ArrayList<SqlParameter>(n);
			final Map<String, Object> sqlValues = new HashMap<String, Object>(n);
			for (int i = 0; i < n; i++)
				{
				final ParameterDef def = params.get(i);
				if (i == 0)
					sqlParams.add(new SqlOutParameter(def.getName(), def.getType().getDefaultSQLType()));
				else
					{
					sqlParams.add(new SqlParameter(def.getName(), def.getType().getDefaultSQLType()));
					sqlValues.put(def.getName(), args.get(i));
					}
				}
			
			final CallableStatementCreatorFactory factory = new CallableStatementCreatorFactory(statement, sqlParams);
			final CallableStatementCreator csc = factory.newCallableStatementCreator(sqlValues);
			final CallableStatementCallback cb = new CallFunctionCallback();
			return (template.execute(csc, cb));
			}
		}
	
	private static final class CallProcedureCallback implements CallableStatementCallback
		{
		private final TimeService timeService;
		private final SQLDialect dialect;
		
		public CallProcedureCallback(TimeService timeService, SQLDialect dialect)
			{
			this.timeService = timeService;
			this.dialect = dialect;
			}
		
		@Override
		public Object doInCallableStatement(CallableStatement stmt) throws SQLException, DataAccessException
			{
			final SQLScriptOutputReader reader = dialect.getScriptOutputReader(stmt.getConnection());
			final StringBuilder sb = new StringBuilder();
			
			SQLException caught = null;
			reader.enable();
			try	{
				final long start = timeService.getCurrentTime();
				int count = 0;
				try	{
					count = stmt.executeUpdate();
					}
				catch (SQLException e)
					{
					caught = e;
					}
				final long end = timeService.getCurrentTime();
				
				String output = null;
				try	{
					output = reader.readOutput();
					}
				catch (SQLException e)
					{
					if (caught == null)
						caught = e;
					}
				
				if (output != null)
					{
					sb.append(output);
					sb.append(" ");
					}
				sb.append("[").append(count).append(" rows, ").append(end - start).append(" msec]\n");
				}
			finally
				{
				try	{
					reader.disable();
					reader.close();
					}
				catch (SQLException e)
					{
					// Report the inner exception if caught
					if (caught == null)
						throw e;
					}
				
				if (caught != null)
					throw caught;
				}
			
			return (sb.toString());
			}
		}
	
	private static final class CallFunctionCallback implements CallableStatementCallback
		{
		public CallFunctionCallback()
			{
			}
		
		@Override
		public Object doInCallableStatement(CallableStatement stmt) throws SQLException, DataAccessException
			{
			stmt.executeUpdate();
			
			return (stmt.getObject(1));
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
	public CallQueryRunner(KeywordService keywordService, DataAccessService dataAccessService,
			ResultBuilderService resultBuilder, SQLGeneratorService sqlGenerator, TimeService timeService)
		{
		super("SQLCall");
		this.keywordService = keywordService;
		this.dataAccessService = dataAccessService;
		this.resultBuilder = resultBuilder;
		this.sqlGenerator = sqlGenerator;
		this.timeService = timeService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return ((t.getLinkType() instanceof JdbcLinkType) && !t.isScript() && t.isManipulation() && !(t instanceof CustomQueryType));
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = dataAccessService.getTransactionTemplate(link);
			if (tx.isReadOnly())
				throw new PerformQueryException("getTransactionTemplate", new RuntimeException("Link is read-only"));
			final JdbcTemplate template = dataAccessService.getJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			final String preDMLStatement = dataAccessService.getPreDMLStatement(link);
			final String postDMLStatement = dataAccessService.getPostDMLStatement(link);
			if (template != null)
				{
				try	{
					if (query.getType() instanceof DMLKeyQueryType)
						res.getRowSets().putAll(performDMLKeyQuery(tx, template, dialect, query, subQueryIndex, params, limit, preDMLStatement, postDMLStatement));
					else
						res.getRowSets().put(query.getName(), performQuery(tx, template, dialect, query, subQueryIndex, params, preDMLStatement, postDMLStatement));
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
	
	private Map<String, RowSetImpl> performDMLKeyQuery(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, int subQueryIndex, List<Object> params, int limit, String preDMLStatement, String postDMLStatement)
		{
		final int maxRows = Math.min(limit, template.getMaxRows());
		
		final JdbcParamReplacer rep = new JdbcParamReplacer(params, keywordService);
		final String statement = rep.replaceAll(query.getStatement());
		final List<Object> args = rep.getRemainingParams();
		
		final RowSetMapper mapper = new SingleRowSetMapper(sqlGenerator, dialect, query, subQueryIndex);
		final LimitedResultSetExtractor extractor = new LimitedResultSetExtractor(mapper, maxRows);
		final TransactionCallback cb = new DMLKeyQueryCallback(template, statement, args, query.getParameters(), preDMLStatement, postDMLStatement, extractor);
		
		final long start = timeService.getCurrentTime();
		final Object updateCount = tx.execute(cb);
		final long end = timeService.getCurrentTime();
		
		final Map<String, RowSetImpl> ret = mapper.getRowSets();
		if (ret.isEmpty() && (updateCount != null))
			{
			// Create a RowSet with the updateCount as single value
			final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, subQueryIndex, RESULT_COLUMN_NAME, updateCount, end - start);
			rs.setAffectedRows(((Number) updateCount).intValue());
			ret.put(query.getName(), rs);
			}
		else
			{
			for (RowSetImpl rs : ret.values())
				{
				rs.setQueryTime(end - start);
				if (extractor.getRowCount() >= maxRows)
					rs.setMoreAvailable(true);
				}
			}
		
		return (ret);
		}
	
	private RowSetImpl performQuery(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, int subQueryIndex, List<Object> params, String preDMLStatement, String postDMLStatement)
		{
		final TransactionCallback cb = getTransactionCallback(template, dialect, query, params, preDMLStatement, postDMLStatement);
		
		final long start = timeService.getCurrentTime();
		final Object ret = tx.execute(cb);
		final long end = timeService.getCurrentTime();
		
		// Create a RowSet with a single value
		return (resultBuilder.createSingletonRowSet(query, subQueryIndex, RESULT_COLUMN_NAME, ret, end - start));
		}
	
	private TransactionCallback getTransactionCallback(JdbcTemplate template, SQLDialect dialect, Query query, List<Object> params, String preDMLStatement, String postDMLStatement)
		{
		final JdbcParamReplacer rep = new JdbcParamReplacer(params, keywordService);
		final String statement = rep.replaceAll(query.getStatement());
		final List<Object> args = rep.getRemainingParams();
		
		final QueryType type = query.getType();
		if (type instanceof DMLQueryType)
			return (new DMLQueryCallback(template, statement, args, preDMLStatement, postDMLStatement));
		else if (type instanceof ProcedureQueryType)
			return (new ProcedureCallback(template, statement, args, query.getParameters(), timeService, dialect));
		else if (type instanceof FunctionQueryType)
			return (new FunctionCallback(template, statement, args, query.getParameters()));
		else
			throw new RuntimeException("Unknown query type: " + type);
		}
	}
