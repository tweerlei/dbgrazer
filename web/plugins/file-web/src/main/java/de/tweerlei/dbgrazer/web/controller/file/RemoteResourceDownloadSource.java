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
package de.tweerlei.dbgrazer.web.controller.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import de.tweerlei.common.io.CopyStreamReader;
import de.tweerlei.dbgrazer.extension.file.RemoteResourceService;
import de.tweerlei.spring.web.view.AbstractDownloadSource;
import de.tweerlei.spring.web.view.DownloadSource;

/**
 * DownloadSource that sends the contents of an InputStream
 * 
 * @author Robert Wruck
 */
public class RemoteResourceDownloadSource extends AbstractDownloadSource
	{
	private final RemoteResourceService remoteResourceService;
	private final String link;
	private final String path;
	
	/**
	 * Constructor
	 * @param remoteResourceService RemoteResourceService
	 * @param link Link name
	 * @param path Resource path
	 */
	public RemoteResourceDownloadSource(RemoteResourceService remoteResourceService,
			String link, String path)
		{
		this.remoteResourceService = remoteResourceService;
		this.link = link;
		this.path = path;
		
		this.setAttachment(true);
		this.setExpireTime(DownloadSource.ALWAYS);
		this.setFileName(new File(path).getName());
		}
	
	@Override
	public String getContentType()
		{
		return ("application/octet-stream");
		}
	
	@Override
	public void write(OutputStream stream) throws IOException
		{
		remoteResourceService.readResource(link, path, new CopyStreamReader(stream));
		}
	}
