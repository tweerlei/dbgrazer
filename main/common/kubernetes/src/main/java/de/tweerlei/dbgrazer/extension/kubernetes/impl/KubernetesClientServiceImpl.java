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
package de.tweerlei.dbgrazer.extension.kubernetes.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.FileUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.extension.kubernetes.ConfigKeys;
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesClientService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.spring.config.ConfigAccessor;
import io.kubernetes.client.ApiClient;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class KubernetesClientServiceImpl implements KubernetesClientService, LinkListener, LinkManager
	{
	private static class KubernetesConnectionHolder
		{
		private final List<ApiClient> allClients;
		private final ThreadLocal<ApiClient> activeClient;
		
		public KubernetesConnectionHolder()
			{
			this.allClients = new LinkedList<ApiClient>();
			this.activeClient = new ThreadLocal<ApiClient>();
			}
		
		public ApiClient getApiClient()
			{
			return (activeClient.get());
			}
		
		public synchronized void setApiClient(ApiClient client)
			{
			allClients.add(client);
			activeClient.set(client);
			}
		}
	
	private final ConfigFileStore configFileStore;
	private final ConfigAccessor configService;
	private final LinkService linkService;
	private final Logger logger;
	private final Map<String, KubernetesConnectionHolder> activeConnections;
	
	/**
	 * Constructor
	 * @param configFileStore ConfigFileStore
	 * @param configService ConfigAccessor
	 * @param linkService LinkService
	 */
	@Autowired
	public KubernetesClientServiceImpl(ConfigFileStore configFileStore, ConfigAccessor configService, LinkService linkService)
		{
		this.configFileStore = configFileStore;
		this.configService = configService;
		this.linkService = linkService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.activeConnections = new ConcurrentHashMap<String, KubernetesConnectionHolder>();
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
		
		activeConnections.clear();
		}
	
	private synchronized void closeConnection(String link)
		{
		final KubernetesConnectionHolder holder = activeConnections.remove(link);
		if (holder != null)
			logger.log(Level.INFO, "Closing connection " + link);
		}
	
	@Override
	public ApiClient getApiClient(String c)
		{
		final KubernetesConnectionHolder holder = activeConnections.get(c);
		if (holder != null)
			{
			final ApiClient ret = holder.getApiClient();
			if (ret != null)
				return (ret);
			}
		
		return (createApiClient(c));
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, KubernetesConnectionHolder> ent : activeConnections.entrySet())
			ret.put(ent.getKey(), 1);
		
		return (ret);
		}
	
	private synchronized ApiClient createApiClient(String c)
		{
		KubernetesConnectionHolder holder = activeConnections.get(c);
		ApiClient ret = null;
		if (holder != null)
			{
			ret = holder.getApiClient();
			if (ret != null)
				return (ret);
			}
		else
			{
			holder = new KubernetesConnectionHolder();
			activeConnections.put(c, holder);
			}
		
		final LinkDef def = linkService.getLink(c, null);
		if ((def == null) /*|| !(def.getType() instanceof WebserviceLinkType)*/)
			throw new RuntimeException("Unknown link " + c);
		
		ret = new ApiClient();
		ret.setBasePath(def.getUrl());
		ret.setConnectTimeout(configService.get(ConfigKeys.CONNECT_TIMEOUT).intValue());
		
		final String caCertPath = def.getProperties().getProperty("ssl.cacert.location");
		if (!StringUtils.empty(caCertPath))
			{
			try	{
				final byte[] certBytes = FileUtils.readFile(configFileStore.getFileLocation(caCertPath), 65536);
				ret.setSslCaCert(new ByteArrayInputStream(certBytes));
				ret.setVerifyingSsl(true);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "Failed to read CA certificate from " + caCertPath, e);
				ret.setVerifyingSsl(false);
				}
			}
		else
			ret.setVerifyingSsl(false);
		
		if (StringUtils.empty(def.getUsername()))
			{
			ret.setApiKeyPrefix("Bearer");
			ret.setApiKey(def.getPassword());
			}
		else
			{
			ret.setUsername(def.getUsername());
			ret.setPassword(def.getPassword());
			}
		
		holder.setApiClient(ret);
		return (ret);
		}
	}
