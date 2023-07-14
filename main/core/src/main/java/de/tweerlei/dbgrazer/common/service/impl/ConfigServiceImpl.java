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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.backend.ConfigLoader;
import de.tweerlei.dbgrazer.common.backend.impl.FileConfigLoader;
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
		private final ConfigLoader configLoader;
		
		public FileConfigProvider(ConfigLoader configLoader)
			{
			this.configLoader = configLoader;
			}
		
		public void reload()
			{
			final Properties props = configLoader.loadConfig();
			
			final Map<String, String> tmp = list();
			tmp.clear();
			for (Map.Entry<?, ?> ent : props.entrySet())
				tmp.put(String.valueOf(ent.getKey()), String.valueOf(ent.getValue()));
			}
		}
	
	private final FileConfigProvider configProvider;
	private final CachedConfigAccessor accessor;
	private final List<ConfigListener> listeners;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param serializerFactory SerializerFactory
	 */
	@Autowired
	public ConfigServiceImpl(ConfigFileStore store, SerializerFactory serializerFactory)
		{
		// For reading, always use a simple file access
		this.configProvider = new FileConfigProvider(new FileConfigLoader(store));
		this.accessor = new CachedConfigAccessor(new ConfigProviderAccessor(configProvider, serializerFactory));
		this.listeners = new LinkedList<ConfigListener>();
		}
	
	/**
	 * Read configuration properties
	 */
	@PostConstruct
	@Override
	public void reload()
		{
		configProvider.reload();
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
