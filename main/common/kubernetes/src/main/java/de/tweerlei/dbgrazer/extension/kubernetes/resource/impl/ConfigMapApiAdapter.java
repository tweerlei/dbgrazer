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
import io.kubernetes.client.models.V1ConfigMap;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1ObjectMeta;

/**
 * KubernetesApiAdapter for ConfigMaps
 * 
 * @author Robert Wruck
 */
@Service
public class ConfigMapApiAdapter extends AbstractCoreV1ApiAdapter<V1ConfigMap>
	{
	/**
	 * Constructor
	 */
	public ConfigMapApiAdapter()
		{
		super("ConfigMap");
		}
	
	@Override
	protected V1ConfigMap deserialize(JSON json, String content)
		{
		return (json.<V1ConfigMap>deserialize(content, V1ConfigMap.class));
		}
	
	@Override
	protected V1ObjectMeta getMetadata(V1ConfigMap content)
		{
		return (content.getMetadata());
		}
	
	@Override
	protected Map<String, Object> getProperties(V1ConfigMap content)
		{
		final Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("Entries", content.getData().size());
		return (ret);
		}
	
	@Override
	protected List<V1ConfigMap> list(CoreV1Api api, String namespace) throws ApiException
		{
		return (api.listNamespacedConfigMap(namespace, null, null, null, null, null, null, null, null, null).getItems());
		}
	
	@Override
	protected V1ConfigMap read(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.readNamespacedConfigMap(name, namespace, null, null, null));
		}
	
	@Override
	protected Object create(CoreV1Api api, String namespace, V1ConfigMap content) throws ApiException
		{
		return (api.createNamespacedConfigMap(namespace, content, null, null, null));
		}
	
	@Override
	protected Object replace(CoreV1Api api, String namespace, String name, V1ConfigMap content) throws ApiException
		{
		return (api.replaceNamespacedConfigMap(name, namespace, content, null, null));
		}
	
	@Override
	protected Object patch(CoreV1Api api, String namespace, String name, V1ConfigMap content) throws ApiException
		{
		return (api.patchNamespacedConfigMap(name, namespace, content, null, null));
		}
	
	@Override
	protected Object delete(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.deleteNamespacedConfigMap(name, namespace, new V1DeleteOptions(), null, null, null, null, null));
		}
	}
