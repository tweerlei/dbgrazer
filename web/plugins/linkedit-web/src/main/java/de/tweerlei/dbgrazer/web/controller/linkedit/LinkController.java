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
package de.tweerlei.dbgrazer.web.controller.linkedit;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class LinkController
	{
	private final LinkService linkService;
	private final FrontendExtensionService extensionService;
	private final ConnectionSettings connectionSettings;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 * @param extensionService FrontendExtensionService
	 * @param connectionSettings ConnectionSettings
	 * @param userSettings UserSettings
	 */
	@Autowired
	public LinkController(LinkService linkService,
			FrontendExtensionService extensionService,
			ConnectionSettings connectionSettings, UserSettings userSettings)
		{
		this.linkService = linkService;
		this.extensionService = extensionService;
		this.connectionSettings = connectionSettings;
		this.userSettings = userSettings;
		}
	
	/**
	 * Display the links
	 * @return Model
	 */
	@RequestMapping(value = "/links.html", method = RequestMethod.GET)
	public Map<String, Object> showLinks()
		{
		if (!userSettings.isLinkEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<SchemaDef, List<LinkDef>> links = linkService.groupAllLinks();
		final Map<String, TabItem<List<LinkDef>>> tabs = new LinkedHashMap<String, TabItem<List<LinkDef>>>();
		
		if (links.isEmpty())
			tabs.put(MessageKeys.EMPTY_TAB, new TabItem<List<LinkDef>>(Collections.<LinkDef>emptyList()));
		else
			{
			for (Map.Entry<SchemaDef, List<LinkDef>> ent : links.entrySet())
				tabs.put(ent.getKey().toString(), new TabItem<List<LinkDef>>(ent.getValue(), ent.getValue().size(), ent.getKey().toString()));
			}
		
		model.put("links", tabs);
		model.put("extensions", extensionService.getLinkOverviewExtensions());
		
		// Hack to enable edit icons in tabs.tag
		connectionSettings.setEditorActive(true);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param query Query name
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/linkhistory.html", method = RequestMethod.GET)
	public Map<String, Object> showLinkHistory(
			@RequestParam("q") String query
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("history", linkService.getHistory(query, 10));
		
		return (model);
		}
	
	/**
	 * Run a query
	 * @param query Query name
	 * @return View
	 */
	@RequestMapping(value = "/remove-link.html", method = RequestMethod.GET)
	public String removeLink(
			@RequestParam("q") String query
			)
		{
		if (userSettings.isLinkEditorEnabled())
			linkService.removeLink(userSettings.getPrincipal().getLogin(), query);
		
		return ("redirect:links.html");
		}
	
	/**
	 * Get all possible group names
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/select-group.html", method = RequestMethod.GET)
	public Map<String, Object> getGroupNames(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		final Set<String> groups = linkService.findAllLinkGroups();
		groups.remove(LinkDef.LOGIN_GROUP);
		model.put("allGroups", groups);
		
		return (model);
		}
	
	/**
	 * Get all possible set names
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/select-set.html", method = RequestMethod.GET)
	public Map<String, Object> getSetNames(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allSets", linkService.findAllLinkSets());
		
		return (model);
		}
	
	/**
	 * Get all possible group names
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/select-schema.html", method = RequestMethod.GET)
	public Map<String, Object> getSchemaNames(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<String> schemas = new TreeSet<String>();
		for (SchemaDef s : linkService.groupAllLinks().keySet())
			schemas.add(s.getName());
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allSchemas", schemas);
		
		return (model);
		}
	
	/**
	 * Get all possible group names
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/select-subschema.html", method = RequestMethod.GET)
	public Map<String, Object> getSubschemaNames(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Set<String> schemas = new TreeSet<String>();
		for (SchemaDef s : linkService.groupAllLinks().keySet())
			{
			if (s.isSubschema())
				schemas.add(s.getVersion());
			}
		
		model.put("value", selected);
		model.put("target", target);
		model.put("allSubschemas", schemas);
		
		return (model);
		}
	}
