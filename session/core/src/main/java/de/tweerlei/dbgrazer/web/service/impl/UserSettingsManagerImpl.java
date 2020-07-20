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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.context.Theme;

import de.tweerlei.common.math.Rational;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.web.service.UserSettingsManager;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.service.LocalizationHelper;

/**
 * Manage user settings
 * 
 * @author Robert Wruck
 */
@Service
public class UserSettingsManagerImpl implements UserSettingsManager
	{
	private final LocalizationHelper localizationHelper;
	private final ConfigAccessor configService;
	
	/**
	 * Constructor
	 * @param localizationHelper LocalizationHelper
	 * @param configService ConfigAccessor
	 */
	@Autowired
	public UserSettingsManagerImpl(LocalizationHelper localizationHelper,
			ConfigAccessor configService)
		{
		this.localizationHelper = localizationHelper;
		this.configService = configService;
		}
	
	@Override
	public String getBaseURI()
		{
		return (configService.get(ConfigKeys.BASE_URI));
		}
	
	@Override
	public boolean isLoginEnabled()
		{
		return (configService.get(ConfigKeys.ENABLE_LOGIN));
		}
	
	@Override
	public boolean isLoginRequired()
		{
		return (configService.get(ConfigKeys.REQUIRE_LOGIN));
		}
	
	@Override
	public Rational getMenuRatio()
		{
		return (configService.get(ConfigKeys.MENU_RATIO));
		}
	
	@Override
	public SortedSet<Integer> getAutorefreshIntervals()
		{
		return (configService.get(ConfigKeys.AUTOREFRESH_INTERVALS));
		}
	
	@Override
	public Set<String> getEffectiveUserGroups(User principal)
		{
		if (principal == null)
			return (Collections.emptySet());
		
		if (principal.isGranted(Authority.ROLE_LINKS))
			return (null);
		
		final Set<String> groups = new HashSet<String>();
		groups.add(LinkDef.LOGIN_GROUP);
		for (Map.Entry<String, Set<Authority>> ent : principal.getGroups().entrySet())
			{
			if (ent.getValue().contains(Authority.ROLE_LOGIN))
				groups.add(ent.getKey());
			}
		
		return (groups);
		}
	
	@Override
	public String getPreferredTheme(User principal)
		{
		if (principal == null)
			return (null);
		
		final String id = principal.getAttributes().get(Theme.class.getSimpleName());
		
		return (id);
		}
	
	@Override
	public Locale getPreferredLocale(User principal)
		{
		if (principal == null)
			return (null);
		
		final String id = principal.getAttributes().get(Locale.class.getSimpleName());
		if (id == null)
			return (null);
		
		return (localizationHelper.getLocale(id));
		}
	
	@Override
	public TimeZone getPreferredTimeZone(User principal)
		{
		if (principal == null)
			return (null);
		
		final String id = principal.getAttributes().get(TimeZone.class.getSimpleName());
		if (id == null)
			return (null);
		
		return (localizationHelper.getTimeZone(id));
		}
	}
