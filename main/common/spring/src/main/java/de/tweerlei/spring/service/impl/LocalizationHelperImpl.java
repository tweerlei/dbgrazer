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
package de.tweerlei.spring.service.impl;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import de.tweerlei.spring.service.LocalizationHelper;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class LocalizationHelperImpl implements LocalizationHelper
	{
	private final Map<String, Charset> charsets;
	private final Map<String, Locale> locales;
	private final Map<String, TimeZone> timezones;
	
	/**
	 * Constructor
	 */
	public LocalizationHelperImpl()
		{
		final Map<String, Charset> cmap = new HashMap<String, Charset>(Charset.availableCharsets());
		charsets = Collections.unmodifiableMap(cmap);
		
		final Map<String, Locale> lmap = new HashMap<String, Locale>();
		for (Locale l : Locale.getAvailableLocales())
			lmap.put(l.toString(), l);
		locales = Collections.unmodifiableMap(lmap);
		
		final Map<String, TimeZone> tmap = new HashMap<String, TimeZone>();
		for (String id : TimeZone.getAvailableIDs())
			tmap.put(id, TimeZone.getTimeZone(id));
		timezones = Collections.unmodifiableMap(tmap);
		}
	
	public Set<String> getSupportedCharsets()
		{
		return (charsets.keySet());
		}
	
	public Charset getCharset(String id)
		{
		final Charset cs = charsets.get(id);
		if (cs == null)
			return (Charset.defaultCharset());
		return (cs);
		}
	
	public String getCharsetID(Charset charset)
		{
		return (charset.name());
		}
	
	public String getCharsetDisplayName(String id, Locale locale)
		{
		final Charset cs = getCharset(id);
		return (cs.displayName(locale));
		}
	
	public Set<String> getSupportedLocales()
		{
		return (locales.keySet());
		}
	
	public Locale getLocale(String id)
		{
		final Locale loc = locales.get(id);
		if (loc == null)
			return (Locale.getDefault());
		return (loc);
		}
	
	public String getLocaleID(Locale locale)
		{
		return (locale.toString());
		}
	
	public String getLocaleDisplayName(String id, Locale locale)
		{
		final Locale loc = getLocale(id);
		return (loc.getDisplayName(locale));
		}
	
	public Set<String> getSupportedTimeZones()
		{
		return (timezones.keySet());
		}
	
	public TimeZone getTimeZone(String id)
		{
		final TimeZone tz = timezones.get(id);
		if (tz == null)
			return (TimeZone.getDefault());
		return (tz);
		}
	
	public String getTimeZoneID(TimeZone timeZone)
		{
		return (timeZone.getID());
		}
	
	public String getTimeZoneDisplayName(String id, Locale locale)
		{
		final TimeZone tz = getTimeZone(id);
		final StringBuilder sb = new StringBuilder();
		sb.append(tz.getDisplayName(false, TimeZone.LONG, locale));
		sb.append(" (");
		sb.append(tz.getDisplayName(false, TimeZone.SHORT, locale));
		sb.append(")");
		return (sb.toString());
		}
	}
