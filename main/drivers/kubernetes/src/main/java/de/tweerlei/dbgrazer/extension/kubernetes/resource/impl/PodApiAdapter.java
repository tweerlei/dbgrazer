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
package de.tweerlei.dbgrazer.extension.kubernetes.resource.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.JSON;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1ContainerStatus;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1ObjectMeta;

/**
 * KubernetesApiAdapter for Pods
 * 
 * @author Robert Wruck
 */
@Service
public class PodApiAdapter extends AbstractCoreV1ApiAdapter<V1Pod>
	{
	/**
	 * Constructor
	 */
	public PodApiAdapter()
		{
		super("Pod");
		}
	
	@Override
	protected V1Pod deserialize(JSON json, String content)
		{
		return (json.<V1Pod>deserialize(content, V1Pod.class));
		}
	
	@Override
	protected V1ObjectMeta getMetadata(V1Pod content)
		{
		return (content.getMetadata());
		}
	
	@Override
	protected Map<String, Object> getProperties(V1Pod content)
		{
		final Map<String, Object> ret = new LinkedHashMap<String, Object>();
		
		int containers = 0;
		int readyContainers = 0;
		for (V1ContainerStatus s : content.getStatus().getContainerStatuses())
			{
			containers++;
			if (s.isReady())
				readyContainers++;
			}
		
		ret.put("Phase", content.getStatus().getPhase());
		ret.put("Containers", containers);
		ret.put("Ready", readyContainers);
		ret.put("StartTime", content.getStatus().getStartTime().toDate());
		ret.put("PodIP", content.getStatus().getPodIP());
		ret.put("HostIP", content.getStatus().getHostIP());
		
		return (ret);
		}
	
	@Override
	protected List<V1Pod> list(CoreV1Api api, String namespace) throws ApiException
		{
		return (api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null).getItems());
		}
	
	@Override
	protected V1Pod read(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.readNamespacedPod(name, namespace, null, null, null));
		}
	
	@Override
	protected Object create(CoreV1Api api, String namespace, V1Pod content) throws ApiException
		{
		return (api.createNamespacedPod(namespace, content, null, null, null));
		}
	
	@Override
	protected Object replace(CoreV1Api api, String namespace, String name, V1Pod content) throws ApiException
		{
		return (api.replaceNamespacedPod(name, namespace, content, null, null));
		}
	
	@Override
	protected Object patch(CoreV1Api api, String namespace, String name, V1Pod content) throws ApiException
		{
		return (api.patchNamespacedPod(name, namespace, content, null, null));
		}
	
	@Override
	protected Object delete(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.deleteNamespacedPod(name, namespace, new V1DeleteOptions(), null, null, null, null, null));
		}
	}
