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

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiObject;
import de.tweerlei.dbgrazer.extension.kubernetes.resource.KubernetesApiAdapter;
import de.tweerlei.dbgrazer.extension.kubernetes.support.CustomObjectsApiExtension;
import de.tweerlei.dbgrazer.extension.kubernetes.support.V1Item;
import de.tweerlei.dbgrazer.extension.kubernetes.support.V1ItemList;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.JSON;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1ObjectMeta;

/**
 * KubernetesApiAdapter for custom objects
 * 
 * @author Robert Wruck
 */
public class CustomObjectApiAdapter extends NamedBase implements KubernetesApiAdapter
	{
	private final String group;
	private final String version;
	private final String kind;
	
	/**
	 * Constructor
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 */
	public CustomObjectApiAdapter(String group, String version, String kind)
		{
		super(group + "/" + version + "/" + kind);
		this.group = group;
		this.version = version;
		this.kind = kind;
		}
	
	@Override
	public final Set<KubernetesApiObject> list(ApiClient client, String namespace) throws ApiException
		{
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
		final JSON json = client.getJSON();
		final Set<KubernetesApiObject> names = new TreeSet<KubernetesApiObject>();
		
		final Object list = api.listNamespacedCustomObject(group, version, namespace, kind, null, null, null, null);
		for (V1Item item : extractList(list, json).getItems())
			{
			final V1ObjectMeta metadata = item.getMetadata();
			names.add(new KubernetesApiObject(
					metadata.getName(),
					metadata.getCreationTimestamp().toDate(),
					metadata.getLabels(),
					null
					));
			}
		
		return (names);
		}
	
	private V1ItemList extractList(Object listObj, JSON json)
		{
		if (listObj instanceof Map)
			{
			final String text = json.serialize(listObj);
			return (json.deserialize(text, V1ItemList.class));
			}
		
		return (null);
		}
	
	private Object deserialize(String content)
		{
		try	{
			return (content.getBytes("UTF-8"));
			}
		catch (UnsupportedEncodingException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public final String read(ApiClient client, String namespace, String name) throws ApiException
		{
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
		final JSON json = client.getJSON();
		
		return (json.serialize(api.getNamespacedCustomObject(group, version, namespace, kind, name)));
		}
	
	@Override
	public final String create(ApiClient client, String namespace, String content) throws ApiException
		{
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
		final JSON json = client.getJSON();
		
		return (json.serialize(api.createNamespacedCustomObject(group, version, namespace, kind, deserialize(content), null)));
		}
	
	@Override
	public final String replace(ApiClient client, String namespace, String name, String content) throws ApiException
		{
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
		final JSON json = client.getJSON();
		
		return (json.serialize(api.replaceNamespacedCustomObject(group, version, namespace, kind, name, deserialize(content))));
		}
	
	@Override
	public final String patch(ApiClient client, String namespace, String name, String content) throws ApiException
		{
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
		final JSON json = client.getJSON();
		
		return (json.serialize(api.patchNamespacedCustomObject(group, version, namespace, kind, name, deserialize(content))));
		}
	
	@Override
	public final String delete(ApiClient client, String namespace, String name) throws ApiException
		{
		final CustomObjectsApiExtension api = new CustomObjectsApiExtension(client);
		final JSON json = client.getJSON();
		
		return (json.serialize(api.deleteNamespacedCustomObject(group, version, namespace, kind, name, new V1DeleteOptions(), null, null, null)));
		}
	}
