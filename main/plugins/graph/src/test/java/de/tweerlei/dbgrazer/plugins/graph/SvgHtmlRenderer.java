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

import java.io.File;

import org.easymock.EasyMock;

import de.tweerlei.common.io.FileUtils;
import de.tweerlei.dbgrazer.plugins.graph.types.DotLRGraphType;
import de.tweerlei.dbgrazer.visualization.model.GraphDefinition;
import de.tweerlei.dbgrazer.visualization.service.GraphService;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Render a DOT graph as SVG embedded in HTML
 * 
 * @author Robert Wruck
 */
public class SvgHtmlRenderer
	{
	/**
	 * Entry point
	 * @param args Arguments
	 * @throws Exception on error
	 */
	public static void main(String[] args) throws Exception
		{
		final ConfigAccessor cfg = EasyMock.createMock(ConfigAccessor.class);
		EasyMock.expect(cfg.get(ConfigKeys.DOT_COMMAND)).andReturn("dot").anyTimes();
		EasyMock.expect(cfg.get(ConfigKeys.DOT_DPI)).andReturn(48).anyTimes();
		EasyMock.replay(cfg);
		
		final GraphService gs = new GraphServiceImpl(cfg);
		
		System.out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>");
		
		for (String file : args)
			{
			final String src = FileUtils.readFile(new File(file), "UTF-8", 65536);
			final GraphDefinition def = new GraphDefinition(file, new DotLRGraphType(), src);
			
			System.out.println("<h1>" + file + "</h1>");
			System.out.println(gs.createSVG(def));
			}
		
		System.out.println("</body></html>");
		}
	}
