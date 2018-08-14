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

import de.tweerlei.dbgrazer.visualization.service.ChartScaling;
import de.tweerlei.dbgrazer.visualization.service.ChartType;

/**
 * Chart definition
 * 
 * @author Robert Wruck
 */
public class ChartDefinition implements Serializable
	{
	private final String name;
	private final ChartType type;
	private final ChartScaling scaling;
	private final Serializable chart;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param type ChartType
	 * @param scaling ChartScaling
	 * @param chart Chart
	 */
	public ChartDefinition(String name, Serializable chart, ChartType type, ChartScaling scaling)
		{
		this.name = name;
		this.type = type;
		this.scaling = scaling;
		this.chart = chart;
		}
	
	/**
	 * Get the chart name
	 * @return Name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * Get the chart type
	 * @return the type
	 */
	public ChartType getType()
		{
		return type;
		}
	
	/**
	 * Get the chart scaling
	 * @return the scaling
	 */
	public ChartScaling getScaling()
		{
		return scaling;
		}
	
	/**
	 * Get the chart
	 * @return Chart object
	 */
	public Serializable getChart()
		{
		return chart;
		}
	}
