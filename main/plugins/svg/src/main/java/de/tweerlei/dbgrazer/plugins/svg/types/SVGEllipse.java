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
 * SVG ellipse
 * 
 * @author Robert Wruck
 */
public class SVGEllipse implements SVGElement
	{
	private final double cx;
	private final double cy;
	private final double rx;
	private final double ry;
	
	/**
	 * Constructor
	 * @param cx Center X
	 * @param cy Center Y
	 * @param rx Radius X
	 * @param ry Radius Y
	 */
	public SVGEllipse(double cx, double cy, double rx, double ry)
		{
		this.cx = cx;
		this.cy = cy;
		this.rx = rx;
		this.ry = ry;
		}
	
	@Override
	public Rectangle2D getBoundingBox()
		{
		return (new Rectangle2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry));
		}
	
	@Override
	public void writeTo(XMLWriter w) throws IOException
		{
		w.writeElement("ellipse", new MapBuilder<>()
				.put("cx", String.valueOf(cx))
				.put("cy", String.valueOf(cy))
				.put("rx", String.valueOf(rx))
				.put("ry", String.valueOf(ry))
				.put("stroke", "#000000")
				.put("fill", "transparent")
				.build());
		}
	}
