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
package de.tweerlei.dbgrazer.extension.kubernetes.support;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import io.kubernetes.client.models.V1ListMeta;

/**
 * List of generic Kubernetes API objects
 * 
 * @author Robert Wruck
 */
public class V1ItemList
	{
	@SerializedName("apiVersion")
	private String apiVersion = null;
	@SerializedName("kind")
	private String kind = null;
	@SerializedName("metadata")
	private V1ListMeta metadata = null;
	@SerializedName("items")
	private List<V1Item> items = new ArrayList<V1Item>();

	public V1ItemList()
		{
		}

	public String getApiVersion()
		{
		return this.apiVersion;
		}

	public void setApiVersion(String apiVersion)
		{
		this.apiVersion = apiVersion;
		}

	public List<V1Item> getItems()
		{
		return this.items;
		}

	public void setItems(List<V1Item> items)
		{
		this.items = items;
		}

	public String getKind()
		{
		return this.kind;
		}

	public void setKind(String kind)
		{
		this.kind = kind;
		}

	public V1ListMeta getMetadata()
		{
		return this.metadata;
		}

	public void setMetadata(V1ListMeta metadata)
		{
		this.metadata = metadata;
		}
	}
