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
package de.tweerlei.dbgrazer.security.service;

import java.util.List;

import de.tweerlei.dbgrazer.security.model.SessionInfo;
import de.tweerlei.dbgrazer.security.model.User;

/**
 * Perform session management
 * 
 * @author Robert Wruck
 */
public interface SessionManagerService
	{
	/**
	 * The login denoting "no current user"
	 */
	public static final String NO_USER = "";
	
	/**
	 * Attach user details to a session
	 * @param sessionId The session ID
	 * @param user The current user
	 * @return false if the session was previously associated with another user
	 */
	public boolean attach(String sessionId, User user);
	
	/**
	 * Detach user details from a session
	 * @param sessionId The session ID
	 * @param user The current user
	 * @return true if the user was detached, false if the session belongs to another user
	 */
	public boolean detach(String sessionId, User user);
	
	/**
	 * Log a request
	 * @param sessionId The session ID
	 * @param request The reuqest string
	 */
	public void logRequest(String sessionId, String request);
	
	/**
	 * Get all open sessions
	 * @return Sessions
	 */
	public List<SessionInfo> getAllSessions();
	}
