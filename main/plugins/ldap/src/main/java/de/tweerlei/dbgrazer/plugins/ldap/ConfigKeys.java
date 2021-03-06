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
package de.tweerlei.dbgrazer.plugins.ldap;

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
	
	private static final String PACKAGE_NAME = "dbgrazer.auth.ldap";
	
	/** LDAP: Server URL */
	public static final ConfigKey<String> LDAP_LINK = ConfigKey.create(PACKAGE_NAME, "linkName", String.class, null);
	
	/** LDAP: Pattern for LDAP user names */
	public static final ConfigKey<String> LDAP_USER_PATTERN = ConfigKey.create(PACKAGE_NAME, "userPattern", String.class, "?");
	
	/** LDAP: Base DN for user accounts, relative to the base DN */
	public static final ConfigKey<String> LDAP_USER_DN = ConfigKey.create(PACKAGE_NAME, "userBaseDN", String.class, "");
	
	/** LDAP: Filter for user accounts */
	public static final ConfigKey<String> LDAP_USER_FILTER = ConfigKey.create(PACKAGE_NAME, "userFilter", String.class, "(uid=?)");
	
	
	private ConfigKeys()
		{
		}
	}
