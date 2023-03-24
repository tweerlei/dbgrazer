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
package de.tweerlei.dbgrazer.web.controller.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.ListBuilder;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBClientService;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.service.QueryRunnerService;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constants.mongodb.MongoDBConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class MongoDBController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String database;
		private String query;
		private String statement;
		private String format;
		
		/**
		 * @return the query
		 */
		public String getQuery()
			{
			return query;
			}
		
		/**
		 * @param query the query to set
		 */
		public void setQuery(String query)
			{
			this.query = query;
			}
		
		/**
		 * @return the database
		 */
		public String getDatabase()
			{
			return database;
			}
		
		/**
		 * @param database the database to set
		 */
		public void setDatabase(String database)
			{
			this.database = database;
			}
		
		/**
		 * Get the statement
		 * @return the statement
		 */
		public String getStatement()
			{
			return statement;
			}
		
		/**
		 * Set the statement
		 * @param statement the statement to set
		 */
		public void setStatement(String statement)
			{
			this.statement = statement;
			}
		
		/**
		 * Get the format
		 * @return the format
		 */
		public String getFormat()
			{
			return format;
			}
		
		/**
		 * Set the format
		 * @param format the format to set
		 */
		public void setFormat(String format)
			{
			this.format = format;
			}
		}
	
	private final QueryService queryService;
	private final QueryRunnerService runner;
	private final ConnectionSettings connectionSettings;
	private final DataFormatterFactory factory;
	private final ResultDownloadService downloadService;
	private final ResultTransformerService resultTransformer;
	private final QuerySettingsManager querySettingsManager;
	private final MongoDBClientService mongoClientService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param runner QueryRunnerService
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param downloadService ResultDownloadService
	 * @param resultTransformer ResultTransformerService
	 * @param querySettingsManager QuerySettingsManager
	 * @param mongoClientService MongoDBClientService
	 */
	@Autowired
	public MongoDBController(QueryService queryService, QueryRunnerService runner,
			ConnectionSettings connectionSettings,
			DataFormatterFactory factory, ResultDownloadService downloadService,
			ResultTransformerService resultTransformer,
			QuerySettingsManager querySettingsManager,
			MongoDBClientService mongoClientService)
		{
		this.queryService = queryService;
		this.runner = runner;
		this.connectionSettings = connectionSettings;
		this.factory = factory;
		this.downloadService = downloadService;
		this.resultTransformer = resultTransformer;
		this.querySettingsManager = querySettingsManager;
		this.mongoClientService = mongoClientService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Get the FormBackingObject
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject()
		{
		final FormBackingObject fbo = new FormBackingObject();
		
		fbo.setQuery(connectionSettings.getCustomQuery().getQuery());
		
		return (fbo);
		}
	
	/**
	 * Show the custom query form
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-MONGODB.html", method = RequestMethod.GET)
	public Map<String, Object> showCustomQueryForm()
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final List<String> databases = new ListBuilder<String>().addAll(mongoClientService.getMongoClient(connectionSettings.getLinkName()).listDatabaseNames()).build();
		model.put("databases", databases);
		
		model.put("tableColumns", Collections.emptyList());
		
		return (model);
		}
	
	/**
	 * Show the custom query form with a given statement
	 * @param fbo FormBackingObject
	 * @return View
	 */
	@RequestMapping(value = "/db/*/submit-MONGODB.html", method = RequestMethod.POST)
	public String fillCustomQueryForm(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			connectionSettings.getCustomQuery().setQuery(fbo.getStatement());
			connectionSettings.getCustomQuery().reset();
			}
		
		return ("redirect:submit-MONGODB.html");
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submit-MONGODB.html", method = RequestMethod.POST)
	public Map<String, Object> performQuery(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getQuery());
			connectionSettings.getCustomQuery().modify();
			}
		
		final DataFormatter fmt = factory.getWebFormatter();
		final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		try	{
			final Result r = performQuery(fbo.getDatabase(), fbo.getStatement(), queryName, Integer.MAX_VALUE);
			
			resultTransformer.translateRowSet(r.getFirstRowSet(), fmt);
			resultTransformer.formatRowSet(r.getFirstRowSet(), fmt, "JSON", EnumSet.allOf(TextTransformerService.Option.class));
//			r.getFirstRowSet().getAttributes().putAll(querySettingsManager.getQuerySettings(null));
			model.put("rs", r.getFirstRowSet());
			
			final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(1);
			tableColumns.add(r.getFirstRowSet().getColumns());
			model.put("tableColumns", tableColumns);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "performQuery", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-MONGODB-export.html", method = RequestMethod.POST)
	public Map<String, Object> performCSVQuery(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getQuery());
			connectionSettings.getCustomQuery().modify();
			}
		
		final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		try	{
			final Result r = performQuery(fbo.getDatabase(), fbo.getStatement(), queryName, Integer.MAX_VALUE);
			final RowSet rs = r.getFirstRowSet();
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getDownloadSource(connectionSettings.getLinkName(), rs, fbo.getFormat()));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "performCSVQuery", e);
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		
		return (model);
		}
	
	private Result performQuery(String database, String statement, String label, int limit)
		{
		final QueryType t = queryService.findQueryType(MongoDBConstants.QUERYTYPE_COMMAND);
		
		final Query q = new QueryImpl(label, new SchemaDef(null, null), null, statement, null, t, null, null, Collections.singletonMap(MongoDBConstants.ATTR_DATABASE, database));
		
		try	{
			final Result r = runner.performQuery(connectionSettings.getLinkName(), q, 0, Collections.emptyList(), factory.getTimeZone(), limit, null);
			return (r);
			}
		catch (PerformQueryException e)
			{
			throw e.getCause();
			}
		}
	}
