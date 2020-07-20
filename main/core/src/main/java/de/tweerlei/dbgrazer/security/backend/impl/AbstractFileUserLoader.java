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
package de.tweerlei.dbgrazer.security.backend.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common.io.Filename;
import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.file.FileAccess;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.file.ObjectPersister;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.security.backend.UserAuthenticator;
import de.tweerlei.dbgrazer.security.backend.UserLoader;
import de.tweerlei.dbgrazer.security.backend.UserPersister;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.impl.UserImpl;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Perform user authentication against properties files
 * 
 * @author Robert Wruck
 */
public abstract class AbstractFileUserLoader implements UserLoader, UserAuthenticator
	{
	private static final String FILE_EXTENSION = "properties";
	private static final String CUSTOM_EXTENSION = "txt";
	
	private static final class UserReader implements StreamReader
		{
		private final UserPersister persister;
		private final String name;
		private final String charset;
		private User user;
		
		public UserReader(UserPersister persister, String name, String charset)
			{
			this.persister = persister;
			this.name = name;
			this.charset = charset;
			}
		
		@Override
		public void read(InputStream stream) throws IOException
			{
			final InputStreamReader r = new InputStreamReader(stream, charset);
			user = persister.readUser(r, name);
			}
		
		public User getUser()
			{
			return (user);
			}
		}
	
	private static final class UserWriter implements StreamWriter
		{
		private final UserPersister persister;
		private final String charset;
		private User user;
		
		public UserWriter(UserPersister persister, User user, String charset)
			{
			this.persister = persister;
			this.charset = charset;
			this.user = user;
			}
		
		@Override
		public void write(OutputStream stream) throws IOException
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, charset);
			persister.writeUser(w, user);
			w.flush();
			}
		}
	
	private static final class ObjectReader<T> implements StreamReader
		{
		private final ObjectPersister<T> persister;
		private final String charset;
		private T object;
		
		public ObjectReader(ObjectPersister<T> persister, String charset)
			{
			this.persister = persister;
			this.charset = charset;
			}
		
		@Override
		public void read(InputStream stream) throws IOException
			{
			final InputStreamReader r = new InputStreamReader(stream, charset);
			object = persister.readObject(r);
			}
		
		public T getObject()
			{
			return (object);
			}
		}

	private static final class ObjectWriter<T> implements StreamWriter
		{
		private final ObjectPersister<T> persister;
		private final String charset;
		private T object;
		
		public ObjectWriter(ObjectPersister<T> persister, T object, String charset)
			{
			this.persister = persister;
			this.charset = charset;
			this.object = object;
			}
		
		@Override
		public void write(OutputStream stream) throws IOException
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, charset);
			persister.writeObject(w, object);
			w.flush();
			}
		}
	
	
	private final ConfigFileStore store;
	private final ConfigAccessor configService;
	private final UserPersister persister;
	private final FileAccess fileAccess;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigAccessor
	 * @param persister UserPersister
	 * @param fileAccess FileAccess
	 */
	protected AbstractFileUserLoader(ConfigFileStore store, ConfigAccessor configService,
			UserPersister persister, FileAccess fileAccess)
		{
		this.store = store;
		this.configService = configService;
		this.persister = persister;
		this.fileAccess = fileAccess;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public boolean authenticate(String username, String password)
		{
		final User u = loadUserData(username);
		if (u == null)
			return (false);
		
		if (StringUtils.empty(u.getPassword()) || !password.equals(u.getPassword()))
			return (false);
		
		return (true);
		}
	
	@Override
	public User loadUser(String username)
		{
		User u = loadUserData(username);
		if (u == null)
			{
			if (!configService.get(ConfigKeys.FILE_CREATE_USERS))
				return (null);
			
			final Set<Authority> authorities = getDefaultAuthorities();
			
			u = new UserImpl(username, username, "", authorities, Collections.<String, Set<Authority>>emptyMap(), Collections.<String, String>emptyMap());
			try	{
				createUser(username, username, u);
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "loadUser", e);
				return (null);
				}
			}
		
		return (u);
		}
	
	private Set<Authority> getDefaultAuthorities()
		{
		final Set<Authority> ret = EnumSet.of(Authority.ROLE_LOGIN);
		
		ret.addAll(configService.get(ConfigKeys.FILE_DEFAULT_ROLES));
		
		return (ret);
		}
	
	@Override
	public SortedSet<String> listUsers()
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
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
				ret.add(fn.getBasename());
			}
		
		return (ret);
		}

	@Override
	public void createUser(String user, String name, User u) throws IOException
		{
		final File file = getFile(name);
		final File dir = file.getParentFile();
		if (!dir.exists())
			fileAccess.createDirectory(user, dir);
		
		fileAccess.createFile(user, new UserWriter(persister, u, store.getFileEncoding()), file);
		}

	@Override
	public void updateUser(String user, String name, String newName, User u) throws IOException
		{
		final File oldFile = getFile(name);
		final File file = getFile(newName);
		
		fileAccess.writeFile(user, new UserWriter(persister, u, store.getFileEncoding()), oldFile, file);
		
		// Rename profile if present
		final File oldProfile = getProfilePath(name);
		if (oldProfile.exists())
			{
			final File newProfile = getProfilePath(newName);
			
			fileAccess.renameFileOrDirectory(user, oldProfile, newProfile);
			}
		}

	@Override
	public void removeUser(String user, String name) throws IOException
		{
		final File file = getFile(name);
		
		fileAccess.removeFileOrDirectory(user, file);
		
		// Remove profile if present
		final File profile = getProfilePath(name);
		if (profile.exists())
			fileAccess.removeFileOrDirectory(user, profile);
		}
	
	@Override
	public List<HistoryEntry> getHistory(String name, int limit) throws IOException
		{
		final File file = getFile(name);
		
		return (fileAccess.getFileHistory(file, limit));
		}
	
	private User loadUserData(String username)
		{
		final File f = getFile(username);
		
		try	{
			final UserReader r = new UserReader(persister, username, store.getFileEncoding());
			fileAccess.readFile(f, r);
			return (r.getUser());
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, f.getAbsolutePath(), e);
			}
		
		return (null);
		}
	
	@Override
	public SortedSet<String> listExtensionObjects(String user, String schema, String extensionName)
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
		final File path = getExtensionPath(user, schema, extensionName);
		
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
			if (CUSTOM_EXTENSION.equals(fn.getExtension()))
				ret.add(fn.getBasename());
			}
		
		return (ret);
		}
	
	@Override
	public <T> T loadExtensionObject(String user, String schema, String extensionName, String name, ObjectPersister<T> pers) throws IOException
		{
		final File f = getExtensionFile(user, schema, extensionName, name);
		final ObjectReader<T> r = new ObjectReader<T>(pers, store.getFileEncoding());
		fileAccess.readFile(f, r);
		return (r.getObject());
		}
	
	@Override
	public <T> void saveExtensionObject(String user, String owner, String schema, String extensionName, String name, T object, ObjectPersister<T> pers) throws IOException
		{
		final File file = getExtensionFile(owner, schema, extensionName, name);
		final File dir = file.getParentFile();
		if (!dir.exists())
			fileAccess.createDirectory(user, dir);
		
		fileAccess.writeFile(user, new ObjectWriter<T>(pers, object, store.getFileEncoding()), file, file);
		}
	
	@Override
	public void removeExtensionObject(String user, String owner, String schema, String extensionName, String name) throws IOException
		{
		final File f = getExtensionFile(owner, schema, extensionName, name);
		fileAccess.removeFileOrDirectory(user, f);
		}
	
	private File getPath()
		{
		final String userPath = configService.get(ConfigKeys.FILE_USER_PATH);
		final File userDir = store.getFileLocation(userPath);
		
		return (userDir);
		}
	
	private File getFile(String user)
		{
		final String basename;
		if (configService.get(ConfigKeys.FILE_LOWERCASE_USERS))
			basename = user.toLowerCase();
		else
			basename = user;
		
		final File path = getPath();
		final Filename fn = new Filename();
		fn.setBasename(basename);
		fn.setExtension(FILE_EXTENSION);
		final File file = new File(path, fn.getFilename());
		return (file);
		}
	
	private File getProfilePath(String user)
		{
		final String basename;
		if (configService.get(ConfigKeys.FILE_LOWERCASE_USERS))
			basename = user.toLowerCase();
		else
			basename = user;
		
		final String userPath = configService.get(ConfigKeys.FILE_PROFILE_PATH);
		final File userDir = store.getFileLocation(userPath);
		
		return (new File(userDir, basename));
		}
	
	private File getExtensionPath(String user, String schema, String extensionName)
		{
		final File path = getProfilePath(user);
		
		return (new File(new File(path, schema), extensionName));
		}
	
	private File getExtensionFile(String user, String schema, String extensionName, String name)
		{
		final File path = getExtensionPath(user, schema, extensionName);
		final Filename fn = new Filename();
		fn.setBasename(name);
		fn.setExtension(CUSTOM_EXTENSION);
		final File file = new File(path, fn.getFilename());
		return (file);
		}
	}
