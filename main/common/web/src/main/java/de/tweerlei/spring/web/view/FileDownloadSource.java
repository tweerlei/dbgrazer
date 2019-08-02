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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.tweerlei.common.io.StreamUtils;

/**
 * DownloadSource for physical files
 * 
 * @author Robert Wruck
 */
public class FileDownloadSource extends AbstractDownloadSource
	{
	private final File file;
	private final String contentType;
	
	/**
	 * Constructor
	 * @param file Path of local file
	 * @param ct Content-Type
	 */
	public FileDownloadSource(File file, String ct)
		{
		this.file = file;
		this.contentType = ct;
		}
	
	public String getContentType()
		{
		return (contentType);
		}
	
	@Override
	public boolean canRead()
		{
		return ((file != null) && file.isFile());
		}
	
	@Override
	public long getLastModified()
		{
		return (file.lastModified());
		}
	
	@Override
	public int getContentLength()
		{
		return ((int) file.length());
		}
	
	public void write(OutputStream stream) throws IOException
		{
		final FileInputStream fis = new FileInputStream(file);
		try	{
			StreamUtils.copy(fis, stream);
			}
		finally
			{
			StreamUtils.closeQuietly(fis);
			}
		}
	}
