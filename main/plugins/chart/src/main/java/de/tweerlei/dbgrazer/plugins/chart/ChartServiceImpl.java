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
package de.tweerlei.dbgrazer.plugins.chart;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.NullStream;
import de.tweerlei.dbgrazer.visualization.model.ChartDefinition;
import de.tweerlei.dbgrazer.visualization.service.ChartService;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * JFreeChart based impl.
 * 
 * @author Robert Wruck
 */
@Service("chartService")
public class ChartServiceImpl implements ChartService
	{
	private static final String MIME_TYPE = "image/png";
	private static final String EXTENSION = ".png";
	
	private final ConfigAccessor configService;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 */
	@Autowired
	public ChartServiceImpl(ConfigAccessor configService)
		{
		this.configService = configService;
		}
	
	@Override
	public String createHTMLMap(ChartDefinition def) throws IOException
		{
		final ChartRenderingInfo info = new ChartRenderingInfo();
		
		ChartUtilities.writeChartAsPNG(new NullStream(), (JFreeChart) def.getChart(), configService.get(ConfigKeys.CHART_WIDTH), configService.get(ConfigKeys.CHART_HEIGHT), info);
		
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		
		ChartUtilities.writeImageMap(pw, def.getName(), info, false);
		
		pw.flush();
		
		return (sw.toString());
		}
	
	@Override
	public void createImage(ChartDefinition def, OutputStream dest) throws IOException
		{
		ChartUtilities.writeChartAsPNG(dest, (JFreeChart) def.getChart(), configService.get(ConfigKeys.CHART_WIDTH), configService.get(ConfigKeys.CHART_HEIGHT));
		}
	
	@Override
	public String getImageContentType()
		{
		return (MIME_TYPE);
		}
	
	@Override
	public String getFileExtension()
		{
		return (EXTENSION);
		}
	}
