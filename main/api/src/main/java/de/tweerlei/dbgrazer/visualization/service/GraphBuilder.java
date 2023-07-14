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
package de.tweerlei.dbgrazer.visualization.service;

import java.util.Collection;
import java.util.Set;

import de.tweerlei.dbgrazer.visualization.model.GraphDefinition;
import de.tweerlei.dbgrazer.visualization.model.GraphEdge;
import de.tweerlei.dbgrazer.visualization.model.GraphNode;

/**
 * Build DOT graph descriptions
 * 
 * @author Robert Wruck
 */
public interface GraphBuilder
	{
	/**
	 * Get a graph type by name
	 * @param name Type name
	 * @return GraphType or null
	 */
	public GraphType getGraphType(String name);
	
	/**
	 * Get all supported graph types
	 * @return Graph types
	 */
	public Collection<GraphType> getGraphTypes();
	
	/**
	 * Build a graph
	 * @param name Graph name (used as image map ID)
	 * @param type Graph type
	 * @param style Graph style
	 * @param title Graph title
	 * @param subtitle Graph subtitle
	 * @param attrs Node attributes
	 * @param nodes Nodes (ID -> label)
	 * @param edges Edges (node ID -> node ID)
	 * @param nodeLink Query to link nodes to (may be null)
	 * @return Graph description
	 */
	public GraphDefinition buildGraph(String name, GraphType type, GraphStyle style, String title, String subtitle, String attrs, Set<GraphNode> nodes, Set<GraphEdge> edges, String nodeLink);
	}
