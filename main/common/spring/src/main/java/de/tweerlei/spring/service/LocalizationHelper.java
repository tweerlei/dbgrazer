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
package de.tweerlei.spring.service;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * Access localization objects (locales, time zones, character sets)
 * 
 * @author Robert Wruck
 */
public interface LocalizationHelper
	{
	/**
	 * Get the IDs of all supported character sets
	 * @return Character set IDs
	 */
	public Set<String> getSupportedCharsets();
	
	/**
	 * Get a character set by ID
	 * @param id Character set ID
	 * @return The matching character set or the platform default if none matched
	 */
	public Charset getCharset(String id);
	
	/**
	 * Get a character set ID
	 * @param charset Character set
	 * @return ID
	 */
	public String getCharsetID(Charset charset);
	
	/**
	 * Get the display name for a character set and a given locale
	 * @param id Character set ID
	 * @param locale Locale
	 * @return Display name
	 */
	public String getCharsetDisplayName(String id, Locale locale);
	
	/**
	 * Get the IDs of all supported locales
	 * @return Locale IDs
	 */
	public Set<String> getSupportedLocales();
	
	/**
	 * Get a locale set by ID
	 * @param id Locale ID
	 * @return The matching locale or the platform default if none matched
	 */
	public Locale getLocale(String id);
	
	/**
	 * Get a locale ID
	 * @param locale Locale
	 * @return ID
	 */
	public String getLocaleID(Locale locale);
	
	/**
	 * Get the display name for a locale and a given locale
	 * @param id Locale ID
	 * @param locale Locale
	 * @return Display name
	 */
	public String getLocaleDisplayName(String id, Locale locale);
	
	/**
	 * Get the IDs of all supported time zones
	 * @return Time zone IDs
	 */
	public Set<String> getSupportedTimeZones();
	
	/**
	 * Get a time zone by ID
	 * @param id Time zone ID
	 * @return The matching time zone or the platform default if none matched
	 */
	public TimeZone getTimeZone(String id);
	
	/**
	 * Get a time zone ID
	 * @param timeZone Time zone
	 * @return ID
	 */
	public String getTimeZoneID(TimeZone timeZone);
	
	/**
	 * Get the display name for a time zone and a given locale
	 * @param id Time zone ID
	 * @param locale Locale
	 * @return Display name
	 */
	public String getTimeZoneDisplayName(String id, Locale locale);
	}
