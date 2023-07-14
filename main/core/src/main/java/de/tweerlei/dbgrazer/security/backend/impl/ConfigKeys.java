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
package de.tweerlei.dbgrazer.security.backend.impl;

import java.util.EnumSet;
import java.util.Set;

import de.tweerlei.dbgrazer.security.model.Authority;
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
	
	private static final String PACKAGE_NAME = "dbgrazer.auth";
	
	/** Admin user name, used by the DummyUserLoader */
	public static final ConfigKey<String> ADMIN_USERNAME = ConfigKey.create(PACKAGE_NAME, "dummy.user", String.class, "admin");
	
	/** Admin password, used by the DummyUserLoader */
	public static final ConfigKey<String> ADMIN_PASSWORD = ConfigKey.create(PACKAGE_NAME, "dummy.password", String.class, "admin");
	
	/** Module prefix for the link loader impl. */
	public static final ConfigKey<String> USER_FILE_ACCESS = ConfigKey.create(PACKAGE_NAME, "userFileAccess", String.class, "direct");
	
	/** Path to user definitions, used by the FileUserLoader */
	public static final ConfigKey<String> FILE_USER_PATH = ConfigKey.create(PACKAGE_NAME, "file.userPath", String.class, "users");
	
	/** Path to user customization files, used by the FileUserLoader */
	public static final ConfigKey<String> FILE_PROFILE_PATH = ConfigKey.create(PACKAGE_NAME, "file.profilePath", String.class, "profiles");
	
	/** File: Convert user names to lower case for lookup */
	public static final ConfigKey<Boolean> FILE_LOWERCASE_USERS = ConfigKey.create(PACKAGE_NAME, "file.lowercase", Boolean.class, Boolean.FALSE);
	
	/** File: Create user definitions for unknown users (with only ROLE_LOGIN defined) */
	public static final ConfigKey<Boolean> FILE_CREATE_USERS = ConfigKey.create(PACKAGE_NAME, "file.create", Boolean.class, Boolean.FALSE);
	
	/** File: Additional roles to grant for users created based on file.create */
	public static final ConfigKey<Set<Authority>> FILE_DEFAULT_ROLES = ConfigKey.createSet(PACKAGE_NAME, "file.defaultRoles", Authority.class, EnumSet.noneOf(Authority.class));
	
	
	private ConfigKeys()
		{
		}
	}
