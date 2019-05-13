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
package de.tweerlei.dbgrazer.web.controller.useredit;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.UserErrorKeys;
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
public class UserEditController
	{
	private static final class AutofillTreeMap extends TreeMap<String, Map<Authority, Boolean>>
		{
		public AutofillTreeMap()
			{
			}
		
		@Override
		public Map<Authority, Boolean> get(Object key)
			{
			Map<Authority, Boolean> ret = super.get(key);
			if (ret == null)
				{
				ret = new EnumMap<Authority, Boolean>(Authority.class);
				put(key.toString(), ret);
				}
			return (ret);
			}
		}
	
	/**
	 * Helper class used as form backing object
	 */
	public static final class FormBackingObject
		{
		private String originalLogin;
		private String login;
		private String name;
		private String originalPassword;
		private String password;
		private boolean noPassword;
		private String localeName;
		private String timeZoneName;
		private final Map<Authority, Boolean> authorities;
		private final AutofillTreeMap groups;
		private final Map<String, String> attributes;
		
		/**
		 * Constructor
		 */
		public FormBackingObject()
			{
			this.authorities = new EnumMap<Authority, Boolean>(Authority.class);
			this.groups = new AutofillTreeMap();
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
		 * Get the originalPassword
		 * @return the originalPassword
		 */
		public String getOriginalPassword()
			{
			return originalPassword;
			}

		/**
		 * Set the originalPassword
		 * @param originalPassword the originalPassword to set
		 */
		public void setOriginalPassword(String originalPassword)
			{
			this.originalPassword = originalPassword;
			}

		/**
		 * Get the noPassword
		 * @return the noPassword
		 */
		public boolean isNoPassword()
			{
			return noPassword;
			}

		/**
		 * Set the noPassword
		 * @param noPassword the noPassword to set
		 */
		public void setNoPassword(boolean noPassword)
			{
			this.noPassword = noPassword;
			}

		/**
		 * Get the authorities
		 * @return the authorities
		 */
		public Map<Authority, Boolean> getAuthorities()
			{
			return authorities;
			}

		/**
		 * Get the login
		 * @return the login
		 */
		public String getLogin()
			{
			return login;
			}

		/**
		 * Set the login
		 * @param login the login to set
		 */
		public void setLogin(String login)
			{
			this.login = login;
			}

		/**
		 * Get the originalLogin
		 * @return the originalLogin
		 */
		public String getOriginalLogin()
			{
			return originalLogin;
			}

		/**
		 * Set the originalLogin
		 * @param originalLogin the originalLogin to set
		 */
		public void setOriginalLogin(String originalLogin)
			{
			this.originalLogin = originalLogin;
			}

		/**
		 * Get the groups
		 * @return the groups
		 */
		public Map<String, Map<Authority, Boolean>> getGroups()
			{
			return groups;
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
	private final LinkService linkService;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param localizationHelper LocalizationHelper
	 * @param userManagerService UserManagerService
	 * @param linkService LinkService
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 */
	@Autowired
	public UserEditController(LocalizationHelper localizationHelper,
			UserManagerService userManagerService, LinkService linkService,
			UserSettingsManager userSettingsManager, UserSettings userSettings)
		{
		this.localizationHelper = localizationHelper;
		this.userManagerService = userManagerService;
		this.linkService = linkService;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		}
	
	/**
	 * Get the FormBackingObject
	 * @param user User name
	 * @param template Template user name
	 * @param locale Request locale
	 * @return FormBackingObject
	 */
	@ModelAttribute("model")
	public FormBackingObject getFormBackingObject(
			@RequestParam(value = "q", required = false) String user,
			@RequestParam(value = "template", required = false) String template,
			Locale locale
			)
		{
		if (!userSettings.isUserEditorEnabled())
			throw new AccessDeniedException();
		
		final FormBackingObject ret = new FormBackingObject();
		
		final boolean creating = StringUtils.empty(user);
		
		final String queryName = creating ? template : user;
		
		if (queryName != null)
			{
			final User u = userManagerService.findUserByName(queryName);
			if (u == null)
				throw new AccessDeniedException();
			
			ret.setName(creating ? "" : u.getName());
			ret.setLogin(creating ? "" : u.getLogin());
			ret.setOriginalLogin(creating ? "" : u.getLogin());
			ret.setPassword("");
			ret.setOriginalPassword(u.getPassword());
			ret.setNoPassword(StringUtils.empty(u.getPassword()));
			
			for (Authority a : u.getGrantedAuthorities())
				ret.getAuthorities().put(a, Boolean.TRUE);
			
			for (Map.Entry<String, Set<Authority>> ent : u.getGroups().entrySet())
				{
				final Map<Authority, Boolean> m = ret.getGroups().get(ent.getKey());
				
				for (Authority a : ent.getValue())
					m.put(a, Boolean.TRUE);
				}
			
			ret.getAttributes().putAll(u.getAttributes());
			
			final Locale loc = userSettingsManager.getPreferredLocale(u);
			if (loc != null)
				ret.setLocaleName(localizationHelper.getLocaleDisplayName(loc.toString(), locale));
			
			final TimeZone tz = userSettingsManager.getPreferredTimeZone(u);
			if (tz != null)
				ret.setTimeZoneName(localizationHelper.getTimeZoneDisplayName(tz.getID(), locale));
			}
		else
			{
			ret.setName("");
			ret.setLogin("");
			ret.setOriginalLogin("");
			ret.setPassword("");
			ret.setOriginalPassword("");
			ret.setNoPassword(false);
			}
		
		return (ret);
		}
	
	/**
	 * Get all possible parameter value lists
	 * @return Queries
	 */
	@ModelAttribute("authorities")
	public Set<Authority> getAuthorities()
		{
		return (EnumSet.allOf(Authority.class));
		}
	
	/**
	 * Get all possible parameter value lists
	 * @return Queries
	 */
	@ModelAttribute("groups")
	public Set<String> getGroups()
		{
		return (linkService.findAllLinkGroups());
		}
	
	/**
	 * Show a parameter input form
	 * @return Model
	 */
	@RequestMapping(value = "/user.html", method = RequestMethod.GET)
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
	@RequestMapping(value = "/user.html", method = RequestMethod.POST)
	public String updateUser(@ModelAttribute("model") FormBackingObject fbo, BindingResult result)
		{
		final boolean creating = StringUtils.empty(fbo.getOriginalLogin());
		if (creating)
			{
			if (!fbo.isNoPassword() && StringUtils.empty(fbo.getPassword()))
				{
				result.reject(UserErrorKeys.EMPTY_PASSWORD);
				return ("user");
				}
			
			final User u = userManagerService.findUserByName(fbo.getLogin());
			if (u != null)
				{
				fbo.setLogin(u.getLogin());
				result.reject(ErrorKeys.EXISTS);
				return ("user");
				}
			}
		else
			{
			final User qOld = userManagerService.findUserByName(fbo.getOriginalLogin());
			final User qNew = userManagerService.findUserByName(fbo.getLogin());
			if ((qNew != null) && (!qNew.getLogin().equals(qOld.getLogin())))
				{
				fbo.setLogin(qNew.getLogin());
				result.reject(ErrorKeys.EXISTS);
				return ("user");
				}
			}
		
		final String password;
		if (fbo.isNoPassword())
			password = "";
		else if (StringUtils.empty(fbo.getPassword()))
			password = fbo.getOriginalPassword();
		else
			password = fbo.getPassword();
		
		final Set<Authority> authorities = EnumSet.noneOf(Authority.class);
		for (Map.Entry<Authority, Boolean> ent : fbo.getAuthorities().entrySet())
			{
			if ((ent.getValue() != null) && ent.getValue())
				authorities.add(ent.getKey());
			}
		
		final Map<String, Set<Authority>> groups = new TreeMap<String, Set<Authority>>();
		for (Map.Entry<String, Map<Authority, Boolean>> ent : fbo.getGroups().entrySet())
			{
			final Set<Authority> auth = EnumSet.noneOf(Authority.class);
			for (Map.Entry<Authority, Boolean> ent2 : ent.getValue().entrySet())
				{
				if ((ent2.getValue() != null) && ent2.getValue())
					auth.add(ent2.getKey());
				}
			if (!auth.isEmpty())
				groups.put(ent.getKey(), auth);
			}
		
		final Map<String, String> attributes = new TreeMap<String, String>();
		for (Map.Entry<String, String> ent : fbo.getAttributes().entrySet())
			{
			if (!StringUtils.empty(ent.getKey()))
				attributes.put(ent.getKey(), StringUtils.notNull(ent.getValue()));
			}
		
		final User u = new UserImpl(fbo.getLogin(), fbo.getName(), password, authorities, groups, attributes);
		
		try	{
			final String name;
			if (creating)
				{
				name = userManagerService.createUser(userSettings.getPrincipal().getLogin(), u);
				if (name == null)
					{
					result.reject(ErrorKeys.WRITE_FAILED);
					return ("user");
					}
				}
			else
				{
				name = userManagerService.updateUser(userSettings.getPrincipal().getLogin(), fbo.getOriginalLogin(), u);
				if (name == null)
					{
					result.reject(ErrorKeys.WRITE_FAILED);
					return ("user");
					}
				}
			
			return ("redirect:users.html");
			}
		catch (BindException e)
			{
			result.addAllErrors(e);
			return ("user");
			}
		}
	}
