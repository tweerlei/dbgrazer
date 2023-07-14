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
package de.tweerlei.dbgrazer.visualization.service;

import java.util.Set;

import de.tweerlei.dbgrazer.visualization.model.SVGDefinition;
import de.tweerlei.dbgrazer.visualization.model.SVGShape;

/**
 * Service that creates graph representations
 * 
 * @author Robert Wruck
 */
public interface SVGBuilder
	{
	/**
	 * Build an SVG
	 * @param name Graph name (used as image map ID)
	 * @param title Graph title
	 * @param subtitle Graph subtitle
	 * @param attrs Node attributes
	 * @param shapes Shapes
	 * @param nodeLink Query to link nodes to (may be null)
	 * @return Graph description
	 */
	public SVGDefinition buildSVG(String name, String title, String subtitle, String attrs, Set<SVGShape> shapes, String nodeLink);
	}
