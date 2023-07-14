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
package de.tweerlei.dbgrazer.common.backend.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.dbgrazer.common.backend.ConfigLoader;
import de.tweerlei.dbgrazer.common.file.FileAccess;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.file.impl.DirectFileAccess;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Read/write configuration from/to properties files
 * 
 * @author Robert Wruck
 */
@Service("fileConfigLoader")
public class FileConfigLoader implements ConfigLoader, ConfigListener
	{
	private static final class ConfigReader implements StreamReader
		{
		private final String charset;
		private Properties props;
		
		public ConfigReader(String charset)
			{
			this.charset = charset;
			}
		
		@Override
		public void read(InputStream stream) throws IOException
			{
			final InputStreamReader r = new InputStreamReader(stream, charset);
			try	{
				props = new Properties();
				props.load(r);
				}
			finally
				{
				StreamUtils.closeQuietly(r);
				}
			}
		
		public Properties getProperties()
			{
			return (props);
			}
		}
	
	private static final class ConfigWriter implements StreamWriter
		{
		private final String charset;
		private Properties props;
		
		public ConfigWriter(Properties props, String charset)
			{
			this.charset = charset;
			this.props = props;
			}
		
		@Override
		public void write(OutputStream stream) throws IOException
			{
			final OutputStreamWriter w = new OutputStreamWriter(stream, charset);
			try	{
				props.store(w, null);
				}
			finally
				{
				w.close();
				}
			}
		}
	
	
	private final ConfigFileStore store;
	private final ConfigService configService;
	private final ModuleLookupService moduleService;
	private final String configFilePath;
	private final Logger logger;
	
	private FileAccess fileAccess;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigAccessor
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public FileConfigLoader(ConfigFileStore store, ConfigService configService,
			ModuleLookupService moduleService)
		{
		this.store = store;
		this.configService = configService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		
		final String configFileProperty = System.getProperty(ConfigKeys.CONFIG_FILE.getKey());
		if (configFileProperty != null)
			this.configFilePath = configFileProperty;
		else
			this.configFilePath = ConfigKeys.CONFIG_FILE.getDefaultValue();
		}
	
	/**
	 * Used for direct instantiation from ConfigServiceImpl
	 * @param store ConfigFileStore
	 */
	public FileConfigLoader(ConfigFileStore store)
		{
		this(store, null, null);
		
		this.fileAccess = new DirectFileAccess();
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		final String loaderPrefix = configService.get(ConfigKeys.CONFIG_FILE_ACCESS);
		
		logger.log(Level.INFO, "Using FileAccess: " + loaderPrefix);
		try	{
			fileAccess = moduleService.findModuleInstance(loaderPrefix + "FileAccess", FileAccess.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			fileAccess = new DirectFileAccess();
			}
		}
	
	@Override
	public Properties loadConfig()
		{
		final File configFile = getFile();
		
		try	{
			final ConfigReader r = new ConfigReader(store.getFileEncoding());
			fileAccess.readFile(configFile, r);
			final Properties props = r.getProperties();
			
			final StringBuilder sb = new StringBuilder();
			sb.append("Loaded properties from ").append(configFile).append("\n");
			for (Map.Entry<Object, Object> ent : props.entrySet())
				sb.append(ent.getKey()).append(" = ").append(ent.getValue()).append("\n");
			
			logger.log(Level.INFO, sb.toString());
			
			// Populate CONFIG_FILE and CONFIG_PATH with the actually used values
			props.setProperty(ConfigKeys.CONFIG_FILE.getKey(), configFile.getAbsolutePath());
			props.setProperty(ConfigKeys.CONFIG_PATH.getKey(), store.getFileLocation(null).getAbsolutePath());
			
			return (props);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, configFile.getAbsolutePath(), e);
			}
		
		return (new Properties());
		}

	@Override
	public void updateConfig(String user, Properties props) throws IOException
		{
		final File configFile = getFile();
		
		final File dir = configFile.getParentFile();
		if (!dir.exists())
			fileAccess.createDirectory(user, dir);
		
		fileAccess.createFile(user, new ConfigWriter(props, store.getFileEncoding()), configFile);
		}
	
	@Override
	public List<HistoryEntry> getHistory(int limit) throws IOException
		{
		final File configFile = getFile();
		
		return (fileAccess.getFileHistory(configFile, limit));
		}
	
	private File getFile()
		{
		final File configFile = store.getFileLocation(configFilePath);
		return (configFile);
		}
	}
