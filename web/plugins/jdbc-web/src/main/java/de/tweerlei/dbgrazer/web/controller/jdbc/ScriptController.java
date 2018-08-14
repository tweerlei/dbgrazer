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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.web.view.GenericDownloadView;
import de.tweerlei.spring.web.view.StringDownloadSource;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class ScriptController
	{
	private static final String SCRIPT_CHARSET = "UTF-8";
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String statement;
		private String type;
		private MultipartFile file;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
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
	
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final ConnectionSettings connectionSettings;
	private final DataFormatterFactory factory;
	private final ResultTransformerService resultTransformer;
	private final QuerySettingsManager querySettingsManager;
	private final TaskProgressService taskProgressService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param resultTransformer ResultTransformerService
	 * @param querySettingsManager QuerySettingsManager
	 * @param taskProgressService TaskProgressService
	 */
	@Autowired
	public ScriptController(QueryService queryService, QueryPerformerService runner,
			ConnectionSettings connectionSettings,
			DataFormatterFactory factory, TaskProgressService taskProgressService,
			ResultTransformerService resultTransformer,
			QuerySettingsManager querySettingsManager
			)
		{
		this.queryService = queryService;
		this.runner = runner;
		this.connectionSettings = connectionSettings;
		this.factory = factory;
		this.resultTransformer = resultTransformer;
		this.querySettingsManager = querySettingsManager;
		this.taskProgressService = taskProgressService;
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
		
		return (fbo);
		}
	
	/**
	 * Show the custom query form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submitexec.html", method = RequestMethod.GET)
	public Map<String, Object> showScriptQueryForm(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<QueryType> resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		if (fbo.getType() == null)
			fbo.setType(resultTypes.iterator().next().getName());
		
		model.put("resultTypes", resultTypes);
		
		model.put("tableColumns", Collections.emptyList());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submitexec.html", method = RequestMethod.POST)
	public Map<String, Object> performScriptQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getStatement());
			connectionSettings.getCustomQuery().setType(fbo.getType());
			connectionSettings.getCustomQuery().modify();
			}
		
		final DataFormatter fmt = factory.getWebFormatter();
		final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		final TaskProgress pr = taskProgressService.createTaskProgress(MessageKeys.TOTAL_STATEMENTS);
		if (pr == null)
			{
			model.put("alreadyRunning", Boolean.TRUE);
			model.put("progress", taskProgressService.getProgress());
			return (model);
			}
		
		try	{
			final Result r = runner.performCustomQuery(connectionSettings.getLinkName(), fbo.getType(), fbo.getStatement(), null, null, queryName, true, pr);
			
			resultTransformer.translateRowSet(r.getFirstRowSet(), fmt);
			model.put("rs", r.getFirstRowSet());
			
			final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(1);
			tableColumns.add(r.getFirstRowSet().getColumns());
			model.put("tableColumns", tableColumns);
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		finally
			{
			taskProgressService.removeTaskProgress(MessageKeys.TOTAL_STATEMENTS);
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/execute.html", method = RequestMethod.POST)
	public Map<String, Object> performScriptExportQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		try	{
			final String q = readFile(fbo.getFile());
			
			final Result r = runner.performCustomQuery(connectionSettings.getLinkName(), (fbo.getType() == null) ? JdbcConstants.QUERYTYPE_TOLERANT_SCRIPT : fbo.getType(), q, null, null, queryName, true, null);
			
			resultTransformer.translateRowSet(r.getFirstRowSet(), fmt);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new StringDownloadSource(r.getFirstRowSet().getFirstValue().toString(), "text/plain", SCRIPT_CHARSET, false));
			}
		catch (IOException e)
			{
			model.put("exception", e);
			}
		catch (PerformQueryException e)
			{
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	private String readFile(MultipartFile file) throws IOException
		{
		final InputStream is = file.getInputStream();
		try {
			// Get charset from content type?
			final Reader r = new InputStreamReader(is, SCRIPT_CHARSET);
			try	{
				final StringWriter sw = new StringWriter();
				StreamUtils.copy(r, sw);
				final String contents = sw.toString();
				return (contents);
				}
			finally
				{
				r.close();
				}
			}
		finally
			{
			is.close();
			}
		}
	}
