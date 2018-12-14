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
package de.tweerlei.dbgrazer.web.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Methods for dealing with visualizations
 * 
 * @author Robert Wruck
 */
public interface VisualizationService
	{
	/**
	 * Get the option names recognized by this Visualizer
	 * @param type QueryType name
	 * @return Option names
	 */
	public Set<String> getOptionNames(String type);
	
	/**
	 * Get the values recognized for a given option name
	 * @param type QueryType name
	 * @param option Option name
	 * @param code Option code
	 * @return Option values
	 */
	public Set<String> getOptionValues(String type, String option, int code);
	
	/**
	 * Build a visualization object
	 * @param r Result
	 * @param f DataFormatter
	 * @param name Name
	 * @param subtitle Subtitle
	 * @param link Link target for individual elements (may be null)
	 * @param options Additional options
	 * @return Opaque container
	 */
	public Visualization build(Result r, DataFormatter f, String name, String subtitle, String link, Map<String, String> options);
	
	/**
	 * Get the MIME type of images created by the writeImageTo method
	 * @param v Visualization
	 * @return Content type
	 */
	public String getImageContentType(Visualization v);
	
	/**
	 * Get the filename extension of images created by the writeImageTo method
	 * @param v Visualization
	 * @return Extension
	 */
	public String getImageFileExtension(Visualization v);
	
	/**
	 * Write the visualized image to an OutputStream
	 * @param v Visualization
	 * @param stream OutputStream
	 * @throws IOException on error
	 */
	public void writeImageTo(Visualization v, OutputStream stream) throws IOException;
	
	/**
	 * Check whether this ResultVisualizer supports the writeSourceTo() method
	 * @param v Visualization
	 * @return true if source text is available
	 */
	public boolean supportsSourceText(Visualization v);
	
	/**
	 * Get the MIME type of images created by the writeSourceTextTo method
	 * @param v Visualization
	 * @return Content type
	 */
	public String getSourceTextContentType(Visualization v);
	
	/**
	 * Get the filename extension of images created by the writeSourceTextTo method
	 * @param v Visualization
	 * @return Extension
	 */
	public String getSourceTextFileExtension(Visualization v);
	
	/**
	 * Write the source text to an OutputStream
	 * @param v Visualization
	 * @param stream OutputStream
	 * @throws IOException on error
	 */
	public void writeSourceTextTo(Visualization v, OutputStream stream) throws IOException;
	
	/**
	 * Check whether this ResultVisualizer supports the getSourceSVG() method
	 * @param v Visualization
	 * @return true if source text is available
	 */
	public boolean supportsSourceSVG(Visualization v);
	
	/**
	 * Render the image as SVG
	 * @param v Visualization
	 * @return SVG source
	 */
	public String getSourceSVG(Visualization v);
	
	/**
	 * Get the HTML map source code
	 * @param v Visualization
	 * @return HTML code
	 */
	public String getHtmlMap(Visualization v);
	
	/**
	 * Create a DownloadSource for a GraphDefinition
	 * @param v Visualization
	 * @param name Image name
	 * @return DownloadSource
	 */
	public DownloadSource getVisualizationDownloadSource(Visualization v, String name);
	
	/**
	 * Create a DownloadSource for a GraphDefinition
	 * @param v Visualization
	 * @param name Image name
	 * @return DownloadSource
	 */
	public DownloadSource getImageDownloadSource(Visualization v, String name);
	
	/**
	 * Create a DownloadSource for a GraphDefinition
	 * @param v Visualization
	 * @param name Image name
	 * @return DownloadSource
	 */
	public DownloadSource getSourceTextDownloadSource(Visualization v, String name);
	}
