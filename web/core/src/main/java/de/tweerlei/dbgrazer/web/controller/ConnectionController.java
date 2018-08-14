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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class ConnectionController
	{
	private final LinkService linkService;
	private final FrontendHelperService frontendHelper;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 * @param frontendHelper FrontendHelperService
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public ConnectionController(LinkService linkService, FrontendHelperService frontendHelper,
			UserSettingsManager userSettingsManager,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.linkService = linkService;
		this.frontendHelper = frontendHelper;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Display the connections menu
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/connections.html", method = RequestMethod.GET)
	public Map<String, Object> showConnections()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, Map<String, String>> all = linkService.findAllLinkSets(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
		
		model.put("allConnections", all);
		
		return (model);
		}
	
	/**
	 * Set the session's current connection
	 * @param connection Connection
	 * @return View
	 */
	@RequestMapping(value = "/switch.html", method = RequestMethod.GET)
	public String switchConnection(
			@RequestParam("c") String connection
			)
		{
		final PathInfo pi = connectionSettings.getSourceURL();
		if (pi != null)
			{
			if (MessageKeys.PATH_DB.equals(pi.getCategory()))
				{
				final SchemaDef prevSchema = linkService.getSchema(pi.getSubcategory());
				final SchemaDef newSchema = linkService.getSchema(connection);
				if ((prevSchema != null) && (newSchema != null) && prevSchema.getName().equals(newSchema.getName()))
					return (frontendHelper.buildPath("redirect:" + MessageKeys.PATH_DB, connection, pi.getPage(), pi.getQuery()));
				}
			}
		
		return (frontendHelper.buildPath("redirect:" + MessageKeys.PATH_DB, connection, "index.html", null));
		}
	
	/**
	 * Reload queries
	 * @return View
	 */
	@RequestMapping(value = "/reload-connections.html", method = RequestMethod.GET)
	public String reload()
		{
		if (userSettings.isLinkEditorEnabled())
			linkService.reloadLinks();
		
		return ("redirect:links.html");
		}
	}
