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
package de.tweerlei.dbgrazer.web.extension.bookmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.controller.bookmark.BookmarkMessageKeys;
import de.tweerlei.dbgrazer.web.extension.ExtensionGroup;
import de.tweerlei.dbgrazer.web.extension.ExtensionLink;
import de.tweerlei.dbgrazer.web.extension.FrontendExtensionAdapter;
import de.tweerlei.dbgrazer.web.service.bookmark.BookmarkManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * JDBC FrontendExtension
 * 
 * @author Robert Wruck
 */
@Service
@Order(3)
public class BookmarkExtension extends FrontendExtensionAdapter
	{
	private static final List<ExtensionLink> TOP_MENU = Collections.singletonList(new ExtensionLink("bookmarks", null, "return showDbMenu(event, 'favorites');", null));
	
	private final BookmarkManager bookmarkManager;
	private final QueryService queryService;
	private final ConnectionSettings connectionSettings;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param bookmarkManager BookmarkManager
	 * @param queryService QueryService
	 * @param connectionSettings ConnectionSettings
	 * @param userSettings UserSettings
	 */
	@Autowired
	public BookmarkExtension(BookmarkManager bookmarkManager, QueryService queryService,
			ConnectionSettings connectionSettings, UserSettings userSettings)
		{
		super("Bookmark");
		this.bookmarkManager = bookmarkManager;
		this.queryService = queryService;
		this.connectionSettings = connectionSettings;
		this.userSettings = userSettings;
		}
	
	@Override
	public List<ExtensionLink> getTopMenuExtensions()
		{
		if (userSettings.getPrincipal() == null)
			return (super.getTopMenuExtensions());
		
		return (TOP_MENU);
		}
	
	@Override
	public List<ExtensionGroup> getQueryOverviewExtensions()
		{
		if (userSettings.getPrincipal() == null)
			return (super.getQueryOverviewExtensions());
		
		final List<ExtensionGroup> ret = new ArrayList<ExtensionGroup>();
		
		if (!bookmarkManager.getFavorites().isEmpty())
			{
			final QueryGroup fav = queryService.groupQueries(connectionSettings.getLinkName(), bookmarkManager.getFavorites(), false, true);
			ret.add(new ExtensionGroup(BookmarkMessageKeys.BOOKMARK_TAB, fav));
			}
		
		return (ret);
		}
	
	@Override
	public List<ExtensionLink> getQueryViewExtensions(Query query)
		{
		if (userSettings.getPrincipal() == null)
			return (super.getQueryViewExtensions(query));
		
		final List<ExtensionLink> ret = new ArrayList<ExtensionLink>();
		
		if (bookmarkManager.getFavorites().contains(query.getName()))
			ret.add(new ExtensionLink(BookmarkMessageKeys.ICON_REMOVE, null, "return removeFromFavorites(event, '" + query.getName() + "');", BookmarkMessageKeys.LABEL_REMOVE));
		else
			ret.add(new ExtensionLink(BookmarkMessageKeys.ICON_ADD, null, "return addToFavorites(event, '" + query.getName() + "');", BookmarkMessageKeys.LABEL_ADD));
		
		return (ret);
		}
	
	@Override
	public List<String> getQueryViewJS(Query query)
		{
		if (userSettings.getPrincipal() == null)
			return (super.getQueryViewJS(query));
		
		return (BookmarkMessageKeys.EXTENSION_JS);
		}
	}
