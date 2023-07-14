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
 * Graph edge
  * 
 * @author Robert Wruck
*/
public class GraphEdge
	{
	private final String startNode;
	private final String endNode;
	private final String name;
	private final String attrs;
	
	/**
	 * Constructor
	 * @param startNode Start node ID
	 * @param endNode End node ID
	 * @param name Edge name
	 * @param attrs Edge attributes
	 */
	public GraphEdge(String startNode, String endNode, String name, String attrs)
		{
		this.startNode = startNode;
		this.endNode = endNode;
		this.name = name;
		this.attrs = attrs;
		}

	/**
	 * Get the startNode
	 * @return the startNode
	 */
	public String getStartNode()
		{
		return startNode;
		}

	/**
	 * Get the endNode
	 * @return the endNode
	 */
	public String getEndNode()
		{
		return endNode;
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
		result = prime * result
				+ ((endNode == null) ? 0 : endNode.hashCode());
		result = prime * result
				+ ((startNode == null) ? 0 : startNode.hashCode());
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
		GraphEdge other = (GraphEdge) obj;
		if (endNode == null) {
			if (other.endNode != null)
				return false;
		} else if (!endNode.equals(other.endNode))
			return false;
		if (startNode == null) {
			if (other.startNode != null)
				return false;
		} else if (!startNode.equals(other.startNode))
			return false;
		return true;
		}
	}
