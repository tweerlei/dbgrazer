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
package de.tweerlei.dbgrazer.web.service;

import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

import de.tweerlei.common.math.Rational;
import de.tweerlei.dbgrazer.security.model.User;

/**
 * Manage user settings
 * 
 * @author Robert Wruck
 */
public interface UserSettingsManager
	{
	/**
	 * Get the base URI
	 * @return base URI
	 */
	public String getBaseURI();
	
	/**
	 * Check whether logins are enabled
	 * @return true if enabled
	 */
	public boolean isLoginEnabled();
	
	/**
	 * Check whether logins are required
	 * @return true if required
	 */
	public boolean isLoginRequired();
	
	/**
	 * Get the menu aspect ratio
	 * @return menu aspect ratio
	 */
	public Rational getMenuRatio();
	
	/**
	 * Get the selectable autorefresh intervals
	 * @return selectable autorefresh intervals
	 */
	public SortedSet<Integer> getAutorefreshIntervals();
	
	/**
	 * Get the effective user groups
	 * @param principal User
	 * @return User groups
	 */
	public Set<String> getEffectiveUserGroups(User principal);
	
	/**
	 * Get a user's preferred locale
	 * @param principal User
	 * @return Locale or null
	 */
	public Locale getPreferredLocale(User principal);
	
	/**
	 * Get a user's preferred time zone
	 * @param principal User
	 * @return TimeZone
	 */
	public TimeZone getPreferredTimeZone(User principal);
	
	/**
	 * Get a user's preferred theme
	 * @param principal User
	 * @return Theme name or null
	 */
	public String getPreferredTheme(User principal);
	}
