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
package de.tweerlei.dbgrazer.extension.file.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.dbgrazer.common.file.FileAccess;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.extension.file.RemoteResourceService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.service.LinkService;

/**
 * Access remote resources.
 * TODO: Support a file access strategy per link (direct, svn or even remote via URLs)
 * 
 * @author Robert Wruck
 */
@Service
public class RemoteResourceServiceImpl implements RemoteResourceService
	{
	private final KeywordService keywordService;
	private final LinkService linkService;
	private final FileAccess fileAccess;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 * @param linkService LinkService
	 * @param fileAccess FileAccess
	 */
	@Autowired
	public RemoteResourceServiceImpl(KeywordService keywordService, LinkService linkService,
			@Qualifier("directFileAccess") FileAccess fileAccess)
		{
		this.keywordService = keywordService;
		this.linkService = linkService;
		this.fileAccess = fileAccess;
		}
	
	@Override
	public void readResource(String link, String path, StreamReader reader) throws IOException
		{
		fileAccess.readFile(getFile(link, path), reader);
		}
	
	@Override
	public void createPath(String link, String path, String user) throws IOException
		{
		fileAccess.createDirectory(user, getFile(link, path));
		}
	
	@Override
	public void createResource(String link, String path, StreamWriter writer, String user) throws IOException
		{
		fileAccess.createFile(user, writer, getFile(link, path));
		}
	
	@Override
	public void updateResource(String link, String path, StreamWriter writer, String user) throws IOException
		{
		final File f = getFile(link, path);
		fileAccess.writeFile(user, writer, f, f);
		}
	
	@Override
	public void renameResource(String link, String path, String newPath, String user) throws IOException
		{
		fileAccess.renameFileOrDirectory(user, getFile(link, path), getFile(link, newPath));
		}
	
	@Override
	public void removeResource(String link, String path, String user) throws IOException
		{
		fileAccess.removeFileOrDirectory(user, getFile(link, path));
		}
	
	private File getFile(String link, String path) throws IOException
		{
		final LinkDef c = linkService.getLink(link, null);
		if (c == null)
			throw new IOException("Link not found: " + link);
		
		final String s = keywordService.normalizePath(path);
		
		return (new File(c.getUrl(), s));
		}
	}
