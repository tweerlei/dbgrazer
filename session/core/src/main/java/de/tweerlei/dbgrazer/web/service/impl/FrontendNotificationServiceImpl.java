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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.model.ErrorRecord;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.spring.service.TimeService;

/**
 * Log notifications to be shown to the user
 * 
 * @author Robert Wruck
 */
@Service
public class FrontendNotificationServiceImpl implements FrontendNotificationService
	{
	private final TimeService timeService;
	private final UserSettings userSettings;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param timeService TimeService
	 * @param userSettings UserSettings
	 */
	@Autowired
	public FrontendNotificationServiceImpl(TimeService timeService, UserSettings userSettings)
		{
		this.timeService = timeService;
		this.userSettings = userSettings;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public void logError(String key, Object... params)
		{
		logObjectError(null, key, params);
		}
	
	@Override
	public void logObjectError(Object info, String key, Object... params)
		{
		logger.log(Level.WARNING, key + ": " + Arrays.asList(params) + ", " + info);
		
		userSettings.getErrors().add(new ErrorRecord(timeService.getCurrentDate(), key, params, info));
		}
	}
