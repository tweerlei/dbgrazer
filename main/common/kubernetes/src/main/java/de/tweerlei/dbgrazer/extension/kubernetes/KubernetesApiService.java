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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Retrieve Kubernetes metadata
 * 
 * @author Robert Wruck
 */
public interface KubernetesApiService
	{
	/**
	 * Kubernetes API resource
	 */
	public static class KubernetesApiResource implements Comparable<KubernetesApiResource>
		{
		private final String name;
		private final String kind;
		private final boolean namespaced;
		private final Set<String> verbs;
		
		/**
		 * Constructor
		 * @param name Name
		 * @param kind Kind
		 * @param namespaced Namespaced
		 * @param verbs Supported verbs
		 */
		public KubernetesApiResource(String name, String kind, boolean namespaced, Set<String> verbs)
			{
			this.name = name;
			this.kind = kind;
			this.namespaced = namespaced;
			this.verbs = Collections.unmodifiableSet(verbs);
			}
		
		/**
		 * Get the name
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}
		
		/**
		 * Get the kind
		 * @return the kind
		 */
		public String getKind()
			{
			return kind;
			}
		
		/**
		 * Get the namespaced
		 * @return the namespaced
		 */
		public boolean isNamespaced()
			{
			return namespaced;
			}
		
		/**
		 * Get the verbs
		 * @return the verbs
		 */
		public Set<String> getVerbs()
			{
			return verbs;
			}
		
		@Override
		public int hashCode()
			{
			return name.hashCode() ^ kind.hashCode() ^ (namespaced ? 31 : 0);
			}
		
		@Override
		public boolean equals(Object o)
			{
			if (o == this)
				return (true);
			if (o == null)
				return (false);
			if (!(o instanceof KubernetesApiResource))
				return (false);
			final KubernetesApiResource other = (KubernetesApiResource) o;
			return (name.equals(other.name) && kind.equals(other.kind) && namespaced == other.namespaced);
			}
		
		@Override
		public int compareTo(KubernetesApiResource o)
			{
			return (kind.compareTo(o.kind));
			}
		}
	
	/**
	 * Kubernetes API object
	 */
	public static class KubernetesApiObject implements Comparable<KubernetesApiObject>
		{
		private final String name;
		private final Date creationTimestamp;
		private Map<String, String> labels;
		
		/**
		 * Constructor
		 * @param name Name
		 * @param creationTimestamp Creation timestamp
		 * @param labels Labels
		 */
		public KubernetesApiObject(String name, Date creationTimestamp, Map<String, String> labels)
			{
			this.name = name;
			this.creationTimestamp = creationTimestamp;
			this.labels = Collections.unmodifiableMap(labels);
			}
		
		/**
		 * Get the name
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}
		
		/**
		 * Get the creationTimestamp
		 * @return the creationTimestamp
		 */
		public Date getCreationTimestamp()
			{
			return creationTimestamp;
			}
		
		/**
		 * Get the labels
		 * @return the labels
		 */
		public Map<String, String> getLabels()
			{
			return labels;
			}
		
		@Override
		public int hashCode()
			{
			return name.hashCode();
			}
		
		@Override
		public boolean equals(Object o)
			{
			if (o == this)
				return (true);
			if (o == null)
				return (false);
			if (!(o instanceof KubernetesApiObject))
				return (false);
			final KubernetesApiObject other = (KubernetesApiObject) o;
			return (name.equals(other.name));
			}
		
		@Override
		public int compareTo(KubernetesApiObject o)
			{
			return (name.compareTo(o.name));
			}
		}
	
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
	 * Flush the metadata cache
	 * @param link Link name
	 */
	public void flushCache(String link);
	}
