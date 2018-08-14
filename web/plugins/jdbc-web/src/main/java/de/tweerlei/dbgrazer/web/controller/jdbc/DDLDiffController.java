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
package de.tweerlei.dbgrazer.web.controller.jdbc;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.exception.CancelledByUserException;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowIterator;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowTransferer;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.impl.AsyncRowIterator;
import de.tweerlei.dbgrazer.query.model.impl.MonitoringStatementHandler;
import de.tweerlei.dbgrazer.query.model.impl.StatementWriter;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.CompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskDMLProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.service.jdbc.ResultCompareService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.SQLObjectDDLWriter;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.service.TimeService;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class DDLDiffController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String filter;
		private String connection2;
		private String catalog2;
		private String schema2;
		private String type;
		private boolean grant;
		private String mode;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			}
		
		/**
		 * Get the catalog
		 * @return the catalog
		 */
		public String getCatalog()
			{
			return catalog;
			}
		
		/**
		 * Set the catalog
		 * @param catalog the catalog to set
		 */
		public void setCatalog(String catalog)
			{
			this.catalog = catalog;
			}
		
		/**
		 * Get the schema
		 * @return the schema
		 */
		public String getSchema()
			{
			return schema;
			}
		
		/**
		 * Set the schema
		 * @param schema the schema to set
		 */
		public void setSchema(String schema)
			{
			this.schema = schema;
			}
		
		/**
		 * Get the filter
		 * @return the filter
		 */
		public String getFilter()
			{
			return filter;
			}
		
		/**
		 * Set the filter
		 * @param filter the filter to set
		 */
		public void setFilter(String filter)
			{
			this.filter = filter;
			}
		
		/**
		 * Get the connection2
		 * @return the connection2
		 */
		public String getConnection2()
			{
			return connection2;
			}
		
		/**
		 * Set the connection2
		 * @param connection2 the connection2 to set
		 */
		public void setConnection2(String connection2)
			{
			this.connection2 = connection2;
			}
		
		/**
		 * Get the catalog2
		 * @return the catalog2
		 */
		public String getCatalog2()
			{
			return catalog2;
			}
		
		/**
		 * Set the catalog2
		 * @param catalog2 the catalog2 to set
		 */
		public void setCatalog2(String catalog2)
			{
			this.catalog2 = catalog2;
			}
		
		/**
		 * Get the schema2
		 * @return the schema2
		 */
		public String getSchema2()
			{
			return schema2;
			}
		
		/**
		 * Set the schema2
		 * @param schema2 the schema2 to set
		 */
		public void setSchema2(String schema2)
			{
			this.schema2 = schema2;
			}
		
		/**
		 * Get the type
		 * @return the type
		 */
		public String getType()
			{
			return type;
			}
		
		/**
		 * Set the type
		 * @param type the type to set
		 */
		public void setType(String type)
			{
			this.type = type;
			}
		
		/**
		 * Get the grant
		 * @return the grant
		 */
		public boolean isGrant()
			{
			return grant;
			}
		
		/**
		 * Set the grant
		 * @param grant the grant to set
		 */
		public void setGrant(boolean grant)
			{
			this.grant = grant;
			}
		
		/**
		 * Get the mode
		 * @return the mode
		 */
		public String getMode()
			{
			return mode;
			}
		
		/**
		 * Set the mode
		 * @param mode the mode to set
		 */
		public void setMode(String mode)
			{
			this.mode = mode;
			}
		}
	
	/**
	 * Helper bean for diff results
	 */
	public static final class DiffResult
		{
		private final String sql;
		private final SortedMap<String, TaskProgress> comparisonResult;
		private final long duration;
		private final boolean moreAvailable;
		
		/**
		 * Constructor
		 * @param sql Generated SQL
		 * @param comparisonResult Comparison statistics
		 * @param duration Time taken in msec
		 * @param moreAvailable true if not all rows were compared
		 */
		public DiffResult(String sql, SortedMap<String, TaskProgress> comparisonResult, long duration, boolean moreAvailable)
			{
			this.sql = sql;
			this.comparisonResult = comparisonResult;
			this.duration = duration;
			this.moreAvailable = moreAvailable;
			}

		/**
		 * Get the sql
		 * @return the sql
		 */
		public String getSql()
			{
			return sql;
			}
		
		/**
		 * Get the comparisonResult
		 * @return the comparisonResult
		 */
		public SortedMap<String, TaskProgress> getComparisonResult()
			{
			return comparisonResult;
			}

		/**
		 * Get the duration
		 * @return the duration
		 */
		public long getDuration()
			{
			return duration;
			}
		
		/**
		 * Get the moreAvailable
		 * @return the moreAvailable
		 */
		public boolean isMoreAvailable()
			{
			return moreAvailable;
			}
		}
	
	private static final class RowProducer implements Runnable
		{
		private final QueryPerformerService runner;
		private final String connection;
		private final String statement;
		private final String label;
		private final RowHandler handler;
		
		public RowProducer(QueryPerformerService runner, String connection, String statement, String label, RowHandler handler)
			{
			this.runner = runner;
			this.connection = connection;
			this.statement = statement;
			this.label = label;
			this.handler = handler;
			}
		
		@Override
		public void run()
			{
			try	{
				runner.performCustomQuery(connection, JdbcConstants.QUERYTYPE_MULTIPLE, statement, label, handler);
				}
			catch (PerformQueryException e)
				{
				Logger.getLogger(getClass().getCanonicalName()).log(Level.WARNING, "RowProducer for " + connection + " failed", e.getCause());
				}
			}
		}
/*	
	private static final class RowConsumer implements Runnable
		{
		private final QueryPerformerService runner;
		private final String connection;
		private final String type;
		private final AsyncStatementIterator statements;
		private Result result;
		private RuntimeException error;
		
		public RowConsumer(QueryPerformerService runner, String connection, String type, AsyncStatementIterator statements)
			{
			this.runner = runner;
			this.connection = connection;
			this.type = type;
			this.statements = statements;
			}
		
		public Result getResult()
			{
			return (result);
			}
		
		public RuntimeException getError()
			{
			return (error);
			}
		
		@Override
		public void run()
			{
			try	{
				result = runner.performCustomQueries(connection, type, statements);
				}
			catch (PerformQueryException e)
				{
				statements.abort();
				error = e.getCause();
				throw error;
				}
			
			Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "RowConsumer for " + connection + " finished");
			}
		}
*/	
	private static final class DiffRowTransferer implements RowTransferer
		{
		private final ResultCompareService transformer;
		private final RowIterator dst;
		private final CompareProgressMonitor monitor;
		private final SQLDialect dialect;
		
		public DiffRowTransferer(ResultCompareService transformer, RowIterator dst, CompareProgressMonitor monitor, SQLDialect dialect)
			{
			this.transformer = transformer;
			this.dst = dst;
			this.monitor = monitor;
			this.dialect = dialect;
			}
		
		@Override
		public Object transfer(RowIterator rows, StatementHandler handler)
			{
			transformer.compareDDLSource(rows, dst, handler, monitor, dialect);
			return null;
			}
		
		@Override
		public String getPrepareStatement()
			{
			return (null);
			}
		
		@Override
		public String getCleanupStatement()
			{
			return (null);
			}
		}
	
	private static final class PrivRowTransferer implements RowTransferer
		{
		private final ResultCompareService transformer;
		private final RowIterator dst;
		private final CompareProgressMonitor monitor;
		private final SQLDialect dialect;
		
		public PrivRowTransferer(ResultCompareService transformer, RowIterator dst, CompareProgressMonitor monitor, SQLDialect dialect)
			{
			this.transformer = transformer;
			this.dst = dst;
			this.monitor = monitor;
			this.dialect = dialect;
			}
		
		@Override
		public Object transfer(RowIterator rows, StatementHandler handler)
			{
			transformer.compareDDLPrivileges(rows, dst, handler, monitor, dialect);
			return null;
			}
		
		@Override
		public String getPrepareStatement()
			{
			return (null);
			}
		
		@Override
		public String getCleanupStatement()
			{
			return (null);
			}
		}
	
	private final TimeService timeService;
	private final MetadataService metadataService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final LinkService linkService;
	private final UserSettingsManager userSettingsManager;
	private final TaskProgressService taskProgressService;
	private final DataFormatterFactory dataFormatterFactory;
	private final ResultCompareService transformer;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param queryService QueryService
	 * @param timeService TimeService
	 * @param runner QueryPerformerService
	 * @param linkService LinkService
	 * @param userSettingsManager UserSettingsManager
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param transformer ResultCompareService
	 * @param taskProgressService TaskProgressService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public DDLDiffController(MetadataService metadataService, QueryService queryService, QueryPerformerService runner,
			TimeService timeService, LinkService linkService, UserSettingsManager userSettingsManager,
			ResultCompareService transformer,
			DataFormatterFactory dataFormatterFactory, TaskProgressService taskProgressService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.timeService = timeService;
		this.metadataService = metadataService;
		this.queryService = queryService;
		this.runner = runner;
		this.linkService = linkService;
		this.userSettingsManager = userSettingsManager;
		this.dataFormatterFactory = dataFormatterFactory;
		this.taskProgressService = taskProgressService;
		this.transformer = transformer;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Get the FormBackingObject
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject()
		{
		final FormBackingObject ret = new FormBackingObject();
		
		return (ret);
		}
	
	/**
	 * Show the schema selection dialog
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/srccompare.html", method = RequestMethod.GET)
	public Map<String, Object> showDMLForm(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if ((fbo.getConnection2() != null) && (fbo.getCatalog2() != null) && (fbo.getSchema2() != null))
			{
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			connectionSettings.getParameterHistory().put("catalog2", fbo.getCatalog2());
			connectionSettings.getParameterHistory().put("schema2", fbo.getSchema2());
			}
		else
			{
			fbo.setConnection2(connectionSettings.getParameterHistory().get("connection2"));
			fbo.setCatalog2(connectionSettings.getParameterHistory().get("catalog2"));
			fbo.setSchema2(connectionSettings.getParameterHistory().get("schema2"));
			}
		
		fbo.setFilter(connectionSettings.getParameterHistory().get("filter"));
		fbo.setType(connectionSettings.getParameterHistory().get("type"));
		fbo.setGrant(Boolean.valueOf(connectionSettings.getParameterHistory().get("grant")));
		fbo.setMode(connectionSettings.getParameterHistory().get("mode"));
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
		model.put("allConnections", all);
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		if (fbo.getConnection2() != null)
			{
			model.put("catalogs", metadataService.getCatalogs(fbo.getConnection2()));
			if (fbo.getCatalog2() != null)
				model.put("schemas", metadataService.getSchemas(fbo.getConnection2()));
			}
		
		model.put("extensionJS", "jdbc.js");
		
		return (model);
		}
	
	/**
	 * Show the schema selection dialog
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/form-srccompare.html", method = RequestMethod.GET)
	public Map<String, Object> showDMLWSForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> all = linkService.findAllLinkNames(null, null, null);
		model.put("allConnections", all);
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/srccompare.html", method = RequestMethod.POST)
	public Map<String, Object> runDML(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model;
		
		final TaskDMLProgressMonitor p = taskProgressService.createDMLProgressMonitor();
		if (p == null)
			{
			model = new HashMap<String, Object>();
			model.put("alreadyRunning", Boolean.TRUE);
			model.put("progress", taskProgressService.getProgress());
			return (model);
			}
		final TaskCompareProgressMonitor c = taskProgressService.createCompareProgressMonitor();
		
		try	{
			model = runDMLInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getFilter(), fbo.getType(), fbo.getConnection2(), fbo.getCatalog2(), fbo.getSchema2(), fbo.isGrant(), fbo.getMode(), p, c);
			
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			connectionSettings.getParameterHistory().put("catalog2", fbo.getCatalog2());
			connectionSettings.getParameterHistory().put("schema2", fbo.getSchema2());
			connectionSettings.getParameterHistory().put("filter", fbo.getFilter());
			connectionSettings.getParameterHistory().put("type", fbo.getType());
			connectionSettings.getParameterHistory().put("grant", String.valueOf(fbo.isGrant()));
			connectionSettings.getParameterHistory().put("mode", fbo.getMode());
			}
		finally
			{
			taskProgressService.removeCompareProgressMonitor();
			taskProgressService.removeDMLProgressMonitor();
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/srccompare.html", method = RequestMethod.GET)
	public Map<String, Object> runDMLWS(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		final Map<String, Object> model;
		
		final TaskDMLProgressMonitor p = new TaskDMLProgressMonitor();
		final TaskCompareProgressMonitor c = new TaskCompareProgressMonitor();
		
		model = runDMLInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getFilter(), fbo.getType(), fbo.getConnection2(), fbo.getCatalog2(), fbo.getSchema2(), fbo.isGrant(), fbo.getMode(), p, c);
		
		return (model);
		}
	
	private Map<String, Object> runDMLInternal(
			String catalog,
			String schema,
			String table,
			String type,
			String conn2,
			String catalog2,
			String schema2,
			boolean grant,
			String mode,
			TaskDMLProgressMonitor p,
			TaskCompareProgressMonitor c
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalog", catalog);
		model.put("schema", schema);
		model.put("object", table);
		model.put("type", type);
		model.put("connection2", conn2);
		model.put("catalog2", catalog2);
		model.put("schema2", schema2);
		model.put("grant", grant);
		model.put("mode", mode);
		
		try	{
			final DiffResult result;
			if (!StringUtils.empty(mode) && connectionSettings.isWritable())
				{
				result = runCompareByPK(new QualifiedName(catalog, schema, table), new QualifiedName(catalog2, schema2, table), conn2, type, grant, mode, c, p);
				}
			else
				{
				result = compareByPK(new QualifiedName(catalog, schema, table), new QualifiedName(catalog2, schema2, table), conn2, type, grant, c, p);
				}
			
			model.put("result", result);
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (CancelledByUserException e)
			{
			model.put("cancelled", Boolean.TRUE);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	private String getHeader(String c1, String c2)
		{
		return (dataFormatterFactory.getMessage(MessageKeys.DML_COMPARE_HEADER, c1, c2));
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	
	private DiffResult compareByPK(QualifiedName srcName, QualifiedName dstName, String conn2, String type, boolean grant, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p) throws PerformQueryException
		{
		final SQLDialect dialect = getSQLDialect();
		
		final StringWriter sw = new StringWriter();
		final StatementHandler h3 = new MonitoringStatementHandler(new StatementWriter(sw, dialect.getStatementTerminator()), p.getTotalStatements());
		
		h3.comment(getHeader(connectionSettings.getLinkName(), conn2));
		
		final DiffResult tempResult;
		if (grant)
			tempResult = comparePrivileges(srcName, dstName, conn2, type, dialect, h3, JdbcConstants.QUERYTYPE_TOLERANT_SCRIPT, c, null);
		else
			tempResult = compareByPK(srcName, dstName, conn2, type, dialect, h3, JdbcConstants.QUERYTYPE_TOLERANT_SCRIPT, c, null);
		
		return (new DiffResult(sw.toString(), tempResult.getComparisonResult(), tempResult.getDuration(), tempResult.isMoreAvailable()));
		}
	
	private DiffResult runCompareByPK(QualifiedName srcName, QualifiedName dstName, String conn2, String type, boolean grant, String mode, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p) throws PerformQueryException
		{
		final SQLDialect dialect = getSQLDialect();
		
		if (grant)
			return (comparePrivileges(srcName, dstName, conn2, type, dialect, null, mode, c, p));
		else
			return (compareByPK(srcName, dstName, conn2, type, dialect, null, mode, c, p));
		}
	
	private DiffResult compareByPK(QualifiedName srcName, QualifiedName dstName, String conn2, String type, SQLDialect dialect, StatementHandler h3, String mode, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p) throws PerformQueryException
		{
		final SQLObjectDDLWriter writer = dialect.getObjectDDLWriter();
		final TableDescription diffDesc = writer.getFindObjectsTableDescription();
		
		final long start = timeService.getCurrentTime();
		
		final RowSet r1 = findObjects(writer, connectionSettings.getLinkName(), srcName, type);
		final RowSet r2 = findObjects(writer, conn2, dstName, type);
		
		// This will prefix all returned rows with ADDITION, DELETION or CHANGE,
		// so that the object name moves from column 2 to column 3
		final RowSet diff = transformer.compareResults(r1, r2, c, diffDesc, dialect);
		
		final StringBuilder sb = new StringBuilder();
		if (h3 == null)
			{
			// run
			for (ResultRow row : diff)
				{
				final QualifiedName srcObjName = new QualifiedName(srcName.getCatalogName(), srcName.getSchemaName(), (String) row.getValues().get(3));
				final QualifiedName dstObjName = new QualifiedName(dstName.getCatalogName(), dstName.getSchemaName(), (String) row.getValues().get(3));
				
				sb.append(dstObjName.getObjectName()).append("\n");
				
				final Result tmp = transferObject(writer, srcObjName, dstObjName, conn2, type, dialect, h3, mode, p, true);
				
				sb.append(tmp.getFirstRowSet().getFirstValue());
				}
			}
		else
			{
			// print
			for (ResultRow row : diff)
				h3.comment((String) row.getValues().get(0) + (String) row.getValues().get(3));
			}
		
		sb.append(diff.getRows().size()).append(" rows");
		
		final long end = timeService.getCurrentTime();
		
		final boolean moreAvailable = r1.isMoreAvailable() || r2.isMoreAvailable();
		
		return (new DiffResult(sb.toString(), collectStatistics(c, p), end - start, moreAvailable));
		}
	
	private RowSet findObjects(SQLObjectDDLWriter writer, String conn, QualifiedName srcName, String type) throws PerformQueryException
		{
		final String srcStmt = writer.findObjects(srcName.getCatalogName(), srcName.getSchemaName(), null, type);
		if (srcStmt == null)
			throw new UnsupportedOperationException("Operation not supported by dialect " + connectionSettings.getDialectName());
		
		final Result r = runner.performCustomQuery(conn, JdbcConstants.QUERYTYPE_MULTIPLE, srcStmt, null, null, "diff", true, null);
		final RowSet rs = r.getFirstRowSet();
		
		final String filter = srcName.getObjectName();
		if (!StringUtils.empty(filter))
			{
			try	{
				final Pattern p = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
				for (Iterator<ResultRow> i = rs.getRows().iterator(); i.hasNext(); )
					{
					if (!p.matcher((String) i.next().getValues().get(2)).matches())
						i.remove();
					}
				}
			catch (RuntimeException e)
				{
				// Filter string might not be a valid RegExp
				final String flt = filter.toLowerCase();
				for (Iterator<ResultRow> i = rs.getRows().iterator(); i.hasNext(); )
					{
					if (!((String) i.next().getValues().get(2)).toLowerCase().equals(flt))
						i.remove();
					}
				}
			}
		
		return (rs);
		}
	
	private Result transferObject(SQLObjectDDLWriter writer, QualifiedName srcName, QualifiedName dstName, String conn2, String type, SQLDialect dialect, StatementHandler h3, String mode, TaskDMLProgressMonitor p, boolean export) throws PerformQueryException
		{
		final String srcStmt = writer.findObjectSource(srcName.getCatalogName(), srcName.getSchemaName(), srcName.getObjectName(), type);
		if (srcStmt == null)
			throw new UnsupportedOperationException("Operation not supported by dialect " + connectionSettings.getDialectName());
		
		final String dstStmt = writer.findObjectSource(dstName.getCatalogName(), dstName.getSchemaName(), dstName.getObjectName(), type);
		if (dstStmt == null)
			throw new UnsupportedOperationException("Operation not supported by dialect " + connectionSettings.getDialectName());
		
		final AsyncRowIterator h2 = new AsyncRowIterator();
		final RowProducer p2 = new RowProducer(runner, conn2, dstStmt, "diff", h2);
		final Thread dst = new Thread(p2);
		
		dst.start();
		
		final Result res;
		try	{
			final CompareProgressMonitor c = new TaskCompareProgressMonitor();
			final DiffRowTransferer transferer = new DiffRowTransferer(transformer, h2, c, dialect);
			res = runner.transferRows(connectionSettings.getLinkName(), srcStmt, transferer, h3, mode, p, export);
			}
		finally
			{
			try	{
				h2.abort();
				dst.interrupt();
				dst.join();
				}
			catch (InterruptedException e)
				{
				logger.log(Level.SEVERE, "Error joining RowProducer for " + conn2, e);
	//			throw new RuntimeException(e);
				}
			}
		
		return (res);
		}
	
	private DiffResult comparePrivileges(QualifiedName srcName, QualifiedName dstName, String conn2, String type, SQLDialect dialect, StatementHandler h3, String mode, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p) throws PerformQueryException
		{
		final SQLObjectDDLWriter writer = dialect.getObjectDDLWriter();
		
		final long start = timeService.getCurrentTime();
		
		final Result tmp = transferPrivileges(writer, srcName, dstName, conn2, type, dialect, h3, mode, c, p, true);
		
		final long end = timeService.getCurrentTime();
		
		return (new DiffResult(String.valueOf(tmp.getFirstRowSet().getFirstValue()), collectStatistics(c, p), end - start, false));
		}
	
	private Result transferPrivileges(SQLObjectDDLWriter writer, QualifiedName srcName, QualifiedName dstName, String conn2, String type, SQLDialect dialect, StatementHandler h3, String mode, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p, boolean export) throws PerformQueryException
		{
		final String srcStmt = writer.findObjectPrivileges(srcName.getCatalogName(), srcName.getSchemaName(), srcName.getObjectName(), type);
		if (srcStmt == null)
			throw new UnsupportedOperationException("Operation not supported by dialect " + connectionSettings.getDialectName());
		
		final String dstStmt = writer.findObjectPrivileges(dstName.getCatalogName(), dstName.getSchemaName(), dstName.getObjectName(), type);
		if (dstStmt == null)
			throw new UnsupportedOperationException("Operation not supported by dialect " + connectionSettings.getDialectName());
		
		final AsyncRowIterator h2 = new AsyncRowIterator();
		final RowProducer p2 = new RowProducer(runner, conn2, dstStmt, "diff", h2);
		final Thread dst = new Thread(p2);
		
		dst.start();
		
		final Result res;
		try	{
			final PrivRowTransferer transferer = new PrivRowTransferer(transformer, h2, c, dialect);
			res = runner.transferRows(connectionSettings.getLinkName(), srcStmt, transferer, h3, mode, p, export);
			}
		finally
			{
			try	{
				h2.abort();
				dst.interrupt();
				dst.join();
				}
			catch (InterruptedException e)
				{
				logger.log(Level.SEVERE, "Error joining RowProducer for " + conn2, e);
	//			throw new RuntimeException(e);
				}
			}
		
		return (res);
		}
	
	private SortedMap<String, TaskProgress> collectStatistics(TaskCompareProgressMonitor c, TaskDMLProgressMonitor p)
		{
//		return (taskProgressService.getProgress());
		final SortedMap<String, TaskProgress> ret = new TreeMap<String, TaskProgress>();
		ret.put(MessageKeys.SOURCE_ROWS, c.getSourceRows());
		ret.put(MessageKeys.DESTINATION_ROWS, c.getDestinationRows());
		ret.put(MessageKeys.MATCHED, c.getMatchedRows());
		ret.put(MessageKeys.INSERTED, c.getInsertedRows());
		ret.put(MessageKeys.UPDATED, c.getUpdatedRows());
		ret.put(MessageKeys.DELETED, c.getDeletedRows());
		if (p != null)
			{
			ret.put(MessageKeys.TOTAL_STATEMENTS, p.getTotalStatements());
			ret.put(MessageKeys.FAILED_STATEMENTS, p.getFailedStatements());
			ret.put(MessageKeys.TOTAL_ROWS, p.getTotalRows());
			ret.put(MessageKeys.COMMITTED_ROWS, p.getCommittedRows());
			}
		return (ret);
		}
	}
