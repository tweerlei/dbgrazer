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
package de.tweerlei.dbgrazer.web.service.impl;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;

/**
 * Configure Spring's MessageSource to use the configured properties files
 * 
 * @author Robert Wruck
 */
@Service
public class MessageSourceConfigurator implements ConfigListener
	{
	private final ConfigFileStore store;
	private final ConfigService configService;
	private final ReloadableResourceBundleMessageSource messageSource;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigService
	 * @param messageSource MessageSource
	 */
	@Autowired
	public MessageSourceConfigurator(ConfigFileStore store, ConfigService configService,
			ReloadableResourceBundleMessageSource messageSource)
		{
		this.store = store;
		this.configService = configService;
		this.messageSource = messageSource;
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
		// Configure the MessageSource
		final String messagesPath = configService.get(ConfigKeys.MESSAGES_FILE);
		logger.log(Level.INFO, "Using custom localizations from base name: " + messagesPath);
		if (messagesPath == null)
			messageSource.setBasenames(new String[] {});
		else
			{
			final File messagesFile = store.getFileLocation(messagesPath);
			messageSource.setBasenames(new String[] { "file:" + messagesFile.getAbsolutePath() });
			}
		messageSource.clearCache();
		}
	}
