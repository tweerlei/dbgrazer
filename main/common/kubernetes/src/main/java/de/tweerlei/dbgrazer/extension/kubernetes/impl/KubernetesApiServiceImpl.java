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
import java.util.LinkedHashMap;
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
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesApiService;
import de.tweerlei.dbgrazer.extension.kubernetes.KubernetesClientService;
import de.tweerlei.dbgrazer.extension.kubernetes.support.CustomObjectsApiExtension;
import de.tweerlei.dbgrazer.extension.kubernetes.support.V1Item;
import de.tweerlei.dbgrazer.extension.kubernetes.support.V1ItemList;
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
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1Endpoints;
import io.kubernetes.client.models.V1Event;
import io.kubernetes.client.models.V1GroupVersionForDiscovery;
import io.kubernetes.client.models.V1LimitRange;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1NamespaceList;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodTemplate;
import io.kubernetes.client.models.V1ReplicationController;
import io.kubernetes.client.models.V1ResourceQuota;
import io.kubernetes.client.models.V1Secret;
import io.kubernetes.client.models.V1Service;
import io.kubernetes.client.models.V1ServiceAccount;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class KubernetesApiServiceImpl implements KubernetesApiService, ConfigListener, LinkListener
	{
	private static enum Kind
		{
		ConfigMap,
		Endpoints,
		Event,
		LimitRange,
		Pod,
		PodTemplate,
		ReplicationController,
		ResourceQuota,
		Secret,
		Service,
		ServiceAccount,
		PersistentVolumeClaim,
		/** unknown kind */
		Other;
		
		public static Kind forName(String name)
			{
			try	{
				return (Kind.valueOf(name));
				}
			catch (IllegalArgumentException e)
				{
				return (Kind.Other);
				}
			}
		}
	
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
	private final Logger logger;
	private final Map<String, KubernetesMetadataHolder> metadataCache;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param linkService LinkService
	 * @param clientService KubernetesClientService
	 */
	@Autowired
	public KubernetesApiServiceImpl(ConfigService configService, LinkService linkService, KubernetesClientService clientService)
		{
		this.configService = configService;
		this.linkService = linkService;
		this.clientService = clientService;
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
				map.put("v1", rsrc);
				apiResources.put("(core)", map);
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
	
	@Override
	public Set<KubernetesApiObject> listApiObjects(String c, String namespace, String group, String version, String kind)
		{
		if ("(core)".equals(group) && "v1".equals(version))
			return (listCoreObjects(c, namespace, kind));
		
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(clientService.getApiClient(c));
		final Set<KubernetesApiObject> names = new TreeSet<KubernetesApiObject>();
		try	{
			final Object list = api.listNamespacedCustomObject(group, version, namespace, kind, null, null, null, null);
			for (V1Item item : extractList(list, api.getApiClient()).getItems())
				{
				final KubernetesApiObject name = getKubernetesApiObject(item.getMetadata(), null);
				names.add(name);
				}
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		
		return (names);
		}
	
	private V1ItemList extractList(Object listObj, ApiClient client)
		{
		if (listObj instanceof Map)
			{
			final String json = client.getJSON().serialize(listObj);
			return (client.getJSON().deserialize(json, V1ItemList.class));
			}
		
		return (null);
		}
	
	private KubernetesApiObject getKubernetesApiObject(V1ObjectMeta metadata, Map<String, String> properties)
		{
		return (new KubernetesApiObject(metadata.getName(), metadata.getCreationTimestamp().toDate(), metadata.getLabels(), properties));
		}
	
	private Set<KubernetesApiObject> listCoreObjects(String c, String namespace, String kind)
		{
		final CoreV1Api api = new CoreV1Api(clientService.getApiClient(c));
		
		final Set<KubernetesApiObject> names = new TreeSet<KubernetesApiObject>();
		try	{
			switch (Kind.forName(kind))
				{
				case ConfigMap:
					for (V1ConfigMap n : api.listNamespacedConfigMap(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case Endpoints:
					for (V1Endpoints n : api.listNamespacedEndpoints(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case Event:
					for (V1Event n : api.listNamespacedEvent(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), getEventProperties(n)));
					break;
				
				case Pod:
					for (V1Pod n : api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), getPodProperties(n)));
					break;
				
				case Secret:
					for (V1Secret n : api.listNamespacedSecret(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case Service:
					for (V1Service n : api.listNamespacedService(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case LimitRange:
					for (V1LimitRange n : api.listNamespacedLimitRange(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case PodTemplate:
					for (V1PodTemplate n : api.listNamespacedPodTemplate(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case ReplicationController:
					for (V1ReplicationController n : api.listNamespacedReplicationController(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case ResourceQuota:
					for (V1ResourceQuota n : api.listNamespacedResourceQuota(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case ServiceAccount:
					for (V1ServiceAccount n : api.listNamespacedServiceAccount(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case PersistentVolumeClaim:
					for (V1PersistentVolumeClaim n : api.listNamespacedPersistentVolumeClaim(namespace, null, null, null, null, null, null, null, null, null).getItems())
						names.add(getKubernetesApiObject(n.getMetadata(), null));
					break;
				
				case Other:
					break;
				}
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		
		return (names);
		}
	
	private Map<String, String> getPodProperties(V1Pod pod)
		{
		final Map<String, String> ret = new LinkedHashMap<String, String>();
		
		ret.put("Phase", pod.getStatus().getPhase());
		ret.put("StartTime", pod.getStatus().getStartTime().toString());
		ret.put("PodIP", pod.getStatus().getPodIP());
		ret.put("HostIP", pod.getStatus().getHostIP());
		
		return (ret);
		}
	
	private Map<String, String> getEventProperties(V1Event event)
		{
		final Map<String, String> ret = new LinkedHashMap<String, String>();
		
		ret.put("Message", event.getMessage());
		
		return (ret);
		}
	
	@Override
	public String getApiObject(String c, String namespace, String group, String version, String kind, String name)
		{
		if ("(core)".equals(group) && "v1".equals(version))
			return (getCoreObject(c, namespace, kind, name));
		
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(clientService.getApiClient(c));
		Object content = null;
		try	{
			content = api.getNamespacedCustomObject(group, version, namespace, kind, name);
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		
		if (content == null)
			return (null);
		
		return (api.getApiClient().getJSON().serialize(content));
		}
	
	private String getCoreObject(String c, String namespace, String kind, String name)
		{
		final CoreV1Api api = new CoreV1Api(clientService.getApiClient(c));
		
		Object content = null;
		try	{
			switch (Kind.forName(kind))
				{
				case ConfigMap:
					content = api.readNamespacedConfigMap(name, namespace, null, null, null);
					break;
				case Endpoints:
					content = api.readNamespacedEndpoints(name, namespace, null, null, null);
					break;
				case Event:
					content = api.readNamespacedEvent(name, namespace, null, null, null);
					break;
				case LimitRange:
					content = api.readNamespacedLimitRange(name, namespace, null, null, null);
					break;
				case PersistentVolumeClaim:
					content = api.readNamespacedPersistentVolumeClaim(name, namespace, null, null, null);
					break;
				case Pod:
					content = api.readNamespacedPod(name, namespace, null, null, null);
					break;
				case PodTemplate:
					content = api.readNamespacedPodTemplate(name, namespace, null, null, null);
					break;
				case ReplicationController:
					content = api.readNamespacedReplicationController(name, namespace, null, null, null);
					break;
				case ResourceQuota:
					content = api.readNamespacedResourceQuota(name, namespace, null, null, null);
					break;
				case Secret:
					content = api.readNamespacedSecret(name, namespace, null, null, null);
					break;
				case Service:
					content = api.readNamespacedService(name, namespace, null, null, null);
					break;
				case ServiceAccount:
					content = api.readNamespacedServiceAccount(name, namespace, null, null, null);
					break;
				case Other:
					break;
				}
			}
		catch (ApiException e)
			{
			logger.log(Level.WARNING, e.getResponseBody(), e);
			}
		
		if (content == null)
			return (null);
		
		return (api.getApiClient().getJSON().serialize(content));
		}
	}
