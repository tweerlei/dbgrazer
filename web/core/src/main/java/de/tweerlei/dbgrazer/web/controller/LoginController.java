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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;
import de.tweerlei.dbgrazer.web.service.SecurityService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.web.service.RequestSettingsService;

/**
 * Poor man's security framework
 * 
 * @author Robert Wruck
 */
@Controller
public class LoginController
	{
	private final SecurityService securityService;
	private final LinkService linkService;
	private final FrontendHelperService frontendHelper;
	private final FrontendNotificationService frontendNotificationService;
	private final RequestSettingsService requestSettingsManager;
	private final UserSettingsManager userSettingsManager;
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param securityService SecurityService
	 * @param linkService LinkService
	 * @param frontendHelper FrontendHelperService
	 * @param frontendNotificationService FrontendNotificationService
	 * @param requestSettingsManager RequestSettingsManager
	 * @param userSettingsManager UserSettingsManager
	 * @param userSettings UserSettings
	 */
	@Autowired
	public LoginController(SecurityService securityService, LinkService linkService,
			FrontendHelperService frontendHelper, FrontendNotificationService frontendNotificationService,
			RequestSettingsService requestSettingsManager, UserSettingsManager userSettingsManager,
			UserSettings userSettings)
		{
		this.securityService = securityService;
		this.linkService = linkService;
		this.frontendHelper = frontendHelper;
		this.frontendNotificationService = frontendNotificationService;
		this.requestSettingsManager = requestSettingsManager;
		this.userSettingsManager = userSettingsManager;
		this.userSettings = userSettings;
		}
	
	/**
	 * Show a parameter input form
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/login.html", method = RequestMethod.GET)
	public Map<String, Object> showLoginForm()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		return (model);
		}
	
	/**
	 * Show a parameter input form
	 * @param username Previous user name
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/relogin.html", method = RequestMethod.GET)
	public Map<String, Object> showReloginForm(
			@RequestParam("q") String username
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("username", username);
		
		return (model);
		}
	
	/**
	 * Log in
	 * @param request Request
	 * @param response Response
	 * @param session The current session
	 * @param username User name
	 * @param password Password
	 * @return Model
	 */
	@RequestMapping(value = "/login.html", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam("username") String username,
			@RequestParam("password") String password
			)
		{
		String redirectTo = null;
		
		if ((userSettings.getPrincipal() == null) && userSettingsManager.isLoginEnabled())
			{
			final User principal = securityService.login(session, username, password);
			if (principal != null)
				{
				final Locale loc = userSettingsManager.getPreferredLocale(principal);
				if (loc != null)
					requestSettingsManager.setLocale(request, response, loc);
				
				final String theme = userSettingsManager.getPreferredTheme(principal);
				if (theme != null)
					requestSettingsManager.setThemeName(request, response, theme);
				
				final TimeZone tz = userSettingsManager.getPreferredTimeZone(principal);
				if (tz != null)
					requestSettingsManager.setTimeZone(request, response, tz);
				
				final Map<String, String> all = linkService.findAllLinkNames(userSettingsManager.getEffectiveUserGroups(userSettings.getPrincipal()), null, null);
				if (all.size() == 1)
					redirectTo = all.values().iterator().next();
				}
			else
				frontendNotificationService.logError(MessageKeys.LOGIN_FAILED);
			}
		
		final PathInfo pi = userSettings.getSourceURL();
		if ((redirectTo != null) && ((pi == null) || (pi.getCategory() == null)))
			return (frontendHelper.buildPath("redirect:" + MessageKeys.PATH_DB, redirectTo, "index.html", null));
		
		if (pi != null)
			return ("redirect:" + frontendHelper.buildPath(pi));
		
		return ("redirect:index.html");
		}
	
	/**
	 * Log in
	 * @param request Request
	 * @param response Response
	 * @param session The current session
	 * @param username User name
	 * @param password Password
	 * @return Model
	 */
	@RequestMapping(value = "/ajax/login.html", method = RequestMethod.POST)
	public String relogin(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam("username") String username,
			@RequestParam("password") String password
			)
		{
		if ((userSettings.getPrincipal() == null) && userSettingsManager.isLoginEnabled())
			{
			final User principal = securityService.login(session, username, password);
			if (principal != null)
				{
				final Locale loc = userSettingsManager.getPreferredLocale(principal);
				if (loc != null)
					requestSettingsManager.setLocale(request, response, loc);
				
				final String theme = userSettingsManager.getPreferredTheme(principal);
				if (theme != null)
					requestSettingsManager.setThemeName(request, response, theme);
				
				final TimeZone tz = userSettingsManager.getPreferredTimeZone(principal);
				if (tz != null)
					requestSettingsManager.setTimeZone(request, response, tz);
				}
			}
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Refresh log in data
	 * @param request Request
	 * @param response Response
	 * @return Model
	 */
	@RequestMapping(value = "/relogin.html")
	public String login(HttpServletRequest request, HttpServletResponse response)
		{
		final User principal = securityService.refreshLogin();
		if (principal != null)
			{
			final Locale loc = userSettingsManager.getPreferredLocale(principal);
			if (loc != null)
				requestSettingsManager.setLocale(request, response, loc);
			
			final String theme = userSettingsManager.getPreferredTheme(principal);
			if (theme != null)
				requestSettingsManager.setThemeName(request, response, theme);
			
			final TimeZone tz = userSettingsManager.getPreferredTimeZone(principal);
			if (tz != null)
				requestSettingsManager.setTimeZone(request, response, tz);
			}
		
		final PathInfo pi = userSettings.getSourceURL();
		if (pi != null)
			return ("redirect:" + frontendHelper.buildPath(pi));
		
		return ("redirect:index.html");
		}
	
	/**
	 * Show a parameter input form
	 * @param session The current session
	 * @return Model
	 */
	@RequestMapping(value = "/logout.html", method = RequestMethod.GET)
	public String logout(HttpSession session)
		{
		if (userSettings.getPrincipal() != null)
			securityService.logout(session, userSettings.getPrincipal());
		
		final PathInfo pi = userSettings.getSourceURL();
		if (pi != null)
			return ("redirect:" + frontendHelper.buildPath(pi));
		
		return ("redirect:index.html");
		}
	
	/**
	 * Show a parameter input form
	 * @param session The current session
	 * @return Model
	 */
	@RequestMapping(value = "/logoff.html", method = RequestMethod.GET)
	public String logoff(HttpSession session)
		{
		if (userSettings.getPrincipal() != null)
			securityService.logout(session, userSettings.getPrincipal());
		
		session.invalidate();
		
		final PathInfo pi = userSettings.getSourceURL();
		if (pi != null)
			return ("redirect:" + frontendHelper.buildPath(pi));
		
		return ("redirect:index.html");
		}
	}
