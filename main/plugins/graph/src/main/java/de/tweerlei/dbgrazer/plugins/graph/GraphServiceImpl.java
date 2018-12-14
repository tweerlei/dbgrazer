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
package de.tweerlei.dbgrazer.plugins.graph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.ProcessUtils;
import de.tweerlei.common5.util.FindReplace;
import de.tweerlei.common5.util.SimpleFindReplace;
import de.tweerlei.dbgrazer.visualization.model.GraphDefinition;
import de.tweerlei.dbgrazer.visualization.service.GraphService;
import de.tweerlei.dbgrazer.visualization.service.GraphType;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * DOT based impl.
 * 
 * @author Robert Wruck
 */
@Service("graphService")
public class GraphServiceImpl implements GraphService
	{
	private static final String DOT_CHARSET = "UTF-8";
	private static final String DOT_MAP_TYPE = "cmapx";
	private static final String DOT_IMAGE_TYPE = "png";
	private static final String MIME_TYPE = "image/png";
	private static final String EXTENSION = ".png";
	private static final SvgRepairer REPAIR_SVG = new SvgRepairer();
	
	private final ConfigAccessor configService;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 */
	@Autowired
	public GraphServiceImpl(ConfigAccessor configService)
		{
		this.configService = configService;
		}
	
	@Override
	public String createHTMLMap(GraphDefinition def) throws IOException
		{
		final String dotPath = getCommand(def.getType());
		if (dotPath == null)
			return (null);
		
		final Integer dotDpi = configService.get(ConfigKeys.DOT_DPI);
		final String[] cmd;
		if (dotDpi != null)
			cmd = new String[] { dotPath, "-T" + DOT_MAP_TYPE, "-Gstart=1", "-Gdpi=" + dotDpi };
		else
			cmd = new String[] { dotPath, "-T" + DOT_MAP_TYPE, "-Gstart=1" };
		
		final ByteArrayInputStream is = new ByteArrayInputStream(def.getGraph().toString().getBytes(DOT_CHARSET));
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		final int rc = ProcessUtils.exec(cmd, null, null, is, os, null, 0);
		if (rc != 0)
			throw new IOException(dotPath + " returned " + rc);
		
		return (os.toString(DOT_CHARSET));
		}
	
	@Override
	public void createImage(GraphDefinition def, OutputStream dest) throws IOException
		{
		final String dotPath = getCommand(def.getType());
		if (dotPath == null)
			return;
		
		final Integer dotDpi = configService.get(ConfigKeys.DOT_DPI);
		final String[] cmd;
		if (dotDpi != null)
			cmd = new String[] { dotPath, "-T" + DOT_IMAGE_TYPE, "-Gstart=1", "-Gdpi=" + dotDpi };
		else
			cmd = new String[] { dotPath, "-T" + DOT_IMAGE_TYPE, "-Gstart=1" };
		
		final ByteArrayInputStream is = new ByteArrayInputStream(def.getGraph().toString().getBytes(DOT_CHARSET));
		
		final int rc = ProcessUtils.exec(cmd, null, null, is, dest, null, 0);
		if (rc != 0)
			throw new IOException(dotPath + " returned " + rc);
		}
	
	@Override
	public String createSVG(GraphDefinition def) throws IOException
		{
		final String dotPath = getCommand(def.getType());
		if (dotPath == null)
			return ("");
		
		final Integer dotDpi = configService.get(ConfigKeys.DOT_DPI);
		
		final String svg = renderSVG(def, dotPath, dotDpi);
		
		final String ret = REPAIR_SVG.replaceAll(svg);
		
		return (ret);
		}
	
	private String renderSVG(GraphDefinition def, String dotPath, Integer dotDpi) throws IOException
		{
		final String[] cmd;
		if (dotDpi != null)
			cmd = new String[] { dotPath, "-Tsvg", "-Gstart=1", "-Gdpi=" + dotDpi };
		else
			cmd = new String[] { dotPath, "-Tsvg", "-Gstart=1" };
		
		final ByteArrayInputStream is = new ByteArrayInputStream(def.getGraph().toString().getBytes(DOT_CHARSET));
		final ByteArrayOutputStream os = new ByteArrayOutputStream(65536);
		
		final int rc = ProcessUtils.exec(cmd, null, null, is, os, null, 0);
		if (rc != 0)
			throw new IOException(dotPath + " returned " + rc);
		
		return (os.toString(DOT_CHARSET));
		}
	
	@Override
	public String getGraphSource(GraphDefinition def)
		{
		return (def.getGraph().toString());
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
	
	private String getCommand(GraphType type)
		{
		final String dotPath = configService.get(ConfigKeys.DOT_COMMAND);
		if (dotPath == null)
			return (null);
		
		// Hack: We assume that all GraphViz commands are found where DOT is and that their base names are exactly as specified in GraphType
		final int index = dotPath.toLowerCase().lastIndexOf("dot");
		if (index < 0)
			return (dotPath);
		
		if (!(type instanceof GraphVizGraphType))
			return (dotPath);
		
		return (dotPath.substring(0, index) + ((GraphVizGraphType) type).getCommand() + dotPath.substring(index + "dot".length()));
		}
	}
