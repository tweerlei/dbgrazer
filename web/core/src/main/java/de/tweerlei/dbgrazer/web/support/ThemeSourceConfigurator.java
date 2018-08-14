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
package de.tweerlei.dbgrazer.web.support;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.web.constant.ConfigKeys;
import de.tweerlei.spring.web.handler.ReloadableResourceBundleThemeSource;
import de.tweerlei.spring.web.handler.ThemeEnumerator;

/**
 * Configure Spring's ThemeSource to use the configured properties files
 * 
 * @author Robert Wruck
 */
public class ThemeSourceConfigurator implements ConfigListener, ThemeEnumerator
	{
	private final ConfigFileStore store;
	private final ConfigService configService;
	private final KeywordService keywordService;
	private final ReloadableResourceBundleThemeSource themeSource;
	private final Logger logger;
	
	private List<String> themeNames;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigService
	 * @param keywordService KeywordService
	 * @param themeSource ReloadableResourceBundleThemeSource
	 */
	@Autowired
	public ThemeSourceConfigurator(ConfigFileStore store, ConfigService configService, KeywordService keywordService,
			ReloadableResourceBundleThemeSource themeSource)
		{
		this.store = store;
		this.configService = configService;
		this.keywordService = keywordService;
		this.themeSource = themeSource;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Read attributes from the manifest
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
		// Configure the ThemeSource
		final String themePath = configService.get(ConfigKeys.THEME_PATH);
		logger.log(Level.INFO, "Using custom themes from base name: " + themePath);
		final File themeFile = store.getFileLocation(themePath);
		themeSource.setBasenamePrefix("file:" + themeFile.getAbsolutePath() + File.separator);
		
		// Set the available theme names
		final String v = configService.get(ConfigKeys.THEME_NAMES);
		if (v == null)
			themeNames = Collections.emptyList();
		else
			themeNames = Collections.unmodifiableList(keywordService.extractValues(v));
		}
	
	@Override
	public List<String> getThemeNames()
		{
		return (themeNames);
		}
	}
