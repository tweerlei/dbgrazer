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
package de.tweerlei.dbgrazer.common.service.impl;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Config location (System properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer";
	
	/** This property will contain the path to the used config.properties */
	public static final ConfigKey<String> CONFIG_FILE = ConfigKey.create(PACKAGE_NAME, "configFile", String.class, "config.properties");
	
	/** This property will contain the path to the used configuration files */
	public static final ConfigKey<String> CONFIG_PATH = ConfigKey.create(PACKAGE_NAME, "configPath", String.class, null);
	
	
	private ConfigKeys()
		{
		}
	}
