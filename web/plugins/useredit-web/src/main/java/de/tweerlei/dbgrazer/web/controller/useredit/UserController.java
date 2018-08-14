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
package de.tweerlei.dbgrazer.web.controller.useredit;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TabItem;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class UserController
	{
	private final UserManagerService userManagerService;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param userManagerService UserManagerService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public UserController(UserManagerService userManagerService, UserSettings userSettings)
		{
		this.userManagerService = userManagerService;
		this.userSettings = userSettings;
		}
	
	/**
	 * Display the users
	 * @return Model
	 */
	@RequestMapping(value = "/users.html", method = RequestMethod.GET)
	public Map<String, Object> showUsers()
		{
		if (!userSettings.isUserEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, Set<String>> usersByStartingLetter = new TreeMap<String, Set<String>>();
		for (String username : userManagerService.findAllUsers())
			{
			final String tabTitle = username.toUpperCase().substring(0, 1);
			Set<String> users = usersByStartingLetter.get(tabTitle);
			if (users == null)
				{
				users = new TreeSet<String>();
				usersByStartingLetter.put(tabTitle, users);
				}
			users.add(username);
			}
		
		final Map<String, TabItem<Set<String>>> tabs = new LinkedHashMap<String, TabItem<Set<String>>>();
		if (usersByStartingLetter.isEmpty())
			tabs.put(MessageKeys.EMPTY_TAB, new TabItem<Set<String>>(Collections.<String>emptySet()));
		else
			{
			for (Map.Entry<String, Set<String>> ent : usersByStartingLetter.entrySet())
				tabs.put(ent.getKey(), new TabItem<Set<String>>(ent.getValue(), ent.getValue().size()));
			}
		
		model.put("users", tabs);
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param query Query name
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/userhistory.html", method = RequestMethod.GET)
	public Map<String, Object> showUserHistory(
			@RequestParam("q") String query
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("history", userManagerService.getHistory(query, 10));
		
		return (model);
		}
	
	/**
	 * Run a query
	 * @param query Query name
	 * @return View
	 */
	@RequestMapping(value = "/remove-user.html", method = RequestMethod.GET)
	public String removeUser(
			@RequestParam("q") String query
			)
		{
		if (userSettings.isUserEditorEnabled())
			userManagerService.removeUser(userSettings.getPrincipal().getLogin(), query);
		
		return ("redirect:users.html");
		}
	}
