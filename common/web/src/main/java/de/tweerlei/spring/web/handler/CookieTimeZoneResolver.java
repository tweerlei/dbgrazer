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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.WebUtils;

/**
 * TimeZoneResolver that uses cookies.
 * Copied from Spring's CookieLocaleResolver.
 * 
 * @author Robert Wruck
 */
public class CookieTimeZoneResolver extends CookieGenerator implements TimeZoneResolver
	{
	/**
	 * Request attribute for the resolved time zone
	 */
	public static final String TIMEZONE_REQUEST_ATTRIBUTE_NAME = CookieTimeZoneResolver.class.getName() + ".TIMEZONE";
	
	/**
	 * The default cookie name used if none is explicitly set.
	 */
	public static final String DEFAULT_COOKIE_NAME = CookieTimeZoneResolver.class.getName() + ".TIMEZONE";
	
	private TimeZone defaultTimeZone;
	
	/**
	 * Constructor
	 */
	public CookieTimeZoneResolver()
		{
		setCookieName(DEFAULT_COOKIE_NAME);
		}
	
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
		// Check request for pre-parsed or preset locale.
		TimeZone timeZone = (TimeZone) request.getAttribute(TIMEZONE_REQUEST_ATTRIBUTE_NAME);
		if (timeZone != null)
			return timeZone;
		
		// Retrieve and parse cookie value.
		Cookie cookie = WebUtils.getCookie(request, getCookieName());
		if (cookie != null)
			{
			timeZone = TimeZone.getTimeZone(cookie.getValue());
			if (timeZone != null)
				{
				request.setAttribute(TIMEZONE_REQUEST_ATTRIBUTE_NAME, timeZone);
				return timeZone;
				}
			}
		
		return determineDefaultTimeZone(request);
		}
	
	public void setTimeZone(HttpServletRequest request, HttpServletResponse response, TimeZone timeZone)
		{
		if (timeZone != null)
			{
			// Set request attribute and add cookie.
			request.setAttribute(TIMEZONE_REQUEST_ATTRIBUTE_NAME, timeZone);
			addCookie(response, timeZone.getID());
			}
		else
			{
			// Set request attribute to fallback locale and remove cookie.
			request.setAttribute(TIMEZONE_REQUEST_ATTRIBUTE_NAME, determineDefaultTimeZone(request));
			removeCookie(response);
			}
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
