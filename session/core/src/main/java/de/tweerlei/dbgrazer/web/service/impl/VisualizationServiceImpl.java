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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedSet;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.web.backend.Visualizer;
import de.tweerlei.dbgrazer.web.download.VisualizationImageDownloadSource;
import de.tweerlei.dbgrazer.web.download.VisualizationSourceDownloadSource;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.VisualizationService;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Methods for dealing with visualizations
 * 
 * @author Robert Wruck
 */
@Service
public class VisualizationServiceImpl implements VisualizationService
	{
	private final Logger logger;
	private final ConfigAccessor configService;
	private final Set<Visualizer> formats;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param formats Known Visualizers
	 */
	@Autowired(required = false)
	public VisualizationServiceImpl(ConfigAccessor configService, List<Visualizer> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.configService = configService;
		this.formats = Collections.unmodifiableSet(new NamedSet<Visualizer>(formats));
		
		this.logger.log(Level.INFO, "Visualizer: " + this.formats);
		}
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 */
	public VisualizationServiceImpl(ConfigAccessor configService)
		{
		this(configService, Collections.<Visualizer>emptyList());
		}
	
	@Override
	public Set<String> getOptionNames(String type)
		{
		return (findVisualizer(type).getOptionNames());
		}
	
	@Override
	public Set<String> getOptionValues(String type, String option, int code)
		{
		return (findVisualizer(type).getOptionValues(option, code));
		}
	
	@Override
	public Visualization build(Result r, DataFormatter f, String name, String subtitle, String link, Map<String, String> options)
		{
		return (findVisualizer(r.getQuery().getType().getName()).build(r, f, name, subtitle, link, options));
		}
	
	@Override
	public String getImageContentType(Visualization v)
		{
		return (findVisualizer(v).getImageContentType());
		}
	
	@Override
	public String getImageFileExtension(Visualization v)
		{
		return (findVisualizer(v).getImageFileExtension());
		}
	
	@Override
	public void writeImageTo(Visualization v, OutputStream stream) throws IOException
		{
		findVisualizer(v).writeImageTo(v, stream);
		}

	@Override
	public boolean supportsSourceText(Visualization v)
		{
		return (findVisualizer(v).supportsSourceText());
		}
	
	@Override
	public String getSourceTextContentType(Visualization v)
		{
		return (findVisualizer(v).getSourceTextContentType());
		}
	
	@Override
	public String getSourceTextFileExtension(Visualization v)
		{
		return (findVisualizer(v).getSourceTextFileExtension());
		}
	
	@Override
	public void writeSourceTextTo(Visualization v, OutputStream stream) throws IOException
		{
		findVisualizer(v).writeSourceTextTo(v, stream);
		}
	
	@Override
	public boolean supportsSourceSVG(Visualization v)
		{
		return (configService.get(ConfigKeys.INLINE_SVG) && findVisualizer(v).supportsSourceSVG());
		}
	
	@Override
	public String getSourceSVG(Visualization v)
		{
		return (findVisualizer(v).getSourceSVG(v));
		}
	
	@Override
	public String getHtmlMap(Visualization v)
		{
		return (findVisualizer(v).getHtmlMap(v));
		}
	
	@Override
	public DownloadSource getVisualizationDownloadSource(Visualization v, String name)
		{
		return (new VisualizationImageDownloadSource(findVisualizer(v), v, name, false));
		}
	
	@Override
	public DownloadSource getImageDownloadSource(Visualization v, String name)
		{
		return (new VisualizationImageDownloadSource(findVisualizer(v), v, name, true));
		}
	
	@Override
	public DownloadSource getSourceTextDownloadSource(Visualization v, String name)
		{
		return (new VisualizationSourceDownloadSource(findVisualizer(v), v, name));
		}
	
	private Visualizer findVisualizer(Visualization v)
		{
		return (findVisualizer(v.getQueryType()));
		}
	
	private Visualizer findVisualizer(String type)
		{
		for (Visualizer f : formats)
			{
			if (f.supports(type))
				return (f);
			}
		return (null);
		}
	}
