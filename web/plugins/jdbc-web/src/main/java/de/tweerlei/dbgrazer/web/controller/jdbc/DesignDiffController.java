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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
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
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskDMLProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.SchemaTransformerService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignManagerService;
import de.tweerlei.dbgrazer.web.service.jdbc.impl.TableSet;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.ermtools.model.SQLSchema;

/**
 * Compare structure of DB objects in the current design
 * 
 * @author Robert Wruck
 */
@Controller
public class DesignDiffController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String connection2;
		private String mode;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
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
	
	private final MetadataService metadataService;
	private final LinkService linkService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final SchemaTransformerService schemaTransformer;
	private final ResultBuilderService resultBuilder;
	private final DataFormatterFactory dataFormatterFactory;
	private final UserSettingsManager userSettingsManager;
	private final TaskProgressService taskProgressService;
	private final DesignManagerService designManagerService;
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
	 * @param designManagerService DesignManagerService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public DesignDiffController(MetadataService metadataService, LinkService linkService,
			QueryService queryService, QueryPerformerService runner, SchemaTransformerService schemaTransformer,
			UserSettingsManager userSettingsManager, ResultBuilderService resultBuilder,
			DataFormatterFactory dataFormatterFactory, TaskProgressService taskProgressService,
			DesignManagerService designManagerService,
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
		this.designManagerService = designManagerService;
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
	@RequestMapping(value = "/db/*/dbdesigner-compare.html", method = RequestMethod.GET)
	public Map<String, Object> showDesignDialog(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (fbo.getConnection2() != null)
			{
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			}
		else
			{
			fbo.setConnection2(connectionSettings.getParameterHistory().get("connection2"));
			}
	
		fbo.setMode(connectionSettings.getParameterHistory().get("mode"));
		
		final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
		model.put("allConnections", all);
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		model.put("resultTypes", resultTypes);
		
		model.put("extensionJS", JdbcMessageKeys.EXTENSION_JS);
		
		return (model);
		}
	
	/**
	 * Show catalogs
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/design-compare.html", method = RequestMethod.POST)
	public Map<String, Object> compareDesigns(
			@ModelAttribute("model") FormBackingObject fbo
			)
		{
		if (!connectionSettings.isDesignerEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final TaskDMLProgressMonitor pr = taskProgressService.createDMLProgressMonitor();
		if (pr == null)
			{
			model.put("alreadyRunning", Boolean.TRUE);
			model.put("progress", taskProgressService.getProgress());
			return (model);
			}
		
		final TableSet design = designManagerService.getCurrentDesign();
		model.put("currentDesign", design);
		
		final TaskCompareProgressMonitor c = taskProgressService.createCompareProgressMonitor();
		
		try	{
			final Set<QualifiedName> missing = new HashSet<QualifiedName>();
			final SQLSchema left = readSchema(connectionSettings.getLinkName(), design.getTableNames(), missing, c.getSourceRows());
			final SQLSchema right = readSchema(fbo.getConnection2(), design.getTableNames(), missing, c.getDestinationRows());
			
			final boolean crossDialect = !StringUtils.equals(connectionSettings.getDialectName(),
					linkService.getLink(fbo.getConnection2(), null).getDialectName());
			
			final SQLDialect dialect = getSQLDialect();
			final StatementProducer p = schemaTransformer.compareSchemas(left, right, false, dialect, crossDialect);
			
			if (!StringUtils.empty(fbo.getMode()) && connectionSettings.isWritable())
				{
				try	{
					final Result r = runner.performCustomQueries(connectionSettings.getLinkName(), p, fbo.getMode(), pr);
					
					model.put("result", r.getFirstRowSet().getFirstValue());
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
				}
			else
				{
				final String header = getHeader(connectionSettings.getLinkName(), fbo.getConnection2());
				model.put("result", resultBuilder.writeScript(p, header, dialect.getStatementTerminator()));
				}
			
			connectionSettings.getParameterHistory().put("connection2", fbo.getConnection2());
			connectionSettings.getParameterHistory().put("mode", fbo.getMode());
			}
		finally
			{
			taskProgressService.removeCompareProgressMonitor();
			taskProgressService.removeDMLProgressMonitor();
			}
		
		return (model);
		}
	
	private SQLSchema readSchema(String conn, Set<QualifiedName> tables, Set<QualifiedName> missing, TaskProgress p)
		{
		p.setTodo(tables.size());
		
		return (new SQLSchema(null, null, metadataService.getTableInfos(conn, tables, missing, ColumnMode.ALL, p)));
		}
	
	private String getHeader(String c1, String c2)
		{
		return (dataFormatterFactory.getMessage(JdbcMessageKeys.DDL_COMPARE_HEADER, c1, c2));
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
