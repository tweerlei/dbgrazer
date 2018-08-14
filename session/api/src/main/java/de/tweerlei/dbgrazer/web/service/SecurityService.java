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
package de.tweerlei.dbgrazer.web.service;

import javax.servlet.http.HttpSession;

import de.tweerlei.dbgrazer.security.model.User;

/**
 * Perform session management
 * 
 * @author Robert Wruck
 */
public interface SecurityService
	{
	/**
	 * Log in for a single request
	 * @param username Username
	 * @param password Password
	 * @return User object or null if login failed
	 */
	public User login(String username, String password);
	
	/**
	 * Log in
	 * @param session The current HttpSession
	 * @param username Username
	 * @param password Password
	 * @return User object or null if login failed
	 */
	public User login(HttpSession session, String username, String password);
	
	/**
	 * Refresh session data after a logged in user was changed
	 * @return User object or null if not logged in
	 */
	public User refreshLogin();
	
	/**
	 * Log out
	 * @param session The current HttpSession
	 * @param user The current user
	 */
	public void logout(HttpSession session, User user);
	
	/**
	 * Apply user permissions to the current connection
	 * @param user The current user
	 */
	public void initializeConnection(User user);
	
	/**
	 * Remove user permissions from the current connection
	 */
	public void cleanupConnection();
	}
