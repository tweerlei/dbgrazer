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
import java.util.Date;
import java.util.List;

import de.tweerlei.common5.collections.Pair;
import de.tweerlei.dbgrazer.visualization.model.ChartDataRow;
import de.tweerlei.dbgrazer.visualization.model.ChartDefinition;

/**
 * Build DOT graph descriptions
 * 
 * @author Robert Wruck
 */
public interface ChartBuilder
	{
	/**
	 * Get a chart type by name
	 * @param name Type name
	 * @return ChartType or null
	 */
	public ChartType getChartType(String name);
	
	/**
	 * Get all supported chart types
	 * @return Chart types
	 */
	public Collection<ChartType> getChartTypes();
	
	/**
	 * Get a chart scaling by name
	 * @param name Type name
	 * @return ChartScaling or null
	 */
	public ChartScaling getChartScaling(String name);
	
	/**
	 * Get all supported chart scalings
	 * @return Chart scalings
	 */
	public Collection<ChartScaling> getChartScalings();
	
	/**
	 * Build a bar chart for distinct string categories
	 * @param name Chart name (used as image map ID)
	 * @param rows Data rows
	 * @param type Preferred chart type
	 * @param scaling Whether to scale the axes logarithmically
	 * @param style ChartStyle
	 * @param title Title
	 * @param subtitle Subtitle
	 * @param yLabel Label for the y column
	 * @param discrete Whether the y values are discrete
	 * @param xLabel Label for the x column
	 * @param rowLink Query to link rows to (may be null)
	 * @return Chart description
	 */
	public ChartDefinition buildCategoryChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink);
	
	/**
	 * Build a line chart for discrete integer rows
	 * @param name Chart name (used as image map ID)
	 * @param rows Data rows
	 * @param type Preferred chart type
	 * @param scaling Whether to scale the axes logarithmically
	 * @param style ChartStyle
	 * @param title Title
	 * @param subtitle Subtitle
	 * @param yLabel Label for the y column
	 * @param yDiscrete Whether the y values are discrete
	 * @param xLabel Label for the x column
	 * @param xDiscrete Whether the x values are discrete
	 * @param rowLink Query to link rows to (may be null)
	 * @return Chart description
	 */
	public ChartDefinition buildNumberChart(String name, List<ChartDataRow<Number, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean yDiscrete, String xLabel, boolean xDiscrete, String rowLink);
	
	/**
	 * Build a line chart for the time domain
	 * @param name Chart name (used as image map ID)
	 * @param rows Data rows
	 * @param type Preferred chart type
	 * @param scaling Whether to scale the axes logarithmically
	 * @param style ChartStyle
	 * @param title Title
	 * @param subtitle Subtitle
	 * @param yLabel Label for the y column
	 * @param discrete Whether the y values are discrete
	 * @param xLabel Label for the x column
	 * @param rowLink Query to link rows to (may be null)
	 * @return Chart description
	 */
	public ChartDefinition buildTimeChart(String name, List<ChartDataRow<Date, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink);
	
	/**
	 * Build a line chart for the time range domain
	 * @param name Chart name (used as image map ID)
	 * @param rows Data rows
	 * @param type Preferred chart type
	 * @param scaling Whether to scale the axes logarithmically
	 * @param style ChartStyle
	 * @param title Title
	 * @param subtitle Subtitle
	 * @param yLabel Label for the y column
	 * @param discrete Whether the y values are discrete
	 * @param xLabel Label for the x column
	 * @param rowLink Query to link rows to (may be null)
	 * @return Chart description
	 */
	public ChartDefinition buildTimerangeChart(String name, List<ChartDataRow<Pair<Date, Date>, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink);
	
	/**
	 * Build a Gantt chart for the time domain
	 * @param name Chart name (used as image map ID)
	 * @param rows Data rows
	 * @param type Preferred chart type
	 * @param scaling Whether to scale the axes logarithmically
	 * @param style ChartStyle
	 * @param title Title
	 * @param subtitle Subtitle
	 * @param yLabel Label for the y column
	 * @param discrete Whether the y values are discrete
	 * @param xLabel Label for the x column
	 * @param rowLink Query to link rows to (may be null)
	 * @return Chart description
	 */
	public ChartDefinition buildTimeSeriesChart(String name, List<ChartDataRow<Pair<Date, Date>, String>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink);
	
	/**
	 * Build a chart with Z values
	 * @param name Chart name (used as image map ID)
	 * @param rows Data rows
	 * @param type Preferred chart type
	 * @param scaling Whether to scale the axes logarithmically
	 * @param style ChartStyle
	 * @param title Title
	 * @param subtitle Subtitle
	 * @param yLabel Label for the y column
	 * @param yDiscrete Whether the y values are discrete
	 * @param xLabel Label for the x column
	 * @param xDiscrete Whether the x values are discrete
	 * @param rowLink Query to link rows to (may be null)
	 * @return Chart description
	 */
	public ChartDefinition buildZChart(String name, List<ChartDataRow<Pair<Number, Number>, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean yDiscrete, String xLabel, boolean xDiscrete, String rowLink);
	}
