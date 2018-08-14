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
package de.tweerlei.dbgrazer.plugins.http;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Authentication settings (config.properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.auth.http";
	
	/** HTTP: Link name for authentication */
	public static final ConfigKey<String> HTTP_LINK = ConfigKey.create(PACKAGE_NAME, "linkName", String.class, null);
	
	/** HTTP: URL */
	public static final ConfigKey<String> HTTP_URL = ConfigKey.create(PACKAGE_NAME, "url", String.class, null);
	
	/** HTTP: Use POST rather than GET */
	public static final ConfigKey<Boolean> HTTP_POST = ConfigKey.create(PACKAGE_NAME, "usePost", Boolean.class, Boolean.FALSE);
	
	/** HTTP: POST body template */
	public static final ConfigKey<String> HTTP_POST_BODY = ConfigKey.create(PACKAGE_NAME, "requestTemplate", String.class, null);
	
	/** HTTP: POST content type */
	public static final ConfigKey<String> HTTP_POST_CONTENT_TYPE = ConfigKey.create(PACKAGE_NAME, "requestContentType", String.class, "application/x-www-form-urlencoded");
	
	/** HTTP: POST SOAP action */
	public static final ConfigKey<String> HTTP_POST_ACTION = ConfigKey.create(PACKAGE_NAME, "requestAction", String.class, null);
	
	/** HTTP: Response pattern */
	public static final ConfigKey<String> HTTP_PATTERN = ConfigKey.create(PACKAGE_NAME, "responsePattern", String.class, null);
	
	
	private ConfigKeys()
		{
		}
	}
