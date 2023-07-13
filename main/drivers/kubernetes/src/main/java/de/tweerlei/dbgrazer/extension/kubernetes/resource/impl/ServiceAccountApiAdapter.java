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

import java.util.List;

import org.springframework.stereotype.Service;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.JSON;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1ServiceAccount;

/**
 * KubernetesApiAdapter for ServiceAccounts
 * 
 * @author Robert Wruck
 */
@Service
public class ServiceAccountApiAdapter extends AbstractCoreV1ApiAdapter<V1ServiceAccount>
	{
	/**
	 * Constructor
	 */
	public ServiceAccountApiAdapter()
		{
		super("ServiceAccount");
		}
	
	@Override
	protected V1ServiceAccount deserialize(JSON json, String content)
		{
		return (json.<V1ServiceAccount>deserialize(content, V1ServiceAccount.class));
		}
	
	@Override
	protected V1ObjectMeta getMetadata(V1ServiceAccount content)
		{
		return (content.getMetadata());
		}
	
	@Override
	protected List<V1ServiceAccount> list(CoreV1Api api, String namespace) throws ApiException
		{
		return (api.listNamespacedServiceAccount(namespace, null, null, null, null, null, null, null, null, null).getItems());
		}
	
	@Override
	protected V1ServiceAccount read(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.readNamespacedServiceAccount(name, namespace, null, null, null));
		}
	
	@Override
	protected Object create(CoreV1Api api, String namespace, V1ServiceAccount content) throws ApiException
		{
		return (api.createNamespacedServiceAccount(namespace, content, null, null, null));
		}
	
	@Override
	protected Object replace(CoreV1Api api, String namespace, String name, V1ServiceAccount content) throws ApiException
		{
		return (api.replaceNamespacedServiceAccount(name, namespace, content, null, null));
		}
	
	@Override
	protected Object patch(CoreV1Api api, String namespace, String name, V1ServiceAccount content) throws ApiException
		{
		return (api.patchNamespacedServiceAccount(name, namespace, content, null, null));
		}
	
	@Override
	protected Object delete(CoreV1Api api, String namespace, String name) throws ApiException
		{
		return (api.deleteNamespacedServiceAccount(name, namespace, new V1DeleteOptions(), null, null, null, null, null));
		}
	}
