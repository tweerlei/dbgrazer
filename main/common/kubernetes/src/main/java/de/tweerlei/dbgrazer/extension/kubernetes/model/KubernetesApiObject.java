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
import java.util.Date;
import java.util.Map;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;

/**
 * Kubernetes API object
 * 
 * @author Robert Wruck
 */
public class KubernetesApiObject extends NamedBase implements Comparable<KubernetesApiObject>
	{
	private final Date creationTimestamp;
	private Map<String, String> labels;
	private Map<String, Object> properties;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param creationTimestamp Creation timestamp
	 * @param labels Labels
	 * @param properties Additional properties
	 */
	public KubernetesApiObject(String name, Date creationTimestamp, Map<String, String> labels, Map<String, Object> properties)
		{
		super(name);
		this.creationTimestamp = creationTimestamp;
		this.labels = (labels == null) ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(labels);
		this.properties = (properties == null) ? Collections.<String, Object>emptyMap() : Collections.unmodifiableMap(properties);
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
	
	/**
	 * Get the labels
	 * @return the labels
	 */
	public Map<String, Object> getProperties()
		{
		return properties;
		}
	
	@Override
	public int hashCode()
		{
		return getName().hashCode();
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
		return (getName().equals(other.getName()));
		}
	
	@Override
	public int compareTo(KubernetesApiObject o)
		{
		return (getName().compareTo(o.getName()));
		}
	}
