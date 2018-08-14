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

import java.io.IOException;
import java.io.OutputStream;

import de.tweerlei.dbgrazer.visualization.model.ChartDefinition;

/**
 * Service that creates chart representations
 * 
 * @author Robert Wruck
 */
public interface ChartService
	{
	/**
	 * Create a HTML image map for an image produced by the createImage method
	 * @param def ChartDefinition
	 * @return HTML code
	 * @throws IOException on error
	 */
	public String createHTMLMap(ChartDefinition def) throws IOException;
	
	/**
	 * Create an image of a graph
	 * @param def ChartDefinition
	 * @param dest Destination output stream
	 * @throws IOException on error
	 */
	public void createImage(ChartDefinition def, OutputStream dest) throws IOException;
	
	/**
	 * Get the MIME type of images created by the createImage method
	 * @return Content type
	 */
	public String getImageContentType();
	
	/**
	 * Get the filename extension of images created by the createImage method
	 * @return Extension
	 */
	public String getFileExtension();
	}
