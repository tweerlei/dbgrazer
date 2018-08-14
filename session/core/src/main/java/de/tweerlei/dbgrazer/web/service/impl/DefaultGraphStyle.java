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
package de.tweerlei.dbgrazer.web.service.impl;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.visualization.service.GraphStyle;
import de.tweerlei.dbgrazer.web.session.RequestSettings;

/**
 * The default graph style
 * 
 * @author Robert Wruck
 */
@Service
public class DefaultGraphStyle implements GraphStyle
	{
	private static final int TITLE_SIZE = 24;
	private static final int SUBTITLE_SIZE = 18;
	
	private final RequestSettings themeSettings;
	
	private Map<String, String> DIGRAPH_ATTRS;
	private Map<String, String> GRAPH_ATTRS;
	private Map<String, String> NODE_ATTRS;
	private Map<String, String> EDGE_ATTRS;
	private Map<String, String> START_ATTRS;
	private Map<String, String> END_ATTRS;
	
	/**
	 * Constructor
	 * @param themeSettings ThemeSettings
	 */
	@Autowired
	public DefaultGraphStyle(RequestSettings themeSettings)
		{
		this.themeSettings = themeSettings;
		}
	
	/**
	 * Initialize
	 */
	@PostConstruct
	public void init()
		{
		DIGRAPH_ATTRS = buildDigraphAttributes();
		GRAPH_ATTRS = buildGraphAttributes();
		NODE_ATTRS = buildNodeAttributes();
		EDGE_ATTRS = buildEdgeAttributes();
		START_ATTRS = buildStartNodeAttributes();
		END_ATTRS = buildEndNodeAttributes();
		}
	
	private Map<String, String> buildDigraphAttributes()
		{
		final Map<String, String> map = new HashMap<String, String>();
		map.put("fontsize", String.valueOf(TITLE_SIZE));
		return (Collections.unmodifiableMap(map));
		}
	
	private Map<String, String> buildGraphAttributes()
		{
		final Map<String, String> map = new HashMap<String, String>();
		map.put("overlap", "scalexy");
		map.put("splines", "true");
		map.put("fontsize", String.valueOf(TITLE_SIZE));
		return (Collections.unmodifiableMap(map));
		}
	
	private Map<String, String> buildNodeAttributes()
		{
		final Map<String, String> map = new HashMap<String, String>();
		map.put("style", "filled");
		return (Collections.unmodifiableMap(map));
		}
	
	private Map<String, String> buildEdgeAttributes()
		{
		final Map<String, String> map = new HashMap<String, String>();
		return (Collections.unmodifiableMap(map));
		}
	
	private Map<String, String> buildStartNodeAttributes()
		{
		final Map<String, String> map = new HashMap<String, String>();
		map.put("label", "");
		map.put("shape", "circle");
		map.put("width", "0.25");
		return (Collections.unmodifiableMap(map));
		}
	
	private Map<String, String> buildEndNodeAttributes()
		{
		final Map<String, String> map = new HashMap<String, String>();
		map.put("label", "");
		map.put("shape", "circle");
		map.put("width", "0.25");
		map.put("peripheries", "2");
		return (Collections.unmodifiableMap(map));
		}
	
	private String formatColor(Color color)
		{
		return (String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
		}
	
	@Override
	public Map<String, String> getDirectedGraphAttributes()
		{
		final Map<String, String> ret = new HashMap<String, String>(DIGRAPH_ATTRS);
		
		ret.put("bgcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.BACKGROUND_COLOR)));
		ret.put("fontcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR)));
		ret.put("fontname", themeSettings.getConfig().get(ThemeKeys.BOLD_FONT));
		
		return (ret);
		}
	
	@Override
	public Map<String, String> getUndirectedGraphAttributes()
		{
		final Map<String, String> ret = new HashMap<String, String>(GRAPH_ATTRS);
		
		ret.put("bgcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.BACKGROUND_COLOR)));
		ret.put("fontcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR)));
		ret.put("fontname", themeSettings.getConfig().get(ThemeKeys.BOLD_FONT));
		
		return (ret);
		}
	
	@Override
	public Map<String, String> getNodeAttributes()
		{
		final Map<String, String> ret = new HashMap<String, String>(NODE_ATTRS);
		
		ret.put("fillcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.BACKGROUND_COLOR)));
		ret.put("color", formatColor(themeSettings.getConfig().get(ThemeKeys.GRID_COLOR)));
		ret.put("fontcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR)));
		ret.put("fontname", themeSettings.getConfig().get(ThemeKeys.NORMAL_FONT));
		
		return (ret);
		}
	
	@Override
	public Map<String, String> getStartNodeAttributes()
		{
		final Map<String, String> ret = new HashMap<String, String>(START_ATTRS);
		
		ret.put("fillcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR)));
		
		return (ret);
		}
	
	@Override
	public Map<String, String> getEndNodeAttributes()
		{
		final Map<String, String> ret = new HashMap<String, String>(END_ATTRS);
		
		ret.put("fillcolor", formatColor(themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR)));
		
		return (ret);
		}
	
	@Override
	public Map<String, String> getEdgeAttributes()
		{
		final Map<String, String> ret = new HashMap<String, String>(EDGE_ATTRS);
		
		ret.put("color", formatColor(themeSettings.getConfig().get(ThemeKeys.GRID_COLOR)));
		
		return (ret);
		}
	
	@Override
	public String getNormalFont()
		{
		return (themeSettings.getConfig().get(ThemeKeys.NORMAL_FONT));
		}
	
	@Override
	public String getBoldFont()
		{
		return (themeSettings.getConfig().get(ThemeKeys.BOLD_FONT));
		}
	
	@Override
	public String getItalicFont()
		{
		return (themeSettings.getConfig().get(ThemeKeys.ITALIC_FONT));
		}
	
	@Override
	public String getBoldItalicFont()
		{
		return (themeSettings.getConfig().get(ThemeKeys.BOLDITALIC_FONT));
		}
	
	@Override
	public int getSubtitleSize()
		{
		return (SUBTITLE_SIZE);
		}
	
	@Override
	public String getHighlightBackgroundColor()
		{
		return (formatColor(themeSettings.getConfig().get(ThemeKeys.HIGHLIGHT_BACKGROUND_COLOR)));
		}
	
	@Override
	public String getHighlightForegroundColor()
		{
		return (formatColor(themeSettings.getConfig().get(ThemeKeys.HIGHLIGHT_FOREGROUND_COLOR)));
		}
	}
