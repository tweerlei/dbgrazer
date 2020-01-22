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
package de.tweerlei.dbgrazer.web.constant;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Frontend settings (config.properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.web";
	
	/** Available theme names */
	public static final ConfigKey<String> THEME_NAMES = ConfigKey.create(PACKAGE_NAME, "ui.themeNames", String.class, "theme");
	
	/** Base name for custom themes */
	public static final ConfigKey<String> THEME_PATH = ConfigKey.create(PACKAGE_NAME, "ui.themePath", String.class, "themes");
	
	/** Base name for custom static resources */
	public static final ConfigKey<String> STATIC_RESOURCE_PATH = ConfigKey.create(PACKAGE_NAME, "ui.staticResourcePath", String.class, "static");
	
	/** TTL in seconds for custom static resources */
	public static final ConfigKey<Integer> STATIC_RESOURCE_TTL = ConfigKey.create(PACKAGE_NAME, "ui.staticResourceTTL", Integer.class, 86400);
	
	/*
	 * Result settings (config.properties only)
	 */
	
	/** Max. levels for drilldown queries */
	public static final ConfigKey<Integer> DRILLDOWN_LEVELS = ConfigKey.create(PACKAGE_NAME, "result.drilldownLevels", Integer.class, 10);
	
	
	private ConfigKeys()
		{
		}
	}
