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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService.ColumnMode;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.exception.CancelledByUserException;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskDMLProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.ermtools.model.SQLSchema;
import de.tweerlei.ermtools.schema.DBSchemaParser;

/**
 * Compare structure of objects in a DB schema
 * 
 * @author Robert Wruck
 */
@Controller
public class SchemaDiffController
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
		private String mode;
		private MultipartFile file;
		
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
		
		/**
		 * Get the file
		 * @return the file
		 */
		public MultipartFile getFile()
			{
			return file;
			}
		
		/**
		 * Set the file
		 * @param file the file to set
		 */
		public void setFile(MultipartFile file)
			{
			this.file = file;
			}
		}
	
	private final MetadataService metadataService;
	private final LinkService linkService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final SchemaTransformerService schemaTransformer;
	private final ResultBuilderService resultBuilder;
	private final DataFormatterFactory dataFormatterFactory;
	private final UserSettingsManager userSettingsManager;
	private final TaskProgressService taskProgressService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param linkService LinkService
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param userSettingsManager UserSettingsManager
	 * @param schemaTransformer SchemaTransformerService
	 * @param resultBuilder ResultBuilderService
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param taskProgressService TaskProgressService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public SchemaDiffController(MetadataService metadataService, LinkService linkService,
			QueryService queryService, QueryPerformerService runner, SchemaTransformerService schemaTransformer,
			UserSettingsManager userSettingsManager, ResultBuilderService resultBuilder,
			DataFormatterFactory dataFormatterFactory, TaskProgressService taskProgressService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.metadataService = metadataService;
		this.linkService = linkService;
		this.queryService = queryService;
		this.runner = runner;
		this.schemaTransformer = schemaTransformer;
		this.resultBuilder = resultBuilder;
		this.dataFormatterFactory = dataFormatterFactory;
		this.userSettingsManager = userSettingsManager;
		this.taskProgressService = taskProgressService;
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
	@RequestMapping(value = "/db/*/dbcompare.html", method = RequestMethod.GET)
	public Map<String, Object> showSchemaDialog(
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
	@RequestMapping(value = "/ws/*/form-dbcompare.html", method = RequestMethod.GET)
	public Map<String, Object> showSchemaWSForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> all = linkService.findAllLinkNames(null, null, null);
		model.put("allConnections", all);
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/dbcompare.html", method = RequestMethod.POST)
	public Map<String, Object> compareSchemas(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isBrowserEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model;
		
		final TaskDMLProgressMonitor pr = taskProgressService.createDMLProgressMonitor();
		if (pr == null)
			{
			model = new HashMap<String, Object>();
			model.put("alreadyRunning", Boolean.TRUE);
			model.put("progress", taskProgressService.getProgress());
			return (model);
			}
		final TaskCompareProgressMonitor c = taskProgressService.createCompareProgressMonitor();
		
		try	{
			if (fbo.getFile() != null)
				{
				SQLSchema sch = null;
				Exception ex = null;
				try	{
					sch = readSchema(fbo.getFile(), fbo.getFilter());
					}
				catch (IOException e)
					{
					ex = e;
					}
				
				if (sch == null)
					{
					model = new HashMap<String, Object>();
					model.put("exception", ex);
					}
				else
					model = compareSchemasInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getFilter(), sch, "XML", fbo.getMode(), pr, c, false, true, true);
				}
			else
				{
				model = compareSchemasInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getFilter(), fbo.getConnection2(), fbo.getCatalog2(), fbo.getSchema2(), fbo.getMode(), pr, c, false, false);
				
				connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
				connectionSettings.getParameterHistory().put("catalog2", fbo.getCatalog2());
				connectionSettings.getParameterHistory().put("schema2", fbo.getSchema2());
				}
			
			connectionSettings.getParameterHistory().put("filter", fbo.getFilter());
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
	@RequestMapping(value = "/ws/*/dbcompare.html", method = RequestMethod.GET)
	public Map<String, Object> compareSchemasWS(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		final Map<String, Object> model;
		
		final TaskDMLProgressMonitor p = new TaskDMLProgressMonitor();
		final TaskCompareProgressMonitor c = new TaskCompareProgressMonitor();
		
		model = compareSchemasInternal(fbo.getCatalog(), fbo.getSchema(), fbo.getFilter(), fbo.getConnection2(), fbo.getCatalog2(), fbo.getSchema2(), fbo.getMode(), p, c, true, true);
		
		return (model);
		}
	
	private Map<String, Object> compareSchemasInternal(
			String catalog,
			String schema,
			String filter,
			SQLSchema right,
			String rightLabel,
			String mode,
			TaskDMLProgressMonitor pr,
			TaskCompareProgressMonitor c,
			boolean flushConn,
			boolean crossSchema,
			boolean crossDialect
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("catalog", catalog);
		model.put("schema", schema);
		model.put("object", filter);
		model.put("mode", mode);
		
		final SQLSchema left = readSchema(connectionSettings.getLinkName(), catalog, schema, filter, c.getSourceRows(), flushConn);
		
		final SQLDialect dialect = getSQLDialect();
		final StatementProducer p = schemaTransformer.compareSchemas(left, right, crossSchema, dialect, crossDialect);
		
		try	{
			if (!StringUtils.empty(mode) && connectionSettings.isWritable())
				{
				final Result r = runner.performCustomQueries(connectionSettings.getLinkName(), p, mode, pr);
				
				model.put("result", r.getFirstRowSet().getFirstValue());
				}
			else
				{
				final String header = getHeader(connectionSettings.getLinkName(), rightLabel);
				model.put("result", resultBuilder.writeScript(p, header, dialect.getStatementTerminator()));
				}
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
	
	private Map<String, Object> compareSchemasInternal(
			String catalog,
			String schema,
			String filter,
			String conn2,
			String catalog2,
			String schema2,
			String mode,
			TaskDMLProgressMonitor pr,
			TaskCompareProgressMonitor c,
			boolean flushConn,
			boolean flushConn2
			)
		{
		final SQLSchema right = readSchema(conn2, catalog2, schema2, filter, c.getDestinationRows(), flushConn2);
		
		final boolean crossSchema = !StringUtils.equals(catalog, catalog2) || !StringUtils.equals(schema, schema2);
		final boolean crossDialect = !StringUtils.equals(connectionSettings.getDialectName(),
				linkService.getLink(conn2, null).getDialectName());
		
		return (compareSchemasInternal(catalog, schema, filter, right, conn2, mode, pr, c, flushConn, crossSchema, crossDialect));
		}
	
	private SQLSchema readSchema(String conn, String catalog, String schema, String filter, TaskProgress p, boolean flush)
		{
		if (flush)
			metadataService.flushCache(conn);
		
		final Map<QualifiedName, String> tables = metadataService.getTables(conn, catalog, schema, TableDescription.TABLE, filter);
		p.setTodo(tables.size());
		
		return (new SQLSchema(catalog, schema, metadataService.getTableInfos(conn, tables.keySet(), null, ColumnMode.ALL, p)));
		}
	
	private SQLSchema readSchema(MultipartFile file, String filter) throws IOException
		{
		final DBSchemaParser p = new DBSchemaParser();
		final InputStream is = file.getInputStream();
		try	{
			return (p.parseSchema(is, StringUtils.nullIfEmpty(filter)));
			}
		catch (SAXException e)
			{
			throw new IOException(e);
			}
		finally
			{
			is.close();
			}
		}
	
	private String getHeader(String c1, String c2)
		{
		return (dataFormatterFactory.getMessage(MessageKeys.DDL_COMPARE_HEADER, c1, c2));
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
