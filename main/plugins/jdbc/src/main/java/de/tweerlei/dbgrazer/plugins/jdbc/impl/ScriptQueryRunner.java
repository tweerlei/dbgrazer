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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.extension.jdbc.DataAccessService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.plugins.jdbc.types.FaultTolerantScriptQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.TestScriptQueryType;
import de.tweerlei.dbgrazer.query.backend.BaseQueryRunner;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;
import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor.CancellationListener;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowInterpreter;
import de.tweerlei.dbgrazer.query.model.RowInterpreter.RowHandlerDef;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowTransferer;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
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
public class ScriptQueryRunner extends BaseQueryRunner
	{
	private static final String RESULT_COLUMN_NAME = "Output";
	
	private static final class StatementTxCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final StatementCallback cb;
		
		public StatementTxCallback(JdbcTemplate template, StatementCallback cb)
			{
			this.template = template;
			this.cb = cb;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			return (template.execute(cb));
			}
		}
	
	private static final class ConnectionTxCallback implements TransactionCallback
		{
		private final JdbcTemplate template;
		private final ConnectionCallback cb;
		
		public ConnectionTxCallback(JdbcTemplate template, ConnectionCallback cb)
			{
			this.template = template;
			this.cb = cb;
			}
		
		@Override
		public Object doInTransaction(TransactionStatus status)
			{
			return (template.execute(cb));
			}
		}
	
	private static final class ScriptRunnerCallback implements StatementCallback, SqlProvider, CancellationListener
		{
		private final TimeService timeService;
		private final SQLDialect dialect;
		private final Iterable<String> statements;
		private final boolean ignoreErrors;
		private final CancelableProgressMonitor monitor;
		private final StringBuilder errors;
		private String currentStatement;
		private Statement currentJdbcStatement;
		private int totalRowCount;
		
		public ScriptRunnerCallback(TimeService timeService, SQLDialect dialect, Iterable<String> statements, boolean ignoreErrors, CancelableProgressMonitor monitor)
			{
			this.timeService = timeService;
			this.dialect = dialect;
			this.statements = statements;
			this.ignoreErrors = ignoreErrors;
			this.monitor = monitor;
			this.errors = new StringBuilder();
			}
		
		public int getTotalRowCount()
			{
			return (totalRowCount);
			}
		
		public String getErrors()
			{
			return (errors.toString());
			}
		
		@Override
		public Object doInStatement(Statement stmt) throws SQLException, DataAccessException
			{
			final SQLScriptOutputReader reader = dialect.getScriptOutputReader(stmt.getConnection());
			
			SQLException caught = null;
			reader.enable();
			try	{
				currentJdbcStatement = stmt;
				if (monitor != null)
					monitor.addListener(this);
				
				for (String sql : statements)
					{
					currentStatement = sql;
					
					final long start = timeService.getCurrentTime();
					int count = 0;
					try	{
						count = stmt.executeUpdate(sql);
						}
					catch (SQLException e)
						{
						caught = e;
						}
					final long end = timeService.getCurrentTime();
					
					if (monitor != null)
						monitor.progress(1);
					
					for (SQLWarning w = stmt.getWarnings(); w != null; w = w.getNextWarning())
						{
						errors.append(w.getMessage());
						errors.append("\n");
						}
					
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
						errors.append(output);
						errors.append(" ");
						}
					errors.append("[").append(count).append(" rows, ").append(end - start).append(" msec]\n");
					totalRowCount += count;
					
					if (caught != null)
						{
						if (ignoreErrors)
							{
							errors.append(caught.getMessage());
							errors.append("\n");
							caught = null;
							}
						else
							break;
						}
					}
				}
			finally
				{
				currentJdbcStatement = null;
				if (monitor != null)
					monitor.removeListener(this);
				
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
			
//			stmt.getConnection().commit();
			
			return (errors.toString());
			}
		
		@Override
		public void cancelled()
			{
			final Statement stmt = currentJdbcStatement;
			if (stmt != null)
				{
				try	{
					stmt.cancel();
					}
				catch (SQLException e)
					{
					throw new RuntimeException(e);
					}
				}
			}
		
		@Override
		public String getSql()
			{
			return (currentStatement);
			}
		}
	
	private static final class DMLRunnerCallback implements StatementCallback, SqlProvider
		{
		private final StatementProducer statements;
		private final String eol;
		private final int commitSize;
		private final DMLProgressMonitor monitor;
		private final boolean rollback;
		private final boolean ignoreErrors;
		private final String preDMLStatement;
		private final String postDMLStatement;
		private final StringBuilder errors;
		private String currentStatement;
		private int totalRowCount;
		private int rowCount;
		
		public DMLRunnerCallback(StatementProducer statements, String eol, int commitSize, DMLProgressMonitor monitor, boolean rollback, boolean ignoreErrors, String preDMLStatement, String postDMLStatement)
			{
			this.statements = statements;
			this.eol = eol;
			this.commitSize = commitSize;
			this.monitor = monitor;
			this.rollback = rollback;
			this.ignoreErrors = ignoreErrors;
			this.preDMLStatement = preDMLStatement;
			this.postDMLStatement = postDMLStatement;
			this.errors = new StringBuilder();
			}
		
		public int getTotalRowCount()
			{
			return (totalRowCount);
			}
		
		public String getErrors()
			{
			return (errors.toString());
			}
		
		public int getRowCount()
			{
			return (rowCount);
			}
		
		@Override
		public Object doInStatement(Statement stmt) throws SQLException, DataAccessException
			{
			final ExecuteStatementHandler esh = new ExecuteStatementHandler(stmt, eol, commitSize, monitor, rollback, ignoreErrors);
			
			if (!StringUtils.empty(preDMLStatement))
				esh.statement(preDMLStatement);
			if (!StringUtils.empty(statements.getPrepareStatement()))
				esh.statement(statements.getPrepareStatement());
			
			DataAccessException caught = null;
			try	{
				statements.produceStatements(esh);
				}
			catch (DataAccessException e)
				{
				caught = e;
				}
			finally
				{
				try	{
					if (!StringUtils.empty(statements.getCleanupStatement()))
						esh.statement(statements.getCleanupStatement());
					if (!StringUtils.empty(postDMLStatement))
						esh.statement(postDMLStatement);
					}
				catch (DataAccessException e)
					{
					// Report the inner exception if caught
					if (caught == null)
						throw e;
					}
				
				errors.append(esh.getTotalRowCount()).append(" rows\n");
				errors.append(esh.getErrors());
				
				if (caught != null)
					throw caught;
				}
			
			totalRowCount += esh.getTotalRowCount();
			rowCount = esh.getUncommittedRowCount();
			
			return (errors.toString());
			}
		
		@Override
		public String getSql()
			{
			return (currentStatement);
			}
		}
	
	private static abstract class TransferRunnerCallback implements ConnectionCallback, SqlProvider 
		{
		private final String sql;
		
		public TransferRunnerCallback(String sql)
			{
			this.sql = sql;
			}
		
		public abstract int getTotalRowCount();
		
		public abstract String getErrors();
		
		public abstract int getRowCount();
		
		@Override
		public final String getSql()
			{
			return (sql);
			}
		}
	
	private static final class ExecuteTransferRunnerCallback extends TransferRunnerCallback
		{
		private final SQLGeneratorService sqlGenerator;
		private final SQLDialect dialect;
		private final TimeZone timeZone;
		private final RowTransferer trans;
		private final String eol;
		private final int commitSize;
		private final DMLProgressMonitor monitor;
		private final boolean rollback;
		private final boolean ignoreErrors;
		private final String preDMLStatement;
		private final String postDMLStatement;
		private final StringBuilder errors;
		private int totalRowCount;
		private int rowCount;
		
		public ExecuteTransferRunnerCallback(String sql, SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone,
				RowTransferer trans, String eol, int commitSize, DMLProgressMonitor monitor, boolean rollback, boolean ignoreErrors, String preDMLStatement, String postDMLStatement)
			{
			super(sql);
			this.sqlGenerator = sqlGenerator;
			this.dialect = dialect;
			this.timeZone = timeZone;
			this.trans = trans;
			this.eol = eol;
			this.commitSize = commitSize;
			this.monitor = monitor;
			this.rollback = rollback;
			this.ignoreErrors = ignoreErrors;
			this.preDMLStatement = preDMLStatement;
			this.postDMLStatement = postDMLStatement;
			this.errors = new StringBuilder();
			}
		
		@Override
		public int getTotalRowCount()
			{
			return (totalRowCount);
			}
		
		@Override
		public String getErrors()
			{
			return (errors.toString());
			}
		
		@Override
		public int getRowCount()
			{
			return (rowCount);
			}
		
		@Override
		public Object doInConnection(Connection c) throws SQLException, DataAccessException
			{
			final Statement dstStmt = c.createStatement();
			try	{
				final ExecuteStatementHandler esh = new ExecuteStatementHandler(dstStmt, eol, commitSize, monitor, rollback, ignoreErrors);
				
				// The prepareStatement must be executed BEFORE executeQuery ist called,
				// since it might alter session properties
				if (!StringUtils.empty(preDMLStatement))
					esh.statement(preDMLStatement);
				if (!StringUtils.empty(trans.getPrepareStatement()))
					esh.statement(trans.getPrepareStatement());
				
				RuntimeException caught = null;
				try	{
					doWithHandler(c, esh);
					}
				catch (TransactionException e)
					{
					caught = e;
					}
				catch (DataAccessException e)
					{
					caught = e;
					}
				finally
					{
					try	{
						if (!StringUtils.empty(trans.getCleanupStatement()))
							esh.statement(trans.getCleanupStatement());
						if (!StringUtils.empty(postDMLStatement))
							esh.statement(postDMLStatement);
						}
					catch (TransactionException e)
						{
						// Report the inner exception if caught
						if (caught == null)
							throw e;
						}
					catch (DataAccessException e)
						{
						// Report the inner exception if caught
						if (caught == null)
							throw e;
						}
					
					errors.append(esh.getTotalRowCount()).append(" rows\n");
					errors.append(esh.getErrors());
					
					if (caught != null)
						throw caught;
					}
				
				totalRowCount += esh.getTotalRowCount();
				rowCount = esh.getUncommittedRowCount();
				
				return (errors.toString());
				}
			finally
				{
				dstStmt.close();
				}
			}
		
		private void doWithHandler(Connection c, ExecuteStatementHandler esh) throws SQLException
			{
			final PreparedStatement srcStmt = c.prepareStatement(getSql());
			try	{
				final ResultSet rs = srcStmt.executeQuery();
				try	{
					final ResultSetIterator rsi = new ResultSetIterator(rs, new ResultSetAccessor(sqlGenerator, dialect, timeZone));
					
					trans.transfer(rsi, esh);
					}
				finally
					{
					rs.close();
					}
				}
			finally
				{
				srcStmt.close();
				}
			}
		}
	
	private static final class NoExecuteTransferRunnerCallback extends TransferRunnerCallback
		{
		private final SQLGeneratorService sqlGenerator;
		private final SQLDialect dialect;
		private final TimeZone timeZone;
		private final RowTransferer trans;
		private int totalRowCount;
 		
		public NoExecuteTransferRunnerCallback(String sql, SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone, RowTransferer trans)
			{
			super(sql);
			this.sqlGenerator = sqlGenerator;
			this.dialect = dialect;
			this.timeZone = timeZone;
			this.trans = trans;
			}
		
 		@Override
		public int getTotalRowCount()
 			{
			return (totalRowCount);
 			}
		
		@Override
		public String getErrors()
			{
			return ("");
			}
		
		@Override
		public int getRowCount()
			{
			return (totalRowCount);
			}
		
		@Override
		public Object doInConnection(Connection c) throws SQLException, DataAccessException
			{
			final PreparedStatement srcStmt = c.prepareStatement(getSql());
			try	{
				final ResultSet rs = srcStmt.executeQuery();
				try	{
					final ResultSetIterator rsi = new ResultSetIterator(rs, new ResultSetAccessor(sqlGenerator, dialect, timeZone));
					
					trans.transfer(rsi, null);
					
					totalRowCount = rsi.getRowCount();
					}
				finally
					{
					rs.close();
					}
				}
			finally
				{
				srcStmt.close();
				}
			
			return ("");
			}
 		}
 	
	private static final class PreparedStatementTransferRunnerCallback extends TransferRunnerCallback
		{
		private final SQLGeneratorService sqlGenerator;
		private final SQLDialect dialect;
		private final TimeZone timeZone;
		private final RowInterpreter trans;
		private final String eol;
		private final int commitSize;
		private final DMLProgressMonitor monitor;
		private final boolean rollback;
		private final boolean ignoreErrors;
		private final String preDMLStatement;
		private final String postDMLStatement;
		private final StringBuilder errors;
		private int totalRowCount;
		private int rowCount;
		
		public PreparedStatementTransferRunnerCallback(String sql, SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone,
				RowInterpreter trans, String eol, int commitSize, DMLProgressMonitor monitor, boolean rollback, boolean ignoreErrors, String preDMLStatement, String postDMLStatement)
			{
			super(sql);
			this.sqlGenerator = sqlGenerator;
			this.dialect = dialect;
			this.timeZone = timeZone;
			this.trans = trans;
			this.eol = eol;
			this.commitSize = commitSize;
			this.monitor = monitor;
			this.rollback = rollback;
			this.ignoreErrors = ignoreErrors;
			this.preDMLStatement = preDMLStatement;
			this.postDMLStatement = postDMLStatement;
			this.errors = new StringBuilder();
			}
		
		@Override
		public int getTotalRowCount()
			{
			return (totalRowCount);
			}
		
		@Override
		public String getErrors()
			{
			return (errors.toString());
			}
		
		@Override
		public int getRowCount()
			{
			return (rowCount);
			}
		
		@Override
		public Object doInConnection(Connection c) throws SQLException, DataAccessException
			{
			final Statement dstStmt = c.createStatement();
			try	{
				final ExecuteStatementHandler esh = new ExecuteStatementHandler(dstStmt, eol, commitSize, monitor, rollback, ignoreErrors);
				
				// The prepareStatement must be executed BEFORE executeQuery ist called,
				// since it might alter session properties
				if (!StringUtils.empty(preDMLStatement))
					esh.statement(preDMLStatement);
				if (!StringUtils.empty(trans.getPrepareStatement()))
					esh.statement(trans.getPrepareStatement());
				
				RuntimeException caught = null;
				try	{
					doWithHandler(c);
					}
				catch (TransactionException e)
					{
					caught = e;
					}
				catch (DataAccessException e)
					{
					caught = e;
					}
				finally
					{
					try	{
						if (!StringUtils.empty(trans.getCleanupStatement()))
							esh.statement(trans.getCleanupStatement());
						if (!StringUtils.empty(postDMLStatement))
							esh.statement(postDMLStatement);
						}
					catch (TransactionException e)
						{
						// Report the inner exception if caught
						if (caught == null)
							throw e;
						}
					catch (DataAccessException e)
						{
						// Report the inner exception if caught
						if (caught == null)
							throw e;
						}
					
					errors.append(esh.getTotalRowCount()).append(" rows\n");
					errors.append(esh.getErrors());
					
					if (caught != null)
						throw caught;
					}
				
				totalRowCount += esh.getTotalRowCount();
				rowCount = esh.getUncommittedRowCount();
				
				return (errors.toString());
				}
			finally
				{
				dstStmt.close();
				}
			}
		
		private void doWithHandler(Connection c) throws SQLException
			{
			final boolean useBatch = JdbcUtils.supportsBatchUpdates(c);
			
			final List<RowHandler> handlers = new ArrayList<RowHandler>();
			try	{
				for (RowHandlerDef def : trans.getStatements())
					{
					if (StringUtils.empty(def.getStmt()))
						handlers.add(null);
					else
						{
						final List<ColumnDef> params = def.getMapper().map(def.getColumns());
						final int n = params.size();
						final int[] types = new int[n];
						for (int i = 0; i < n; i++)
							types[i] = params.get(i).getType().getDefaultSQLType();
						
						final PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(def.getStmt(), types);
						// the PreparedStatementCreator created by newPreparedStatementCreator would try to actually set parameters - that we don't know yet
						final PreparedStatement ps = c.prepareStatement(def.getStmt());
						if (useBatch)
							handlers.add(new BatchStatementHandler(ps, factory, def.getMapper(), commitSize, monitor, rollback, ignoreErrors));
						else
							handlers.add(new PreparedStatementHandler(ps, factory, def.getMapper(), commitSize, monitor, rollback, ignoreErrors));
						}
					}
				
				final PreparedStatement srcStmt = c.prepareStatement(getSql());
				try	{
					final ResultSet rs = srcStmt.executeQuery();
					try	{
						final ResultSetIterator rsi = new ResultSetIterator(rs, new ResultSetAccessor(sqlGenerator, dialect, timeZone));
						
						trans.transfer(rsi, handlers);
						}
					finally
						{
						rs.close();
						}
					}
				finally
					{
					srcStmt.close();
					}
				}
			finally
				{
				for (RowHandler handler : handlers)
					{
					if (handler != null)
						{
						final CountingRowHandler crh = (CountingRowHandler) handler;
						handler.endRows();
						
						errors.append(crh.getTotalRowCount()).append(" rows\n");
						errors.append(crh.getErrors());
						
						totalRowCount += crh.getTotalRowCount();
						rowCount = crh.getUncommittedRowCount();
						}
					}
				}
			}
		}
	
	private final KeywordService keywordService;
	private final DataAccessService dataAccessService;
	private final SQLGeneratorService sqlGenerator;
	private final ResultBuilderService resultBuilder;
	private final TimeService timeService;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 * @param dataAccessService DataAccessService
	 * @param sqlGenerator SQLGeneratorService
	 * @param resultBuilder ResultBuilderService
	 * @param timeService TimeService
	 */
	@Autowired
	public ScriptQueryRunner(KeywordService keywordService, DataAccessService dataAccessService,
			SQLGeneratorService sqlGenerator, ResultBuilderService resultBuilder, TimeService timeService)
		{
		super("SQLScript");
		this.keywordService = keywordService;
		this.dataAccessService = dataAccessService;
		this.sqlGenerator = sqlGenerator;
		this.resultBuilder = resultBuilder;
		this.timeService = timeService;
		}
	
	@Override
	public boolean supports(QueryType t)
		{
		return ((t.getLinkType() instanceof JdbcLinkType) && t.isScript());
		}
	
	@Override
	public Result performQuery(String link, Query query, int subQueryIndex, List<Object> params, TimeZone timeZone, int limit, CancelableProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = getTransactionTemplate(link, query.getType());
			if (tx.isReadOnly())
				throw new PerformQueryException("getTransactionTemplate", new RuntimeException("Link is read-only"));
			
			final JdbcTemplate template = dataAccessService.getUnlimitedJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			if (template != null)
				{
				try	{
					res.getRowSets().put(query.getName(), runScript(tx, template, dialect, query, subQueryIndex, params, monitor));
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
	
	private RowSet runScript(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, int subQueryIndex, List<Object> params, CancelableProgressMonitor monitor)
		{
		final JdbcParamReplacer rep = new JdbcParamReplacer(params, keywordService);
		final String statement = rep.replaceAll(query.getStatement());
//		final List<Object> args = rep.getRemainingParams();
		final List<String> statements = sqlGenerator.parseScript(statement, dialect);
		final boolean ignoreErrors = isIgnoreErrors(query.getType());
		final ScriptRunnerCallback runner = new ScriptRunnerCallback(timeService, dialect, statements, ignoreErrors, monitor);
		final TransactionCallback cb = new StatementTxCallback(template, runner);
		
		final long start = timeService.getCurrentTime();
		
		Object ret = null;
		try	{
			ret = tx.execute(cb);
			}
		catch (TransactionException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		catch (DataAccessException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		
		final long end = timeService.getCurrentTime();
		
		final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, subQueryIndex, RESULT_COLUMN_NAME, String.valueOf(ret), end - start);
		rs.setAffectedRows(runner.getTotalRowCount());
		return (rs);
		}
	
	@Override
	public Result performQueries(String link, Query query, StatementProducer statements, TimeZone timeZone, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = getTransactionTemplate(link, query.getType());
			if (tx.isReadOnly())
				throw new PerformQueryException("getTransactionTemplate", new RuntimeException("Link is read-only"));
			
			final JdbcTemplate template = dataAccessService.getJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			final String preDMLStatement = dataAccessService.getPreDMLStatement(link);
			final String postDMLStatement = dataAccessService.getPostDMLStatement(link);
			if (template != null)
				{
				try	{
					res.getRowSets().put(query.getName(), runScript(tx, template, dialect, query, statements, commitSize, monitor, preDMLStatement, postDMLStatement));
					}
				catch (TransactionException e)
					{
					throw new PerformQueryException("performQueries", e);
					}
				catch (DataAccessException e)
					{
					throw new PerformQueryException("performQueries", e);
					}
				}
			}
		
		return (res);
		}
	
	private RowSet runScript(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, StatementProducer statements,
			int commitSize, DMLProgressMonitor monitor, String preDMLStatement, String postDMLStatement)
		{
		final String eol = getStatementTerminator(dialect);
		final boolean rollback = isRollbackOnly(query.getType());
		final boolean ignoreErrors = isIgnoreErrors(query.getType());
		final DMLRunnerCallback runner = new DMLRunnerCallback(statements, eol, commitSize, monitor, rollback, ignoreErrors, preDMLStatement, postDMLStatement);
		final TransactionCallback cb = new StatementTxCallback(template, runner);
		
		final long start = timeService.getCurrentTime();
		
		Object ret = null;
		try	{
			ret = tx.execute(cb);
			}
		catch (TransactionException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		catch (DataAccessException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		
		if (monitor != null)
			monitor.getCommittedRows().progress(runner.getRowCount());
		
		final long end = timeService.getCurrentTime();
		
		final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, 0, RESULT_COLUMN_NAME, String.valueOf(ret), end - start);
		rs.setAffectedRows(runner.getTotalRowCount());
		return (rs);
		}
	
	@Override
	public Result transferRows(String link, Query query, TimeZone timeZone, RowTransferer transferer, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = getTransactionTemplate(link, query.getType());
			final JdbcTemplate template = dataAccessService.getUnlimitedJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			final String preDMLStatement = dataAccessService.getPreDMLStatement(link);
			final String postDMLStatement = dataAccessService.getPostDMLStatement(link);
			if (template != null)
				{
				try	{
					res.getRowSets().put(query.getName(), runScript(tx, template, dialect, query, timeZone, transferer, commitSize, monitor, preDMLStatement, postDMLStatement));
					}
				catch (TransactionException e)
					{
					throw new PerformQueryException("performQueries", e);
					}
				catch (DataAccessException e)
					{
					throw new PerformQueryException("performQueries", e);
					}
				}
			}
		
		return (res);
		}
	
	private RowSet runScript(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, TimeZone timeZone, RowTransferer transferer,
			int commitSize, DMLProgressMonitor monitor, String preDMLStatement, String postDMLStatement)
		{
		final String eol = getStatementTerminator(dialect);
		final boolean rollback = isRollbackOnly(query.getType());
		final boolean ignoreErrors = isIgnoreErrors(query.getType());
		final TransferRunnerCallback runner;
		if (tx.isReadOnly())
			runner = new NoExecuteTransferRunnerCallback(query.getStatement(), sqlGenerator, dialect, timeZone, transferer);
		else
			runner = new ExecuteTransferRunnerCallback(query.getStatement(), sqlGenerator, dialect, timeZone, transferer, eol, commitSize, monitor, rollback, ignoreErrors, preDMLStatement, postDMLStatement);
		final TransactionCallback cb = new ConnectionTxCallback(template, runner);
		
		final long start = timeService.getCurrentTime();
		
		Object ret = null;
		try	{
			ret = tx.execute(cb);
			}
		catch (TransactionException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		catch (DataAccessException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		
		if (monitor != null)
			monitor.getCommittedRows().progress(runner.getRowCount());
		
		final long end = timeService.getCurrentTime();
		
		final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, 0, RESULT_COLUMN_NAME, String.valueOf(ret), end - start);
		rs.setAffectedRows(runner.getTotalRowCount());
		return (rs);
		}
	
	@Override
	public Result transferRows(String link, Query query, TimeZone timeZone, RowInterpreter interpreter, int commitSize, DMLProgressMonitor monitor) throws PerformQueryException
		{
		final Result res = new ResultImpl(query);
		
		if (supports(query.getType()))
			{
			final TransactionTemplate tx = getTransactionTemplate(link, query.getType());
			if (tx.isReadOnly())
				throw new PerformQueryException("getTransactionTemplate", new RuntimeException("Link is read-only"));
			
			final JdbcTemplate template = dataAccessService.getUnlimitedJdbcTemplate(link);
			final SQLDialect dialect = dataAccessService.getSQLDialect(link);
			final String preDMLStatement = dataAccessService.getPreDMLStatement(link);
			final String postDMLStatement = dataAccessService.getPostDMLStatement(link);
			if (template != null)
				{
				try	{
					res.getRowSets().put(query.getName(), runScript(tx, template, dialect, query, timeZone, interpreter, commitSize, monitor, preDMLStatement, postDMLStatement));
					}
				catch (TransactionException e)
					{
					throw new PerformQueryException("performQueries", e);
					}
				catch (DataAccessException e)
					{
					throw new PerformQueryException("performQueries", e);
					}
				}
			}
		
		return (res);
		}
	
	private RowSet runScript(TransactionTemplate tx, JdbcTemplate template, SQLDialect dialect, Query query, TimeZone timeZone, RowInterpreter interpreter,
			int commitSize, DMLProgressMonitor monitor, String preDMLStatement, String postDMLStatement)
		{
		final String eol = getStatementTerminator(dialect);
		final boolean rollback = isRollbackOnly(query.getType());
		final boolean ignoreErrors = isIgnoreErrors(query.getType());
		
		final TransferRunnerCallback runner = new PreparedStatementTransferRunnerCallback(query.getStatement(), sqlGenerator, dialect, timeZone, interpreter, eol, commitSize, monitor, rollback, ignoreErrors, preDMLStatement, postDMLStatement);
		final TransactionCallback cb = new ConnectionTxCallback(template, runner);
		
		final long start = timeService.getCurrentTime();
		
		Object ret = null;
		try	{
			ret = tx.execute(cb);
			}
		catch (TransactionException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		catch (DataAccessException e)
			{
			ret = runner.getErrors() + e.getMessage() + "\n";
			}
		finally
			{
			if (monitor != null)
				monitor.getCommittedRows().progress(runner.getRowCount());
			}
		
		final long end = timeService.getCurrentTime();
		
		final RowSetImpl rs = resultBuilder.createSingletonRowSet(query, 0, RESULT_COLUMN_NAME, String.valueOf(ret), end - start);
		rs.setAffectedRows(runner.getTotalRowCount());
		return (rs);
		}
	
	private boolean isRollbackOnly(QueryType type)
		{
		return (type instanceof TestScriptQueryType);
		}
	
	private boolean isIgnoreErrors(QueryType type)
		{
		return ((type instanceof FaultTolerantScriptQueryType) || (type instanceof TestScriptQueryType));
		}
	
	private String getStatementTerminator(SQLDialect d)
		{
		return (d.dmlRequiresTerminator() ? d.getStatementTerminator() : null);
		}
	
	private TransactionTemplate getTransactionTemplate(String link, QueryType type)
		{
		final TransactionTemplate tx;
		if (isRollbackOnly(type))
			tx = dataAccessService.getTestTransactionTemplate(link);
		else
			tx = dataAccessService.getTransactionTemplate(link);
		
		return (tx);
		}
	}
