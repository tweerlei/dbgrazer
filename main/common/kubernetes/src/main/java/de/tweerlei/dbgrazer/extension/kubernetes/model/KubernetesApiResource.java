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
package de.tweerlei.dbgrazer.extension.kubernetes.model;

import java.util.Collections;
import java.util.Set;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;

/**
 * Kubernetes API resource
 * 
 * @author Robert Wruck
 */
public class KubernetesApiResource extends NamedBase implements Comparable<KubernetesApiResource>
	{
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
		super(name);
		this.kind = kind;
		this.namespaced = namespaced;
		this.verbs = Collections.unmodifiableSet(verbs);
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
		return getName().hashCode() ^ kind.hashCode() ^ (namespaced ? 31 : 0);
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
		return (getName().equals(other.getName()) && kind.equals(other.kind) && namespaced == other.namespaced);
		}
	
	@Override
	public int compareTo(KubernetesApiResource o)
		{
		return (kind.compareTo(o.kind));
		}
	}
