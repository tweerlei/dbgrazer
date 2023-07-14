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
package de.tweerlei.dbgrazer.extension.mongodb.impl;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.extension.mongodb.MongoDBClientService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class MongoDBClientServiceImpl implements MongoDBClientService, LinkListener, LinkManager
	{
	private final LinkService linkService;
	private final Logger logger;
	private final Map<String, MongoClient> activeConnections;
	
	/**
	 * Constructor
	 * @param linkService LinkService
	 */
	@Autowired
	public MongoDBClientServiceImpl(LinkService linkService)
		{
		this.linkService = linkService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.activeConnections = new ConcurrentHashMap<String, MongoClient>();
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		linkService.addListener(this);
		linkService.addManager(this);
		}
	
	@Override
	public void linksChanged()
		{
		closeConnections();
		}
	
	@Override
	public void linkChanged(String link)
		{
		closeConnection(link);
		}
	
	/**
	 * Close all connections
	 */
	@PreDestroy
	public synchronized void closeConnections()
		{
		logger.log(Level.INFO, "Closing " + activeConnections.size() + " connections");
		
		for (MongoClient client : activeConnections.values())
			client.close();
		
		activeConnections.clear();
		}
	
	private synchronized void closeConnection(String link)
		{
		final MongoClient client = activeConnections.remove(link);
		if (client != null)
			{
			logger.log(Level.INFO, "Closing connection " + link);
			client.close();
			}
		}
	
	@Override
	public MongoClient getMongoClient(String c)
		{
		final MongoClient client = activeConnections.get(c);
		if (client != null)
			return (client);
	
		return (createMongoClient(c));
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, MongoClient> ent : activeConnections.entrySet())
			ret.put(ent.getKey(), 1);
		
		return (ret);
		}
	
	private synchronized MongoClient createMongoClient(String c)
		{
		MongoClient client = activeConnections.get(c);
		if (client == null)
			{
			client = MongoClients.create(initMongoClientSettings(c));
			activeConnections.put(c, client);
			}
		
		return (client);
		}
	
	private MongoClientSettings initMongoClientSettings(String c)
		{
		final LinkDef def = linkService.getLinkData(c);
		if ((def == null) /*|| !(def.getType() instanceof WebserviceLinkType)*/)
			throw new RuntimeException("Unknown link " + c);
		
		final MongoClientSettings.Builder builder = MongoClientSettings.builder();
		builder.applyConnectionString(new ConnectionString(def.getUrl()));
		
		if (!StringUtils.empty(def.getUsername()))
			builder.credential(MongoCredential.createCredential(def.getUsername(), def.getDriver(), def.getPassword().toCharArray()));
		
		return (builder.build());
		}
	}
