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
package de.tweerlei.dbgrazer.link.backend.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common.io.Filename;
import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.file.FileAccess;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.link.backend.LinkLoader;
import de.tweerlei.dbgrazer.link.backend.LinkPersister;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Property file based impl.
 * 
 * @author Robert Wruck
 */
public abstract class AbstractFileLinkLoader implements LinkLoader
	{
	private static final String FILE_EXTENSION = "properties";
	
	private static final class LinkReader implements StreamReader
		{
		private final LinkPersister persister;
		private final String name;
		private final String charset;
		private LinkDef conn;
		
		public LinkReader(LinkPersister persister, String name, String charset)
			{
			this.persister = persister;
			this.name = name;
			this.charset = charset;
			}
		
		@Override
		public void read(InputStream stream) throws IOException
			{
			final InputStreamReader r = new InputStreamReader(stream, charset);
			conn = persister.readLink(r, name);
			}
		
		public LinkDef getLinkDef()
			{
			return (conn);
			}
		}
	
	private static final class LinkWriter implements StreamWriter
		{
		private final LinkPersister persister;
		private final String charset;
		private LinkDef conn;
		
		public LinkWriter(LinkPersister persister, LinkDef conn, String charset)
			{
			this.persister = persister;
			this.charset = charset;
			this.conn = conn;
			}
		
		@Override
		public void write(OutputStream stream) throws IOException
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, charset);
			persister.writeLink(w, conn);
			w.flush();
			}
		}
	
	private final ConfigFileStore store;
	private final ConfigAccessor configService;
	private final LinkPersister persister;
	private final FileAccess fileAccess;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigAccessor
	 * @param persister LinkPersister
	 * @param fileAccess FileAccess
	 */
	protected AbstractFileLinkLoader(ConfigFileStore store, ConfigAccessor configService,
			LinkPersister persister, FileAccess fileAccess)
		{
		this.store = store;
		this.configService = configService;
		this.persister = persister;
		this.fileAccess = fileAccess;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public SortedMap<String, LinkDef> loadLinks()
		{
		final SortedMap<String, LinkDef> ret = new TreeMap<String, LinkDef>(StringComparators.CASE_INSENSITIVE);
		
		final File path = getPath();
		
		final List<File> files;
		try	{
			files = fileAccess.listFiles(path);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "listFiles", e);
			return (ret);
			}
		
		for (File f : files)
			{
			final Filename fn = new Filename(f);
			if (FILE_EXTENSION.equals(fn.getExtension()))
				{
				try	{
					final LinkReader r = new LinkReader(persister, fn.getBasename(), store.getFileEncoding());
					fileAccess.readFile(f, r);
					if (r.getLinkDef() != null)
						ret.put(r.getLinkDef().getName(), r.getLinkDef());
					}
				catch (IOException e)
					{
					logger.log(Level.WARNING, f.getAbsolutePath(), e);
					}
				}
			}
		
		return (ret);
		}
	
	@Override
	public final void createLink(String user, String name, LinkDef conn) throws IOException
		{
		final File file = getFile(name);
		final File dir = file.getParentFile();
		if (!dir.exists())
			fileAccess.createDirectory(user, dir);
		
		fileAccess.createFile(user, new LinkWriter(persister, conn, store.getFileEncoding()), file);
		}
	
	@Override
	public final void updateLink(String user, String name, String newName, LinkDef conn) throws IOException
		{
		final File oldFile = getFile(name);
		final File file = getFile(newName);
		
		fileAccess.writeFile(user, new LinkWriter(persister, conn, store.getFileEncoding()), oldFile, file);
		}
	
	@Override
	public final void removeLink(String user, String name) throws IOException
		{
		final File file = getFile(name);
		
		fileAccess.removeFileOrDirectory(user, file);
		}
	
	@Override
	public List<HistoryEntry> getHistory(String name, int limit) throws IOException
		{
		final File file = getFile(name);
		
		return (fileAccess.getFileHistory(file, limit));
		}
	
	private File getPath()
		{
		final String linkPath = configService.get(ConfigKeys.LINK_PATH);
		final File linkDir = store.getFileLocation(linkPath);
		
		return (linkDir);
		}
	
	private File getFile(String name)
		{
		final File path = getPath();
		final Filename fn = new Filename();
		fn.setBasename(name);
		fn.setExtension(FILE_EXTENSION);
		final File file = new File(path, fn.getFilename());
		return (file);
		}
	}
