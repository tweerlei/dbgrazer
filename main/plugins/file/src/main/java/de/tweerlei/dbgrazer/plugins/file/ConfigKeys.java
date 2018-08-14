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
package de.tweerlei.dbgrazer.plugins.file;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Settings for filesystem queries (config.properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.backend.file";
	
	/** List hidden files */
	public static final ConfigKey<Boolean> FILE_LIST_HIDDEN = ConfigKey.create(PACKAGE_NAME, "listHidden", Boolean.class, Boolean.FALSE);
	
	/** Max. file size to read */
	public static final ConfigKey<Integer> FILE_MAX_SIZE = ConfigKey.create(PACKAGE_NAME, "maxSize", Integer.class, 65536);
	
	/** Character set for reading file contents */
	public static final ConfigKey<String> FILE_CHARSET = ConfigKey.create(PACKAGE_NAME, "charset", String.class, "UTF-8");
	
	
	private ConfigKeys()
		{
		}
	}
