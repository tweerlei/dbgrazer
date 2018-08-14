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
package de.tweerlei.dbgrazer.extension.http.impl;

import java.io.IOException;
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

import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.http.ConfigKeys;
import de.tweerlei.dbgrazer.extension.http.HttpClientService;
import de.tweerlei.dbgrazer.extension.http.HttpConstants;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.spring.config.impl.CompositeConfigProvider;
import de.tweerlei.spring.config.impl.ConfigProviderAccessor;
import de.tweerlei.spring.config.impl.MapBasedConfigProvider;
import de.tweerlei.spring.http.HttpClient;
import de.tweerlei.spring.http.HttpClientOptions;
import de.tweerlei.spring.http.HttpEntity;
import de.tweerlei.spring.service.ModuleLookupService;
import de.tweerlei.spring.service.SerializerFactory;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class HttpClientServiceImpl implements HttpClientService, ConfigListener, LinkListener, LinkManager
	{
	private final SerializerFactory serializerFactory;
	private final ConfigService configService;
	private final LinkService linkService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	private final Map<String, HttpClient> activeConnections;
	
	private String clientPrefix;
	
	/**
	 * Constructor
	 * @param serializerFactory SerializerFactory
	 * @param configService ConfigService
	 * @param linkService LinkService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public HttpClientServiceImpl(SerializerFactory serializerFactory, ConfigService configService,
			LinkService linkService, ModuleLookupService moduleService)
		{
		this.serializerFactory = serializerFactory;
		this.configService = configService;
		this.linkService = linkService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.activeConnections = new ConcurrentHashMap<String, HttpClient>();
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		linkService.addListener(this);
		linkService.addManager(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		clientPrefix = configService.get(ConfigKeys.HTTP_CLIENT);
		
		logger.log(Level.INFO, "Using HTTP client: " + clientPrefix);
		
		closeConnections();
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
		
		for (HttpClient client : activeConnections.values())
			client.close();
		
		activeConnections.clear();
		}
	
	private synchronized void closeConnection(String link)
		{
		final HttpClient client = activeConnections.remove(link);
		if (client != null)
			{
			logger.log(Level.INFO, "Closing connection " + link);
			client.close();
			}
		}
	
	@Override
	public HttpEntity get(String c, String endpoint) throws IOException
		{
		final LinkDef def = linkService.getLink(c, null);
		if (def == null)
			throw new IOException("Unknown link " + c);
		
		return (getClient(c).get(def.getUrl() + endpoint, def.getUsername(), def.getPassword()));
		}
	
	@Override
	public HttpEntity getExternal(String url) throws IOException
		{
		for (LinkDef def : linkService.findLinksByType(HttpConstants.LINKTYPE_WEBSERVICE))
			{
			if (url.startsWith(def.getUrl()))
				return (getClient(def.getName()).get(url, def.getUsername(), def.getPassword()));
			}
		
		return (getClient(configService.get(ConfigKeys.HTTP_ANON_LINK)).get(url, null, null));
		}
	
	@Override
	public HttpEntity post(String c, String endpoint, HttpEntity request) throws IOException
		{
		final LinkDef def = linkService.getLink(c, null);
		if (def == null)
			throw new IOException("Unknown link " + c);
		
		return (getClient(c).post(def.getUrl() + endpoint, request, def.getUsername(), def.getPassword()));
		}
	
	@Override
	public HttpEntity post(String c, String endpoint, List<HttpEntity> request) throws IOException
		{
		final LinkDef def = linkService.getLink(c, null);
		if (def == null)
			throw new IOException("Unknown link " + c);
		
		return (getClient(c).post(def.getUrl() + endpoint, request, def.getUsername(), def.getPassword()));
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, HttpClient> ent : activeConnections.entrySet())
			ret.put(ent.getKey(), 1);
		
		return (ret);
		}
	
	private HttpClient getClient(String c)
		{
		final HttpClient ret = activeConnections.get(c);
		if (ret != null)
			return (ret);
		
		return (createClient(c));
		}
	
	private synchronized HttpClient createClient(String c)
		{
		HttpClient ret = activeConnections.get(c);
		if (ret != null)
			return (ret);
		
		final LinkDef def = linkService.getLink(c, null);
		if ((def == null) /*|| !(def.getType() instanceof WebserviceLinkType)*/)
			throw new RuntimeException("Unknown link " + c);
		
		final ConfigProviderAccessor accessor = new ConfigProviderAccessor(new CompositeConfigProvider(
				new MapBasedConfigProvider(def.getProperties()),
				configService.getConfigProvider()
				), serializerFactory);
		
		ret = moduleService.findModuleInstance(clientPrefix + "HttpClient", HttpClient.class);
		ret.setConnectTimeout(accessor.get(ConfigKeys.HTTP_CONNECT_TIMEOUT));
		ret.setReadTimeout(accessor.get(ConfigKeys.HTTP_READ_TIMEOUT));
		ret.setProxy(accessor.get(ConfigKeys.HTTP_PROXY_HOST), accessor.get(ConfigKeys.HTTP_PROXY_PORT));
		if (!accessor.get(ConfigKeys.HTTP_STRICT_MODE))
			ret.setOption(HttpClientOptions.SKIP_MULTIPART_TYPE, true);
		
		activeConnections.put(c, ret);
		return (ret);
		}
	}
