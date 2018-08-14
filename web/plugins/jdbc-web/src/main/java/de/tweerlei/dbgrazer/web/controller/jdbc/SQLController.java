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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.service.jdbc.SQLReconstructionService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;
import de.tweerlei.ermtools.dialect.impl.SQLDialectFactory;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class SQLController
	{
	private static final int MAX_PARAMS = 10;
	
	private static final String VIEW_PLAN = "plan";
	private static final String VIEW_CHART = "chart";
	
	private static final String ATTR_LABEL = "label";
	private static final String ATTR_TABLENAME = "tableName";
	private static final String ATTR_PARAM_TYPE = "paramType";
	private static final String ATTR_PARAM_VALUE = "paramValue";
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class ParameterFBO
		{
		private String value;
		private ColumnType type;
		private boolean enabled;
		
		/**
		 * Constructor
		 */
		public ParameterFBO()
			{
			}
		
		/**
		 * @return the value
		 */
		public String getValue()
			{
			return value;
			}
		
		/**
		 * @param value the value to set
		 */
		public void setValue(String value)
			{
			this.value = value;
			}
		
		/**
		 * @return the type
		 */
		public ColumnType getType()
			{
			return type;
			}
		
		/**
		 * @param type the type to set
		 */
		public void setType(ColumnType type)
			{
			this.type = type;
			}

		/**
		 * @return the enabled
		 */
		public boolean isEnabled()
			{
			return enabled;
			}
		
		/**
		 * @param enabled the enabled to set
		 */
		public void setEnabled(boolean enabled)
			{
			this.enabled = enabled;
			}
		}
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String query;
		private String statement;
		private String label;
		private String tableName;
		private String view;
		private String format;
		private boolean allRows;
		private final Map<Integer, ParameterFBO> params;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.params = new TreeMap<Integer, ParameterFBO>();
			}
		
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
		 * @return the statement
		 */
		public String getStatement()
			{
			return statement;
			}
		
		/**
		 * @param statement the statement to set
		 */
		public void setStatement(String statement)
			{
			this.statement = statement;
			}
		
		/**
		 * @return the label
		 */
		public String getLabel()
			{
			return label;
			}
		
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label)
			{
			this.label = label;
			}
		
		/**
		 * @return the tableName
		 */
		public String getTableName()
			{
			return tableName;
			}
		
		/**
		 * @param tableName the tableName to set
		 */
		public void setTableName(String tableName)
			{
			this.tableName = tableName;
			}
		
		/**
		 * @return the view
		 */
		public String getView()
			{
			return view;
			}
		
		/**
		 * @param view the view to set
		 */
		public void setView(String view)
			{
			this.view = view;
			}
		
		/**
		 * @return the params
		 */
		public Map<Integer, ParameterFBO> getParams()
			{
			return params;
			}

		/**
		 * @return the format
		 */
		public String getFormat()
			{
			return format;
			}
		
		/**
		 * @param format the format to set
		 */
		public void setFormat(String format)
			{
			this.format = format;
			}
		
		/**
		 * @return the allRows
		 */
		public boolean isAllRows()
			{
			return allRows;
			}
		
		/**
		 * @param allRows the allRows to set
		 */
		public void setAllRows(boolean allRows)
			{
			this.allRows = allRows;
			}
		}
	
	private final MetadataService metadataService;
	private final QueryPerformerService runner;
	private final ResultCache resultCache;
	private final ConnectionSettings connectionSettings;
	private final DataFormatterFactory factory;
	private final DownloadService downloadService;
	private final VisualizationService visualizationService;
	private final ResultTransformerService resultTransformer;
	private final SQLReconstructionService reconstructionService;
	private final QuerySettingsManager querySettingsManager;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param metadataService MetadataService
	 * @param runner QueryPerformerService
	 * @param resultCache ResultCache
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param downloadService DownloadService
	 * @param visualizationService VisualizationService
	 * @param resultTransformer ResultTransformerService
	 * @param reconstructionService SQLReconstructionService
	 * @param querySettingsManager QuerySettingsManager
	 */
	@Autowired
	public SQLController(MetadataService metadataService, QueryPerformerService runner,
			ResultCache resultCache, ConnectionSettings connectionSettings,
			DataFormatterFactory factory, DownloadService downloadService,
			ResultTransformerService resultTransformer,
			SQLReconstructionService reconstructionService,
			VisualizationService visualizationService, QuerySettingsManager querySettingsManager
			)
		{
		this.metadataService = metadataService;
		this.runner = runner;
		this.resultCache = resultCache;
		this.connectionSettings = connectionSettings;
		this.factory = factory;
		this.downloadService = downloadService;
		this.visualizationService = visualizationService;
		this.resultTransformer = resultTransformer;
		this.reconstructionService = reconstructionService;
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
		
		fbo.setLabel(connectionSettings.getCustomQuery().getAttributes().get(ATTR_LABEL));
		fbo.setTableName(connectionSettings.getCustomQuery().getAttributes().get(ATTR_TABLENAME));
		fbo.setQuery(connectionSettings.getCustomQuery().getQuery());
		
		for (int i = 0; i < MAX_PARAMS; i++)
			{
			final ParameterFBO p = new ParameterFBO();
			final String type = connectionSettings.getCustomQuery().getAttributes().get(ATTR_PARAM_TYPE + i);
			if (type == null)
				p.setType(ColumnType.INTEGER);
			else
				p.setType(ColumnType.valueOf(type));
			final String value = connectionSettings.getCustomQuery().getAttributes().get(ATTR_PARAM_VALUE + i);
			if (value == null)
				{
				p.setValue("");
				p.setEnabled(false);
				}
			else
				{
				p.setValue(value);
				p.setEnabled(true);
				}
			fbo.getParams().put(i, p);
			}
		
		return (fbo);
		}
	
	/**
	 * Show the custom query form
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-JDBC.html", method = RequestMethod.GET)
	public Map<String, Object> showCustomQueryForm()
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("tableColumns", Collections.emptyList());
		model.put("columnTypes", CollectionUtils.list(ColumnType.values()));
		
		return (model);
		}
	
	/**
	 * Show the custom query form with a given statement
	 * @param fbo FormBackingObject
	 * @return View
	 */
	@RequestMapping(value = "/db/*/submit-JDBC.html", method = RequestMethod.POST)
	public String showCustomQueryForm(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			connectionSettings.getCustomQuery().setQuery(fbo.getStatement());
//			connectionSettings.getCustomQuery().setType(JdbcConstants.QUERYTYPE_CUSTOM);
			connectionSettings.getCustomQuery().reset();
			}
		if (fbo.getLabel() != null)
			connectionSettings.getCustomQuery().getAttributes().put(ATTR_LABEL, fbo.getLabel());
		if (fbo.getTableName() != null)
			connectionSettings.getCustomQuery().getAttributes().put(ATTR_TABLENAME, fbo.getTableName());
		
		return ("redirect:submit-JDBC.html");
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/submit-JDBC.html", method = RequestMethod.POST)
	public Map<String, Object> performQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getQuery());
//			connectionSettings.getCustomQuery().setType(JdbcConstants.QUERYTYPE_CUSTOM);
			connectionSettings.getCustomQuery().modify();
			}
		for (Map.Entry<Integer, ParameterFBO> ent : fbo.getParams().entrySet())
			{
			if (ent.getValue().isEnabled())
				{
				connectionSettings.getCustomQuery().getAttributes().put(ATTR_PARAM_TYPE + ent.getKey(), ent.getValue().getType().name());
				connectionSettings.getCustomQuery().getAttributes().put(ATTR_PARAM_VALUE + ent.getKey(), ent.getValue().getValue());
				}
			else
				{
				connectionSettings.getCustomQuery().getAttributes().remove(ATTR_PARAM_TYPE + ent.getKey());
				connectionSettings.getCustomQuery().getAttributes().remove(ATTR_PARAM_VALUE + ent.getKey());
				}
			}
		connectionSettings.getCustomQuery().getAttributes().put(ATTR_LABEL, fbo.getLabel());
		connectionSettings.getCustomQuery().getAttributes().put(ATTR_TABLENAME, fbo.getTableName());
		resultCache.clearCachedObjects(CacheClass.RESULT_VISUALIZATION);
		
		final DataFormatter fmt = factory.getWebFormatter();
		final String queryName;
		if (!StringUtils.empty(fbo.getLabel()))
			queryName = fbo.getLabel();
		else
			queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		final List<ParameterDef> paramDefs = extractParams(fbo);
		final List<Object> params = translateParams(fbo);
		
		try	{
			if (VIEW_PLAN.equals(fbo.getView()))
				{
				final SQLExecutionPlan plan = metadataService.analyzeStatement(connectionSettings.getLinkName(), fbo.getStatement(), params);
				model.put("plan", plan);
				}
			else
				{
				final Result r;
				if (VIEW_CHART.equals(fbo.getView()))
					{
					r = runner.performCustomChartQuery(connectionSettings.getLinkName(), JdbcConstants.QUERYTYPE_SPLIT, fbo.getStatement(), queryName);
					if (!r.getFirstRowSet().getRows().isEmpty())
						{
						final Visualization c = visualizationService.build(r, fmt, ViewConstants.IMAGEMAP_ID, null, null, querySettingsManager.getQuerySettings(null));
						if (c != null)
							{
							final String key = resultCache.addCachedObject(CacheClass.RESULT_VISUALIZATION, c);
							model.put(RowSetConstants.ATTR_IMAGE_ID, key);
							
							// No link - no html map
	//						final String map = visualizationService.getHtmlMap(c);
	//						model.put(RowSetConstants.ATTR_IMAGEMAP, map);
	//						model.put(RowSetConstants.ATTR_IMAGEMAP_ID, VisualizationSettings.IMAGEMAP_ID);
							
							model.put(RowSetConstants.ATTR_OPTION_CODE, c.getOptionCode());
							model.put(RowSetConstants.ATTR_OPTION_NAMES, visualizationService.getOptionNames(r.getQuery().getType().getName()));
							model.put(RowSetConstants.ATTR_SOURCE_TEXT, visualizationService.hasSourceText(c));
							}
						}
					}
				else
					{
					r = runner.performCustomQuery(connectionSettings.getLinkName(), JdbcConstants.QUERYTYPE_CUSTOM, fbo.getStatement(), paramDefs, params, queryName, true, null);
					}
				
				// translateRowSet translates column names, so generate SQL first
				final String sql = reconstructionService.buildSQL(r.getFirstRowSet(), getSQLDialect());
				model.put("sql", sql);
				resultTransformer.translateRowSet(r.getFirstRowSet(), fmt);
				model.put("rs", r.getFirstRowSet());
				
				final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(1);
				tableColumns.add(r.getFirstRowSet().getColumns());
				model.put("tableColumns", tableColumns);
				}
			}
		catch (PerformQueryException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e.getCause());
			model.put("exception", e.getCause());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/submit-JDBC-export.html", method = RequestMethod.POST)
	public Map<String, Object> performCSVQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		if (!connectionSettings.isSubmitEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!StringUtils.empty(fbo.getStatement()))
			{
			querySettingsManager.addCustomHistoryEntry(fbo.getStatement());
			connectionSettings.getCustomQuery().setQuery(fbo.getQuery());
//			connectionSettings.getCustomQuery().setType(JdbcConstants.QUERYTYPE_CUSTOM);
			connectionSettings.getCustomQuery().modify();
			}
		for (Map.Entry<Integer, ParameterFBO> ent : fbo.getParams().entrySet())
			{
			if (ent.getValue().isEnabled())
				{
				connectionSettings.getCustomQuery().getAttributes().put(ATTR_PARAM_TYPE + ent.getKey(), ent.getValue().getType().name());
				connectionSettings.getCustomQuery().getAttributes().put(ATTR_PARAM_VALUE + ent.getKey(), ent.getValue().getValue());
				}
			else
				{
				connectionSettings.getCustomQuery().getAttributes().remove(ATTR_PARAM_TYPE + ent.getKey());
				connectionSettings.getCustomQuery().getAttributes().remove(ATTR_PARAM_VALUE + ent.getKey());
				}
			}
		connectionSettings.getCustomQuery().getAttributes().put(ATTR_LABEL, fbo.getLabel());
		connectionSettings.getCustomQuery().getAttributes().put(ATTR_TABLENAME, fbo.getTableName());
		
		final SQLDialect dialect = getSQLDialect();
		final String queryName;
		if (!StringUtils.empty(fbo.getLabel()))
			queryName = fbo.getLabel();
		else
			queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
		
		final List<ParameterDef> paramDefs = extractParams(fbo);
		
		try	{
			if (fbo.isAllRows())
				{
				final Query query = runner.createCustomQuery(JdbcConstants.QUERYTYPE_CUSTOM, fbo.getStatement(), null, queryName);
				
				final String tableName;
				if (!StringUtils.empty(fbo.getTableName()))
					tableName = fbo.getTableName();
				else
					tableName = factory.getMessage(MessageKeys.DEFAULT_TABLE_NAME);
				
				final Map<Integer, String> params = new HashMap<Integer, String>();
				for (Map.Entry<Integer, ParameterFBO> ent : fbo.getParams().entrySet())
					params.put(ent.getKey(), ent.getValue().getValue());
				
				model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getStreamDownloadSource(connectionSettings.getLinkName(), query, params, tableName, null, dialect, fbo.getFormat()));
				}
			else
				{
				final List<Object> params = translateParams(fbo);
				final Result r = runner.performCustomQuery(connectionSettings.getLinkName(), JdbcConstants.QUERYTYPE_CUSTOM, fbo.getStatement(), paramDefs, params, queryName, true, null);
				final RowSet rs = r.getFirstRowSet();
				
				final String tableName;
				if (!StringUtils.empty(fbo.getTableName()))
					tableName = fbo.getTableName();
				else if (!rs.getRows().isEmpty() && (rs.getColumns().get(0).getSourceObject() != null))
					tableName = dialect.getQualifiedTableName(rs.getColumns().get(0).getSourceObject());
				else
					tableName = factory.getMessage(MessageKeys.DEFAULT_TABLE_NAME);
				
				model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getDownloadSource(connectionSettings.getLinkName(), rs, tableName, null, dialect, fbo.getFormat()));
				}
			}
		catch (PerformQueryException e)
			{
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/submit.html", method = RequestMethod.POST)
	public Map<String, Object> performCSVExportQuery(@ModelAttribute("model") FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			final String queryName = factory.getMessage(MessageKeys.DEFAULT_CHART_TITLE);
			final String tableName = factory.getMessage(MessageKeys.DEFAULT_TABLE_NAME);
			
			final Query query = runner.createCustomQuery(JdbcConstants.QUERYTYPE_MULTIPLE, fbo.getQuery(), null, queryName);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getStreamDownloadSource(connectionSettings.getLinkName(), query, Collections.<Integer, String>emptyMap(), tableName, null, getSQLDialect(), fbo.getFormat()));
			}
		catch (RuntimeException e)
			{
			logger.log(Level.WARNING, "runCompareIDs", e);
			model.put("exception", e);
			}
		
		return (model);
		}
	
	private List<ParameterDef> extractParams(FormBackingObject fbo)
		{
		final List<ParameterDef> ret = new ArrayList<ParameterDef>(fbo.getParams().size());
		
		for (int i = 0; i < MAX_PARAMS; i++)
			{
			final ParameterFBO p = fbo.getParams().get(i);
			if ((p != null) && p.isEnabled())
				{
				for (int j = ret.size(); j < i; j++)
					ret.add(new ParameterDefImpl("bind", ColumnType.UNKNOWN, null));
				ret.add(new ParameterDefImpl("bind", p.getType(), null));
				}
			}
		
		return (ret);
		}
	
	private List<Object> translateParams(FormBackingObject fbo)
		{
		final List<Object> ret = new ArrayList<Object>(fbo.getParams().size());
		final DataFormatter fmt = factory.getWebFormatter();
		
		for (int i = 0; i < MAX_PARAMS; i++)
			{
			final ParameterFBO p = fbo.getParams().get(i);
			if ((p != null) && p.isEnabled())
				{
				for (int j = ret.size(); j < i; j++)
					ret.add(null);
				ret.add(fmt.parse(p.getType(), p.getValue()));
				}
			}
		
		return (ret);
		}
	
	private SQLDialect getSQLDialect()
		{
		return (SQLDialectFactory.getSQLDialect(connectionSettings.getDialectName()));
		}
	}
