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
package de.tweerlei.spring.web.service;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.context.Theme;

/**
 * Manage request settings
 * 
 * @author Robert Wruck
 */
public interface RequestSettingsService
	{
	/**
	 * Get the locale for the given request
	 * @param request HttpServletRequest
	 * @return Locale
	 */
	public Locale getLocale(HttpServletRequest request);
	
	/**
	 * Get the theme name for the given request
	 * @param request HttpServletRequest
	 * @return Theme name
	 */
	public String getThemeName(HttpServletRequest request);
	
	/**
	 * Get the theme for the given request
	 * @param request HttpServletRequest
	 * @return Theme
	 */
	public Theme getTheme(HttpServletRequest request);
	
	/**
	 * Set the time zone for the given request
	 * @param request HttpServletRequest
	 * @return TimeZone
	 */
	public TimeZone getTimeZone(HttpServletRequest request);
	
	/**
	 * Set the locale for the given request/response
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param locale Locale
	 */
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale);
	
	/**
	 * Set the theme name for the given request/response
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param themeName Theme name
	 */
	public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName);
	
	/**
	 * Set the time zone for the given request/response
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param timeZone TimeZone
	 */
	public void setTimeZone(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone);
	}
