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
package de.tweerlei.dbgrazer.web.session;

import java.util.Locale;
import java.util.TimeZone;

import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Appearance settings for the current request.
 * Unfortunately, Spring doesn't support TimeZone and Theme as standard handler method parameters.
 * 
 * @author Robert Wruck
 */
public interface RequestSettings
	{
	/**
	 * Set a configuration accessor for the theme settings
	 * @param accessor ConfigAccessor
	 */
	public void setConfig(ConfigAccessor accessor);
	
	/**
	 * Get a configuration accessor for the theme settings
	 * @return ConfigAccessor
	 */
	public ConfigAccessor getConfig();
	
	/**
	 * Set the current locale
	 * @param locale Locale
	 */
	public void setLocale(Locale locale);
	
	/**
	 * Get the current locale
	 * @return Locale
	 */
	public Locale getLocale();
	
	/**
	 * Get a user displayable name for the locale
	 * @return Name
	 */
	public String getLocaleName();
	
	/**
	 * Set the current theme name
	 * @param themeName Theme name
	 */
	public void setThemeName(String themeName);
	
	/**
	 * Get a user displayable name for the theme
	 * @return Name
	 */
	public String getThemeName();
	
	/**
	 * Set the current time zone
	 * @param timeZone TimeZone
	 */
	public void setTimeZone(TimeZone timeZone);
	
	/**
	 * Get the current time zone
	 * @return TimeZone
	 */
	public TimeZone getTimeZone();
	
	/**
	 * Get a user displayable name for the time zone
	 * @return Name
	 */
	public String getTimeZoneName();
	}
