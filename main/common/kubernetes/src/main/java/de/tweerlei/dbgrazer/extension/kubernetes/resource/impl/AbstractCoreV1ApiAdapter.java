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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiObject;
import de.tweerlei.dbgrazer.extension.kubernetes.resource.KubernetesApiAdapter;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.JSON;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1ObjectMeta;

/**
 * Base class for KubernetesApiAdapters
 * @param <T> Resource type
 * 
 * @author Robert Wruck
 */
public abstract class AbstractCoreV1ApiAdapter<T> extends NamedBase implements KubernetesApiAdapter
	{
	/** The group name for core API objects */
	public static final String CORE_GROUP = "(core)";
	/** The version name for core API objects */
	public static final String CORE_VERSION = "v1";
	
	/**
	 * Constructor
	 * @param name Name
	 */
	public AbstractCoreV1ApiAdapter(String name)
		{
		super(CORE_GROUP + "/" + CORE_VERSION + "/" + name);
		}
	
	/**
	 * Deserialize a resource from JSON
	 * @param json JSON
	 * @param content JSON text
	 * @return Resource
	 */
	protected abstract T deserialize(JSON json, String content);
	
	/**
	 * Get resource metadata
	 * @param content Resource
	 * @return V1ObjectMeta
	 */
	protected abstract V1ObjectMeta getMetadata(T content);
	
	/**
	 * Get additional properties
	 * @param content Resource
	 * @return Properties
	 */
	protected Map<String, Object> getProperties(T content)
		{
		return (null);
		}
	
	@Override
	public final Set<KubernetesApiObject> list(ApiClient client, String namespace) throws ApiException
		{
		final CoreV1Api api = new CoreV1Api(client);
		final Set<KubernetesApiObject> names = new TreeSet<KubernetesApiObject>();
		for (T resource : list(api, namespace))
			{
			final V1ObjectMeta metadata = getMetadata(resource);
			names.add(new KubernetesApiObject(
					metadata.getName(),
					metadata.getCreationTimestamp().toDate(),
					metadata.getLabels(),
					getProperties(resource)
					));
			}
		return (names);
		}
	
	/**
	 * List resources
	 * @param api CoreV1Api
	 * @param namespace Namespace name
	 * @return List of resources
	 * @throws ApiException on error
	 */
	protected abstract List<T> list(CoreV1Api api, String namespace) throws ApiException;
	
	@Override
	public final String read(ApiClient client, String namespace, String name) throws ApiException
		{
		final CoreV1Api api = new CoreV1Api(client);
		final JSON json = client.getJSON();
		return (json.serialize(read(api, namespace, name)));
		}
	
	/**
	 * Get a resource
	 * @param api CoreV1Api
	 * @param namespace Namespace name
	 * @param name Resource name
	 * @return Resource
	 * @throws ApiException on error
	 */
	protected abstract T read(CoreV1Api api, String namespace, String name) throws ApiException;
	
	@Override
	public final String create(ApiClient client, String namespace, String content) throws ApiException
		{
		final CoreV1Api api = new CoreV1Api(client);
		final JSON json = client.getJSON();
		return (json.serialize(create(api, namespace, deserialize(json, content))));
		}
	
	/**
	 * Create a resource
	 * @param api CoreV1Api
	 * @param namespace Namespace name
	 * @param content Resource JSON
	 * @return Server response
	 * @throws ApiException on error
	 */
	protected abstract Object create(CoreV1Api api, String namespace, T content) throws ApiException;
	
	@Override
	public final String replace(ApiClient client, String namespace, String name, String content) throws ApiException
		{
		final CoreV1Api api = new CoreV1Api(client);
		final JSON json = client.getJSON();
		return (json.serialize(replace(api, name, namespace, deserialize(json, content))));
		}
	
	/**
	 * Replace a resource
	 * @param api CoreV1Api
	 * @param namespace Namespace name
	 * @param name Resource name
	 * @param content Resource
	 * @return Server response
	 * @throws ApiException on error
	 */
	protected abstract Object replace(CoreV1Api api, String namespace, String name, T content) throws ApiException;
	
	@Override
	public final String patch(ApiClient client, String namespace, String name, String content) throws ApiException
		{
		final CoreV1Api api = new CoreV1Api(client);
		final JSON json = client.getJSON();
		return (json.serialize(patch(api, name, namespace, deserialize(json, content))));
		}
	
	/**
	 * Patch a resource
	 * @param api CoreV1Api
	 * @param namespace Namespace name
	 * @param name Resource name
	 * @param content Patch
	 * @return Server response
	 * @throws ApiException on error
	 */
	protected abstract Object patch(CoreV1Api api, String namespace, String name, T content) throws ApiException;
	
	@Override
	public final String delete(ApiClient client, String namespace, String name) throws ApiException
		{
		final CoreV1Api api = new CoreV1Api(client);
		final JSON json = client.getJSON();
		return (json.serialize(delete(api, name, namespace)));
		}
	
	/**
	 * Delete a resource
	 * @param api CoreV1Api
	 * @param namespace Namespace name
	 * @param name Resource name
	 * @return Server response
	 * @throws ApiException on error
	 */
	protected abstract Object delete(CoreV1Api api, String namespace, String name) throws ApiException;
	}
