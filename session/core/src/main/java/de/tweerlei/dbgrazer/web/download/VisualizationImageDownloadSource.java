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
package de.tweerlei.dbgrazer.web.download;

import java.io.IOException;
import java.io.OutputStream;

import de.tweerlei.dbgrazer.web.backend.Visualizer;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.spring.web.view.AbstractDownloadSource;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * DownloadSource for graph images
 * 
 * @author Robert Wruck
 */
public class VisualizationImageDownloadSource extends AbstractDownloadSource
	{
	private final Visualizer visualizer;
	private final Visualization obj;
	
	/**
	 * Constructor
	 * @param visualizer ResultVisualizer
	 * @param obj Visualization container
	 * @param name Graph name
	 * @param attachment Attachment flag
	 */
	public VisualizationImageDownloadSource(Visualizer visualizer, Visualization obj, String name, boolean attachment)
		{
		this.visualizer = visualizer;
		this.obj = obj;
		
		this.setAttachment(attachment);
		this.setExpireTime(DownloadSource.ALWAYS);
		this.setFileName(name + visualizer.getImageFileExtension());
		}
	
	@Override
	public String getContentType()
		{
		return (visualizer.getImageContentType());
		}
	
	@Override
	public void write(OutputStream stream) throws IOException
		{
		visualizer.writeImageTo(obj, stream);
		}
	}
