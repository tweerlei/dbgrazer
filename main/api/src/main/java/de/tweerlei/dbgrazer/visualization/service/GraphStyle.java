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

import java.util.Map;

/**
 * Defined appearance of graphs
 * 
 * @author Robert Wruck
 */
public interface GraphStyle
	{
	/**
	 * Get the default attributes for directed graphs
	 * @return Attributes
	 */
	public Map<String, String> getDirectedGraphAttributes();
	
	/**
	 * Get the default attributes for undirected graphs
	 * @return Attributes
	 */
	public Map<String, String> getUndirectedGraphAttributes();
	
	/**
	 * Get the default attributes for nodes
	 * @return Attributes
	 */
	public Map<String, String> getNodeAttributes();
	
	/**
	 * Get the default attributes for start nodes
	 * @return Attributes
	 */
	public Map<String, String> getStartNodeAttributes();
	
	/**
	 * Get the default attributes for end nodes
	 * @return Attributes
	 */
	public Map<String, String> getEndNodeAttributes();
	
	/**
	 * Get the default attributes for edges
	 * @return Attributes
	 */
	public Map<String, String> getEdgeAttributes();
	
	/**
	 * Get the normal font name
	 * @return Font name
	 */
	public String getNormalFont();
	
	/**
	 * Get the bold font name
	 * @return Font name
	 */
	public String getBoldFont();
	
	/**
	 * Get the italic font name
	 * @return Font name
	 */
	public String getItalicFont();
	
	/**
	 * Get the bold italic font name
	 * @return Font name
	 */
	public String getBoldItalicFont();
	
	/**
	 * Get the font size to use for the subtitle
	 * @return Font size
	 */
	public int getSubtitleSize();
	
	/**
	 * Get the highlight background color name
	 * @return Color name
	 */
	public String getHighlightBackgroundColor();
	
	/**
	 * Get the highlight foreground color name
	 * @return Color name
	 */
	public String getHighlightForegroundColor();
	}
