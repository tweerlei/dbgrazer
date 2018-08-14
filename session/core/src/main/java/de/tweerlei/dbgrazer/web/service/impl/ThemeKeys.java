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

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ThemeKeys
	{
	/*
	 * Colors
	 */
	
	/** Background color for chart and graph images */
	public static final ConfigKey<Color> BACKGROUND_COLOR = ConfigKey.create("backgroundColor", Color.class, new Color(255, 255, 255));
	
	/** Foreground (text) color for charts and graphs */
	public static final ConfigKey<Color> FOREGROUND_COLOR = ConfigKey.create("foregroundColor", Color.class, new Color(0, 0, 0));
	
	/** Highlight background color for chart and graph images */
	public static final ConfigKey<Color> HIGHLIGHT_BACKGROUND_COLOR = ConfigKey.create("highlightBackgroundColor", Color.class, new Color(238, 238, 238));
	
	/** Highlight foreground (text) color for charts and graphs */
	public static final ConfigKey<Color> HIGHLIGHT_FOREGROUND_COLOR = ConfigKey.create("highlightForegroundColor", Color.class, new Color(0, 0, 0));
	
	/** Grid/line color for charts and graphs */
	public static final ConfigKey<Color> GRID_COLOR = ConfigKey.create("gridColor", Color.class, new Color(128, 128, 128));
	
	/** Base color for chart data rows */
	public static final ConfigKey<Color> BASE_COLOR = ConfigKey.create("baseColor", Color.class, new Color(71, 145, 188));
	
	/** Color for positive differences */
	public static final ConfigKey<Color> POSITIVE_COLOR = ConfigKey.create("positiveColor", Color.class, new Color(69, 166, 83));
	
	/** Color for negative differences */
	public static final ConfigKey<Color> NEGATIVE_COLOR = ConfigKey.create("negativeColor", Color.class, new Color(209, 99, 62));
	
	/*
	 * Fonts
	 */
	
	/** Normal font */
	public static final ConfigKey<String> NORMAL_FONT = ConfigKey.create("normalFont", String.class, "Verdana");
	
	/** Bold font */
	public static final ConfigKey<String> BOLD_FONT = ConfigKey.create("boldFont", String.class, "Verdana Bold");
	
	/** Italic font */
	public static final ConfigKey<String> ITALIC_FONT = ConfigKey.create("italicFont", String.class, "Verdana Italic");
	
	/** Bold italic font */
	public static final ConfigKey<String> BOLDITALIC_FONT = ConfigKey.create("bolditalicFont", String.class, "Verdana Bold Italic");
	
	
	private ThemeKeys()
		{
		}
	}
