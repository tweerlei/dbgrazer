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
package de.tweerlei.dbgrazer.security.model;

import java.util.Date;

/**
 * Session information
 *
 * @author Robert Wruck
 */
public interface SessionInfo
	{
	/**
	 * Get the user name
	 * @return User name
	 */
	public String getUsername();
	
	/**
	 * Get the creation time
	 * @return Creation time
	 */
	public Date getCreationTime();
	
	/**
	 * Get the login time
	 * @return Login time
	 */
	public Date getLoginTime();
	
	/**
	 * Get the time of the last request
	 * @return Time of the last request
	 */
	public Date getLastRequestTime();
	
	/**
	 * Get the last request string
	 * @return Last request string
	 */
	public String getLastRequest();
	}
