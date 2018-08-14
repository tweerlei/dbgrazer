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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.CacheClass;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QueryTransformerService;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.ResultCache;
import de.tweerlei.spring.web.view.ErrorDownloadSource;
import de.tweerlei.spring.web.view.GenericDownloadView;

/**
 * Controller for creating query dependency graph
 * 
 * @author Robert Wruck
 */
@Controller
public class QueryGraphController
	{
	private final QueryService queryService;
	private final QueryTransformerService queryTransformerService;
	private final VisualizationService visualizationService;
	private final FrontendHelperService frontendHelper;
	private final ResultCache resultCache;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param queryTransformerService QueryTransformerService
	 * @param visualizationService VisualizationService
	 * @param frontendHelper FrontendHelperService
	 * @param resultCache ResultCache
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QueryGraphController(QueryService queryService, QueryTransformerService queryTransformerService,
			VisualizationService visualizationService, FrontendHelperService frontendHelper,
			ResultCache resultCache, ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.queryTransformerService = queryTransformerService;
		this.visualizationService = visualizationService;
		this.frontendHelper = frontendHelper;
		this.resultCache = resultCache;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Show the query graph
	 * @param query Query name
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/querygraph.html", method = RequestMethod.GET)
	public Map<String, Object> showQueryGraph(
			@RequestParam("q") String query
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		if (q != null)
			{
			final Visualization def = queryTransformerService.buildGraph(connectionSettings.getLinkName(), query, ViewConstants.IMAGEMAP_ID,
					frontendHelper.buildPath(MessageKeys.PATH_DB, connectionSettings.getLinkName(), "querygraph.html", "q="));
			resultCache.clearCachedObjects(CacheClass.SCHEMA_VISUALIZATION);
			final String key = resultCache.addCachedObject(CacheClass.SCHEMA_VISUALIZATION, def);
			
			model.put(RowSetConstants.ATTR_IMAGE_ID, key);
			model.put(RowSetConstants.ATTR_IMAGEMAP, visualizationService.getHtmlMap(def));
			model.put(RowSetConstants.ATTR_IMAGEMAP_ID, ViewConstants.IMAGEMAP_ID);
			
			model.put("title", query);
			}
		
		return (model);
		}
	
	/**
	 * Show the query graph
	 * @param query Query name
	 * @param key Cache key
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/qgraph.html", method = RequestMethod.GET)
	public Map<String, Object> showGraph(
			@RequestParam("q") String query,
			@RequestParam("key") String key
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Visualization def = resultCache.getCachedObject(CacheClass.SCHEMA_VISUALIZATION, key, Visualization.class);
		if (def == null)
			{
			logger.log(Level.WARNING, "Cached graph not found: " + key);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getVisualizationDownloadSource(def, query));
		
		return (model);
		}
	
	/**
	 * Show the query graph
	 * @param query Query name
	 * @param key Cache key
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/qgraph-source.html", method = RequestMethod.GET)
	public Map<String, Object> showGraphSource(
			@RequestParam("q") String query,
			@RequestParam("key") String key
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Visualization def = resultCache.getCachedObject(CacheClass.SCHEMA_VISUALIZATION, key, Visualization.class);
		if (def == null)
			{
			logger.log(Level.WARNING, "Cached graph not found: " + key);
			
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new ErrorDownloadSource());
			}
		else
			model.put(GenericDownloadView.SOURCE_ATTRIBUTE, visualizationService.getSourceTextDownloadSource(def, query));
		
		return (model);
		}
	}
