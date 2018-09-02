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
package de.tweerlei.dbgrazer.web.service.bookmark.impl;

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.dbgrazer.web.model.UserObjectKey;
import de.tweerlei.dbgrazer.web.service.bookmark.BookmarkManager;
import de.tweerlei.dbgrazer.web.service.bookmark.BookmarkPersister;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Manage query settings
 * 
 * @author Robert Wruck
 */
@Service
public class BookmarkManagerImpl implements BookmarkManager
	{
	private static final String BOOKMARK_EXTENSION = "bookmarks";
	private static final UserBookmarks EMPTY_BOOKMARKS = new ReadOnlyUserBookmarks();
	private static final UserObjectKey<UserBookmarks> KEY_BOOKMARKS = UserObjectKey.create(UserBookmarks.class, false);
	
	private final ConfigAccessor configService;
	private final UserManagerService userManagerService;
	private final BookmarkPersister bookmarkPersister;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param userManagerService UserManagerService
	 * @param bookmarkPersister BookmarkPersister
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public BookmarkManagerImpl(ConfigAccessor configService,
			UserManagerService userManagerService, BookmarkPersister bookmarkPersister,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.configService = configService;
		this.userManagerService = userManagerService;
		this.bookmarkPersister = bookmarkPersister;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public SortedSet<String> getFavorites()
		{
		UserBookmarks ret = connectionSettings.getUserObject(KEY_BOOKMARKS);
		
		if (ret == null)
			{
			if (userSettings.getPrincipal() == null)
				ret = EMPTY_BOOKMARKS;
			else
				ret = loadBookmarks(userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName());
			
			connectionSettings.setUserObject(KEY_BOOKMARKS, ret);
			}
		
		return (ret.getBookmarks());
		}
	
	@Override
	public void addToFavorites(Query query)
		{
		if ((query == null) || (userSettings.getPrincipal() == null))
			return;
		
		final SortedSet<String> f = getFavorites();
		if (!f.contains(query.getName()))
			{
			final int l = configService.get(ConfigKeys.BOOKMARK_LIMIT);
			while (f.size() >= l)
				f.remove(f.last());
			f.add(query.getName());
			updateBookmarks(f, userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName());
			}
		}
	
	@Override
	public void removeFromFavorites(String queryName)
		{
		if (userSettings.getPrincipal() == null)
			return;
		
		final SortedSet<String> f = getFavorites();
		if (f.contains(queryName))
			{
			f.remove(queryName);
			updateBookmarks(f, userSettings.getPrincipal().getLogin(), connectionSettings.getSchemaName());
			}
		}
	
	@Override
	public void refreshFavorites()
		{
		if (userSettings.getPrincipal() == null)
			return;
		
		for (Map.Entry<String, SchemaSettings> ent : userSettings.getSchemaSettings().entrySet())
			ent.getValue().setUserObject(KEY_BOOKMARKS, loadBookmarks(userSettings.getPrincipal().getLogin(), ent.getKey()));
		}
	
	private UserBookmarks loadBookmarks(String user, String schema)
		{
		try	{
			return (new MutableUserBookmarks(userManagerService.loadExtensionObject(user, schema, BOOKMARK_EXTENSION, BOOKMARK_EXTENSION, bookmarkPersister)));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "getUserBookmarks", e);
			return (new MutableUserBookmarks());
			}
		}
	
	private void updateBookmarks(SortedSet<String> favorites, String user, String schema)
		{
		try	{
			userManagerService.saveExtensionObject(user, user, schema, BOOKMARK_EXTENSION, BOOKMARK_EXTENSION, favorites, bookmarkPersister);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "updateUserBookmarks", e);
			}
		}
	}
