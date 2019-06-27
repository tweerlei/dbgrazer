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
package de.tweerlei.dbgrazer.web.controller.kubernetes;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class KubernetesMessageKeys
	{
	/** JS extension file */
	public static final String EXTENSION_JS = "kubernetes.js";
	
	/*
	 * Multilevel names for the DB browser
	 */
	
	/** Topic tab title */
	public static final String NAMESPACE_LEVEL = "$kubernetesNamespaceLevel";
	/** Partition tab title */
	public static final String KIND_LEVEL = "$kubernetesKindLevel";
	/** Message tab title */
	public static final String OBJECT_LEVEL = "$kubernetesObjectLevel";
	
	/*
	 * Tab titles, prefixed with "$" for detection by tabs.tag
	 */
	
	/** Topics tab title */
	public static final String NAMESPACE_TAB = "$kubernetesNamespaceTab";
	/** Partitions tab title */
	public static final String KIND_TAB = "$kubernetesKindTab";
	/** Messages tab title */
	public static final String OBJECT_TAB = "$kubernetesObjectTab";
	
	/*
	 * Column names
	 */
	/** ID column */
	public static final String ID = "id";
	/** Name column */
	public static final String NAMESPACE = "kubernetesNamespace";
	/** Name column */
	public static final String KIND = "kubernetesKind";
	/** Name column */
	public static final String OBJECT = "kubernetesObject";
	/** API group column */
	public static final String API_GROUP = "kubernetesApiGroup";
	/** API version column */
	public static final String API_VERSION = "kubernetesApiVersion";
	
	
	private KubernetesMessageKeys()
		{
		}
	}
