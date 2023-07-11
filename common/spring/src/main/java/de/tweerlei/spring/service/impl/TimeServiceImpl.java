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
package de.tweerlei.spring.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import de.tweerlei.spring.service.TimeService;

/**
 * Implementation of TimeService
 * 
 * @author Robert Wruck
 */
@Service("timeService")
public class TimeServiceImpl implements TimeService
	{
	private final long startupTime;
	
	/**
	 * Constructor
	 */
	public TimeServiceImpl()
		{
		startupTime = getCurrentTime();
		}
	
	public long getCurrentTime()
		{
		return System.currentTimeMillis();
		}
	
	public Date getCurrentDate()
		{
		return (new Date(getCurrentTime()));
		}
	
	public long getStartupTime()
		{
		return startupTime;
		}
	
	public Date getStartupDate()
		{
		return (new Date(getStartupTime()));
		}
	
	public long getUpTime()
		{
		return getCurrentTime() - startupTime;
		}
	}
