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
package de.tweerlei.dbgrazer.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.model.QueryGroupVisitor;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultOrientation;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.ConfigKeys;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.exception.AjaxRedirectException;
import de.tweerlei.dbgrazer.web.exception.QueryException;
import de.tweerlei.dbgrazer.web.exception.QueryNotFoundException;
import de.tweerlei.dbgrazer.web.exception.RedirectException;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.QueryParameters;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.ExportService;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QueryPerformerService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.service.StringTransformerService;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for running queries
 * 
 * @author Robert Wruck
 */
@Controller
public class QueryRunController
	{
	private static final List<Integer> PKCOLUMN_0 = Collections.singletonList(0);
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private Query query;
		private final Map<Integer, String> params;
		private final Map<Integer, MultipartFile> files;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.params = new TreeMap<Integer, String>();
			this.files = new TreeMap<Integer, MultipartFile>();
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, String> getParams()
			{
			return params;
			}
		
		/**
		 * Get the files
		 * @return the files
		 */
		public Map<Integer, MultipartFile> getFileparams()
			{
			return files;
			}
		
		/**
		 * Get the selected
		 * @return the selected
		 */
		public Query getQuery()
			{
			return query;
			}

		/**
		 * Set the selected
		 * @param query the selected to set
		 */
		public void setQuery(Query query)
			{
			this.query = query;
			}
		
		/**
		 * Get the settings
		 * @return the settings
		 */
		public Map<Integer, String> getEffectiveParams()
			{
			if (files.isEmpty())
				return (params);
			
			try	{
				final Map<Integer, String> ret = new TreeMap<Integer, String>(params);
				for (Map.Entry<Integer, MultipartFile> ent : files.entrySet())
					ret.put(ent.getKey(), new String(ent.getValue().getBytes(), "UTF-8"));
				
				return (ret);
				}
			catch (IOException e)
				{
				throw new RuntimeException(e);
				}
			}
		}
	
	private static class VisualizationBuilder implements Runnable
		{
		private final VisualizationService visualizationService;
		private final QuerySettingsManager querySettingsManager;
		private final FrontendHelperService frontendHelper;
		private final DataFormatter fmt;
		private final Result result;
		private final String imageMapID;
		private final String nodeLink;
		private final Collection<String> params;
		
		private Visualization def;
		
		public VisualizationBuilder(VisualizationService visualizationService, QuerySettingsManager querySettingsManager, FrontendHelperService frontendHelper, DataFormatter fmt,
				Result result, String imageMapID, String nodeLink, Collection<String> params)
			{
			this.visualizationService = visualizationService;
			this.querySettingsManager = querySettingsManager;
			this.frontendHelper = frontendHelper;
			this.fmt = fmt;
			this.result = result;
			this.imageMapID = imageMapID;
			this.nodeLink = nodeLink;
			this.params = params;
			this.def = null;
			}
		
		@Override
		public void run()
			{
			def = visualizationService.build(result, fmt, imageMapID, frontendHelper.getQuerySubtitle(result.getQuery(), params), nodeLink,
					querySettingsManager.getQuerySettings(result.getQuery()));
			}
		
		public Visualization getVisualization()
			{
			if (def == null)
				run();
			return (def);
			}
		}
	
	private static class RelatedVisitor implements QueryGroupVisitor
		{
		private final int paramCount;
		private final ResultRow related;
		private final ResultRow searches;
		
		public RelatedVisitor(Query query, RowSet rs)
			{
			this.paramCount = query.getParameters().size();
			this.related = rs.getRows().get(0);
			this.searches = rs.getRows().get(1);
			}
		
		@Override
		public final boolean visitList(Query q)
			{
			return (visitQuery(q));
			}
		
		@Override
		public final boolean visitView(Query q)
			{
			return (visitQuery(q));
			}
		
		@Override
		public final boolean visitListView(Query q)
			{
//			return (visitQuery(q));
			return false;
			}
		
		@Override
		public final boolean visitSubquery(Query q)
			{
//			return (visitQuery(q));
			return false;
			}
		
		@Override
		public final boolean visitAction(Query q)
			{
//			return (visitQuery(q));
			return false;
			}
		
		@Override
		public boolean visitQuery(Query q)
			{
			if (q.getParameters().size() <= paramCount)
				related.getValues().add(q);
			else
				searches.getValues().add(q);
			
			return false;
			}
		}
	
	private final ConfigAccessor configService;
	private final QueryService queryService;
	private final QueryPerformerService runner;
	private final DataFormatterFactory factory;
	private final ResultDownloadService downloadService;
	private final ExportService exportService;
	private final VisualizationService visualizationService;
	private final ResultTransformerService resultTransformer;
	private final FrontendHelperService frontendHelper;
	private final FrontendExtensionService extensionService;
	private final QuerySettingsManager querySettingsManager;
	private final StringTransformerService stringTransformerService;
	private final TextTransformerService textFormatterService;
	private final ResultCache resultCache;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param queryService QueryService
	 * @param runner QueryPerformerService
	 * @param resultCache ResultCache
	 * @param connectionSettings ConnectionSettings
	 * @param factory DataFormatterFactory
	 * @param downloadService ResultDownloadService
	 * @param exportService ExportService
	 * @param visualizationService VisualizationService
	 * @param resultTransformer ResultTransformerService
	 * @param frontendHelper FrontendHelperService
	 * @param extensionService FrontendExtensionService
	 * @param querySettingsManager QuerySettingsManager
	 * @param stringTransformerService StringTransformerService
	 * @param textFormatterService TextFormatterService
	 */
	@Autowired
	public QueryRunController(ConfigAccessor configService, QueryService queryService,
			QueryPerformerService runner, ResultDownloadService downloadService, StringTransformerService stringTransformerService,
			DataFormatterFactory factory, ResultTransformerService resultTransformer, FrontendHelperService frontendHelper,
			ExportService exportService, VisualizationService visualizationService, QuerySettingsManager querySettingsManager,
			TextTransformerService textFormatterService, FrontendExtensionService extensionService,
			ResultCache resultCache, ConnectionSettings connectionSettings)
		{
		this.configService = configService;
		this.queryService = queryService;
		this.runner = runner;
		this.factory = factory;
		this.downloadService = downloadService;
		this.exportService = exportService;
		this.visualizationService = visualizationService;
		this.resultTransformer = resultTransformer;
		this.frontendHelper = frontendHelper;
		this.extensionService = extensionService;
		this.querySettingsManager = querySettingsManager;
		this.stringTransformerService = stringTransformerService;
		this.textFormatterService = textFormatterService;
		this.resultCache = resultCache;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Get the FormBackingObject
	 * @param query Query name
	 * @return FormBackingObject
	 * @throws QueryNotFoundException if the query name is invalid
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(
			@RequestParam("q") String query
			) throws QueryNotFoundException
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		if (q == null)
			throw new QueryNotFoundException(query);
		
		connectionSettings.setQueryGroup(q.getGroupName());
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		final FormBackingObject ret = new FormBackingObject();
		ret.setQuery(q);
		ret.getParams().putAll(querySettingsManager.getDefaultParameterValues(q, fmt));
		
		return (ret);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/query.html", method = RequestMethod.GET)
	public Map<String, Object> showQueryForm(@ModelAttribute("model") FormBackingObject fbo)
		{
		return (showQueryForm(fbo.getQuery()));
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/query.html", method = RequestMethod.GET)
	public Map<String, Object> showAjaxQueryForm(@ModelAttribute("model") FormBackingObject fbo)
		{
		return (showQueryForm(fbo.getQuery()));
		}
	
	private Map<String, Object> showQueryForm(Query query)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<Integer, String> values = new HashMap<Integer, String>();
		
		int i = 0 ;
		for (ParameterDef p : query.getParameters())
			{
			if (!StringUtils.empty(p.getValueQuery()))
				values.put(i, p.getValueQuery());
			i++;
			}
		
		model.put("values", values);
		model.put("extensions", extensionService.getQueryViewExtensions(query));
		model.put("extensionJS", extensionService.getQueryViewJS(query));
		model.put("tableColumns", Collections.emptyList());
		
		return (model);
		}
	
	/**
	 * Show available values from a value quers
	 * @param fbo FormBackingObject
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/select-value.html", method = RequestMethod.GET)
	public Map<String, Object> performValueQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("v") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		final Result r = performQuery(connectionSettings.getLinkName(), new QueryParameters(fbo.getQuery()));
		final Map<String, String> values = resultTransformer.convertToMap(r.getFirstRowSet(), fmt);
		
		model.put("values", values);
		model.put("title", fbo.getQuery().getName());
		model.put("value", selected);
		model.put("target", target);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @return Model
	 * @throws RedirectException when trying to perform an query with not historizable parameters
	 */
	@RequestMapping(value = "/db/*/result.html", method = RequestMethod.GET)
	public Map<String, Object> performQuery(@ModelAttribute("model") FormBackingObject fbo) throws RedirectException
		{
		if (fbo.getQuery().getType().isManipulation() || querySettingsManager.hasFilteredParameters(fbo.getQuery()))
			throw new RedirectException("query.html?q=" + stringTransformerService.toURL(fbo.getQuery().getName()));
		
		return (performQuery(fbo, true, true));
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param historize Whether to create a history entry
	 * @param showRelated Whether to show related queries
	 * @return Model
	 * @throws AjaxRedirectException when trying to perform an explorer query
	 */
	@RequestMapping(value = "/db/*/ajax/result.html", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> performAjaxQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam(value = "historize", required = false) Boolean historize,
			@RequestParam(value = "related", required = false) Boolean showRelated
			) throws AjaxRedirectException
		{
		if (fbo.getQuery().getType().isExplorer())
			{
			throw new AjaxRedirectException(frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "result.html",
					"q=" + stringTransformerService.toURL(fbo.getQuery().getName()) + frontendHelper.getQueryParams(fbo.getParams(), false)));
			}
		
		return (performQuery(fbo, (historize != null) && historize.booleanValue(), (showRelated == null) || showRelated.booleanValue()));
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param level Drill down level
	 * @return Model
	 * @throws AjaxRedirectException when trying to perform an explorer query
	 */
	@RequestMapping(value = "/db/*/ajax/drilldown.html", method = RequestMethod.GET)
	public Map<String, Object> performDrilldownQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam(value = "level", required = true) Integer level
			) throws AjaxRedirectException
		{
		if (fbo.getQuery().getType().isExplorer())
			{
			throw new AjaxRedirectException(frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "result.html",
					"q=" + stringTransformerService.toURL(fbo.getQuery().getName()) + frontendHelper.getQueryParams(fbo.getParams(), false)));
			}
		
		final Map<String, Object> model = performQuery(fbo, false, false);
		
		model.put("level", getNextLevel(level));
		
		return (model);
		}
	
	private int getNextLevel(Integer level)
		{
		if (level == null)
			return (0);
		
		final int nextLevel = level + 1;
		final int maxLevel = configService.get(ConfigKeys.DRILLDOWN_LEVELS);
		
		return ((nextLevel < maxLevel) ? nextLevel : level);
		}
	
	private Map<String, Object> performQuery(FormBackingObject fbo, boolean historize, boolean showRelated)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		
		final Map<String, Result> results = performRecursiveQuery(connectionSettings.getLinkName(), query);
		
		// query successful, historize
		if (historize && querySettingsManager.isHistoryEnabled())
			querySettingsManager.addHistoryEntry(query.getQuery(), query.getVisibleParameters());
		
		final Map<String, TabItem<RowSet>> rowSets = new LinkedHashMap<String, TabItem<RowSet>>();
		int imageIndex = 0;
		
		resultCache.clearCachedObjects(CacheClass.RESULT_VISUALIZATION);
		
		// Show related queries on top for PANELS and DASHBOARD
		if (showRelated && (query.getQuery().getType().getOrientation() != ResultOrientation.ACROSS))
			addRelatedQueries(rowSets, query);
		
		for (Map.Entry<String, Result> ent : results.entrySet())
			{
			final Result r = ent.getValue();
			if (r.getQuery().getType().getResultType() == ResultType.VISUALIZATION)
				{
				final Map<Integer, String> effectiveParams = querySettingsManager.buildParameterMap(CollectionUtils.concat(r.getParameterValues(), query.getEffectiveParameters()));
				
				final RowSetImpl rowSet = new RowSetImpl(r.getQuery(), RowSetConstants.INDEX_VISUALIZATION, null);
				rowSets.put(ent.getKey(), new TabItem<RowSet>(rowSet, -1, r.getQuery().getName(), effectiveParams, frontendHelper.getQueryParams(effectiveParams, true)));
				
				if (!r.getFirstRowSet().getRows().isEmpty())
					{
					final String nodeLink = getLink(r);
					final String imageMapID = ViewConstants.IMAGEMAP_ID + imageIndex++;
					
					final Visualization dotString = new VisualizationBuilder(visualizationService, querySettingsManager, frontendHelper, fmt, r, imageMapID, nodeLink, effectiveParams.values()).getVisualization();
					if (dotString != null)
						{
						final String key = resultCache.addCachedObject(CacheClass.RESULT_VISUALIZATION, dotString);
						rowSet.getAttributes().put(RowSetConstants.ATTR_IMAGE_ID, key);
						
						if (visualizationService.supportsSourceSVG(dotString))
							rowSet.getAttributes().put(RowSetConstants.ATTR_SVG, visualizationService.getSourceSVG(dotString));
						else if (!r.getQuery().getType().getName().equals(VisualizationSettings.GRAPH_QUERY_TYPE) || (nodeLink != null))
							{
							final String map = visualizationService.getHtmlMap(dotString);
							rowSet.getAttributes().put(RowSetConstants.ATTR_IMAGEMAP, map);
							rowSet.getAttributes().put(RowSetConstants.ATTR_IMAGEMAP_ID, imageMapID);
							}
						
						rowSet.getAttributes().put(RowSetConstants.ATTR_OPTION_CODE, dotString.getOptionCode());
						rowSet.getAttributes().put(RowSetConstants.ATTR_OPTION_NAMES, visualizationService.getOptionNames(r.getQuery().getType().getName()));
						rowSet.getAttributes().put(RowSetConstants.ATTR_SOURCE_TEXT, visualizationService.supportsSourceText(dotString));
						}
					}
				
				// Don't show subqueries when the visualization is a subquery itself
				if (query.getQuery().getType().getResultType() != ResultType.RECURSIVE)
					{
					if (r.getQuery().getType().getName().equals(VisualizationSettings.GRAPH_QUERY_TYPE))
						addRowSets(rowSets, r, query.getAllParameters(), fmt, configService.get(ConfigKeys.SHOW_GRAPH_SUBQUERIES) ? 0 : 2, false);
					else if (configService.get(ConfigKeys.SHOW_CHART_SUBQUERIES))
						addRowSets(rowSets, r, query.getAllParameters(), fmt, 0, false);
					}
				}
			else if (r.getQuery().getType().getName().equals(VisualizationSettings.TREE_QUERY_TYPE))
				{
				final Map<Integer, String> effectiveParams = querySettingsManager.buildParameterMap(CollectionUtils.concat(r.getParameterValues(), query.getAllParameters()));
				
				final RowSetImpl rowSet = new RowSetImpl(r.getFirstRowSet().getQuery(), RowSetConstants.INDEX_TREE, r.getFirstRowSet().getColumns());
				rowSet.setMoreAvailable(r.getFirstRowSet().isMoreAvailable());
				rowSet.setQueryTime(r.getFirstRowSet().getQueryTime());
				rowSet.getRows().addAll(r.getFirstRowSet().getRows());
				
				rowSet.getAttributes().put(RowSetConstants.ATTR_PARENT_QUERY, r.getQuery());
				rowSet.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, r.getQuery().getSubQueries().size() > 1);
				rowSet.getAttributes().put(RowSetConstants.ATTR_EXPAND_LEVELS, querySettingsManager.getAdditionalParameters(r.getQuery(), fbo.getParams()));
				resultTransformer.translateRowSet(rowSet, fmt);
				rowSets.put(ent.getKey(), new TabItem<RowSet>(rowSet, rowSet.getRows().size(), r.getQuery().getName(), effectiveParams, frontendHelper.getQueryParams(effectiveParams, true)));
				
				// Don't show subqueries when the tree is a subquery itself
				if (query.getQuery().getType().getResultType() != ResultType.RECURSIVE)
					{
					if (configService.get(ConfigKeys.SHOW_TREE_SUBQUERIES));
						addRowSets(rowSets, r, query.getAllParameters(), fmt, 1, false);
					}
				}
			else if (r.getQuery().getType().getName().equals(VisualizationSettings.MULTILEVEL_QUERY_TYPE))
				{
				for (Map.Entry<String, RowSet> rent : r.getRowSets().entrySet())
					{
					final Map<Integer, String> effectiveParams = querySettingsManager.buildParameterMap(CollectionUtils.concat(rent.getValue().getParameterValues(), query.getAllParameters()));
					
					final RowSetImpl rowSet = new RowSetImpl(rent.getValue().getQuery(), RowSetConstants.INDEX_MULTILEVEL, rent.getValue().getColumns());
					rowSet.setMoreAvailable(rent.getValue().isMoreAvailable());
					rowSet.setQueryTime(rent.getValue().getQueryTime());
					rowSet.getRows().addAll(rent.getValue().getRows());
					
					rowSet.getAttributes().put(RowSetConstants.ATTR_PARENT_QUERY, r.getQuery());
					rowSet.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, r.getQuery().getSubQueries().size() > 1);
					rowSet.getAttributes().put(RowSetConstants.ATTR_EXPAND_LEVELS, querySettingsManager.getAdditionalParameters(r.getQuery(), fbo.getParams()));
					resultTransformer.translateRowSet(rowSet, fmt);
					rowSets.put(rent.getKey(), new TabItem<RowSet>(rowSet, rowSet.getRows().size(), rowSet.getQuery().getName(), effectiveParams, frontendHelper.getQueryParams(effectiveParams, true)));
					}
				
				model.put("subquery", r.getFirstRowSet().getQuery());
				}
			else
				addRowSets(rowSets, r, query.getAllParameters(), fmt, 0, querySettingsManager.isTrimColumnsActive(r.getQuery()));
			}
		
		// Show related queries last for all other types
		if (query.getQuery().getType().getName().equals(VisualizationSettings.NAVIGATOR_QUERY_TYPE))
			model.put("rs", getNavigatorQueries(query.getQuery()));
		else if (showRelated && (query.getQuery().getType().getOrientation() == ResultOrientation.ACROSS))
			addRelatedQueries(rowSets, query);
		
		model.put("results", rowSets);
		model.put("title", frontendHelper.getQueryTitle(query.getQuery().getName(), query.getVisibleParameters()));
		model.put("paramString", frontendHelper.getQueryParams(fbo.getParams(), true));
		model.put("formats", exportService.getSupportedExportFormats());
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		model.put("formatters", textFormatterService.getSupportedTextFormats());
		model.put("extensions", extensionService.getQueryViewExtensions(query.getQuery()));
		model.put("extensionJS", extensionService.getQueryViewJS(query.getQuery()));
		model.put("pkColumns", PKCOLUMN_0);
		
		// If a second subquery is defined for an EXPLORER view, load it as initial detail view
		if (query.getQuery().getType().getName().equals(VisualizationSettings.EXPLORER_QUERY_TYPE) && (query.getQuery().getSubQueries().size() > 1))
			model.put("detailQuery", query.getQuery().getSubQueries().get(1).getName());
		
		if (query.getQuery().getType().getSubQueryResolver() != null)
			model.put("additionalParams", query.getQuery().getType().getSubQueryResolver().getAdditionalParameters(query.getQuery()));
		
		final List<List<ColumnDef>> tableColumns = new ArrayList<List<ColumnDef>>(rowSets.size());
		for (TabItem<RowSet> rs : rowSets.values())
			{
			if ((rs.getPayload().getQuery() == null) || (rs.getPayload().getQuery().getType().getResultType() != ResultType.TABLE))
				tableColumns.add(null);
			else if (rs.getPayload().getQuery().getType().isColumnPrefixed())
				{
				// Aggregate results are shown with a leading COUNT column
				final List<ColumnDef> cols = new ArrayList<ColumnDef>(rs.getPayload().getColumns().size() + 1);
				cols.add(new ColumnDefImpl(null, ColumnType.INTEGER, null, null, null, null));
				cols.addAll(rs.getPayload().getColumns());
				tableColumns.add(cols);
				}
			else
				tableColumns.add(rs.getPayload().getColumns());
			}
		model.put("tableColumns", tableColumns);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/result-export.html", method = RequestMethod.GET)
	public Map<String, Object> performExportQuery(@ModelAttribute("model") final FormBackingObject fbo,
			@RequestParam("format") String format
			)
		{
		return (performExportQueryInternal(fbo, format));
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/result-export.html", method = RequestMethod.GET)
	public Map<String, Object> performWSExportQuery(@ModelAttribute("model") final FormBackingObject fbo,
			@RequestParam("format") String format
			)
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		
		return (performExportQueryInternal(fbo, format));
		}
	
	private Map<String, Object> performExportQueryInternal(final FormBackingObject fbo,
			String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		
		final Map<String, Result> results = performRecursiveQuery(connectionSettings.getLinkName(), query);
		
		final Result result = new ResultImpl(fbo.getQuery());
		for (Map.Entry<String, Result> ent : results.entrySet())
			{
			final Result r = ent.getValue();
			if ((r.getQuery().getType().getResultType() == ResultType.VISUALIZATION) && !r.getFirstRowSet().getRows().isEmpty())
				{
				final RowSetImpl rowSet = new RowSetImpl(r.getQuery(), RowSetConstants.INDEX_VISUALIZATION, null);
				result.getRowSets().put(ent.getKey(), rowSet);
				
				final VisualizationBuilder v = new VisualizationBuilder(visualizationService, querySettingsManager, frontendHelper, fmt, r, ViewConstants.IMAGEMAP_ID, null, query.getEffectiveParameters());
				factory.doWithDefaultTheme(v);
				rowSet.getAttributes().put(RowSetConstants.ATTR_VISUALIZATION, v.getVisualization());
				
				if (r.getQuery().getType().getName().equals(VisualizationSettings.GRAPH_QUERY_TYPE))
					addRowSets(result, r, configService.get(ConfigKeys.SHOW_GRAPH_SUBQUERIES) ? 0 : 2);
				else if (configService.get(ConfigKeys.SHOW_CHART_SUBQUERIES))
					addRowSets(result, r, 0);
				}
			else if (r.getQuery().getType().getName().equals(VisualizationSettings.TREE_QUERY_TYPE) && !r.getFirstRowSet().getRows().isEmpty())
				{
				result.getRowSets().put(ent.getKey(), r.getFirstRowSet());
				}
			else
				addRowSets(result, r, 0);
			}
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, exportService.getExportDownloadSource(result, format));
		
		return (model);
		}
	
	private void addRelatedQueries(Map<String, TabItem<RowSet>> rowSets, QueryParameters query)
		{
		final RowSet rowSet = getRelatedQueries(query.getQuery());
		if (rowSet != null)
			{
			rowSets.put(MessageKeys.EMPTY_TAB, new TabItem<RowSet>(
					rowSet,
					-1, //rowSet.getRows().size()
					null,
					query.getActualParameters(),
					frontendHelper.getQueryParams(query.getActualParameters(), true)
					));
			}
		}
	
	private RowSet getNavigatorQueries(Query query)
		{
		if (query.getSubQueries().size() <= 1)
			{
			// Show related queries
			final RowSet rowSet = getRelatedQueries(query);
			if (rowSet != null)
				return (rowSet);
			}
		
		final RowSetImpl rowSet = new RowSetImpl(null, RowSetConstants.INDEX_RELATED, null);
		rowSet.getAttributes().put(RowSetConstants.ATTR_PARENT_QUERY, query);
		
		if (query.getSubQueries().size() > 1)
			{
			// Show defined subqueries
			final ResultRow tmp = new DefaultResultRow(query.getSubQueries().size());
			for (SubQueryDef s : query.getSubQueries())
				{
				final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), s.getName());
				if (q != null)
					tmp.getValues().add(q);
				}
			rowSet.getRows().add(tmp);
			}
		
		return (rowSet);
		}
	
	private RowSet getRelatedQueries(Query query)
		{
		final QueryGroup all = queryService.groupRelatedQueries(connectionSettings.getLinkName(), query.getName());
		if (!all.getViews().isEmpty() || !all.getQueries().isEmpty() || !all.getLists().isEmpty())
			{
			final ResultRow tmp = new DefaultResultRow(all.getViews().size() + all.getQueries().size() + all.getLists().size());
			final ResultRow tmp2 = new DefaultResultRow(all.getViews().size() + all.getQueries().size() + all.getLists().size());
			
			final RowSetImpl rowSet = new RowSetImpl(null, RowSetConstants.INDEX_RELATED, null);
			rowSet.getAttributes().put(RowSetConstants.ATTR_PARENT_QUERY, query);
			rowSet.getRows().add(tmp);
			rowSet.getRows().add(tmp2);
			
			all.accept(new RelatedVisitor(query, rowSet));
			
			return (rowSet);
			}
		
		return (null);
		}
	
	private void addRowSets(Map<String, TabItem<RowSet>> rowSets, Result r, Collection<String> params, DataFormatter fmt, int skip, boolean trim)
		{
		int i = 0;
		for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
			{
			if (++i > skip)
				{
				if (ent.getValue().getColumns() != null)
					{
					if (ent.getValue().getQuery().getType().getResultType() == ResultType.SINGLE)
						resultTransformer.formatRowSet(ent.getValue(), fmt, querySettingsManager.getFormatName(ent.getValue().getQuery()), querySettingsManager.getFormatOptions(ent.getValue().getQuery()));
					else
						{
						final boolean depth = true;
						final boolean sum = ent.getValue().getQuery().getAttributes().containsKey(RowSetConstants.ATTR_SUM_ROW);
						resultTransformer.translateRowSet(ent.getValue(), fmt, trim, depth, sum);
						}
					}
				
				ent.getValue().getAttributes().putAll(querySettingsManager.getQuerySettings(ent.getValue().getQuery()));
				
				final Map<Integer, String> effectiveParams = querySettingsManager.buildParameterMap(CollectionUtils.concat(ent.getValue().getParameterValues(), params));
				
				rowSets.put(ent.getKey(), new TabItem<RowSet>(
						ent.getValue(),
						(ent.getValue().getSubQueryIndex() < 0) ? -1 : ent.getValue().getRows().size(),
						(ent.getValue().getQuery() == null) ? null : ent.getValue().getQuery().getName(),
						effectiveParams,
						(ent.getValue().getQuery() == null) ? null : frontendHelper.getQueryParams(effectiveParams, true)
						));
				}
			}
		}
	
	private void addRowSets(Result out, Result r, int skip)
		{
		int i = 0;
		for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
			{
			if (++i > skip)
				{
				out.getRowSets().put(ent.getKey(), ent.getValue());
				}
			}
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param subQueryIndex Subquery index
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/query-export.html", method = RequestMethod.GET)
	public Map<String, Object> performSubQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("index") Integer subQueryIndex,
			@RequestParam("format") String format
			)
		{
		return (performSubQueryInternal(fbo, subQueryIndex, format));
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param subQueryIndex Subquery index
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/query-export.html", method = RequestMethod.GET)
	public Map<String, Object> performWSSubQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam(value = "index", required = false) Integer subQueryIndex,
			@RequestParam("format") String format
			)
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		
		final int sqi = (subQueryIndex == null) ? 0 : subQueryIndex;
		
		return (performSubQueryInternal(fbo, sqi, format));
		}
	
	private Map<String, Object> performSubQueryInternal(FormBackingObject fbo,
			int subQueryIndex,
			String format
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		final Result r = performQuery(connectionSettings.getLinkName(), query);
		
		RowSet rowSet = null;
		for (RowSet rs : r.getRowSets().values())
			{
			rowSet = rs;
			if (rs.getSubQueryIndex() == subQueryIndex)
				break;
			}
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getDownloadSource(connectionSettings.getLinkName(), rowSet, format));
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/query-fullexport.html", method = RequestMethod.GET)
	public Map<String, Object> performFullQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("format") String format
			)
		{
		return (performFullQueryInternal(fbo, format));
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param format Export format
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/query-fullexport.html", method = RequestMethod.GET)
	public Map<String, Object> performWSFullQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("format") String format
			)
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		
		return (performFullQueryInternal(fbo, format));
		}
	
	private Map<String, Object> performFullQueryInternal(FormBackingObject fbo, String format)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, downloadService.getStreamDownloadSource(connectionSettings.getLinkName(), query, format));
		
		return (model);
		}
	
	/**
	 * Create a graph image for a GRAPH type query
	 * @param fbo FormBackingObject
	 * @param key Cache key
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/graph.html", method = RequestMethod.GET)
	public Map<String, Object> performGraphQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("key") String key
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Visualization dotString = resultCache.getCachedObject(CacheClass.RESULT_VISUALIZATION, key, Visualization.class);
		if (dotString == null)
			{
			logger.log(Level.WARNING, "Cached graph not found: " + key);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getVisualizationDownloadSource(dotString, fbo.getQuery().getName()));
		
		return (model);
		}
	
	/**
	 * Create a graph source text for a GRAPH type query
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/graph-source.html", method = RequestMethod.GET)
	public Map<String, Object> performGraphSourceQuery(@ModelAttribute("model") final FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		final Map<String, Result> results = performRecursiveQuery(connectionSettings.getLinkName(), query);
		
		final Result r = results.values().iterator().next();
		if ((r.getQuery().getType().getResultType() == ResultType.VISUALIZATION) && !r.getFirstRowSet().getRows().isEmpty())
			{
			final VisualizationBuilder v = new VisualizationBuilder(visualizationService, querySettingsManager, frontendHelper, fmt, r, ViewConstants.IMAGEMAP_ID, null, fbo.getParams().values());
			factory.doWithDefaultTheme(v);
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getSourceTextDownloadSource(v.getVisualization(), fbo.getQuery().getName()));
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
		
		return (model);
		}
	
	/**
	 * Create a graph source text for a GRAPH type query
	 * @param fbo FormBackingObject
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/graph-image.html", method = RequestMethod.GET)
	public Map<String, Object> performGraphImageQuery(@ModelAttribute("model") final FormBackingObject fbo)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final DataFormatter fmt = factory.getWebFormatter();
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		final Map<String, Result> results = performRecursiveQuery(connectionSettings.getLinkName(), query);
		
		final Result r = results.values().iterator().next();
		if ((r.getQuery().getType().getResultType() == ResultType.VISUALIZATION) && !r.getFirstRowSet().getRows().isEmpty())
			{
			final VisualizationBuilder v = new VisualizationBuilder(visualizationService, querySettingsManager, frontendHelper, fmt, r, ViewConstants.IMAGEMAP_ID, null, fbo.getParams().values());
			factory.doWithDefaultTheme(v);
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getImageDownloadSource(v.getVisualization(), fbo.getQuery().getName()));
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param label Element label
	 * @param level Current tree level
	 * @param left Tree lines
	 * @param target Target DOM element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/tree.html", method = RequestMethod.GET)
	public Map<String, Object> performTreeQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("label") String label,
			@RequestParam("level") Integer level,
			@RequestParam("left") String left,
			@RequestParam(value = "target", required = false) String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!fbo.getQuery().getType().getName().equals(VisualizationSettings.TREE_QUERY_TYPE))
			{
			logger.log(Level.WARNING, "performTreeQuery: Not a tree query: " + fbo.getQuery().getName());
			return (model);
			}
		
		final DataFormatter fmt = factory.getWebFormatter();
		
		try	{
			final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getQuery().getSubQueries().get(level).getName());
			final QueryParameters query = querySettingsManager.prepareParameters(q, fbo.getEffectiveParams());
			final Result r = performQuery(connectionSettings.getLinkName(), query);
			
			boolean allEmpty = true;
			for (RowSet rs : r.getRowSets().values())
				{
				rs.getAttributes().put(RowSetConstants.ATTR_PARENT_QUERY, fbo.getQuery());
				rs.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, fbo.getQuery().getSubQueries().size() > level + 1);
				if (!rs.getRows().isEmpty())
					{
					resultTransformer.translateRowSet(rs, fmt);
					allEmpty = false;
					}
				}
			
			model.put("result", r);
			model.put("allEmpty", allEmpty);
			model.put("label", label);
			model.put("level", level + 1);
			model.put("left", StringUtils.empty(left) ? left : (left + "-"));
			model.put("targetElement", target);
			}
		catch (QueryException e)
			{
			model.putAll(e.getModel());
			}
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param level Current tree level
	 * @param target Target DOM element
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/multilevel.html", method = RequestMethod.GET)
	public Map<String, Object> performMultilevelQuery(@ModelAttribute("model") FormBackingObject fbo,
			@RequestParam("level") Integer level,
			@RequestParam(value = "target", required = false) String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		if (!fbo.getQuery().getType().getName().equals(VisualizationSettings.MULTILEVEL_QUERY_TYPE))
			{
			logger.log(Level.WARNING, "performTreeQuery: Not a multi level query: " + fbo.getQuery().getName());
			return (model);
			}
		
		final DataFormatter fmt = factory.getWebFormatter();
		final QueryParameters query = querySettingsManager.prepareParameters(fbo.getQuery(), fbo.getEffectiveParams());
		final List<String> allParams = query.getAllParameters();
		Collections.reverse(allParams);
		final Map<Integer, String> params = querySettingsManager.buildParameterMap(allParams);
		
		try	{
			final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), fbo.getQuery().getSubQueries().get(level).getName());
			final Result r = performQuery(connectionSettings.getLinkName(), query);
			final Map<String, TabItem<RowSet>> rowSets = new LinkedHashMap<String, TabItem<RowSet>>();
			
			boolean allEmpty = true;
			for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
				{
				final Map<Integer, String> effectiveParams = querySettingsManager.buildParameterMap(CollectionUtils.concat(ent.getValue().getParameterValues(), query.getAllParameters()));
				
				final RowSetImpl rowSet = new RowSetImpl(ent.getValue().getQuery(), RowSetConstants.INDEX_MULTILEVEL, ent.getValue().getColumns());
				rowSet.setMoreAvailable(ent.getValue().isMoreAvailable());
				rowSet.setQueryTime(ent.getValue().getQueryTime());
				rowSet.getRows().addAll(ent.getValue().getRows());
				rowSet.getAttributes().put(RowSetConstants.ATTR_PARENT_QUERY, fbo.getQuery());
				rowSet.getAttributes().put(RowSetConstants.ATTR_MORE_LEVELS, fbo.getQuery().getSubQueries().size() > level + 1);
				if (!rowSet.getRows().isEmpty())
					{
					resultTransformer.translateRowSet(rowSet, fmt);
					allEmpty = false;
					}
				rowSets.put(ent.getKey(), new TabItem<RowSet>(rowSet, rowSet.getRows().size(), rowSet.getQuery().getName(), effectiveParams, frontendHelper.getQueryParams(effectiveParams, true)));
				}
			
			model.put("subquery", q);
			model.put("results", rowSets);
			model.put("allEmpty", allEmpty);
			model.put("level", level + 1);
			model.put("params", params);
			model.put("levelParams", frontendHelper.getQueryParams(params, true));
			model.put("targetElement", target);
			}
		catch (QueryException e)
			{
			model.putAll(e.getModel());
			}
		
		return (model);
		}
	
	private String getLink(Result r)
		{
		final TargetDef target = r.getFirstRowSet().getQuery().getTargetQueries().get(0);
		if ((target == null) || target.isParameter())
			return (null);
		
		return (frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "result.html", "q=" + target.getQueryName()));
		}
	
	private Result performQuery(String connection, QueryParameters query)
		{
		try	{
			return (runner.performQuery(connection, query));
			}
		catch (PerformQueryException e)
			{
			throw new QueryException(e.getQueryName(), query.getQuery().getName(), e.getCause());
			}
		catch (RuntimeException e)
			{
			throw new QueryException(query.getQuery().getName(), null, e);
			}
		}
	
	private Map<String, Result> performRecursiveQuery(String connection, QueryParameters query)
		{
		try	{
			return (runner.performRecursiveQuery(connection, query));
			}
		catch (PerformQueryException e)
			{
			throw new QueryException(e.getQueryName(), query.getQuery().getName(), e.getCause());
			}
		catch (RuntimeException e)
			{
			throw new QueryException(query.getQuery().getName(), null, e);
			}
		}
	}
