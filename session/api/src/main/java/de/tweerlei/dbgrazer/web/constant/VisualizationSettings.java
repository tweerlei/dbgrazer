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
package de.tweerlei.dbgrazer.web.constant;

/**
 * Settings for data visualization
 * 
 * @author Robert Wruck
 */
public final class VisualizationSettings
	{
	/** Graph type name for ERM graphs */
	public static final String ERM_GRAPH_TYPE = "DOT_LR";
	
	/** Query type name for graphs */
	public static final String GRAPH_QUERY_TYPE = "GRAPH";
	
	/** Query type name for charts */
	public static final String CHART_QUERY_TYPE = "CHART";
	
	/** Query type name for graphics */
	public static final String SVG_QUERY_TYPE = "SVG";
	
	/** Query type name for time charts */
	public static final String TIMECHART_QUERY_TYPE = "TIMECHART";
	
	/** Query type name for dashboards */
	public static final String DASHBOARD_QUERY_TYPE = "DASHBOARD";
	
	/** Query type name for panels */
	public static final String PANELS_QUERY_TYPE = "PANELS";
	
	/** Query type name for explorer */
	public static final String EXPLORER_QUERY_TYPE = "EXPLORER";
	
	/** Query type name for navigator */
	public static final String NAVIGATOR_QUERY_TYPE = "NAVIGATOR";
	
	/** Query type name for trees */
	public static final String TREE_QUERY_TYPE = "TREE";
	
	/** Query type name for multilevel */
	public static final String MULTILEVEL_QUERY_TYPE = "MULTILEVEL";
	
	
	private VisualizationSettings()
		{
		}
	}
