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
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.spring.service.StringTransformerService;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class QuerySettingsController
	{
	private final QueryService queryService;
	private final QuerySettingsManager querySettingsManager;
	private final VisualizationService visualizationService;
	private final StringTransformerService stringTransformerService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param querySettingsManager QuerySettingsManager
	 * @param visualizationService VisualizationService
	 * @param stringTransformerService StringTransformerService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QuerySettingsController(QueryService queryService, QuerySettingsManager querySettingsManager,
			VisualizationService visualizationService, StringTransformerService stringTransformerService,
			ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.querySettingsManager = querySettingsManager;
		this.visualizationService = visualizationService;
		this.stringTransformerService = stringTransformerService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Run a query
	 * @param query Query name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/run-query.html", method = RequestMethod.GET)
	public String runQuery(
			@RequestParam("q") String query
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		if (q == null)
			return ("redirect:index.html");
		
		if (q.getParameters().isEmpty() && !q.getType().isManipulation())
			return ("redirect:result.html?q=" + stringTransformerService.toURL(query));
		
		return ("redirect:query.html?q=" + stringTransformerService.toURL(query));
		}
	
	/**
	 * Set the preferred graph type
	 * @param query Query name
	 * @param type ResultType
	 * @param option Option name
	 * @param category Option code
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/graphtypes.html", method = RequestMethod.GET)
	public Map<String, Object> showGraphTypeMenu(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "setting", required = true) String option,
			@RequestParam(value = "category", required = false) Integer category
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		final String vt = (q == null) ? type : q.getType().getName();
		final int optionCode = (category == null) ? 0 : category.intValue();
		final Set<String> values = visualizationService.getOptionValues(vt, option, optionCode);
		final String current = querySettingsManager.getQuerySettings(q).get(option);
		
		model.put("query", query);
		model.put("setting", option);
		model.put("type", vt);
		model.put("types", values);
		model.put("currentType", values.contains(current) ? current : values.iterator().next());
		
		return (model);
		}
	
	/**
	 * Set the preferred graph type
	 * @param query Query name
	 * @param type ResultType
	 * @param option Option name
	 * @param value Option value
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/graphtype.html", method = RequestMethod.GET)
	public String setGraphType(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "t", required = false) String type,
			@RequestParam(value = "s", required = true) String option,
			@RequestParam(value = "v", required = true) String value
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		final String vt = (q == null) ? type : q.getType().getName();
		
		if (visualizationService.getOptionNames(vt).contains(option))
			querySettingsManager.getQuerySettings(q).put(option, value);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Toggle formatting active/inactive
	 * @param query Query name
	 * @param value New setting
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/formatter.html", method = RequestMethod.GET)
	public String toggleFormatter(
			@RequestParam("q") String query,
			@RequestParam("v") String value
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		querySettingsManager.setFormatName(q, value);
		querySettingsManager.setFormattingActive(q, !StringUtils.empty(value));
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Toggle formatting active/inactive
	 * @param query Query name
	 * @param value New setting
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/formatmode.html", method = RequestMethod.GET)
	public String toggleFormattingActive(
			@RequestParam("q") String query,
			@RequestParam("v") Boolean value
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		querySettingsManager.setFormattingActive(q, value);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Toggle syntax coloring active/inactive
	 * @param query Query name
	 * @param value New setting
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/coloringmode.html", method = RequestMethod.GET)
	public String toggleSyntaxColoringActive(
			@RequestParam("q") String query,
			@RequestParam("v") Boolean value
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		querySettingsManager.setSyntaxColoringActive(q, value);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Toggle line numbering active/inactive
	 * @param query Query name
	 * @param value New setting
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/linemode.html", method = RequestMethod.GET)
	public String toggleLineNumbersActive(
			@RequestParam("q") String query,
			@RequestParam("v") Boolean value
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		querySettingsManager.setLineNumbersActive(q, value);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Toggle the trim columns mode
	 * @param query Query name
	 * @param value New setting
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/trimcols.html", method = RequestMethod.GET)
	public String toggleTrimColumns(
			@RequestParam("q") String query,
			@RequestParam("v") Boolean value
			)
		{
		final Query q = queryService.findQueryByName(connectionSettings.getLinkName(), query);
		
		querySettingsManager.setTrimColumnsActive(q, value);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	}
