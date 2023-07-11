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
package de.tweerlei.spring.web.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import de.tweerlei.common.textdata.JSONWriter;
import de.tweerlei.spring.web.view.AbstractDownloadSource;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * Download an Object as JSON
 * 
 * @author Robert Wruck
 */
public class JsonDownloadSource extends AbstractDownloadSource
	{
	private static final String CHARSET = "UTF-8";
	private static final String CONTENT_TYPE = "application/json; charset=" + CHARSET;
	
	private final Object obj;
	
	/**
	 * Constructor
	 * @param obj Object
	 */
	public JsonDownloadSource(Object obj)
		{
		this.obj = obj;
		
		this.setExpireTime(DownloadSource.ALWAYS);
		}
	
	public String getContentType()
		{
		return (CONTENT_TYPE);
		}
	
	public void write(OutputStream stream) throws IOException
		{
		final OutputStreamWriter osw = new OutputStreamWriter(stream, CHARSET);
		try	{
			final JSONWriter jw = new JSONWriter(osw);
			
			jw.write(obj);
			}
		finally
			{
			osw.flush();
			}
		}
	}
