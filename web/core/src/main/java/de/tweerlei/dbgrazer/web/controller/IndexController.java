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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.FrontendExtensionService;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Controller for simple pages
 * 
 * @author Robert Wruck
 */
@Controller
public class IndexController
	{
	private final TaskProgressService taskProgressService;
	private final FrontendExtensionService extensionService;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param taskProgressService TaskProgressService
	 * @param extensionService FrontendExtensionService
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 */
	@Autowired
	public IndexController(TaskProgressService taskProgressService, FrontendExtensionService extensionService,
			UserSettingsManager userSettingsManager, UserSettings userSettings)
		{
		this.taskProgressService = taskProgressService;
		this.extensionService = extensionService;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		}
	
	/**
	 * Show the index page
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public void showWelcomePage()
		{
		}
	
	/**
	 * Display the user menu
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/user.html", method = RequestMethod.GET)
	public Map<String, Object> showUserMenu()
		{
		if ((userSettings.getPrincipal() == null) && userSettingsManager.isLoginRequired())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Display the admin menu
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/admin.html", method = RequestMethod.GET)
	public Map<String, Object> showAdminMenu()
		{
		if (!userSettings.isLinkEditorEnabled() && !userSettings.isUserEditorEnabled() && !userSettings.isReloadEnabled())
			throw new AccessDeniedException();
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("extensions", extensionService.getAdminMenuExtensions());
		
		return (model);
		}
	
	/**
	 * Toggle editor active/inactive
	 * @return View
	 */
	@RequestMapping(value = "/ajax/editmode.html", method = RequestMethod.GET)
	public String toggleEditorActive()
		{
		userSettings.setEditorActive(!userSettings.isEditorActive());
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Get the current progress
	 * @return View
	 */
	@RequestMapping(value = "/ajax/progress.html", method = RequestMethod.GET)
	public Map<String, Object> showTaskProgress()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("progress", taskProgressService.getProgress());
		
		return (model);
		}
	
	/**
	 * Cancel all pending tasks
	 * @return View
	 */
	@RequestMapping(value = "/ajax/cancel.html", method = RequestMethod.GET)
	public String cancelTasks()
		{
		for (TaskProgress p : userSettings.getTaskProgress().values())
			p.cancel();
		
		return (ViewConstants.EMPTY_VIEW);
		}
	}
