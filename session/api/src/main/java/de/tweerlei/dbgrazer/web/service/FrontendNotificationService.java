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

/**
 * Log notifications to be shown to the user
 * 
 * @author Robert Wruck
 */
public interface FrontendNotificationService
	{
	/**
	 * Log an error
	 * @param key Error key for getting the localized message
	 * @param params Parameters to be inserted into the message template
	 */
	public void logError(String key, Object... params);
	
	/**
	 * Log an error
	 * @param info Additional info
	 * @param key Error key for getting the localized message
	 * @param params Parameters to be inserted into the message template
	 */
	public void logObjectError(Object info, String key, Object... params);
	}
