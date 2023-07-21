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
package de.tweerlei.dbgrazer.plugins.svg.types;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.common5.collections.builders.MapBuilder;

/**
 * SVG rectangle
 * 
 * @author Robert Wruck
 */
public class SVGRectangle implements SVGElement
	{
	private final double x;
	private final double y;
	private final double width;
	private final double height;
	
	/**
	 * Constructor
	 * @param x X position
	 * @param y Y position
	 * @param width Width
	 * @param height Height
	 */
	public SVGRectangle(double x, double y, double width, double height)
		{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		}
	
	@Override
	public Rectangle2D getBoundingBox()
		{
		return (new Rectangle2D.Double(x, y, width, height));
		}
	
	@Override
	public void writeTo(XMLWriter w) throws IOException
		{
		w.writeElement("rect", new MapBuilder<>()
				.put("x", String.valueOf(x))
				.put("y", String.valueOf(y))
				.put("width", String.valueOf(width))
				.put("height", String.valueOf(height))
				.put("stroke", "#000000")
				.put("fill", "transparent")
				.build());
		}
	}
