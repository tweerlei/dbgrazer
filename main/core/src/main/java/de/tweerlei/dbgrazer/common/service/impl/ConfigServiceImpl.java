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
package de.tweerlei.dbgrazer.common.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.spring.config.ConfigKey;
import de.tweerlei.spring.config.ConfigProvider;
import de.tweerlei.spring.config.impl.CachedConfigAccessor;
import de.tweerlei.spring.config.impl.ConfigProviderAccessor;
import de.tweerlei.spring.config.impl.MapBackedConfigProvider;
import de.tweerlei.spring.service.SerializerFactory;

/**
 * Property based impl.
 * 
 * @author Robert Wruck
 */
@Service
public class ConfigServiceImpl implements ConfigService
	{
	private static final class FileConfigProvider extends MapBackedConfigProvider
		{
		private final Logger logger;
		
		public FileConfigProvider()
			{
			this.logger = Logger.getLogger(getClass().getCanonicalName());
			}
		
		public void reload(File file, File dir, String charset)
			{
			try	{
				final InputStreamReader isr = new InputStreamReader(new FileInputStream(file), charset);
				try	{
					final Properties props = new Properties();
					props.load(isr);
					
					final Map<String, String> tmp = list();
					tmp.clear();
					for (Map.Entry<?, ?> ent : props.entrySet())
						tmp.put(String.valueOf(ent.getKey()), String.valueOf(ent.getValue()));
					
					tmp.put(ConfigKeys.CONFIG_FILE.getKey(), file.getAbsolutePath());
					tmp.put(ConfigKeys.CONFIG_PATH.getKey(), dir.getAbsolutePath());
					}
				finally
					{
					StreamUtils.closeQuietly(isr);
					}
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, file.getAbsolutePath(), e);
				}
			}
		}
	
	private final ConfigFileStore store;
	private final FileConfigProvider configProvider;
	private final CachedConfigAccessor accessor;
	private final Logger logger;
	private final List<ConfigListener> listeners;
	
	/**
	 * Constructor
	 * @param serializerFactory SerializerFactory
	 * @param store ConfigFileStore
	 */
	@Autowired
	public ConfigServiceImpl(ConfigFileStore store, SerializerFactory serializerFactory)
		{
		this.store = store;
		this.configProvider = new FileConfigProvider();
		this.accessor = new CachedConfigAccessor(new ConfigProviderAccessor(configProvider, serializerFactory));
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.listeners = new LinkedList<ConfigListener>();
		}
	
	/**
	 * Read configuration properties
	 */
	@PostConstruct
	@Override
	public void reload()
		{
		final String configPath = store.getConfigFilePath();
		final File configFile = store.getFileLocation(configPath);
		configProvider.reload(configFile, store.getFileLocation(null), store.getFileEncoding());
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Loaded properties from ").append(configFile).append("\n");
		for (Map.Entry<String, String> ent : configProvider.list().entrySet())
			sb.append(ent.getKey()).append(" = ").append(ent.getValue()).append("\n");
		
		logger.log(Level.INFO, sb.toString());
		
		accessor.flush();
		
		fireConfigChanged();
		}
	
	@Override
	public void addListener(ConfigListener listener)
		{
		this.listeners.add(listener);
		}
	
	private void fireConfigChanged()
		{
		for (ConfigListener l : listeners)
			l.configChanged();
		}
	
	@Override
	public <T> T get(ConfigKey<T> key)
		{
		return (accessor.get(key));
		}
	
	@Override
	public <T> T getRaw(ConfigKey<T> key)
		{
		return (accessor.getRaw(key));
		}
	
	@Override
	public ConfigProvider getConfigProvider()
		{
		return (configProvider);
		}
	}
