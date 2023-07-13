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
package de.tweerlei.dbgrazer.extension.kubernetes;

import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiObject;
import de.tweerlei.dbgrazer.extension.kubernetes.model.KubernetesApiResource;

/**
 * Retrieve Kubernetes metadata
 * 
 * @author Robert Wruck
 */
public interface KubernetesApiService
	{
	/**
	 * Get the supported namespaced API resources for a link
	 * @param c Link name
	 * @return Map: API group name -> API version -> Resource Kind -> Resource
	 */
	public Map<String, Map<String, Map<String, KubernetesApiResource>>> getApiResources(String c);
	
	/**
	 * List cluster namespaces
	 * @param c Link name
	 * @return Namespace names
	 */
	public Set<String> listNamespaces(String c);
	
	/**
	 * List objects
	 * @param c Link name
	 * @param namespace Namespace name
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 * @return KubernetesApiObjects
	 */
	public Set<KubernetesApiObject> listApiObjects(String c, String namespace, String group, String version, String kind);
	
	/**
	 * Get an object as JSON
	 * @param c Link name
	 * @param namespace Namespace name
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 * @param name Object name
	 * @return JSON or null
	 */
	public String getApiObject(String c, String namespace, String group, String version, String kind, String name);
	
	/**
	 * Get an object as JSON
	 * @param c Link name
	 * @param namespace Namespace name
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 * @param json Object definition
	 * @return JSON or null
	 */
	public String createApiObject(String c, String namespace, String group, String version, String kind, String json);
	
	/**
	 * Get an object as JSON
	 * @param c Link name
	 * @param namespace Namespace name
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 * @param name Object name
	 * @param json Object definition
	 * @return JSON or null
	 */
	public String replaceApiObject(String c, String namespace, String group, String version, String kind, String name, String json);
	
	/**
	 * Get an object as JSON
	 * @param c Link name
	 * @param namespace Namespace name
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 * @param name Object name
	 * @param json Object definition
	 * @return JSON or null
	 */
	public String patchApiObject(String c, String namespace, String group, String version, String kind, String name, String json);
	
	/**
	 * Get an object as JSON
	 * @param c Link name
	 * @param namespace Namespace name
	 * @param group API group name
	 * @param version API version
	 * @param kind Resource kind
	 * @param name Object name
	 * @return JSON or null
	 */
	public String deleteApiObject(String c, String namespace, String group, String version, String kind, String name);
	
	/**
	 * Flush the metadata cache
	 * @param link Link name
	 */
	public void flushCache(String link);
	}
