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
package de.tweerlei.spring.web.service.impl;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ThemeResolver;

import de.tweerlei.spring.web.handler.TimeZoneResolver;
import de.tweerlei.spring.web.service.RequestSettingsService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
//@Service("requestSettingsService")
public class RequestSettingsServiceImpl implements RequestSettingsService
	{
	private final LocaleResolver localeResolver;
	private final ThemeResolver themeResolver;
	private final ThemeSource themeSource;
	private final TimeZoneResolver timeZoneResolver;
	
	/**
	 * Constructor
	 * @param localeResolver LocaleResolver
	 * @param themeResolver ThemeResolver
	 * @param timeZoneResolver TimeZoneResolver
	 * @param themeSource ThemeSource
	 */
	@Autowired
	public RequestSettingsServiceImpl(LocaleResolver localeResolver, ThemeResolver themeResolver,
			TimeZoneResolver timeZoneResolver, ThemeSource themeSource)
		{
		this.localeResolver = localeResolver;
		this.themeResolver = themeResolver;
		this.themeSource = themeSource;
		this.timeZoneResolver = timeZoneResolver;
		}
	
	public Locale getLocale(HttpServletRequest request)
		{
		return (localeResolver.resolveLocale(request));
		}
	
	public String getThemeName(HttpServletRequest request)
		{
		return (themeResolver.resolveThemeName(request));
		}
	
	public Theme getTheme(HttpServletRequest request)
		{
		return (themeSource.getTheme(getThemeName(request)));
		}
	
	public TimeZone getTimeZone(HttpServletRequest request)
		{
		return (timeZoneResolver.resolveTimeZone(request));
		}
	
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale)
		{
		localeResolver.setLocale(request, response, locale);
		}
	
	public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName)
		{
		themeResolver.setThemeName(request, response, themeName);
		}
	
	public void setTimeZone(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone)
		{
		timeZoneResolver.setTimeZone(request, response, timeZone);
		}
	}
