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

import java.awt.Paint;
import java.awt.Stroke;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Defines appearance of charts
 * 
 * @author Robert Wruck
 */
public interface ChartStyle
	{
	/**
	 * Get the locale for number and date formatting
	 * @return Locale
	 */
	public Locale getLocale();
	
	/**
	 * Get the time zone for date formatting
	 * @return TimeZone
	 */
	public TimeZone getTimeZone();
	
	/**
	 * Get the font family name
	 * @return Font family name
	 */
	public String getFontFamily();
	
	/**
	 * Get the Paint to use for the background
	 * @return Paint
	 */
	public Paint getBackgroundPaint();
	
	/**
	 * Get the Paint to use for the foreground
	 * @return Paint
	 */
	public Paint getForegroundPaint();
	
	/**
	 * Get the Paint to use for the grid lines
	 * @return Paint
	 */
	public Paint getGridPaint();
	
	/**
	 * Get the Paint to use for a data row
	 * @param i Row index
	 * @return Paint
	 */
	public Paint getRowPaint(int i);
	
	/**
	 * Get the Stroke to use for a data row
	 * @param i Row index
	 * @return Stroke
	 */
	public Stroke getRowStroke(int i);
	
	/**
	 * Get the Paint to use for a data row
	 * @param i Row index
	 * @return Paint
	 */
	public Paint getSecondaryRowPaint(int i);
	
	/**
	 * Get the Stroke to use for a data row
	 * @param i Row index
	 * @return Stroke
	 */
	public Stroke getSecondaryRowStroke(int i);
	
	/**
	 * Get the Paint to use for a positive difference
	 * @return Paint
	 */
	public Paint getPositivePaint();
	
	/**
	 * Get the Paint to use for a negative difference
	 * @return Paint
	 */
	public Paint getNegativePaint();
	
	/**
	 * Generate an item URL from a base URL and the item label
	 * @param baseURL Base URL
	 * @param item Item label
	 * @return URL
	 */
	public String getURL(String baseURL, String item);
	}
