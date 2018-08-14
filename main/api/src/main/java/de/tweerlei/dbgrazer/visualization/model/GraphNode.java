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
package de.tweerlei.dbgrazer.visualization.model;

/**
 * Graph node
 */
public class GraphNode
	{
	private final String id;
	private final String name;
	private final String link;
	private final String attrs;
	
	/**
	 * Constructor
	 * @param id Node ID
	 * @param name Node text
	 * @param link URL parameter
	 * @param attrs Node attributes
	 */
	public GraphNode(String id, String name, String link, String attrs)
		{
		this.id = id;
		this.name = name;
		this.link = link;
		this.attrs = attrs;
		}

	/**
	 * Get the node ID
	 * @return the id
	 */
	public String getId()
		{
		return id;
		}

	/**
	 * @return the name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * @return the link
	 */
	public String getLink()
		{
		return link;
		}

	/**
	 * Get the attrs
	 * @return the attrs
	 */
	public String getAttrs()
		{
		return attrs;
		}

	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
		}

	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphNode other = (GraphNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
		}
	}
