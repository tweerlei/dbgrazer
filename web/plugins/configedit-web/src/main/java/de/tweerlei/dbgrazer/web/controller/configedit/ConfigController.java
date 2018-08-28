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
package de.tweerlei.dbgrazer.web.controller.configedit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.common.service.ConfigManagerService;
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
	private final ConfigManagerService configManagerService;
	private final ConfigEnumeratorService enumerator;
	private final UserSettings userSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param configManagerService ConfigManagerService
	 * @param enumerator ConfigEnumeratorService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public ConfigController(ConfigService configService, ConfigManagerService configManagerService,
			ConfigEnumeratorService enumerator, UserSettings userSettings)
		{
		this.configService = configService;
		this.configManagerService = configManagerService;
		this.enumerator = enumerator;
		this.userSettings = userSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
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
	 * Display an edit dialog
	 * @param key Config key name
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/config.html", method = RequestMethod.GET)
	public Map<String, Object> showConfigDialog(@RequestParam("q") String key)
		{
		if (!userSettings.isConfigEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("key", key);
		model.put("value", configService.getConfigProvider().get(key));
		
		return (model);
		}
	
	/**
	 * Set a config entry
	 * @param key Config key name
	 * @param value Value to set
	 * @return Model
	 */
	@RequestMapping(value = "/set-config.html", method = RequestMethod.POST)
	public String setConfig(@RequestParam("q") String key, @RequestParam("v") String value)
		{
		if (!userSettings.isConfigEditorEnabled())
			throw new AccessDeniedException();
		
		if (getAllConfigSettings().containsKey(key))
			{
			final Map<String, String> config = new HashMap<String, String>(configService.getConfigProvider().list());
			config.put(key, value);
			try	{
				configManagerService.updateConfig(userSettings.getPrincipal().getLogin(), config);
				
				configService.reload();
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "updateConfig", e);
				}
			}
		
		return ("redirect:config.html");
		}
	
	/**
	 * Unset a config entry
	 * @param key Config key name
	 * @return Model
	 */
	@RequestMapping(value = "/unset-config.html", method = RequestMethod.GET)
	public String unsetConfig(@RequestParam("q") String key)
		{
		if (!userSettings.isConfigEditorEnabled())
			throw new AccessDeniedException();
		
		final Map<String, String> config = new HashMap<String, String>(configService.getConfigProvider().list());
		config.remove(key);
		try	{
			configManagerService.updateConfig(userSettings.getPrincipal().getLogin(), config);
			
			configService.reload();
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "updateConfig", e);
			}
		
		return ("redirect:config.html");
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
	
	/**
	 * Show a parameter input form
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/confighistory.html", method = RequestMethod.GET)
	public Map<String, Object> showConfigHistory()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("history", configManagerService.getHistory(10));
		
		return (model);
		}
	
	private Map<String, String> getAllConfigSettings()
		{
		final Map<String, String> ret = new TreeMap<String, String>();
		
		for (ConfigKey<?> k : enumerator.getConfigKeys())
			ret.put(k.getKey(), (k.getDefaultValue() == null) ? null : String.valueOf(k.getDefaultValue()));
		
		return (ret);
		}
	}
