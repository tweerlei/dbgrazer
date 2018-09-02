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

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.service.SessionManagerService;
import de.tweerlei.dbgrazer.security.service.UserAuthenticatorService;
import de.tweerlei.dbgrazer.web.service.SecurityService;
import de.tweerlei.dbgrazer.web.session.ConnectionSettings;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class SecurityServiceImpl implements SecurityService
	{
	private static final String TIMEOUT_ATTRIBUTE = "de.tweerlei.dbgrazer.security.SessionTimeout";
	
	private final ConfigAccessor configService;
	private final UserAuthenticatorService authenticator;
	private final SessionManagerService sessionManagerService;
	private final UserSettings userSettings;
	private final ConnectionSettings connectionSettings;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param authenticator UserAuthenticatorService
	 * @param sessionManagerService SessionManagerService
	 * @param userSettings UserSettings
	 * @param connectionSettings ConnectionSettings
	 */
	@Autowired
	public SecurityServiceImpl(ConfigAccessor configService,
			UserAuthenticatorService authenticator, SessionManagerService sessionManagerService,
			UserSettings userSettings, ConnectionSettings connectionSettings)
		{
		this.configService = configService;
		this.authenticator = authenticator;
		this.sessionManagerService = sessionManagerService;
		this.userSettings = userSettings;
		this.connectionSettings = connectionSettings;
		}
	
	private void attach(HttpSession s, User user)
		{
		sessionManagerService.attach(s.getId(), user);
		
		if (s.getAttribute(TIMEOUT_ATTRIBUTE) == null)
			{
			// apply the session timeout if configured
			final Integer timeout = configService.get(ConfigKeys.SESSION_TIMEOUT);
			if (timeout != null)
				{
				// save original timeout
				s.setAttribute(TIMEOUT_ATTRIBUTE, s.getMaxInactiveInterval());
				
				s.setMaxInactiveInterval(timeout);
				}
			}
		}
	
	private void detach(HttpSession s, User user)
		{
		if (sessionManagerService.detach(s.getId(), user))
			{
			final Integer timeout = (Integer) s.getAttribute(TIMEOUT_ATTRIBUTE);
			if (timeout != null)
				{
				// restore the original timeout
				s.setMaxInactiveInterval(timeout);
				
				s.removeAttribute(TIMEOUT_ATTRIBUTE);
				}
			}
		}
	
	private void userLoggedIn(User principal)
		{
		userSettings.setPrincipal(principal);
		
		userSettings.setReloadEnabled(principal.isGranted(Authority.ROLE_RELOAD));
		// Start with editor deactivated
//		userSettings.setEditorActive(principal.isGranted(Authority.ROLE_EDIT) && configService.get(ConfigKeys.ENABLE_EDITOR));
		userSettings.setLinkEditorEnabled(principal.isGranted(Authority.ROLE_LINKS) && configService.get(ConfigKeys.ENABLE_LINKS));
		userSettings.setUserEditorEnabled(principal.isGranted(Authority.ROLE_USERS) && configService.get(ConfigKeys.ENABLE_USERS));
		userSettings.setConfigEditorEnabled(principal.isGranted(Authority.ROLE_RELOAD) && configService.get(ConfigKeys.ENABLE_CONFIG));
		userSettings.setSqlDisplayEnabled(configService.get(ConfigKeys.SHOW_SQL));
		userSettings.setDotDisplayEnabled(configService.get(ConfigKeys.SHOW_DOT));
		
		for (SchemaSettings s : userSettings.getSchemaSettings().values())
			s.clearUserObjects();
		}
	
	private void userLoggedOut()
		{
		userSettings.setPrincipal(null);
		
		userSettings.setReloadEnabled(false);
		userSettings.setEditorActive(false);
		userSettings.setLinkEditorEnabled(false);
		userSettings.setUserEditorEnabled(false);
		userSettings.setConfigEditorEnabled(false);
		userSettings.setSqlDisplayEnabled(false);
		userSettings.setDotDisplayEnabled(false);
		
		for (SchemaSettings s : userSettings.getSchemaSettings().values())
			s.clearUserObjects();
		}
	
	@Override
	public User login(String username, String password)
		{
		if (!authenticator.login(username, password))
			return (null);
		
		final User user = authenticator.loadUser(username);
		if ((user != null) && user.isGranted(Authority.ROLE_WS))
			return (user);
		
		return (null);
		}
	
	@Override
	public User login(HttpSession session, String username, String password)
		{
		if (!authenticator.login(username, password))
			return (null);
		
		final User user = authenticator.loadUser(username);
		if ((user != null) && user.isGranted(Authority.ROLE_LOGIN))
			{
			attach(session, user);
			userLoggedIn(user);
			return (user);
			}
		
		return (null);
		}
	
	@Override
	public User refreshLogin()
		{
		if (userSettings.getPrincipal() != null)
			{
			final User user = authenticator.loadUser(userSettings.getPrincipal().getLogin());
			if ((user != null) && user.isGranted(Authority.ROLE_LOGIN))
				{
				userLoggedIn(user);
				return (user);
				}
			}
		
		return (null);
		}
	
	@Override
	public void logout(HttpSession session, User user)
		{
		userLoggedOut();
		detach(session, user);
		}
	
	@Override
	public void initializeConnection(User user)
		{
		if (user != null)
			{
			connectionSettings.setEditorEnabled(user.isGranted(Authority.ROLE_EDIT, connectionSettings.getGroupName()) && configService.get(ConfigKeys.ENABLE_EDITOR));
			connectionSettings.setEditorActive(connectionSettings.isEditorEnabled() && userSettings.isEditorActive());
			connectionSettings.setSubmitEnabled(user.isGranted(Authority.ROLE_SUBMIT, connectionSettings.getGroupName()) && configService.get(ConfigKeys.ENABLE_SUBMIT));
			connectionSettings.setBrowserEnabled(user.isGranted(Authority.ROLE_BROWSE, connectionSettings.getGroupName()) && configService.get(ConfigKeys.ENABLE_BROWSER));
			connectionSettings.setDesignerEnabled(connectionSettings.isBrowserEnabled() && configService.get(ConfigKeys.ENABLE_DESIGNER));
			connectionSettings.setRecordEditorEnabled(user.isGranted(Authority.ROLE_DML, connectionSettings.getGroupName()) && configService.get(ConfigKeys.ENABLE_DML));
			connectionSettings.setWsApiEnabled(user.isGranted(Authority.ROLE_WS, connectionSettings.getGroupName()) && configService.get(ConfigKeys.ENABLE_WS));
			}
		}
	
	@Override
	public void cleanupConnection()
		{
		connectionSettings.setEditorEnabled(false);
		connectionSettings.setEditorActive(false);
		connectionSettings.setSubmitEnabled(false);
		connectionSettings.setBrowserEnabled(false);
		connectionSettings.setDesignerEnabled(false);
		connectionSettings.setRecordEditorEnabled(false);
		connectionSettings.setWsApiEnabled(false);
		}
	}
