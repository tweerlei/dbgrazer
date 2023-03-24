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
package de.tweerlei.dbgrazer.web.controller.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.service.QueryRunnerService;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
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
public class WSController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String statement;
		private String type;
		private String format;
		private final Map<String, String> attributes;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.attributes = new HashMap<String, String>();
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
		
		/**
		 * Get the attributes
		 * @return Attributes
		 */
		public Map<String, String> getAttributes()
			{
			return (attributes);
			}
		}
	
	private final QueryService queryService;
	private final QueryRunnerService runner;
	private final ConnectionSettings connectionSettings;
	private final DataFormatterFactory factory;
	private final TextTransformerService textFormatterService;
	private final ResultDownloadService downloadService;
	private final ResultTransformerService resultTransformer;
	private final QuerySettingsManager querySettingsManager;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param runner QueryRunnerService
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param textFormatterService TextFormatterService
	 * @param downloadService ResultDownloadService
	 * @param resultTransformer ResultTransformerService
	 * @param querySettingsManager QuerySettingsManager
	 */
	@Autowired
	public WSController(QueryService queryService, QueryRunnerService runner,
			ConnectionSettings connectionSettings,
			DataFormatterFactory factory, ResultDownloadService downloadService,
			TextTransformerService textFormatterService, ResultTransformerService resultTransformer,
			QuerySettingsManager querySettingsManager)
		{
		this.queryService = queryService;
		this.runner = runner;
		this.connectionSettings = connectionSettings;
		this.factory = factory;
		this.textFormatterService = textFormatterService;
		this.downloadService = downloadService;
		this.resultTransformer = resultTransformer;
		this.querySettingsManager = querySettingsManager;
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
		
		fbo.setType(connectionSettings.getCustomQuery().getType());
		fbo.setStatement(connectionSettings.getCustomQuery().getQuery());
		
		fbo.getAttributes().putAll(connectionSettings.getCustomQuery().getAttributes());
		
		return (fbo);
		}
	
	/**
	 * Show the custom query form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-WEBSERVICE.html", method = RequestMethod.GET)
	public Map<String, Object> showCustomQueryForm(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<QueryType> resultTypes = queryService.findSimpleQueryTypes(connectionSettings.getType());
		final Set<String> formatters = textFormatterService.getSupportedTextFormats();
		
		final Set<String> attributeNames = new TreeSet<String>();
		for (QueryType t : resultTypes)
			attributeNames.addAll(t.getSupportedAttributes().keySet());
		
		if (fbo.getType() == null)
			fbo.setType(resultTypes.iterator().next().getName());
		
		model.put("resultTypes", resultTypes);
		model.put("formatters", formatters);
		model.put("attributeNames", attributeNames);
		
		return (model);
		}
	
	/**
	 * Show the custom query form with a given statement
	 * @param fbo FormBackingObject
	 * @return View
	 */
	@RequestMapping(value = "/db/*/submit-WEBSERVICE.html", method = RequestMethod.POST)
	public String fillCustomQueryForm(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			connectionSettings.getCustomQuery().setQuery(fbo.getStatement());
			connectionSettings.getCustomQuery().setType(fbo.getType());
			connectionSettings.getCustomQuery().getAttributes().putAll(fbo.getAttributes());
			connectionSettings.getCustomQuery().reset();
			}
		
		return ("redirect:submit-WEBSERVICE.html");
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submit-WEBSERVICE.html", method = RequestMethod.POST)
	public Map<String, Object> performQuery(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getStatement());
			connectionSettings.getCustomQuery().setType(fbo.getType());
			connectionSettings.getCustomQuery().getAttributes().putAll(fbo.getAttributes());
			connectionSettings.getCustomQuery().modify();
			}
		
		final DataFormatter fmt = factory.getWebFormatter();
		final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		try	{
			final Result r = performQuery(fbo.getStatement(), fbo.getType(), queryName, fbo.getAttributes(), Integer.MAX_VALUE);
			
			final Iterator<RowSet> it = r.getRowSets().values().iterator();
			final RowSet response = it.next();
			final RowSet headers = it.next();
			
			resultTransformer.formatRowSet(response, fmt, fbo.getAttributes().get(RowSetConstants.ATTR_FORMATTER), querySettingsManager.getFormatOptions(null));
			response.getAttributes().putAll(querySettingsManager.getQuerySettings(null));
			model.put("rs", response);
			
			model.put("headers", headers);
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
	@RequestMapping(value = "/db/*/submit-WEBSERVICE-export.html", method = RequestMethod.POST)
	public Map<String, Object> performCSVQuery(@ModelAttribute FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getStatement());
			connectionSettings.getCustomQuery().setType(fbo.getType());
			connectionSettings.getCustomQuery().getAttributes().putAll(fbo.getAttributes());
			connectionSettings.getCustomQuery().modify();
			}
		
		final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		try	{
			final Result r = performQuery(fbo.getStatement(), fbo.getType(), queryName, fbo.getAttributes(), Integer.MAX_VALUE);
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
	
	private Result performQuery(String statement, String type, String label, Map<String, String> attrs, int limit)
		{
		final QueryType t = queryService.findQueryType(type);
		
		final Query q = new QueryImpl(label, new SchemaDef(null, null), null, statement, null, t, null, null, attrs);
		
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
