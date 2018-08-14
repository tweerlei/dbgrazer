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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.impl.UserImpl;
import de.tweerlei.dbgrazer.security.service.UserManagerService;
import de.tweerlei.dbgrazer.web.constant.ErrorKeys;
import de.tweerlei.dbgrazer.web.exception.AccessDeniedException;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.service.LocalizationHelper;

/**
 * Controller for editing users
 * 
 * @author Robert Wruck
 */
@Controller
public class ProfileController
	{
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private boolean virtual;
		private String name;
		private String password;
		private String password2;
		private String localeName;
		private String timeZoneName;
		private final Map<String, String> attributes;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.attributes = new TreeMap<String, String>();
			}
		
		/**
		 * Get the name
		 * @return the name
		 */
		public String getName()
			{
			return name;
			}

		/**
		 * Set the name
		 * @param name the name to set
		 */
		public void setName(String name)
			{
			this.name = name;
			}

		/**
		 * Get the password
		 * @return the password
		 */
		public String getPassword()
			{
			return password;
			}

		/**
		 * Set the password
		 * @param password the password to set
		 */
		public void setPassword(String password)
			{
			this.password = password;
			}

		/**
		 * Get the password
		 * @return the password
		 */
		public String getPassword2()
			{
			return password2;
			}

		/**
		 * Set the password
		 * @param password the password to set
		 */
		public void setPassword2(String password)
			{
			this.password2 = password;
			}
		
		/**
		 * Get the attributes
		 * @return the attributes
		 */
		public Map<String, String> getAttributes()
			{
			return attributes;
			}
		
		/**
		 * @return the virtual
		 */
		public boolean isVirtual()
			{
			return virtual;
			}
		
		/**
		 * @param virtual the virtual to set
		 */
		public void setVirtual(boolean virtual)
			{
			this.virtual = virtual;
			}

		/**
		 * Get the localeName
		 * @return the localeName
		 */
		public String getLocaleName()
			{
			return localeName;
			}

		/**
		 * Set the localeName
		 * @param localeName the localeName to set
		 */
		public void setLocaleName(String localeName)
			{
			this.localeName = localeName;
			}

		/**
		 * Get the timeZoneName
		 * @return the timeZoneName
		 */
		public String getTimeZoneName()
			{
			return timeZoneName;
			}

		/**
		 * Set the timeZoneName
		 * @param timeZoneName the timeZoneName to set
		 */
		public void setTimeZoneName(String timeZoneName)
			{
			this.timeZoneName = timeZoneName;
			}
		}
	
	private final LocalizationHelper localizationHelper;
	private final UserManagerService userManagerService;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param localizationHelper LocalizationHelper
	 * @param userManagerService UserManagerService
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 */
	@Autowired
	public ProfileController(LocalizationHelper localizationHelper,
			UserManagerService userManagerService, UserSettingsManager userSettingsManager,
			UserSettings userSettings)
		{
		this.localizationHelper = localizationHelper;
		this.userManagerService = userManagerService;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param locale Request locale
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(Locale locale)
		{
		if (userSettings.getPrincipal() == null)
			throw new AccessDeniedException();
		
		final FormBackingObject ret = new FormBackingObject();
		
		final User u = userManagerService.findUserByName(userSettings.getPrincipal().getLogin());
		if (u == null)
			ret.setVirtual(true);
		else
			{
			ret.setVirtual(false);
			ret.setName(u.getName());
			ret.setPassword("");
			ret.setPassword2("");
			
			ret.getAttributes().putAll(u.getAttributes());
			
			final Locale loc = userSettingsManager.getPreferredLocale(u);
			if (loc != null)
				ret.setLocaleName(localizationHelper.getLocaleDisplayName(loc.toString(), locale));
			
			final TimeZone tz = userSettingsManager.getPreferredTimeZone(u);
			if (tz != null)
				ret.setTimeZoneName(localizationHelper.getTimeZoneDisplayName(tz.getID(), locale));
			}
		
		return (ret);
		}
	
	/**
	 * Show a parameter input form
	 * @return Model
	 */
	@RequestMapping(value = "/profile.html", method = RequestMethod.GET)
	public Map<String, Object> showUserForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param fbo FormBackingObject
	 * @param result BindingResult
	 * @return Model
	 */
	@RequestMapping(value = "/profile.html", method = RequestMethod.POST)
	public String updateUser(@ModelAttribute("model") FormBackingObject fbo, BindingResult result)
		{
		if (fbo.isVirtual())
			return ("profile");
		
		final String password;
		if (StringUtils.empty(fbo.getPassword()))
			password = userSettings.getPrincipal().getPassword();
		else if (fbo.getPassword().equals(fbo.getPassword2()))
			password = fbo.getPassword();
		else
			{
			result.reject(ErrorKeys.PASSWORD_MISMATCH);
			return ("profile");
			}
		
		final Map<String, String> attributes = new TreeMap<String, String>();
		for (Map.Entry<String, String> ent : fbo.getAttributes().entrySet())
			{
			if (!StringUtils.empty(ent.getValue()))
				attributes.put(ent.getKey(), ent.getValue());
			}
		
		final User u = new UserImpl(userSettings.getPrincipal().getLogin(), fbo.getName(), password, userSettings.getPrincipal().getGrantedAuthorities(), userSettings.getPrincipal().getGroups(), attributes);
		
		try	{
			final String name;
			name = userManagerService.updateUser(userSettings.getPrincipal().getLogin(), userSettings.getPrincipal().getLogin(), u);
			if (name == null)
				{
				result.reject(ErrorKeys.WRITE_FAILED);
				return ("profile");
				}
			
			return ("redirect:relogin.html");
			}
		catch (BindException e)
			{
			result.addAllErrors(e);
			return ("profile");
			}
		}
	}
