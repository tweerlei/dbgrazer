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
package de.tweerlei.dbgrazer.web.backend.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.web.backend.Visualizer;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;

/**
 * Dummy impl.
 * 
 * @author Robert Wruck
 */
@Service
public class DummyResultVisualizer extends NamedBase implements Visualizer
	{
	private static final String FORMAT_NAME = "PNG";
	private static final String MIME_TYPE = "image/png";
	private static final String EXTENSION = ".png";
	
	/**
	 * Constructor
	 */
	public DummyResultVisualizer()
		{
		super("Dummy");
		}
	
	@Override
	public boolean supports(String type)
		{
		return (true);
		}
	
	@Override
	public Set<String> getOptionNames()
		{
		return (Collections.emptySet());
		}
	
	@Override
	public Set<String> getOptionValues(String option, int code)
		{
		return (Collections.emptySet());
		}
	
	@Override
	public Visualization build(Result r, DataFormatter f, String name, String subtitle, String link, Map<String, String> options)
		{
		return (new Visualization(r.getQuery().getType().getName(), name, null));
		}
	
	@Override
	public String getImageContentType()
		{
		return (MIME_TYPE);
		}
	
	@Override
	public String getImageFileExtension()
		{
		return (EXTENSION);
		}
	
	@Override
	public void writeImageTo(Visualization obj, OutputStream stream) throws IOException
		{
		final BufferedImage img = new BufferedImage(320, 200, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = img.createGraphics();
		try	{
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, 320, 200);
			
			g.drawString("No visualization plugin installed.", 10, 10);
			}
		finally
			{
			g.dispose();
			}
		
		ImageIO.write(img, FORMAT_NAME, stream);
		}
	
	@Override
	public boolean supportsSourceText()
		{
		return (false);
		}
	
	@Override
	public String getSourceTextContentType()
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public String getSourceTextFileExtension()
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public void writeSourceTextTo(Visualization obj, OutputStream stream) throws IOException
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public boolean supportsSourceSVG()
		{
		return (false);
		}
	
	@Override
	public String getSourceSVG(Visualization obj)
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public String getHtmlMap(Visualization obj)
		{
		return ("");
		}
	}
