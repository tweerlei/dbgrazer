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

import de.tweerlei.dbgrazer.common.util.impl.NamedSet;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.DownloadService;
import de.tweerlei.dbgrazer.web.service.ExportService;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.service.ResultDownloadService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class RestController
	{
	private final QueryService queryService;
	private final ResultDownloadService resultDownloadService;
	private final DownloadService downloadService;
	private final ExportService exportService;
	private final FrontendExtensionService extensionService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param resultDownloadService ResultDownloadService
	 * @param downloadService DownloadService
	 * @param exportService ExportService
	 * @param extensionService FrontendExtensionService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public RestController(QueryService queryService, ResultDownloadService resultDownloadService,
			DownloadService downloadService, ExportService exportService,
			FrontendExtensionService extensionService,
			ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.resultDownloadService = resultDownloadService;
		this.downloadService = downloadService;
		this.exportService = exportService;
		this.extensionService = extensionService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the index page
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/index.html", method = RequestMethod.GET)
	public Map<String, Object> showWSPage()
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("extensions", extensionService.getRestApiExtensions());
		
		return (model);
		}
	
	/**
	 * Show the logout page
	 */
	@RequestMapping(value = "/ws/*/denied.html", method = RequestMethod.GET)
	public void showLogoutPage()
		{
		}
	
	/**
	 * Show the logout page
	 */
	@RequestMapping(value = "/ws/*/form-result-export.html", method = RequestMethod.GET)
	public void showResultExportForm()
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		}
	
	/**
	 * Show the logout page
	 */
	@RequestMapping(value = "/ws/*/form-query-export.html", method = RequestMethod.GET)
	public void showQueryExportForm()
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		}
	
	/**
	 * Show the logout page
	 */
	@RequestMapping(value = "/ws/*/form-query-fullexport.html", method = RequestMethod.GET)
	public void showQueryFullExportForm()
		{
		if (!connectionSettings.isWsApiEnabled())
			throw new AccessDeniedException();
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/dllinks.html", method = RequestMethod.GET)
	public Map<String, Object> showDownloadFormats()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", resultDownloadService.getSupportedDownloadFormats());
		
		return (model);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/explinks.html", method = RequestMethod.GET)
	public Map<String, Object> showExportFormats()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", exportService.getSupportedExportFormats());
		
		return (model);
		}
	
	/**
	 * Show download menu
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/tbllinks.html", method = RequestMethod.GET)
	public Map<String, Object> showTableDownloadFormats()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("downloadFormats", downloadService.getSupportedDownloadFormats());
		
		return (model);
		}
	
	/**
	 * Show the custom query form
	 * @return Model
	 */
	@RequestMapping(value = "/ws/*/runmodes.html", method = RequestMethod.GET)
	public Map<String, Object> showScriptQueryTypes()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<QueryType> resultTypes;
		
		if (connectionSettings.isWritable())
			resultTypes = queryService.findScriptQueryTypes(connectionSettings.getType());
		else
			resultTypes = new NamedSet<QueryType>();
		
		model.put("resultTypes", resultTypes);
		
		return (model);
		}
	}
