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
import java.io.Writer;

import de.tweerlei.common.io.ByteOrderMarkWriter;

/**
 * Download a String
 * 
 * @author Robert Wruck
 */
public class StringDownloadSource extends AbstractDownloadSource
	{
	private final String data;
	private final String contentType;
	private final String charset;
	private final boolean bom;
	
	/**
	 * Constructor
	 * @param data Data string
	 * @param contentType ContentType (without charset declaration)
	 * @param charset Charset name
	 * @param bom Include byte order mark
	 */
	public StringDownloadSource(String data, String contentType, String charset, boolean bom)
		{
		this.data = data;
		this.contentType = contentType;
		this.charset = charset;
		this.bom = bom;
		
		this.setExpireTime(DownloadSource.ALWAYS);
		}
	
	public String getContentType()
		{
		return (contentType + "; charset=" + charset);
		}
	
	public void write(OutputStream stream) throws IOException
		{
		Writer osw = new OutputStreamWriter(stream, charset);
		if (bom)
			osw = new ByteOrderMarkWriter(osw);
		try	{
			osw.write(data);
			}
		finally
			{
			osw.flush();
			}
		}
	}
