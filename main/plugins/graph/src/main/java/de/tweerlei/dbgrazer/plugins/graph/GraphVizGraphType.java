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
package de.tweerlei.dbgrazer.plugins.graph;

import de.tweerlei.dbgrazer.visualization.backend.BaseGraphType;
import de.tweerlei.dbgrazer.visualization.service.GraphDirection;

/**
 * Graph type
 * 
 * @author Robert Wruck
 */
public abstract class GraphVizGraphType extends BaseGraphType
	{
	private final String command;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param command GraphViz command name
	 * @param direction The direction
	 */
	protected GraphVizGraphType(String name, String command, GraphDirection direction)
		{
		super(name, direction);
		this.command = command;
		}
	
	/**
	 * Get the GraphViz command name
	 * @return command name
	 */
	public String getCommand()
		{
		return command;
		}
	}
