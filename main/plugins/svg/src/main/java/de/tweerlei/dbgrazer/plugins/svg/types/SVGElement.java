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

/**
 * SVG element that renders itself as XML
 * 
 * @author Robert Wruck
 */
public interface SVGElement
	{
	/**
	 * Get this element's bounding box
	 * @return Bounding box
	 */
	public Rectangle2D getBoundingBox();
	
	/**
	 * Render this element to an XMLWriter
	 * @param w XMLWriter
	 * @throws IOException on error
	 */
	public void writeTo(XMLWriter w) throws IOException;
	}
