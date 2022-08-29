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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Joins;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.OrderBy;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.exception.CancelledByUserException;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultRowMapper;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowInterpreter;
import de.tweerlei.dbgrazer.query.model.RowIterator;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.impl.AsyncRowIterator;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.IdentityResultRowMapper;
import de.tweerlei.dbgrazer.query.model.impl.MonitoringStatementHandler;
import de.tweerlei.dbgrazer.query.model.impl.StatementCollection;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.model.CompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.StatementWriter;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskDMLProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.service.jdbc.BrowserSettingsManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.ResultCompareService;
import de.tweerlei.dbgrazer.web.service.jdbc.ResultCompareService.CompareFlags;
import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableFilterEntry;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.service.TimeService;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class DataDiffController
	{
	/**
	 * Run mode
	 */
	public static enum RunMode
		{
		/** Preview */
		PREVIEW,
		/** Single statement */
		SINGLE,
		/** Batch */
		BATCH
		}
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String catalog;
		private String schema;
		private String object;
		private String connection2;
		private String catalog2;
		private String schema2;
		private String filter;
		private RunMode runMode;
		private OrderBy order;
		private boolean useInsert;
		private boolean useUpdate;
		private boolean useDelete;
		private boolean useMerge;
		private String execMode;
		private String[] pkColumns;
		private String[] dataColumns;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			order = OrderBy.PK;
			runMode = RunMode.PREVIEW;
			useInsert = true;
			useUpdate = true;
			useDelete = true;
			useMerge = false;
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
		 * Get the object
		 * @return the object
		 */
		public String getObject()
			{
			return object;
			}
		
		/**
		 * Set the object
		 * @param object the object to set
		 */
		public void setObject(String object)
			{
			this.object = object;
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
		 * Get the order
		 * @return the order
		 */
		public OrderBy getOrder()
			{
			return order;
			}
		
		/**
		 * Set the order
		 * @param order the order to set
		 */
		public void setOrder(OrderBy order)
			{
			this.order = order;
			}
		
		/**
		 * Get the runMode
		 * @return the runMode
		 */
		public RunMode getRunMode()
			{
			return runMode;
			}
		
		/**
		 * Set the runMode
		 * @param runMode the runMode to set
		 */
		public void setRunMode(RunMode runMode)
			{
			this.runMode = runMode;
			}
		
		/**
		 * Get the execMode
		 * @return the execMode
		 */
		public String getExecMode()
			{
			return execMode;
			}
		
		/**
		 * Set the execMode
		 * @param execMode the execMode to set
		 */
		public void setExecMode(String execMode)
			{
			this.execMode = execMode;
			}
		
		/**
		 * Get the pkColumns
		 * @return the pkColumns
		 */
		public String[] getPkColumns()
			{
			return pkColumns;
			}
		
		/**
		 * Set the pkColumns
		 * @param pkColumns the pkColumns to set
		 */
		public void setPkColumns(String[] pkColumns)
			{
			this.pkColumns = pkColumns;
			}
		
		/**
		 * Get the dataColumns
		 * @return the dataColumns
		 */
		public String[] getDataColumns()
			{
			return dataColumns;
			}
		
		/**
		 * Set the dataColumns
		 * @param dataColumns the dataColumns to set
		 */
		public void setDataColumns(String[] dataColumns)
			{
			this.dataColumns = dataColumns;
			}

		/**
		 * Get the useInsert
		 * @return the useInsert
		 */
		public boolean isUseInsert()
			{
			return useInsert;
			}
		
		/**
		 * Set the useInsert
		 * @param useInsert the useInsert to set
		 */
		public void setUseInsert(boolean useInsert)
			{
			this.useInsert = useInsert;
			}
		
		/**
		 * Get the useUpdate
		 * @return the useUpdate
		 */
		public boolean isUseUpdate()
			{
			return useUpdate;
			}
		
		/**
		 * Set the useUpdate
		 * @param useUpdate the useUpdate to set
		 */
		public void setUseUpdate(boolean useUpdate)
			{
			this.useUpdate = useUpdate;
			}

		/**
		 * Get the useDelete
		 * @return the useDelete
		 */
		public boolean isUseDelete()
			{
			return useDelete;
			}
		
		/**
		 * Set the useDelete
		 * @param useDelete the useDelete to set
		 */
		public void setUseDelete(boolean useDelete)
			{
			this.useDelete = useDelete;
			}
		
		/**
		 * Get the useMerge
		 * @return the useMerge
		 */
		public boolean isUseMerge()
			{
			return useMerge;
			}
		
		/**
		 * Set the useMerge
		 * @param useMerge the useMerge to set
		 */
		public void setUseMerge(boolean useMerge)
			{
			this.useMerge = useMerge;
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
		private final TimeZone timeZone;
		private final RowHandler handler;
		
		public RowProducer(QueryPerformerService runner, String connection, String statement, String label, TimeZone timeZone, RowHandler handler)
			{
			this.runner = runner;
			this.connection = connection;
			this.statement = statement;
			this.label = label;
			this.timeZone = timeZone;
			this.handler = handler;
			}
		
		@Override
		public void run()
			{
			try	{
				runner.performCustomQuery(connection, JdbcConstants.QUERYTYPE_MULTIPLE, statement, label, timeZone, handler);
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
	private static final class DiffRowTransferer implements StatementProducer
		{
		private final ResultCompareService transformer;
		private final RowIterator src;
		private final RowIterator dst;
		private final CompareProgressMonitor monitor;
		private final TableDescription tableDesc;
		private final OrderBy orderBy;
		private final SQLDialect dialect;
		private final CompareFlags flags;
		
		public DiffRowTransferer(ResultCompareService transformer, RowIterator src, RowIterator dst, CompareProgressMonitor monitor, TableDescription tableDesc, OrderBy orderBy, SQLDialect dialect, CompareFlags flags)
			{
			this.transformer = transformer;
			this.src = src;
			this.dst = dst;
			this.monitor = monitor;
			this.tableDesc = tableDesc;
			this.dialect = dialect;
			this.flags = flags;
			this.orderBy = orderBy;
			}
		
		@Override
		public void produceStatements(StatementHandler handler)
			{
			transformer.compareResultsByPK(src, dst, handler, monitor, tableDesc, dialect, orderBy, flags);
			}
		
		@Override
		public String getPrepareStatement()
			{
			return (dialect.prepareInsert(tableDesc));
			}
		
		@Override
		public String getCleanupStatement()
			{
			return (dialect.finishInsert(tableDesc));
			}
		}
	
	// UPDATE SET <non-pk-columns> WHERE <pk-columns>
	private static final class UpdateResultRowMapper implements ResultRowMapper
		{
		private final Set<Integer> pk;
		private final ResultRow ret;
		
		public UpdateResultRowMapper(Set<Integer> pk)
			{
			this.pk = pk;
			this.ret = new DefaultResultRow();
			}
		
		@Override
		public ResultRow map(ResultRow row)
			{
			final List<Object> values = row.getValues();
			final List<Object> out = ret.getValues();
			out.clear();
			for (int i = 0; i < values.size(); i++)
				{
				if (!pk.contains(i))
					out.add(values.get(i));
				}
			for (Integer i : pk)
				out.add(values.get(i));
			return (ret);
			}
		
		@Override
		public List<ColumnDef> map(List<ColumnDef> columns)
			{
			final List<ColumnDef> r = new ArrayList<ColumnDef>(pk.size());
			for (int i = 0; i < columns.size(); i++)
				{
				if (!pk.contains(i))
					r.add(columns.get(i));
				}
			for (Integer i : pk)
				r.add(columns.get(i));
			return (r);
			}
		}
	
	// DELETE WHERE <pk-columns>
	private static final class DeleteResultRowMapper implements ResultRowMapper
		{
		private final Set<Integer> pk;
		private final ResultRow ret;
		
		public DeleteResultRowMapper(Set<Integer> pk)
			{
			this.pk = pk;
			this.ret = new DefaultResultRow(pk.size());
			}
		
		@Override
		public ResultRow map(ResultRow row)
			{
			ret.getValues().clear();
			for (Integer i : pk)
				ret.getValues().add(row.getValues().get(i));
			return (ret);
			}
		
		@Override
		public List<ColumnDef> map(List<ColumnDef> columns)
			{
			final List<ColumnDef> r = new ArrayList<ColumnDef>(pk.size());
			for (Integer i : pk)
				r.add(columns.get(i));
			return (r);
			}
		}
	
	private static final class DiffRowInterpreter implements RowInterpreter
		{
		private final ResultCompareService transformer;
		private final RowIterator src;
		private final RowIterator dst;
		private final CompareProgressMonitor monitor;
		private final TableDescription tableDesc;
		private final List<RowHandlerDef> statements;
		private final SQLDialect dialect;
		
		public DiffRowInterpreter(ResultCompareService transformer, RowIterator src, RowIterator dst, CompareProgressMonitor monitor, TableDescription tableDesc, List<ColumnDef> columns, String insert, String update, String delete, SQLDialect dialect)
			{
			this.transformer = transformer;
			this.src = src;
			this.dst = dst;
			this.monitor = monitor;
			this.tableDesc = tableDesc;
			this.statements = new ArrayList<RowHandlerDef>(3);
			this.statements.add(new RowHandlerDef(insert, columns, new IdentityResultRowMapper()));
			this.statements.add(new RowHandlerDef(update, columns, new UpdateResultRowMapper(tableDesc.getPKColumns())));
			this.statements.add(new RowHandlerDef(delete, columns, new DeleteResultRowMapper(tableDesc.getPKColumns())));
			this.dialect = dialect;
			}
		
		@Override
		public void produceRows(List<RowHandler> handlers)
			{
			transformer.compareResultsByPK(src, dst, handlers.get(0), handlers.get(1), handlers.get(2), monitor, tableDesc, dialect);
			}
		
		@Override
		public List<RowHandlerDef> getStatements()
			{
			return (statements);
			}
		
		@Override
		public String getPrepareStatement()
			{
			return (dialect.prepareInsert(tableDesc));
			}
		
		@Override
		public String getCleanupStatement()
			{
			return (dialect.finishInsert(tableDesc));
			}
		}
	
	private final TimeService timeService;
	private final MetadataService metadataService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final LinkService linkService;
	private final UserSettingsManager userSettingsManager;
	private final TaskProgressService taskProgressService;
	private final SQLGeneratorService sqlGenerator;
	private final DataFormatterFactory dataFormatterFactory;
	private final ResultCompareService transformer;
	private final BrowserSettingsManagerService browserSettingsManager;
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
	 * @param sqlGenerator SQLGeneratorService
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param transformer ResultCompareService
	 * @param taskProgressService TaskProgressService
	 * @param browserSettingsManager BrowserSettingsManagerService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public DataDiffController(MetadataService metadataService, QueryService queryService, QueryPerformerService runner,
			TimeService timeService, LinkService linkService, UserSettingsManager userSettingsManager,
			SQLGeneratorService sqlGenerator, ResultCompareService transformer, BrowserSettingsManagerService browserSettingsManager,
			DataFormatterFactory dataFormatterFactory, TaskProgressService taskProgressService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.timeService = timeService;
		this.metadataService = metadataService;
		this.queryService = queryService;
		this.runner = runner;
		this.linkService = linkService;
		this.userSettingsManager = userSettingsManager;
		this.sqlGenerator = sqlGenerator;
		this.dataFormatterFactory = dataFormatterFactory;
		this.taskProgressService = taskProgressService;
		this.browserSettingsManager = browserSettingsManager;
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
	
	private static String[] toArray(Collection<String> c)
		{
		return (c.toArray(new String[c.size()]));
		}
	
	private static Set<String> toSet(String[] a)
		{
		final Set<String> ret = new HashSet<String>();
		if (a != null)
			{
			for (String t : a)
				ret.add(t);
			}
		return (ret);
		}
	
	/**
	 * Show the schema selection dialog
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/dml.html", method = RequestMethod.GET)
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
		
		if (connectionSettings.getParameterHistory().get("order") != null)
			fbo.setOrder(OrderBy.valueOf(connectionSettings.getParameterHistory().get("order")));
		if (connectionSettings.getParameterHistory().get("runMode") != null)
			fbo.setRunMode(RunMode.valueOf(connectionSettings.getParameterHistory().get("runMode")));
		if (connectionSettings.getParameterHistory().get("useInsert") != null)
			fbo.setUseInsert(Boolean.valueOf(connectionSettings.getParameterHistory().get("useInsert")));
		if (connectionSettings.getParameterHistory().get("useUpdate") != null)
			fbo.setUseUpdate(Boolean.valueOf(connectionSettings.getParameterHistory().get("useUpdate")));
		if (connectionSettings.getParameterHistory().get("useDelete") != null)
			fbo.setUseDelete(Boolean.valueOf(connectionSettings.getParameterHistory().get("useDelete")));
		if (connectionSettings.getParameterHistory().get("useMerge") != null)
			fbo.setUseMerge(Boolean.valueOf(connectionSettings.getParameterHistory().get("useMerge")));
		fbo.setExecMode(connectionSettings.getParameterHistory().get("mode"));
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
		model.put("allConnections", all);
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		model.put("runModes", RunMode.values());
		model.put("orders", OrderBy.values());
		
		final QualifiedName qn = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		final TableFilterEntry filter = browserSettingsManager.getTableFilters().get(qn.toString());
		if (filter != null)
			fbo.setFilter(filter.getWhere());
		
		final TableDescription srcDesc = metadataService.getTableInfo(connectionSettings.getLinkName(), qn, ColumnMode.ALL);
		final Set<Integer> pk = srcDesc.getPKColumns();
		
		final List<String> pkColumns = new ArrayList<String>(srcDesc.getColumns().size());
		final List<String> dataColumns = new ArrayList<String>(srcDesc.getColumns().size());
		int i = 0;
		for (ColumnDescription c : srcDesc.getColumns())
			{
			if (pk.contains(i))
				pkColumns.add(c.getName());
			else
				dataColumns.add(c.getName());
			i++;
			}
		fbo.setPkColumns(toArray(pkColumns));
		fbo.setDataColumns(toArray(dataColumns));
		
		model.put("allColumns", srcDesc.getColumns());
		
		if (fbo.getConnection2() != null)
			{
			model.put("catalogs", metadataService.getCatalogs(fbo.getConnection2()));
			if (fbo.getCatalog2() != null)
				model.put("schemas", metadataService.getSchemas(fbo.getConnection2(), fbo.getCatalog2()));
			}
		
		model.put("extensionJS", JdbcMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show the schema selection dialog
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/form-dml.html", method = RequestMethod.GET)
	public Map<String, Object> showDMLWSForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> all = linkService.findAllLinkNames(null, null, null);
		model.put("allConnections", all);
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		model.put("runModes", RunMode.values());
		model.put("orders", OrderBy.values());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dml.html", method = RequestMethod.POST)
	public Map<String, Object> runDML(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final TaskDMLProgressMonitor p = taskProgressService.createDMLProgressMonitor();
		if (p == null)
			{
			model.put("alreadyRunning", Boolean.TRUE);
			model.put("progress", taskProgressService.getProgress());
			return (model);
			}
		final TaskCompareProgressMonitor c = taskProgressService.createCompareProgressMonitor();
		
		final QualifiedName qn = new QualifiedName(fbo.getCatalog(), fbo.getSchema(), fbo.getObject());
		
		try	{
			final DiffResult result = runDMLInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getObject(),
					fbo.getConnection2(), fbo.getCatalog2(), fbo.getSchema2(),
					fbo.getFilter(), fbo.getOrder(), fbo.getRunMode(), fbo.getExecMode(),
					new CompareFlags(fbo.isUseInsert(), fbo.isUseUpdate(), fbo.isUseDelete(), fbo.isUseMerge()),
					toSet(fbo.getPkColumns()), toSet(fbo.getDataColumns()),
					p, c, false);
			
			model.put("result", result);
			
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			connectionSettings.getParameterHistory().put("catalog2", fbo.getCatalog2());
			connectionSettings.getParameterHistory().put("schema2", fbo.getSchema2());
			connectionSettings.getParameterHistory().put("order", (fbo.getOrder() == null) ? null : fbo.getOrder().name());
			connectionSettings.getParameterHistory().put("runMode", (fbo.getRunMode() == null) ? null : fbo.getRunMode().name());
			connectionSettings.getParameterHistory().put("useInsert", Boolean.toString(fbo.isUseInsert()));
			connectionSettings.getParameterHistory().put("useUpdate", Boolean.toString(fbo.isUseUpdate()));
			connectionSettings.getParameterHistory().put("useDelete", Boolean.toString(fbo.isUseDelete()));
			connectionSettings.getParameterHistory().put("useMerge", Boolean.toString(fbo.isUseMerge()));
			connectionSettings.getParameterHistory().put("mode", fbo.getExecMode());
			
			final TableFilterEntry ent = browserSettingsManager.getTableFilters().get(qn.toString());
			if (ent != null)
				browserSettingsManager.getTableFilters().put(qn.toString(), new TableFilterEntry(fbo.getFilter(), ent.getOrderBy()));
			else
				browserSettingsManager.getTableFilters().put(qn.toString(), new TableFilterEntry(fbo.getFilter(), ""));
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
	@RequestMapping(value = "/ws/*/dml.html", method = RequestMethod.GET)
	public Map<String, Object> runDMLWS(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final TaskDMLProgressMonitor p = new TaskDMLProgressMonitor();
		final TaskCompareProgressMonitor c = new TaskCompareProgressMonitor();
		
		try	{
			final DiffResult result = runDMLInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getObject(),
					fbo.getConnection2(), fbo.getCatalog2(), fbo.getSchema2(),
					fbo.getFilter(), fbo.getOrder(), fbo.getRunMode(), fbo.getExecMode(),
					new CompareFlags(fbo.isUseInsert(), fbo.isUseUpdate(), fbo.isUseDelete(), fbo.isUseMerge()),
					toSet(fbo.getPkColumns()), toSet(fbo.getDataColumns()),
					p, c, true);
			
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
	
	private DiffResult runDMLInternal(
			String catalog,
			String schema,
			String table,
			String conn2,
			String catalog2,
			String schema2,
			String filter,
			OrderBy order,
			RunMode runMode,
			String mode,
			CompareFlags flags,
			Set<String> pkColumns,
			Set<String> dataColumns,
			TaskDMLProgressMonitor p,
			TaskCompareProgressMonitor c,
			boolean flush
			) throws PerformQueryException
		{
		if ((runMode != RunMode.PREVIEW) && !StringUtils.empty(mode) && connectionSettings.isWritable())
			{
			if (order == OrderBy.NONE)
				return (runCompareFull(new QualifiedName(catalog, schema, table), new QualifiedName(catalog2, schema2, table), conn2, filter, flags, mode, c, p, flush));
			else
				return (runCompareByPK(new QualifiedName(catalog, schema, table), new QualifiedName(catalog2, schema2, table), conn2, filter, runMode, flags, mode, pkColumns, dataColumns, order, c, p, flush));
			}
		else
			{
			if (order == OrderBy.NONE)
				return (compareFull(new QualifiedName(catalog, schema, table), new QualifiedName(catalog2, schema2, table), conn2, filter, flags, c, p, flush));
			else
				return (compareByPK(new QualifiedName(catalog, schema, table), new QualifiedName(catalog2, schema2, table), conn2, filter, runMode, flags, pkColumns, dataColumns, order, c, p, flush));
			}
		}
	
	private DiffResult compareFull(QualifiedName srcName, QualifiedName dstName, String conn2, String filter, CompareFlags flags, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p, boolean flush) throws PerformQueryException
		{
		if (flush)
			metadataService.flushCache(connectionSettings.getLinkName());
		
		final TableDescription srcDesc = metadataService.getTableInfo(connectionSettings.getLinkName(), srcName, ColumnMode.SORTED);
		// Don't read destination table description because we want to select the same columns (and fail if that's not possible) 
		final TableDescription dstDesc = new TableDescription(dstName.getCatalogName(), dstName.getSchemaName(), dstName.getObjectName(), srcDesc.getComment(), srcDesc.getType(), srcDesc.getPrimaryKey(), srcDesc.getColumns(), srcDesc.getIndices(), srcDesc.getReferencedKeys(), srcDesc.getReferencingKeys(), srcDesc.getPrivileges());
		
		final SQLDialect dialect = getSQLDialect();
		final String prepare = dialect.prepareInsert(dstDesc);
		final String cleanup = dialect.finishInsert(dstDesc);
		
		final StringWriter sw = new StringWriter();
		final StatementHandler h = new MonitoringStatementHandler(new StatementWriter(sw, dialect.getScriptStatementWrapper()), p.getTotalStatements());
		
		h.comment(getHeader(connectionSettings.getLinkName(), conn2));
		
		if (!StringUtils.empty(prepare))
			h.statement(prepare);
		
		final DiffResult tempResult = compareFull(srcDesc, dstDesc, conn2, dialect, filter, flags, h, c, false);
		
		if (!StringUtils.empty(cleanup))
			h.statement(cleanup);
		
		return (new DiffResult(sw.toString(), tempResult.getComparisonResult(), tempResult.getDuration(), tempResult.isMoreAvailable()));
		}
	
	private DiffResult runCompareFull(QualifiedName srcName, QualifiedName dstName, String conn2, String filter, CompareFlags flags, String mode, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p, boolean flush) throws PerformQueryException
		{
		if (flush)
			metadataService.flushCache(connectionSettings.getLinkName());
		
		final TableDescription srcDesc = metadataService.getTableInfo(connectionSettings.getLinkName(), srcName, ColumnMode.SORTED);
		// Don't read destination table description because we want to select the same columns (and fail if that's not possible) 
		final TableDescription dstDesc = new TableDescription(dstName.getCatalogName(), dstName.getSchemaName(), dstName.getObjectName(), srcDesc.getComment(), srcDesc.getType(), srcDesc.getPrimaryKey(), srcDesc.getColumns(), srcDesc.getIndices(), srcDesc.getReferencedKeys(), srcDesc.getReferencingKeys(), srcDesc.getPrivileges());
		
		final SQLDialect dialect = getSQLDialect();
		final String prepare = dialect.prepareInsert(dstDesc);
		final String cleanup = dialect.finishInsert(dstDesc);
		
		final StatementCollection h = new StatementCollection(prepare, cleanup);
		
		final DiffResult tempResult = compareFull(srcDesc, dstDesc, conn2, dialect, filter, flags, h, c, true);
		
		final Result r = runner.performCustomQueries(connectionSettings.getLinkName(), h, mode, p);
		
		return (new DiffResult(String.valueOf(r.getFirstRowSet().getFirstValue()), tempResult.getComparisonResult(), tempResult.getDuration(), tempResult.isMoreAvailable()));
		}
	
	private DiffResult compareFull(TableDescription srcDesc, TableDescription dstDesc, String conn2, SQLDialect dialect, String filter, CompareFlags flags, StatementHandler h, TaskCompareProgressMonitor c, boolean export) throws PerformQueryException
		{
		final long start = timeService.getCurrentTime();
		
		final String srcStmt = sqlGenerator.generateSelect(srcDesc, Style.SIMPLE, Joins.NONE, filter, OrderBy.PK, dialect);
		final Result r1 = runner.performCustomQuery(connectionSettings.getLinkName(), JdbcConstants.QUERYTYPE_MULTIPLE, srcStmt, null, null, "diff", export, null);
		
		final String dstStmt = sqlGenerator.generateSelect(dstDesc, Style.SIMPLE, Joins.NONE, filter, OrderBy.PK, dialect);
		final Result r2 = runner.performCustomQuery(conn2, JdbcConstants.QUERYTYPE_MULTIPLE, dstStmt, null, null, "diff", export, null);
		
		transformer.compareResults(r1.getFirstRowSet(), r2.getFirstRowSet(), h, c, srcDesc, dialect, flags);
		
		final long end = timeService.getCurrentTime();
		
		final boolean moreAvailable = r1.getFirstRowSet().isMoreAvailable() || r2.getFirstRowSet().isMoreAvailable();
		
		return (new DiffResult(null, collectStatistics(c, null), end - start, moreAvailable));
		}
	
	private String getHeader(String c1, String c2)
		{
		return (dataFormatterFactory.getMessage(JdbcMessageKeys.DML_COMPARE_HEADER, c1, c2));
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	
	private DiffResult compareByPK(QualifiedName srcName, QualifiedName dstName, String conn2, String filter, RunMode runMode, CompareFlags flags, Set<String> pkColumns, Set<String> dataColumns, OrderBy order, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p, boolean flush) throws PerformQueryException
		{
		final SQLDialect dialect = getSQLDialect();
		
		if (flush)
			metadataService.flushCache(connectionSettings.getLinkName());
		
		final TableDescription origDesc = metadataService.getTableInfo(connectionSettings.getLinkName(), srcName, ColumnMode.SORTED);
		final TableDescription srcDesc = filterColumns(origDesc, pkColumns, dataColumns);
		// Don't read destination table description because we want to select the same columns (and fail if that's not possible) 
		final TableDescription dstDesc = new TableDescription(dstName.getCatalogName(), dstName.getSchemaName(), dstName.getObjectName(), srcDesc.getComment(), srcDesc.getType(), srcDesc.getPrimaryKey(), srcDesc.getColumns(), srcDesc.getIndices(), srcDesc.getReferencedKeys(), srcDesc.getReferencingKeys(), srcDesc.getPrivileges());
		
		final String prepare = dialect.prepareInsert(dstDesc);
		final String cleanup = dialect.finishInsert(dstDesc);
		
		final StringWriter sw = new StringWriter();
		final StatementHandler h3 = new MonitoringStatementHandler(new StatementWriter(sw, dialect.getScriptStatementWrapper()), p.getTotalStatements());
		
		h3.comment(getHeader(connectionSettings.getLinkName(), conn2));
		
		if (!StringUtils.empty(prepare))
			h3.statement(prepare);
		
		final DiffResult tempResult;
		if (runMode == RunMode.BATCH)
			{
			h3.comment("Preview not supported for BATCH mode");
			tempResult = new DiffResult(null, Collections.<String, TaskProgress>emptySortedMap(), 0, false);
			}
		else
			tempResult = compareByPK(srcDesc, dstDesc, conn2, dialect, filter, runMode, flags, order, h3, JdbcConstants.QUERYTYPE_TOLERANT_SCRIPT, c, null, false);
		
		if (!StringUtils.empty(cleanup))
			h3.statement(cleanup);
		
		return (new DiffResult(sw.toString(), tempResult.getComparisonResult(), tempResult.getDuration(), tempResult.isMoreAvailable()));
		}
	
	private DiffResult runCompareByPK(QualifiedName srcName, QualifiedName dstName, String conn2, String filter, RunMode runMode, CompareFlags flags, String mode, Set<String> pkColumns, Set<String> dataColumns, OrderBy order, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p, boolean flush) throws PerformQueryException
		{
		final SQLDialect dialect = getSQLDialect();
		
		if (flush)
			metadataService.flushCache(connectionSettings.getLinkName());
		
		final TableDescription origDesc = metadataService.getTableInfo(connectionSettings.getLinkName(), srcName, ColumnMode.SORTED);
		final TableDescription srcDesc = filterColumns(origDesc, pkColumns, dataColumns);
		// Don't read destination table description because we want to select the same columns (and fail if that's not possible) 
		final TableDescription dstDesc = new TableDescription(dstName.getCatalogName(), dstName.getSchemaName(), dstName.getObjectName(), srcDesc.getComment(), srcDesc.getType(), srcDesc.getPrimaryKey(), srcDesc.getColumns(), srcDesc.getIndices(), srcDesc.getReferencedKeys(), srcDesc.getReferencingKeys(), srcDesc.getPrivileges());
		
		return (compareByPK(srcDesc, dstDesc, conn2, dialect, filter, runMode, flags, order, null, mode, c, p, true));
		}
	
	private TableDescription filterColumns(TableDescription srcDesc, Set<String> pkColumns, Set<String> dataColumns)
		{
		if ((pkColumns == null) || (dataColumns == null) || (pkColumns.isEmpty() && dataColumns.isEmpty()))
			return (srcDesc);
		
		final List<String> pkCols = new ArrayList<String>();
		final List<ColumnDescription> columns = new ArrayList<ColumnDescription>();
		
		for (ColumnDescription c : srcDesc.getColumns())
			{
			if (pkColumns.contains(c.getName()))
				{
				pkCols.add(c.getName());
				columns.add(c);
				}
			else if (dataColumns.contains(c.getName()))
				{
				columns.add(c);
				}
			}
		
		final PrimaryKeyDescription pk = new PrimaryKeyDescription(null, pkCols);
		
		return (new TableDescription(srcDesc.getName().getCatalogName(), srcDesc.getName().getSchemaName(), srcDesc.getName().getObjectName(), srcDesc.getComment(), srcDesc.getType(), pk, columns, null, null, null, null));
		}
	
	private DiffResult compareByPK(TableDescription srcDesc, TableDescription dstDesc, String conn2, SQLDialect dialect, String filter, RunMode runMode, CompareFlags flags, OrderBy order, StatementHandler h3, String type, TaskCompareProgressMonitor c, TaskDMLProgressMonitor p, boolean export) throws PerformQueryException
		{
		final String srcStmt = sqlGenerator.generateSelect(srcDesc, Style.SIMPLE, Joins.NONE, filter, order, dialect);
		final String dstStmt = sqlGenerator.generateSelect(dstDesc, Style.SIMPLE, Joins.NONE, filter, order, dialect);
		
		final AsyncRowIterator h1 = new AsyncRowIterator();
		final RowProducer p1 = new RowProducer(runner, connectionSettings.getLinkName(), srcStmt, "diff", dataFormatterFactory.getTimeZone(), h1);
		final Thread src = new Thread(p1);
		
		final AsyncRowIterator h2 = new AsyncRowIterator();
		final RowProducer p2 = new RowProducer(runner, conn2, dstStmt, "diff", dataFormatterFactory.getTimeZone(), h2);
		final Thread dst = new Thread(p2);
		
		src.start();
		dst.start();
		
		final long start = timeService.getCurrentTime();
		
		final Result res;
		try	{
			if (runMode == RunMode.BATCH)
				{
				final List<ColumnDef> columns = new ArrayList<ColumnDef>(srcDesc.getColumns().size());
				for (ColumnDescription cd : srcDesc.getColumns())
					{
					final ColumnType ct = ColumnType.forSQLType(cd.getType());
					columns.add(new ColumnDefImpl(cd.getName(), ct, dialect.dataTypeToString(cd.getType()), null, null, null));
					}
				
				final StatementCollection sc = new StatementCollection(null, null);
				final SQLWriter sw = dataFormatterFactory.getSQLWriter(sc, dialect, false);
				if (flags.useInsert)
					sw.writeInsert(srcDesc.getName(), columns, null);
				else
					sc.statement(null);
				if (flags.useUpdate)
					{
					if (!sw.writeUpdate(srcDesc.getName(), columns, null, null, srcDesc.getPKColumns()))
						sc.statement(null);
					}
				else
					sc.statement(null);
				if (flags.useDelete)
					sw.writeDelete(srcDesc.getName(), columns, null, srcDesc.getPKColumns());
				else
					sc.statement(null);
				final Iterator<String> it = sc.iterator();
				
				final DiffRowInterpreter transferer = new DiffRowInterpreter(transformer, h1, h2, c, srcDesc, columns, it.next(), it.next(), it.next(), dialect);
				res = runner.transferRows(connectionSettings.getLinkName(), srcStmt, transferer, type, p, export);
				}
			else
				{
				final DiffRowTransferer transferer = new DiffRowTransferer(transformer, h1, h2, c, srcDesc, order, dialect, flags);
				res = runner.performCustomQueries(connectionSettings.getLinkName(), transferer, h3, type, p, export);
				}
			}
		finally
			{
			try	{
				h1.abort();
				src.interrupt();
				src.join();
				}
			catch (InterruptedException e)
				{
				logger.log(Level.SEVERE, "Error joining RowProducer for " + connectionSettings.getLinkName(), e);
	//			throw new RuntimeException(e);
				}
			
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
		
		final long end = timeService.getCurrentTime();
		
		return (new DiffResult(String.valueOf(res.getFirstRowSet().getFirstValue()), collectStatistics(c, p), end - start, !export));
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
