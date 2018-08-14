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
package de.tweerlei.spring.service;

import java.util.Date;

/**
 * Service for accessing the system time.
 * Use this instead of System.currentTimeMillis(), new Date() and Calendar.getInstance()
 * 
 * @author Robert Wruck
 */
public interface TimeService
	{
	/**
	 * Get the current time
	 * @return Timestamp
	 */
	public long getCurrentTime();
	
	/**
	 * Get the current time - usually the same as new Date(getCurrentTime())
	 * @return Date
	 */
	public Date getCurrentDate();
	
	/**
	 * Get the time this TimeService was initialized
	 * @return Timestamp
	 */
	public long getStartupTime();
	
	/**
	 * Get the time this TimeService was initialized - usually the same as new Date(getStartupTime())
	 * @return Date
	 */
	public Date getStartupDate();
	
	/**
	 * Get the up time in milliseconds - usually the same as getCurrentTime() - getStartupTime()
	 * @return Timestamp
	 */
	public long getUpTime();
	}
