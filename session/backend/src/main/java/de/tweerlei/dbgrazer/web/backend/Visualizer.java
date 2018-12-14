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
package de.tweerlei.dbgrazer.web.backend;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.common.util.Named;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;

/**
 * Visualize a Result
 * 
 * @author Robert Wruck
 */
public interface Visualizer extends Named
	{
	/**
	 * Check whether this Visualizer supports a given QueryType
	 * @param type QueryType name
	 * @return true if supported
	 */
	public boolean supports(String type);
	
	/**
	 * Get the option names recognized by this Visualizer
	 * @return Option names
	 */
	public Set<String> getOptionNames();
	
	/**
	 * Get the values recognized for a given option name
	 * @param option Option name
	 * @param code Option code
	 * @return Option values
	 */
	public Set<String> getOptionValues(String option, int code);
	
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
	 * @return Content type
	 */
	public String getImageContentType();
	
	/**
	 * Get the filename extension of images created by the writeImageTo method
	 * @return Extension
	 */
	public String getImageFileExtension();
	
	/**
	 * Write the visualized image to an OutputStream
	 * @param obj Opaque container returned from build()
	 * @param stream OutputStream
	 * @throws IOException on error
	 */
	public void writeImageTo(Visualization obj, OutputStream stream) throws IOException;
	
	/**
	 * Check whether this ResultVisualizer supports the writeSourceTo() method
	 * @return true if source text is available
	 */
	public boolean supportsSourceText();
	
	/**
	 * Get the MIME type of images created by the writeSourceTextTo method
	 * @return Content type
	 */
	public String getSourceTextContentType();
	
	/**
	 * Get the filename extension of images created by the writeSourceTextTo method
	 * @return Extension
	 */
	public String getSourceTextFileExtension();
	
	/**
	 * Write the source text to an OutputStream
	 * @param obj Opaque container returned from build()
	 * @param stream OutputStream
	 * @throws IOException on error
	 */
	public void writeSourceTextTo(Visualization obj, OutputStream stream) throws IOException;
	
	/**
	 * Check whether this ResultVisualizer supports the getSourceSVG() method
	 * @return true if source text is available
	 */
	public boolean supportsSourceSVG();
	
	/**
	 * Render the image as SVG
	 * @param obj Opaque container returned from build()
	 * @return SVG source
	 */
	public String getSourceSVG(Visualization obj);
	
	/**
	 * Get the HTML map source code
	 * @param obj Opaque container returned from build()
	 * @return HTML code
	 */
	public String getHtmlMap(Visualization obj);
	}
