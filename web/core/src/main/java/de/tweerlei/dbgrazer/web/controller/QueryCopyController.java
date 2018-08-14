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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class QueryCopyController
	{
	private final LinkService linkService;
	private final QueryService queryService;
	private final FrontendHelperService frontendHelper;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 * @param queryService QueryService
	 * @param frontendHelper FrontendHelperService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public QueryCopyController(LinkService linkService, QueryService queryService,
			FrontendHelperService frontendHelper,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.linkService = linkService;
		this.queryService = queryService;
		this.frontendHelper = frontendHelper;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Get all possible group names
	 * @param selected Selected value
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/copy-query.html", method = RequestMethod.GET)
	public Map<String, Object> getAllSchemaNames(
			@RequestParam("q") String selected
			)
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> connections = new TreeMap<String, String>();
		for (Map.Entry<SchemaDef, List<LinkDef>> ent : linkService.groupAllLinks().entrySet())
			{
			if (!connectionSettings.getSchemaName().equals(ent.getKey().getName()))
				{
				// Sort by description
				for (LinkDef c : ent.getValue())
					connections.put(c.getFullDescription(), c.getName());
				}
			}
		
		model.put("query", selected);
		model.put("connections", connections);
		
		return (model);
		}
	
	/**
	 * Copy a query to another schema
	 * @param query Query name
	 * @param connection Connection name
	 * @param overwrite Overwrite existint queries
	 * @return View
	 */
	@RequestMapping(value = "/db/*/copy-query.html", method = RequestMethod.POST)
	public String copyQuery(
			@RequestParam("q") String query,
			@RequestParam("connection") String connection,
			@RequestParam(value = "overwrite", required = false) Boolean overwrite
			)
		{
		if (!connectionSettings.isEditorEnabled())
			throw new AccessDeniedException();
		
		final boolean b = (overwrite == null) ? false : overwrite.booleanValue();
		final List<Query> queries = queryService.findReferencedQueries(connectionSettings.getLinkName(), query, true);
		for (Query q : queries)
			{
			final Query existing = queryService.findQueryByName(connection, q.getName());
			try	{
				if (existing == null)
					queryService.createQuery(connection, userSettings.getPrincipal().getLogin(), q);
				else if (b)
					queryService.updateQuery(connection, userSettings.getPrincipal().getLogin(), q.getName(), q);
				}
			catch (BindException e)
				{
				logger.log(Level.WARNING, e.getMessage());
				}
			}
		
		return (frontendHelper.buildPath("redirect:..", connection, "index.html", null));
		}
	}
