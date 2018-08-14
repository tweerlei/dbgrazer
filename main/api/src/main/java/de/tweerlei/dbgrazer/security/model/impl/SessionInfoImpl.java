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
package de.tweerlei.dbgrazer.security.model.impl;

import java.util.Date;

import de.tweerlei.dbgrazer.security.model.SessionInfo;

/**
 * Session information
 *
 * @author Robert Wruck
 */
public class SessionInfoImpl implements SessionInfo, Cloneable
	{
	private final Date creationTime;
	private Date loginTime;
	private String username;
	private String requestString;
	private Date requestTime;
	
	/**
	 * Constructor
	 * @param username User name
	 * @param now Current date
	 */
	public SessionInfoImpl(String username, Date now)
		{
		this.creationTime = now;
		this.loginTime = (Date) now.clone();
		this.username = username;
		}
	
	/**
	 * Copy constructor
	 * @param other Other object
	 */
	private SessionInfoImpl(SessionInfoImpl other)
		{
		this.creationTime = (Date) other.creationTime.clone();
		this.loginTime = (Date) other.loginTime.clone();
		this.username = other.username;
		this.requestString = other.requestString;
		final Date tmp = other.requestTime;
		if (tmp != null)
			this.requestTime = (Date) tmp.clone();
		}
	
	/**
	 * Replace the username atomically
	 * @param newname New user name
	 * @param now Current date
	 * @return Previous user name
	 */
	public synchronized String replaceUsername(String newname, Date now)
		{
		final String ret = username;
		if (!username.equals(newname))
			{
			this.username = newname;
			this.loginTime = now;
			}
		return (ret);
		}
	
	/**
	 * Set the last request string
	 * @param request Request string
	 * @param now Current date
	 */
	public void setLastRequest(String request, Date now)
		{
		this.requestString = request;
		this.requestTime = now;
		}
	
	@Override
	public String getUsername()
		{
		return username;
		}
	
	@Override
	public Date getCreationTime()
		{
		return creationTime;
		}
	
	@Override
	public Date getLoginTime()
		{
		return loginTime;
		}
	
	@Override
	public Date getLastRequestTime()
		{
		return requestTime;
		}
	
	@Override
	public String getLastRequest()
		{
		return requestString;
		}
	
	@Override
	public SessionInfoImpl clone()
		{
		return (new SessionInfoImpl(this));
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + creationTime.hashCode();
		result = prime * result + loginTime.hashCode();
		result = prime * result + username.hashCode();
		return result;
		}
	
	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SessionInfoImpl other = (SessionInfoImpl) obj;
		if (!creationTime.equals(other.creationTime))
			return false;
		if (!loginTime.equals(other.loginTime))
			return false;
		if (!username.equals(other.username))
			return false;
		return true;
		}
	
	@Override
	public String toString()
		{
		return (username);
		}
	}
