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
package de.tweerlei.dbgrazer.link.model;

/**
 * Keys for localized messages in messages.properties
 * 
 * @author Robert Wruck
 */
public final class LinkErrorKeys
	{
	/** Empty name */
	public static final String UNKNOWN_TYPE = "error_unknownLinkType";
	/** Empty name */
	public static final String EMPTY_NAME = "error_emptyName";
	/** Empty user name */
	public static final String EMPTY_USERNAME = "error_emptyUsername";
	/** Empty password */
	public static final String EMPTY_PASSWORD = "error_emptyPassword";
	/** Empty driver class name */
	public static final String EMPTY_DRIVER = "error_emptyDriver";
	/** Empty URL */
	public static final String EMPTY_URL = "error_emptyUrl";
	/** Empty schema name */
	public static final String EMPTY_SCHEMA = "error_emptySchema";
	/** Driver class not found */
	public static final String DRIVER_CLASS_NOT_FOUND = "error_driverClassNotFound";
	
	private LinkErrorKeys()
		{
		}
	}
