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

import java.io.Serializable;

import de.tweerlei.dbgrazer.visualization.service.GraphType;

/**
 * Graph definition
 * 
 * @author Robert Wruck
 */
public class GraphDefinition implements Serializable
	{
	private final String name;
	private final GraphType type;
	private final Serializable graph;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param type Type
	 * @param graph Graph
	 */
	public GraphDefinition(String name, GraphType type, Serializable graph)
		{
		this.name = name;
		this.type = type;
		this.graph = graph;
		}

	/**
	 * Get the graph name
	 * @return Name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * Get the graph type
	 * @return GraphType
	 */
	public GraphType getType()
		{
		return type;
		}

	/**
	 * Get the graph
	 * @return Graph object
	 */
	public Serializable getGraph()
		{
		return graph;
		}
	}
