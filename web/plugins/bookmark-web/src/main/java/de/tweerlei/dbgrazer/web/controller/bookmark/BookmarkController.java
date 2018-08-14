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
package de.tweerlei.dbgrazer.web.controller.bookmark;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.service.bookmark.BookmarkManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for up-/downloading links as Oracle TNSNames
 * 
 * @author Robert Wruck
 */
@Controller
public class BookmarkController
	{
	private final QueryService queryService;
	private final BookmarkManager bookmarkManager;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param bookmarkManager BookmarkManager
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public BookmarkController(QueryService queryService, BookmarkManager bookmarkManager,
			ConnectionSettings connectionSettings)
		{
		this.queryService = queryService;
		this.bookmarkManager = bookmarkManager;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Display the favorites menu
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/favorites.html", method = RequestMethod.GET)
	public Map<String, Object> showFavorites()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final Map<String, String> favorites = new LinkedHashMap<String, String>();
		for (String q : bookmarkManager.getFavorites())
			favorites.put(q, q);
		model.put("favorites", favorites);
		
		return (model);
		}
	
	/**
	 * Add a query to the favorites
	 * @param query Query name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/add-favorite.html", method = RequestMethod.GET)
	public String addToFavorites(
			@RequestParam(value = "q", required = false) String query
			)
		{
		bookmarkManager.addToFavorites(queryService.findQueryByName(connectionSettings.getLinkName(), query));
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Remove a query from the favorites
	 * @param query Query name
	 * @return View
	 */
	@RequestMapping(value = "/db/*/ajax/remove-favorite.html", method = RequestMethod.GET)
	public String removeFromFavorites(
			@RequestParam(value = "q", required = false) String query
			)
		{
		bookmarkManager.removeFromFavorites(query);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	}
