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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.tweerlei.dbgrazer.web.constant.ViewConstants;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.dbgrazer.web.session.RequestSettings;
import de.tweerlei.spring.service.LocalizationHelper;
import de.tweerlei.spring.web.handler.ThemeEnumerator;
import de.tweerlei.spring.web.service.RequestSettingsService;
import de.tweerlei.spring.web.view.GenericDownloadView;
import de.tweerlei.spring.web.view.JsonDownloadSource;

/**
 * Poor man's security framework
 * 
 * @author Robert Wruck
 */
@Controller
public class UserSettingsController
	{
	private final LocalizationHelper localizationHelper;
	private final ThemeEnumerator themeEnumerator;
	private final FrontendHelperService frontendHelper;
	private final UserSettingsManager userSettingsManager;
	private final RequestSettingsService requestSettingsManager;
	private final RequestSettings themeSettings;
	
	/**
	 * Constructor
	 * @param localizationHelper LocalizationHelper
	 * @param themeEnumerator ThemeEnumerator
	 * @param requestSettingsManager RequestSettingsManager
	 * @param frontendHelper FrontendHelperService
	 * @param userSettingsManager UserSettingsManager
	 * @param themeSettings ThemeSettings
	 */
	@Autowired
	public UserSettingsController(LocalizationHelper localizationHelper, ThemeEnumerator themeEnumerator,
			RequestSettingsService requestSettingsManager,
			FrontendHelperService frontendHelper, UserSettingsManager userSettingsManager,
			RequestSettings themeSettings)
		{
		this.localizationHelper = localizationHelper;
		this.themeEnumerator = themeEnumerator;
		this.requestSettingsManager = requestSettingsManager;
		this.frontendHelper = frontendHelper;
		this.userSettingsManager = userSettingsManager;
		this.themeSettings = themeSettings;
		}
	
	/**
	 * Set the preferred graph type
	 * @return View
	 */
	@RequestMapping(value = "/ajax/themes.html", method = RequestMethod.GET)
	public Map<String, Object> showThemeMenu()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final List<String> themes = themeEnumerator.getThemeNames();
		
		model.put("themes", themes);
		model.put("theme", themeSettings.getThemeName());
		
		return (model);
		}
	
	/**
	 * Set the preferred graph type
	 * @param selected Selected value
	 * @param target Target element ID
	 * @return View
	 */
	@RequestMapping(value = "/ajax/select-theme.html", method = RequestMethod.GET)
	public Map<String, Object> showThemes(
			@RequestParam("q") String selected,
			@RequestParam("id") String target
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final List<String> themes = themeEnumerator.getThemeNames();
		
		model.put("value", selected);
		model.put("target", target);
		model.put("themes", themes);
		
		return (model);
		}
	
	/**
	 * Set the preferred graph type
	 * @param locale Request locale
	 * @return View
	 */
	@RequestMapping(value = "/ajax/locales.html", method = RequestMethod.GET)
	public Map<String, Object> showLocaleMenu(Locale locale)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final String selected = localizationHelper.getLocaleID(locale);
		final SortedMap<String, String> locales = getLocales(selected, locale);
		
		model.put("locales", locales);
		model.put("locale", selected);
		
		return (model);
		}
	
	/**
	 * Set the preferred graph type
	 * @param selected Selected value
	 * @param target Target element ID
	 * @param locale Request locale
	 * @return View
	 */
	@RequestMapping(value = "/ajax/select-locale.html", method = RequestMethod.GET)
	public Map<String, Object> showLocales(
			@RequestParam("q") String selected,
			@RequestParam("id") String target,
			Locale locale
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SortedMap<String, String> locales = getLocales(selected, locale);
		
		model.put("value", selected);
		model.put("target", target);
		model.put("locales", locales);
		
		return (model);
		}
	
	private SortedMap<String, String> getLocales(String selected, Locale locale)
		{
		final SortedMap<String, String> locales = new TreeMap<String, String>();
		
		final Set<String> all = localizationHelper.getSupportedLocales();
		for (String id : all)
			locales.put(localizationHelper.getLocaleDisplayName(id, locale), id);
		// Since multiple IDs can map to the same display name, make sure that the selected ID is in the map
		if (all.contains(selected))
			locales.put(localizationHelper.getLocaleDisplayName(selected, locale), selected);
		
		return (locales);
		}
	
	/**
	 * Set the preferred graph type
	 * @param locale Request locale
	 * @return View
	 */
	@RequestMapping(value = "/ajax/timezones.html", method = RequestMethod.GET)
	public Map<String, Object> showTimeZoneMenu(Locale locale)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final String selected = localizationHelper.getTimeZoneID(themeSettings.getTimeZone());
		final SortedMap<String, String> timezones = getTimeZones(selected, locale);
		
		model.put("timezones", timezones);
		model.put("timezone", selected);
		
		return (model);
		}
	
	/**
	 * Set the preferred graph type
	 * @param selected Selected value
	 * @param target Target element ID
	 * @param locale Request locale
	 * @return View
	 */
	@RequestMapping(value = "/ajax/select-timezone.html", method = RequestMethod.GET)
	public Map<String, Object> showTimeZones(
			@RequestParam("q") String selected,
			@RequestParam("id") String target,
			Locale locale)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final SortedMap<String, String> timezones = getTimeZones(selected, locale);
		
		model.put("value", selected);
		model.put("target", target);
		model.put("timezones", timezones);
		
		return (model);
		}
	
	private SortedMap<String, String> getTimeZones(String selected, Locale locale)
		{
		final SortedMap<String, String> timezones = new TreeMap<String, String>();
		
		final Set<String> all = localizationHelper.getSupportedTimeZones();
		for (String id : all)
			timezones.put(localizationHelper.getTimeZoneDisplayName(id, locale), id);
		// Since multiple IDs can map to the same display name, make sure that the selected ID is in the map
		if (all.contains(selected))
			timezones.put(localizationHelper.getTimeZoneDisplayName(selected, locale), selected);
		
		return (timezones);
		}
	
	/**
	 * Change the user's time zone
	 * @param request Request
	 * @param response Response
	 * @param id TimeZone ID
	 * @return View
	 */
	@RequestMapping(value = "/ajax/theme.html", method = RequestMethod.GET)
	public String changeTheme(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("q") String id
			)
		{
		requestSettingsManager.setThemeName(request, response, id);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Change the user's time zone
	 * @param request Request
	 * @param response Response
	 * @param id TimeZone ID
	 * @return View
	 */
	@RequestMapping(value = "/ajax/locale.html", method = RequestMethod.GET)
	public String changeLocale(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("q") String id
			)
		{
		final Locale loc = localizationHelper.getLocale(id);
		if (loc != null)
			requestSettingsManager.setLocale(request, response, loc);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Change the user's time zone
	 * @param request Request
	 * @param response Response
	 * @param id TimeZone ID
	 * @return View
	 */
	@RequestMapping(value = "/ajax/timezone.html", method = RequestMethod.GET)
	public String changeTimeZone(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("q") String id
			)
		{
		final TimeZone tz = localizationHelper.getTimeZone(id);
		if (tz != null)
			requestSettingsManager.setTimeZone(request, response, tz);
		
		return (ViewConstants.EMPTY_VIEW);
		}
	
	/**
	 * Get the menu rows per column
	 * @param items Item count
	 * @return View
	 */
	@RequestMapping(value = "/ajax/menurows.html", method = RequestMethod.GET)
	public Map<String, Object> getMenuRows(
			@RequestParam("q") Integer items
			)
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		final int n = frontendHelper.getMenuRows(items, userSettingsManager.getMenuRatio());
		
		model.put(GenericDownloadView.SOURCE_ATTRIBUTE, new JsonDownloadSource(n));
		
		return (model);
		}
	
	/**
	 * Set the preferred chart type
	 * @return View
	 */
	@RequestMapping(value = "/ajax/autorefreshs.html", method = RequestMethod.GET)
	public Map<String, Object> showAutoRefreshIntervalMenu()
		{
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("intervals", userSettingsManager.getAutorefreshIntervals());
		
		return (model);
		}
	}
