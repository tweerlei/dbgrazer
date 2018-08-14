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
package de.tweerlei.dbgrazer.web.session.impl;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.session.RequestSettings;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Implementation that uses the current Spring Theme
 * 
 * @author Robert Wruck
 */
@Service
@Scope("request")
public class RequestSettingsImpl implements RequestSettings
	{
	private ConfigAccessor accessor;
	private Locale locale;
	private String themeName;
	private TimeZone timeZone;
	
	@Override
	public void setConfig(ConfigAccessor accessor)
		{
		this.accessor = accessor;
		}
	
	@Override
	public ConfigAccessor getConfig()
		{
		return (accessor);
		}
	
	@Override
	public void setLocale(Locale locale)
		{
		this.locale = locale;
		}
	
	@Override
	public Locale getLocale()
		{
		return (locale);
		}
	
	@Override
	public String getLocaleName()
		{
		return (locale.toString());
		}
	
	@Override
	public void setThemeName(String themeName)
		{
		this.themeName = themeName;
		}
	
	@Override
	public String getThemeName()
		{
		return (themeName);
		}
	
	@Override
	public void setTimeZone(TimeZone timeZone)
		{
		this.timeZone = timeZone;
		}
	
	@Override
	public TimeZone getTimeZone()
		{
		return (timeZone);
		}
	
	@Override
	public String getTimeZoneName()
		{
		return (timeZone.getDisplayName(false, TimeZone.SHORT, getLocale()));
		}
	}
