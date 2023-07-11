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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.theme.CookieThemeResolver;

/**
 * CookieThemeResolver that checks the requested theme against a list of theme names loaded from a ThemeEnumerator.
 * The first theme in the list is used as default theme, setDefaultThemeName has no effect.
 * 
 * @author Robert Wruck
 */
public class ValidatingCookieThemeResolver extends CookieThemeResolver
	{
	private final ThemeEnumerator enumerator;
	
	/**
	 * Constructor
	 * @param enumerator ThemeEnumerator
	 */
	public ValidatingCookieThemeResolver(ThemeEnumerator enumerator)
		{
		this.enumerator = enumerator;
		}
	
	@Override
	public String getDefaultThemeName()
		{
		return (enumerator.getThemeNames().isEmpty() ? super.getDefaultThemeName() : enumerator.getThemeNames().get(0));
		}
	
	@Override
	public String resolveThemeName(HttpServletRequest request)
		{
		final String theme = super.resolveThemeName(request);
		if (enumerator.getThemeNames().contains(theme))
			return (theme);
		return (getDefaultThemeName());
		}
	
	@Override
	public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName)
		{
		if (enumerator.getThemeNames().contains(themeName))
			super.setThemeName(request, response, themeName);
		}
	}
