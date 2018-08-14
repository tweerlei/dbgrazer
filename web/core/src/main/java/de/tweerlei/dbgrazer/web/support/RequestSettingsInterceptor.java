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
package de.tweerlei.dbgrazer.web.support;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.context.Theme;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.tweerlei.dbgrazer.web.session.RequestSettings;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.config.ConfigProvider;
import de.tweerlei.spring.config.impl.ConfigProviderAccessor;
import de.tweerlei.spring.config.impl.MessageSourceConfigProvider;
import de.tweerlei.spring.service.SerializerFactory;
import de.tweerlei.spring.web.service.RequestSettingsService;

/**
 * Interceptor for preparing the RequestSettings
 * 
 * @author Robert Wruck
 */
public class RequestSettingsInterceptor extends HandlerInterceptorAdapter
	{
	private final SerializerFactory serializerFactory;
	private final RequestSettingsService requestSettingsManager;
	private final RequestSettings themeSettings;
	
	/**
	 * Constructor
	 * @param serializerFactory SerializerFactory
	 * @param requestSettingsManager RequestSettingsManager
	 * @param themeSettings RequestSettings
	 */
	@Autowired
	public RequestSettingsInterceptor(SerializerFactory serializerFactory,
			RequestSettingsService requestSettingsManager, RequestSettings themeSettings)
		{
		this.serializerFactory = serializerFactory;
		this.requestSettingsManager = requestSettingsManager;
		this.themeSettings = themeSettings;
		}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
		{
		final Locale locale = requestSettingsManager.getLocale(request);
		final Theme theme = requestSettingsManager.getTheme(request);
		final TimeZone timeZone = requestSettingsManager.getTimeZone(request);
		
		themeSettings.setLocale(locale);
		themeSettings.setThemeName(theme.getName());
		themeSettings.setTimeZone(timeZone);
		
		final ConfigProvider provider = new MessageSourceConfigProvider(theme.getMessageSource(), locale);
		final ConfigAccessor accessor = new ConfigProviderAccessor(provider, serializerFactory);
		themeSettings.setConfig(accessor);
		
		return (true);
		}
	}
