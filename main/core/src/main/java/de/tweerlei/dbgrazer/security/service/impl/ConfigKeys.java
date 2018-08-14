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
package de.tweerlei.dbgrazer.security.service.impl;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Backend settings (config.properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.auth";
	
	/** Module prefix for the user loader impl. */
	public static final ConfigKey<String> USER_AUTHN = ConfigKey.create(PACKAGE_NAME, "userAuthentication", String.class, "dummy");
	
	/** Module prefix for the user loader impl. */
	public static final ConfigKey<String> USER_AUTHZ = ConfigKey.create(PACKAGE_NAME, "userAuthorization", String.class, "dummy");
	
	/** Module prefix for the user loader impl. */
	public static final ConfigKey<String> USER_LOADER = ConfigKey.create(PACKAGE_NAME, "userLoader", String.class, "dummy");
	
	
	private ConfigKeys()
		{
		}
	}
