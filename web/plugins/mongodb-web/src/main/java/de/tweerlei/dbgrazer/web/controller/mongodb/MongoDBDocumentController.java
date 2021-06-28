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
package de.tweerlei.dbgrazer.web.controller.mongodb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.client.result.InsertOneResult;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBClientService;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck <wruck@tweerlei.de>
 */
@Controller
public class MongoDBDocumentController
	{
	private final MongoDBClientService mongoClientService;
	private final QuerySettingsManager querySettingsManager;
	private final FrontendHelperService frontendHelperService;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param mongoClientService MongoDBClientService
	 * @param querySettingsManager QuerySettingsManager
	 * @param frontendHelperService FrontendHelperService
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public MongoDBDocumentController(MongoDBClientService mongoClientService, FrontendHelperService frontendHelperService,
			QuerySettingsManager querySettingsManager, ConnectionSettings connectionSettings)
		{
		this.mongoClientService = mongoClientService;
		this.querySettingsManager = querySettingsManager;
		this.frontendHelperService = frontendHelperService;
		this.connectionSettings = connectionSettings;
		}
	
	/**
	 * Show the send dialog
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Message to copy from
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/ajax/save-document.html", method = RequestMethod.GET)
	public Map<String, Object> showSendDialog(
			@RequestParam(value = "database", required = false) String database,
			@RequestParam(value = "collection", required = false) String collection,
			@RequestParam(value = "id", required = false) String id
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("database", database);
		model.put("collection", collection);
		
		if ((database != null) && (collection != null) && (id != null))
			{
			final Iterable<Document> records = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection).find(new Document("_id", id)).limit(1);
			final Iterator<Document> it = records.iterator();
			if (it.hasNext())
				{
				model.put("key", id);
				model.put("message", it.next().toJson());
				}
			}
		else
			{
			model.put("key", "");
			model.put("message", connectionSettings.getCustomQuery().getQuery());
			}
		
		return (model);
		}
	
	/**
	 * Send a message
	 * @param database Topic name
	 * @param collection Partition number
	 * @param id Message key
	 * @param message Message body
	 * @return Model
	 */
	@RequestMapping(value = "/db/*/save-document.html", method = RequestMethod.POST)
	public Map<String, Object> sendMessage(
			@RequestParam("database") String database,
			@RequestParam(value = "collection", required = false) String collection,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "message", required = false) String message
			)
		{
		if (!connectionSettings.isBrowserEnabled() || !connectionSettings.isWritable())
			throw new AccessDeniedException();
		
		if (!StringUtils.empty(message))
			{
			querySettingsManager.addCustomHistoryEntry(message);
			connectionSettings.getCustomQuery().setQuery(message);
			connectionSettings.getCustomQuery().modify();
			}
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		try	{
			final InsertOneResult md = mongoClientService.getMongoClient(connectionSettings.getLinkName()).getDatabase(database).getCollection(collection).insertOne(Document.parse(message));
			model.put("result", new Document(MongoDBConstants.ID_PROPERTY, md.getInsertedId()).toJson());
			model.put("exceptionText", null);
			}
		catch (RuntimeException e)
			{
			model.put("exceptionText", frontendHelperService.toJSONString(e.getMessage()));
			}
		
		return (model);
		}
	}
