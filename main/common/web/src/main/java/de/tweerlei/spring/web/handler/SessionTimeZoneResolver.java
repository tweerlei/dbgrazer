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
package de.tweerlei.spring.web.handler;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.WebUtils;

/**
 * TimeZoneResolver that uses the current session.
 * Copied from Spring's SessionLocaleResolver.
 * 
 * @author Robert Wruck
 */
public class SessionTimeZoneResolver implements TimeZoneResolver
	{
	/**
	 * Session attribute name
	 */
	public static final String TIMEZONE_SESSION_ATTRIBUTE_NAME = SessionTimeZoneResolver.class.getName() + ".TIMEZONE";
	
	private TimeZone defaultTimeZone;
	
	/**
	 * Set the default time zone
	 * @param defaultTimeZone TimeZone
	 */
	public void setDefaultTimeZone(TimeZone defaultTimeZone)
		{
		this.defaultTimeZone = defaultTimeZone;
		}

	/**
	 * Get the default time zone
	 * @return TimeZone
	 */
	protected TimeZone getDefaultTimeZone()
		{
		return this.defaultTimeZone;
		}
	
	public TimeZone resolveTimeZone(HttpServletRequest request)
		{
		TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, TIMEZONE_SESSION_ATTRIBUTE_NAME);
		if (timeZone == null)
			timeZone = determineDefaultTimeZone(request);
		
		return timeZone;
		}
	
	public void setTimeZone(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone)
		{
		WebUtils.setSessionAttribute(request, TIMEZONE_SESSION_ATTRIBUTE_NAME, timeZone);
		}
	
	/**
	 * Determine the default time zone
	 * @param request HttpServletRequest
	 * @return TimeZone
	 */
	protected TimeZone determineDefaultTimeZone(HttpServletRequest request)
		{
		TimeZone tz = getDefaultTimeZone();
		if (tz == null)
			tz = TimeZone.getDefault();
		
		return tz;
		}
	}
