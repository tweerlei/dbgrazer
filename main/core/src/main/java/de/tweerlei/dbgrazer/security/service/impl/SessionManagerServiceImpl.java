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
package de.tweerlei.dbgrazer.security.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.security.event.SessionCreationEvent;
import de.tweerlei.dbgrazer.security.event.SessionDestructionEvent;
import de.tweerlei.dbgrazer.security.model.SessionInfo;
import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.security.model.impl.SessionInfoImpl;
import de.tweerlei.dbgrazer.security.service.SessionManagerService;
import de.tweerlei.spring.service.TimeService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class SessionManagerServiceImpl implements SessionManagerService, ApplicationListener
	{
	private final TimeService timeService;
	private final Logger logger;
	private final ConcurrentMap<String, SessionInfoImpl> sessions;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 */
	@Autowired
	public SessionManagerServiceImpl(TimeService timeService)
		{
		this.timeService = timeService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.sessions = new ConcurrentHashMap<String, SessionInfoImpl>();
		}
	
	private boolean addSession(String sid)
		{
		logger.log(Level.INFO, "Session created: " + sid);
		
		final SessionInfoImpl current = sessions.putIfAbsent(sid, new SessionInfoImpl(NO_USER, timeService.getCurrentDate()));
		if (current != null)
			{
			logger.log(Level.WARNING, "New session already occupied: " + sid + ", " + current);
			return (false);
			}
		
		return (true);
		}
	
	private boolean removeSession(String sid)
		{
		final SessionInfoImpl current = sessions.remove(sid);
		if (current == null)
			{
			logger.log(Level.WARNING, "Destroyed nonexistent session: " + sid);
			return (false);
			}
		else
			{
			logger.log(Level.INFO, "Session destroyed: " + sid + ", " + current);
			return (true);
			}
		}
	
	private SessionInfoImpl getSession(String sid)
		{
		SessionInfoImpl existingSession = sessions.get(sid);
		if (existingSession == null)
			{
			// Should not happen...
			logger.log(Level.WARNING, "Session not found: " + sid);
			
			final SessionInfoImpl newSession = new SessionInfoImpl(NO_USER, timeService.getCurrentDate());
			existingSession = sessions.putIfAbsent(sid, newSession);
			
			if (existingSession == null)
				return (newSession);
			}
		
		return (existingSession);
		}
	
	@Override
	public boolean attach(String sid, User user)
		{
		logger.log(Level.INFO, "Login: " + sid + ", " + user.getLogin());
		
		final String previous = getSession(sid).replaceUsername(user.getLogin(), timeService.getCurrentDate());
		if (!previous.equals(NO_USER))
			{
			logger.log(Level.WARNING, "Replacing user for session: " + sid + ", " + previous + " / " + user.getLogin());
			return (false);
			}
		
		return (true);
		}
	
	@Override
	public boolean detach(String sid, User user)
		{
		logger.log(Level.INFO, "Logout: " + sid + ", " + user.getLogin());
		
		final String previous = getSession(sid).replaceUsername(NO_USER, timeService.getCurrentDate());
		if (!previous.equals(user.getLogin()))
			{
			logger.log(Level.WARNING, "User mismatch on logout: " + sid + ", " + user.getLogin() + " / " + previous);
			return (false);
			}
		
		return (true);
		}
	
	@Override
	public void logRequest(String sid, String request)
		{
		getSession(sid).setLastRequest(request, timeService.getCurrentDate());
		}
	
	@Override
	public List<SessionInfo> getAllSessions()
		{
		final List<SessionInfo> ret = new ArrayList<SessionInfo>();
		
		for (SessionInfoImpl si : sessions.values())
			ret.add(si.clone());
		
		return (ret);
		}
	
	@Override
	public void onApplicationEvent(ApplicationEvent e)
		{
		if (e instanceof SessionCreationEvent)
			{
			addSession(((SessionCreationEvent) e).getSessionId());
			}
		else if (e instanceof SessionDestructionEvent)
			{
			removeSession(((SessionDestructionEvent) e).getSessionId());
			}
		}
	}
