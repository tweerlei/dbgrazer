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

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.color.HSLColor;
import de.tweerlei.common.color.RGBColor;
import de.tweerlei.dbgrazer.visualization.service.ChartStyle;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.session.RequestSettings;

/**
 * The default chart style
 * 
 * @author Robert Wruck
 */
@Service
public class DefaultChartStyle implements ChartStyle
	{
	private static final Stroke DEFAULT_STROKE = new BasicStroke(2.0f);
	
	private final FrontendHelperService frontendHelper;
	private final RequestSettings themeSettings;
	
	/**
	 * Constructor
	 * @param frontendHelper FrontendHelperService
	 * @param themeSettings ThemeSettings
	 */
	@Autowired
	public DefaultChartStyle(FrontendHelperService frontendHelper, RequestSettings themeSettings)
		{
		this.frontendHelper = frontendHelper;
		this.themeSettings = themeSettings;
		}
	
	@Override
	public Locale getLocale()
		{
		return (themeSettings.getLocale());
		}
	
	@Override
	public TimeZone getTimeZone()
		{
		return (themeSettings.getTimeZone());
		}
	
	@Override
	public String getFontFamily()
		{
		return (themeSettings.getConfig().get(ThemeKeys.NORMAL_FONT));
		}
	
	@Override
	public Paint getBackgroundPaint()
		{
		return (themeSettings.getConfig().get(ThemeKeys.BACKGROUND_COLOR));
		}
	
	@Override
	public Paint getForegroundPaint()
		{
		return (themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR));
		}
	
	@Override
	public Paint getGridPaint()
		{
		return (themeSettings.getConfig().get(ThemeKeys.GRID_COLOR));
		}
	
	@Override
	public Paint getRowPaint(int i)
		{
		final HSLColor c = new HSLColor(new RGBColor(themeSettings.getConfig().get(ThemeKeys.BASE_COLOR)));
		final double h = c.getHue();
		final double l = c.getLightness();
		c.setHue((h + i * 210.0 / 360.0) % 1.0);
		c.setLightness(l + ((i + 1) % 3 - 1) * 0.15);
		
		return (c.toRGBColor().toColor());
		}
	
	@Override
	public Stroke getRowStroke(int i)
		{
		return (DEFAULT_STROKE);
		}
	
	@Override
	public Paint getSecondaryRowPaint(int i)
		{
		return (themeSettings.getConfig().get(ThemeKeys.FOREGROUND_COLOR));
		}
	
	@Override
	public Stroke getSecondaryRowStroke(int i)
		{
		return (DEFAULT_STROKE);
		}
	
	@Override
	public Paint getPositivePaint()
		{
		return (themeSettings.getConfig().get(ThemeKeys.POSITIVE_COLOR));
		}
	
	@Override
	public Paint getNegativePaint()
		{
		return (themeSettings.getConfig().get(ThemeKeys.NEGATIVE_COLOR));
		}
	
	@Override
	public String getURL(String baseURL, String item)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append(baseURL);
		sb.append(frontendHelper.paramEncode(item, true));
		return (sb.toString());
		}
	}
