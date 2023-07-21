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
package de.tweerlei.dbgrazer.plugins.svg;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.common5.collections.builders.MapBuilder;
import de.tweerlei.dbgrazer.plugins.svg.types.SVGCircle;
import de.tweerlei.dbgrazer.plugins.svg.types.SVGElement;
import de.tweerlei.dbgrazer.plugins.svg.types.SVGEllipse;
import de.tweerlei.dbgrazer.plugins.svg.types.SVGLine;
import de.tweerlei.dbgrazer.plugins.svg.types.SVGRectangle;
import de.tweerlei.dbgrazer.visualization.model.SVGDefinition;
import de.tweerlei.dbgrazer.visualization.model.SVGShape;
import de.tweerlei.dbgrazer.visualization.service.SVGBuilder;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service("svgBuilder")
public class SVGBuilderImpl implements SVGBuilder
	{
	@Override
	public SVGDefinition buildSVG(String name, String title, String subtitle, String attrs, Set<SVGShape> shapes, String nodeLink)
		{
		return (new SVGDefinition(name, buildSVG(attrs, shapes, nodeLink)));
		}
	
	private String buildSVG(String attrs, Set<SVGShape> shapes, String nodeLink)
		{
		final StringWriter sw = new StringWriter();
		final XMLWriter w = new XMLWriter(sw);
		
		try	{
			writeSVG(w, attrs, createSVGElements(shapes), nodeLink);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		
		return (sw.toString());
		}
	
	private void writeSVG(XMLWriter w, String attrs, List<SVGElement> elements, String nodeLink) throws IOException
		{
		final Rectangle2D boundingBox = calculateBoundingBox(elements);
		
		// Add uniform margin
		final Rectangle2D viewBox;
		if (boundingBox == null)
			viewBox = new Rectangle2D.Double();
		else
			viewBox = new Rectangle2D.Double(0, 0, boundingBox.getWidth() + boundingBox.getX(), boundingBox.getHeight() + boundingBox.getY());
		
		w.writeXMLDeclaration("UTF-8");
		
		w.startElement("svg", new MapBuilder<>()
				.put("version", "1.1")
				.put("width", String.valueOf(viewBox.getWidth()))
				.put("height", String.valueOf(viewBox.getHeight()))
				.put("viewBox", viewBox.getX() + " " + viewBox.getY() + " " + viewBox.getWidth() + " " + viewBox.getHeight())
				.build());
		
		for (SVGElement e : elements)
			e.writeTo(w);
		
		w.endElement("svg");
		}
	
	private Rectangle2D calculateBoundingBox(List<SVGElement> elements)
		{
		Rectangle2D ret = null;
		
		for (SVGElement e : elements)
			{
			if (ret == null)
				ret = e.getBoundingBox();
			else
				ret = ret.createUnion(e.getBoundingBox());
			}
		
		return (ret);
		}
	
	private List<SVGElement> createSVGElements(Set<SVGShape> shapes)
		{
		final List<SVGElement> el = new ArrayList<SVGElement>();
		
		for (SVGShape s : shapes)
			el.add(createSVGElement(s));
		
		return (el);
		}
	
	private SVGElement createSVGElement(SVGShape s)
		{
		if ("rect".equals(s.getName()))
			return (new SVGRectangle(s.getCoordinate1(), s.getCoordinate2(), s.getCoordinate3(), s.getCoordinate4()));
		else if ("circle".equals(s.getName()))
			return (new SVGCircle(s.getCoordinate1(), s.getCoordinate2(), s.getCoordinate3()));
		else if ("ellipse".equals(s.getName()))
			return (new SVGEllipse(s.getCoordinate1(), s.getCoordinate2(), s.getCoordinate3(), s.getCoordinate4()));
		else if ("line".equals(s.getName()))
			return (new SVGLine(s.getCoordinate1(), s.getCoordinate2(), s.getCoordinate3(), s.getCoordinate4()));
		else
			return (new SVGRectangle(s.getCoordinate1(), s.getCoordinate2(), s.getCoordinate3(), s.getCoordinate4()));
		}
	}
