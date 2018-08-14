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
package de.tweerlei.dbgrazer.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.ConfigEnumeratorService;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.config.ConfigKey;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class ConfigController
	{
	private final ConfigService configService;
	private final ConfigEnumeratorService enumerator;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param enumerator ConfigEnumeratorService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public ConfigController(ConfigService configService, ConfigEnumeratorService enumerator,
			UserSettings userSettings)
		{
		this.configService = configService;
		this.enumerator = enumerator;
		this.userSettings = userSettings;
		}
	
	/**
	 * Display the config settings
	 * @return Model
	 */
	@RequestMapping(value = "/config.html", method = RequestMethod.GET)
	public Map<String, Object> showConfig()
		{
		if (!userSettings.isReloadEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("settings", new TreeMap<String, String>(configService.getConfigProvider().list()));
		model.put("allSettings", getAllConfigSettings());
		
		return (model);
		}
	
	/**
	 * Display the config settings
	 * @return Model
	 */
	@RequestMapping(value = "/reload-config.html", method = RequestMethod.GET)
	public String reloadConfig()
		{
		if (userSettings.isReloadEnabled())
			configService.reload();
		
		return ("redirect:config.html");
		}
	
	private Map<String, String> getAllConfigSettings()
		{
		final Map<String, String> ret = new TreeMap<String, String>();
		
		for (ConfigKey<?> k : enumerator.getConfigKeys())
			ret.put(k.getKey(), (k.getDefaultValue() == null) ? null : String.valueOf(k.getDefaultValue()));
		
		return (ret);
		}
	}
