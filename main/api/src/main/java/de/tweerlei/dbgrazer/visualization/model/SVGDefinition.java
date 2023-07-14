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

/**
 * SVG definition
 * 
 * @author Robert Wruck
 */
public class SVGDefinition implements Serializable
	{
	private final String name;
	private final Serializable svg;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param svg SVG source
	 */
	public SVGDefinition(String name, Serializable svg)
		{
		this.name = name;
		this.svg = svg;
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
	 * Get the graph
	 * @return Graph object
	 */
	public Serializable getSVG()
		{
		return svg;
		}
	}
