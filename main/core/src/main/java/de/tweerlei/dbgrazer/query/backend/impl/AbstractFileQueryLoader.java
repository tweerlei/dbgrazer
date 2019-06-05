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
package de.tweerlei.dbgrazer.query.backend.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.backend.QueryLoader;
import de.tweerlei.dbgrazer.query.backend.QueryPersister;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * QueryLoader that loads query definitions from files
 * 
 * @author Robert Wruck
 */
public abstract class AbstractFileQueryLoader implements QueryLoader
	{
	private static final String FILE_EXTENSION = "txt";
	private static final String PROPERTIES_FILE = "schema.properties"; 
	
	private static final class QueryReader implements StreamReader
		{
		private final QueryPersister persister;
		private final String name;
		private final SchemaDef scope;
		private final String charset;
		private Query query;
		
		public QueryReader(QueryPersister persister, String name, SchemaDef scope, String charset)
			{
			this.persister = persister;
			this.name = name;
			this.scope = scope;
			this.charset = charset;
			}
		
		@Override
		public void read(InputStream stream) throws IOException
			{
			final InputStreamReader r = new InputStreamReader(stream, charset);
			query = persister.readQuery(r, name, scope);
			}
		
		public Query getQuery()
			{
			return (query);
			}
		}
	
	private static final class QueryWriter implements StreamWriter
		{
		private final QueryPersister persister;
		private final String charset;
		private Query query;
		
		public QueryWriter(QueryPersister persister, Query query, String charset)
			{
			this.persister = persister;
			this.charset = charset;
			this.query = query;
			}
		
		@Override
		public void write(OutputStream stream) throws IOException
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, charset);
			persister.writeQuery(w, query);
			w.flush();
			}
		}
	
	private static final class AttributesReader implements StreamReader
		{
		private final String charset;
		private Map<String, String> attributes;
		
		public AttributesReader(String charset)
			{
			this.charset = charset;
			}
		
		@Override
		public void read(InputStream stream) throws IOException
			{
			final InputStreamReader r = new InputStreamReader(stream, charset);
			final Properties props = new Properties();
			props.load(r);
			
			attributes = new HashMap<String, String>();
			for (Map.Entry<?, ?> ent : props.entrySet())
				attributes.put(String.valueOf(ent.getKey()), String.valueOf(ent.getValue()));
			}
		
		public Map<String, String> getAttributes()
			{
			return (attributes);
			}
		}
	
	private static final class AttributesWriter implements StreamWriter
		{
		private final String charset;
		private Map<String, String> attributes;
		
		public AttributesWriter(Map<String, String> attributes, String charset)
			{
			this.charset = charset;
			this.attributes = attributes;
			}
		
		@Override
		public void write(OutputStream stream) throws IOException
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, charset);
			final Properties props = new Properties();
			for (Map.Entry<String, String> ent : attributes.entrySet())
				props.setProperty(ent.getKey(), ent.getValue());
			props.store(w, null);
			w.flush();
			}
		}
	
	private final ConfigFileStore store;
	private final ConfigAccessor configService;
	private final QueryPersister persister;
	private final FileAccess fileAccess;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigAccessor
	 * @param persister QueryPersister
	 * @param fileAccess FileAccess
	 */
	protected AbstractFileQueryLoader(ConfigFileStore store, ConfigAccessor configService,
			QueryPersister persister, FileAccess fileAccess)
		{
		this.store = store;
		this.configService = configService;
		this.persister = persister;
		this.fileAccess = fileAccess;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public final SortedMap<String, Query> loadQueries(SchemaDef schema)
		{
		final SortedMap<String, Query> ret = new TreeMap<String, Query>(StringComparators.CASE_INSENSITIVE);
		
		final File path = getPath(schema);
		
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
					final QueryReader r = new QueryReader(persister, fn.getBasename(), schema, store.getFileEncoding());
					fileAccess.readFile(f, r);
					if (r.getQuery() != null)
						ret.put(r.getQuery().getName(), r.getQuery());
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
	public final void createQuery(SchemaDef schema, String user, String name, Query query) throws IOException
		{
		final File file = getFile(schema, name);
		final File dir = file.getParentFile();
		if (!dir.exists())
			fileAccess.createDirectory(user, dir);
		
		fileAccess.createFile(user, new QueryWriter(persister, query, store.getFileEncoding()), file);
		}
	
	@Override
	public final void updateQuery(SchemaDef schema, String user, String name, String newName, Query query) throws IOException
		{
		final File oldFile = getFile(schema, name);
		final File file = getFile(schema, newName);
		
		fileAccess.writeFile(user, new QueryWriter(persister, query, store.getFileEncoding()), oldFile, file);
		}
	
	@Override
	public final void removeQuery(SchemaDef schema, String user, String name) throws IOException
		{
		final File file = getFile(schema, name);
		if (file.exists())
			fileAccess.removeFileOrDirectory(user, file);
		}
	
	@Override
	public List<HistoryEntry> getHistory(SchemaDef schema, String name, int limit) throws IOException
		{
		final File file = getFile(schema, name);
		if (file.exists())
			return (fileAccess.getFileHistory(file, limit));
		return (new ArrayList<HistoryEntry>());
		}
	
	@Override
	public Query getQueryVersion(SchemaDef schema, String name, String version) throws IOException
		{
		final File file = getFile(schema, name);
		if (file.exists())
			{
			final QueryReader r = new QueryReader(persister, name, schema, store.getFileEncoding());
			fileAccess.getFileVersion(file, version, r);
			return (r.getQuery());
			}
		return (null);
		}
	
	@Override
	public List<SchemaDef> getSubSchemas(SchemaDef schema)
		{
		final List<SchemaDef> ret = new ArrayList<SchemaDef>();
		
		if (!schema.isMainSchema())
			return (ret);
		
		final File path = getPath(schema);
		
		final List<File> files;
		try	{
			files = fileAccess.listDirectories(path);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "listDirectories", e);
			return (ret);
			}
		
		for (File f : files)
			ret.add(new SchemaDef(schema.getName(), f.getName()));
		
		return (ret);
		}
	
	@Override
	public void renameSchema(String user, SchemaDef oldName, SchemaDef newName) throws IOException
		{
		if (oldName.equals(newName))
			return;
		
		if ((oldName.isQuerySet() && newName.isQuerySet())
				|| (oldName.isMainSchema() && newName.isMainSchema())
				)
			{
			final File oldFile = getPath(oldName);
			final File newFile = getPath(newName);
			
			fileAccess.renameFileOrDirectory(user, oldFile, newFile);
			}
		else if (oldName.isSubschema() && newName.isSubschema())
			{
			if (oldName.getName().equals(newName.getName()))
				{
				final File oldFile = getPath(oldName);
				final File newFile = getPath(newName);
				
				fileAccess.renameFileOrDirectory(user, oldFile, newFile);
				}
			else
				{
				final File oldFile = getPath(oldName.getUnversionedSchema());
				final File newFile = getPath(newName.getUnversionedSchema());
				
				fileAccess.renameFileOrDirectory(user, oldFile, newFile);
				
				if (!oldName.getVersion().equals(newName.getVersion()))
					{
					final File oldSubFile = getPath(new SchemaDef(newName.getName(), oldName.getVersion()));
					final File newSubFile = getPath(newName);
					
					fileAccess.renameFileOrDirectory(user, oldSubFile, newSubFile);
					}
				}
			}
		else
			throw new IOException("Can not convert between schema, subschema and dialect");
		}
	
	@Override
	public Map<String, String> loadAttributes(SchemaDef schema)
		{
		final File f = new File(getPath(schema), PROPERTIES_FILE);
		if (f.isFile())
			{
			try	{
				final AttributesReader r = new AttributesReader(store.getFileEncoding());
				fileAccess.readFile(f, r);
				return (r.getAttributes());
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "loadAttributes", e);
				}
			}
		return (Collections.emptyMap());
		}
	
	@Override
	public void updateAttributes(SchemaDef schema, String user, Map<String, String> attributes) throws IOException
		{
		final File dir = getPath(schema);
		if (!dir.exists())
			fileAccess.createDirectory(user, dir);
		
		final File f = new File(dir, PROPERTIES_FILE);
		if (f.isFile())
			fileAccess.createFile(user, new AttributesWriter(attributes, store.getFileEncoding()), f);
		else
			fileAccess.writeFile(user, new AttributesWriter(attributes, store.getFileEncoding()), f, f);
		}
	
	private File getPath(SchemaDef schema)
		{
		if (schema.isQuerySet())
			{
			final String schemaPath = configService.get(ConfigKeys.DIALECT_PATH);
			final File schemaDir = store.getFileLocation(schemaPath);
			
			return (new File(schemaDir, schema.getVersion()));
			}
		else
			{
			final String schemaPath = configService.get(ConfigKeys.SCHEMA_PATH);
			final File schemaDir = store.getFileLocation(schemaPath);
			
			if (schema.isSubschema())
				return (new File(new File(schemaDir, schema.getName()), schema.getVersion()));
			else
				return (new File(schemaDir, schema.getName()));
			}
		}
	
	private File getFile(SchemaDef schema, String name)
		{
		final File path = getPath(schema);
		final Filename fn = new Filename();
		fn.setBasename(name);
		fn.setExtension(FILE_EXTENSION);
		final File file = new File(path, fn.getFilename());
		return (file);
		}
	}
