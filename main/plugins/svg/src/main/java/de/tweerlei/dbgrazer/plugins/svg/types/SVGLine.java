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
 * SVG line
 * 
 * @author Robert Wruck
 */
public class SVGLine implements SVGElement
	{
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	
	/**
	 * Constructor
	 * @param x1 Starting X
	 * @param y1 Starting Y
	 * @param x2 Ending X
	 * @param y2 Ending Y
	 */
	public SVGLine(double x1, double y1, double x2, double y2)
		{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		}
	
	@Override
	public Rectangle2D getBoundingBox()
		{
		return (new Rectangle2D.Double(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)));
		}
	
	@Override
	public void writeTo(XMLWriter w) throws IOException
		{
		w.writeElement("line", new MapBuilder<>()
				.put("x1", String.valueOf(x1))
				.put("y1", String.valueOf(y1))
				.put("x2", String.valueOf(x2))
				.put("y2", String.valueOf(y2))
				.put("stroke", "#000000")
				.build());
		}
	}
