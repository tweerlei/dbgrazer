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
package de.tweerlei.dbgrazer.security.event;

import org.springframework.context.ApplicationEvent;

/**
 * An event concerning an HttpSession
 * 
 * @author Robert Wruck
 */
public abstract class SessionEvent extends ApplicationEvent
	{
	/**
	 * Constructor
	 * @param sessionId The session ID
	 */
	public SessionEvent(String sessionId)
		{
		super(sessionId);
		}
	
	/**
	 * Get the session ID
	 * @return the session ID
	 */
	public String getSessionId()
		{
		return (getSource().toString());
		}
	}
