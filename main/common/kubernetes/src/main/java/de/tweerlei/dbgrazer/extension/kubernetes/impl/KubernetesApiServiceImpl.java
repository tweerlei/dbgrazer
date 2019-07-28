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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesApiService;
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesClientService;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiObject;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiResource;
import de.tweerlei.dbgrazer.extension.kubernetes.resource.KubernetesApiAdapter;
import de.tweerlei.dbgrazer.extension.kubernetes.resource.impl.AbstractCoreV1ApiAdapter;
import de.tweerlei.dbgrazer.extension.kubernetes.resource.impl.CustomObjectApiAdapter;
import de.tweerlei.dbgrazer.extension.kubernetes.support.CustomObjectsApiExtension;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkService;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.ApisApi;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1APIGroup;
import io.kubernetes.client.models.V1APIGroupList;
import io.kubernetes.client.models.V1APIResource;
import io.kubernetes.client.models.V1APIResourceList;
import io.kubernetes.client.models.V1GroupVersionForDiscovery;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1NamespaceList;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class KubernetesApiServiceImpl implements KubernetesApiService, ConfigListener, LinkListener
	{
	private static class KubernetesMetadataHolder
		{
		private final Map<String, Map<String, Map<String, KubernetesApiResource>>> apiResources;
		
		public KubernetesMetadataHolder(Map<String, Map<String, Map<String, KubernetesApiResource>>> apiResources)
			{
			final Map<String, Map<String, Map<String, KubernetesApiResource>>> tmp = new TreeMap<String, Map<String, Map<String, KubernetesApiResource>>>();
			for (Map.Entry<String, Map<String, Map<String, KubernetesApiResource>>> ent : apiResources.entrySet())
				{
				final Map<String, Map<String, KubernetesApiResource>> tmp2 = new TreeMap<String, Map<String, KubernetesApiResource>>();
				for (Map.Entry<String, Map<String, KubernetesApiResource>> ent2 : ent.getValue().entrySet())
					tmp2.put(ent2.getKey(), Collections.unmodifiableMap(new TreeMap<String, KubernetesApiResource>(ent2.getValue())));
				tmp.put(ent.getKey(), Collections.unmodifiableMap(tmp2));
				}
			this.apiResources = Collections.unmodifiableMap(tmp);
			}
		
		public Map<String, Map<String, Map<String, KubernetesApiResource>>> getApiResources()
			{
			return (apiResources);
			}
		}
	
	private final ConfigService configService;
	private final LinkService linkService;
	private final KubernetesClientService clientService;
	private final Map<String, KubernetesApiAdapter> adapters;
	private final Logger logger;
	private final Map<String, KubernetesMetadataHolder> metadataCache;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param linkService LinkService
	 * @param clientService KubernetesClientService
	 * @param adapters Known KubernetesApiAdapters
	 */
	@Autowired
	public KubernetesApiServiceImpl(ConfigService configService, LinkService linkService, KubernetesClientService clientService,
			Set<KubernetesApiAdapter> adapters)
		{
		this.configService = configService;
		this.linkService = linkService;
		this.clientService = clientService;
		this.adapters = new NamedMap<KubernetesApiAdapter>(adapters);
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.metadataCache = new ConcurrentHashMap<String, KubernetesMetadataHolder>();
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		linkService.addListener(this);
		}
	
	@Override
	public void configChanged()
		{
		flushCache(null);
		}
	
	@Override
	public void linksChanged()
		{
		flushCache(null);
		}
	
	@Override
	public void linkChanged(String link)
		{
		flushCache(link);
		}
	
	@Override
	public void flushCache(String link)
		{
		if (link == null)
			metadataCache.clear();
		else
			metadataCache.remove(link);
		}
	
	private KubernetesMetadataHolder getMetadataHolder(String link)
		{
		KubernetesMetadataHolder ret = metadataCache.get(link);
		if (ret == null)
			{
			logger.log(Level.INFO, "Retrieving metadata for " + link);
			
			final ApiClient client = clientService.getApiClient(link);
			final Map<String, Map<String, Map<String, KubernetesApiResource>>> apiResources = new TreeMap<String, Map<String, Map<String, KubernetesApiResource>>>();
			
			try	{
				final V1APIGroupList l = new ApisApi(client).getAPIVersions();
				for (V1APIGroup r : l.getGroups())
					{
					final String groupName = StringUtils.notNull(r.getName());
					Map<String, Map<String, KubernetesApiResource>> map = apiResources.get(groupName);
					if (map == null)
						{
						map = new TreeMap<String, Map<String, KubernetesApiResource>>();
						apiResources.put(groupName, map);
						}
					for (V1GroupVersionForDiscovery v : r.getVersions())
						map.put(StringUtils.notNull(v.getVersion()), new TreeMap<String, KubernetesApiResource>());
					}
				}
			catch (ApiException e)
				{
				throw new RuntimeException(e);
				}
			
			final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
			for (Map.Entry<String, Map<String, Map<String, KubernetesApiResource>>> ent : apiResources.entrySet())
				{
				for (Map.Entry<String, Map<String, KubernetesApiResource>> ent2 : ent.getValue().entrySet())
					{
					try	{
						final V1APIResourceList l = api.getAPIResources(ent.getKey(), ent2.getKey());
						for (V1APIResource r : l.getResources())
							{
							if (r.getVerbs().contains("list"))
								ent2.getValue().put(r.getKind(), new KubernetesApiResource(r.getName(), r.getKind(), r.isNamespaced(), new TreeSet<String>(r.getVerbs())));
							}
						}
					catch (ApiException e)
						{
						logger.log(Level.WARNING, "getAPIResources", e);
//						throw new RuntimeException(e);
						}
					}
				}
			
			try	{
				final Map<String, KubernetesApiResource> rsrc = new TreeMap<String, KubernetesApiResource>();
				final V1APIResourceList l = new CoreV1Api(client).getAPIResources();
				for (V1APIResource r : l.getResources())
					{
					if (r.getVerbs().contains("list"))
						rsrc.put(r.getKind(), new KubernetesApiResource(r.getKind(), r.getKind(), r.isNamespaced(), new TreeSet<String>(r.getVerbs())));
					}
				
				final Map<String, Map<String, KubernetesApiResource>> map = new TreeMap<String, Map<String, KubernetesApiResource>>();
				map.put(AbstractCoreV1ApiAdapter.CORE_VERSION, rsrc);
				apiResources.put(AbstractCoreV1ApiAdapter.CORE_GROUP, map);
				}
			catch (ApiException e)
				{
				logger.log(Level.WARNING, "getAPIResources", e);
//				throw new RuntimeException(e);
				}
			
			ret = new KubernetesMetadataHolder(apiResources);
			metadataCache.put(link, ret);
			}
		return (ret);
		}
	
	@Override
	public Map<String, Map<String, Map<String, KubernetesApiResource>>> getApiResources(String c)
		{
		return (getMetadataHolder(c).getApiResources());
		}
	
	@Override
	public Set<String> listNamespaces(String c)
		{
		final CoreV1Api api = new CoreV1Api(clientService.getApiClient(c));
		
		final Set<String> names = new TreeSet<String>();
		try	{
			final V1NamespaceList l = api.listNamespace(null, null, null, null, null, null, null, null, null);
			for (V1Namespace n : l.getItems())
				names.add(n.getMetadata().getName());
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		
		return (names);
		}
	
	private KubernetesApiAdapter getAdapter(String group, String version, String kind)
		{
		KubernetesApiAdapter ret = adapters.get(group + "/" + version + "/" + kind);
		if (ret != null)
			return (ret);
		
		synchronized(adapters)
			{
			ret = new CustomObjectApiAdapter(group, version, kind);
			adapters.put(ret.getName(), ret);
			}
		
		return (ret);
		}
	
	@Override
	public Set<KubernetesApiObject> listApiObjects(String c, String namespace, String group, String version, String kind)
		{
		final KubernetesApiAdapter api = getAdapter(group, version, kind);
		try	{
			return (api.list(clientService.getApiClient(c), namespace));
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			return (Collections.emptySet());
			}
		}
	
	@Override
	public String getApiObject(String c, String namespace, String group, String version, String kind, String name)
		{
		final KubernetesApiAdapter api = getAdapter(group, version, kind);
		try	{
			return (api.read(clientService.getApiClient(c), namespace, name));
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			return (e.getResponseBody());
			}
		}
	
	@Override
	public String createApiObject(String c, String namespace, String group, String version, String kind, String json)
		{
		final KubernetesApiAdapter api = getAdapter(group, version, kind);
		try	{
			return (api.create(clientService.getApiClient(c), namespace, json));
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			return (e.getResponseBody());
			}
		}
	
	@Override
	public String replaceApiObject(String c, String namespace, String group, String version, String kind, String name, String json)
		{
		final KubernetesApiAdapter api = getAdapter(group, version, kind);
		try	{
			return (api.replace(clientService.getApiClient(c), namespace, name, json));
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			return (e.getResponseBody());
			}
		}
	
	@Override
	public String patchApiObject(String c, String namespace, String group, String version, String kind, String name, String json)
		{
		final KubernetesApiAdapter api = getAdapter(group, version, kind);
		try	{
			return (api.patch(clientService.getApiClient(c), namespace, name, json));
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			return (e.getResponseBody());
			}
		}
	
	@Override
	public String deleteApiObject(String c, String namespace, String group, String version, String kind, String name)
		{
		final KubernetesApiAdapter api = getAdapter(group, version, kind);
		try	{
			return (api.delete(clientService.getApiClient(c), namespace, name));
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			return (e.getResponseBody());
			}
		}
	}
