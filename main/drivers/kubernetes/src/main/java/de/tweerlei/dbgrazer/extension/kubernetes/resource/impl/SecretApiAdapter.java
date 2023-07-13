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
import io.kubernetes.client.models.V1Secret;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1ObjectMeta;

/**
 * KubernetesApiAdapter for Secrets
 * 
 * @author Robert Wruck
 */
@Service
public class SecretApiAdapter extends AbstractCoreV1ApiAdapter<V1Secret>
	{
	/**
	 * Constructor
	 */
	public SecretApiAdapter()
		{
		super("Secret");
		}
	
	@Override
	protected V1Secret deserialize(JSON json, String content)
		{
		return (json.<V1Secret>deserialize(content, V1Secret.class));
		}
	
	@Override
	protected V1ObjectMeta getMetadata(V1Secret content)
		{
		return (content.getMetadata());
		}
	
	@Override
	protected Map<String, Object> getProperties(V1Secret content)
		{
		final Map<String, Object> ret = new LinkedHashMap<String, Object>();
		ret.put("Entries", content.getData().size());
		return (ret);
		}
	
	@Override
	protected List<V1Secret> list(CoreV1Api api, String namespace) throws ApiException
		{
		return (api.listNamespacedSecret(namespace, null, null, null, null, null, null, null, null, null).getItems());
		}
	
	@Override
	protected V1Secret read(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.readNamespacedSecret(name, namespace, null, null, null));
		}
	
	@Override
	protected Object create(CoreV1Api api, String namespace, V1Secret content) throws ApiException
		{
		return (api.createNamespacedSecret(namespace, content, null, null, null));
		}
	
	@Override
	protected Object replace(CoreV1Api api, String namespace, String name, V1Secret content) throws ApiException
		{
		return (api.replaceNamespacedSecret(name, namespace, content, null, null));
		}
	
	@Override
	protected Object patch(CoreV1Api api, String namespace, String name, V1Secret content) throws ApiException
		{
		return (api.patchNamespacedSecret(name, namespace, content, null, null));
		}
	
	@Override
	protected Object delete(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.deleteNamespacedSecret(name, namespace, new V1DeleteOptions(), null, null, null, null, null));
		}
	}
