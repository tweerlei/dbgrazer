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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.backend.ConfigLoader;
import de.tweerlei.dbgrazer.common.backend.impl.DummyConfigLoader;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.common.service.ConfigManagerService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Write configuration
 * 
 * @author Robert Wruck
 */
@Service
public class ConfigManagerServiceImpl implements ConfigManagerService, ConfigListener
	{
	private final ConfigService configService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	
	private ConfigLoader loader;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public ConfigManagerServiceImpl(ConfigService configService, ModuleLookupService moduleService)
		{
		this.configService = configService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
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
		final String loaderPrefix = configService.get(ConfigKeys.CONFIG_LOADER);
		
		logger.log(Level.INFO, "Using ConfigLoader: " + loaderPrefix);
		try	{
			loader = moduleService.findModuleInstance(loaderPrefix + "ConfigLoader", ConfigLoader.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			loader = new DummyConfigLoader();
			}
		}
	
	@Override
	public void updateConfig(String user, Map<String, String> settings) throws IOException
		{
		final Properties props = new Properties();
		props.putAll(settings);
		
		loader.updateConfig(user, props);
		}
	
	@Override
	public List<HistoryEntry> getHistory(int limit)
		{
		try	{
			final List<HistoryEntry> l = loader.getHistory(limit);
			if (l.size() > 1)
				Collections.sort(l);
			return (l);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "getHistory: getHistory failed", e);
			return (Collections.emptyList());
			}
		}
	}
