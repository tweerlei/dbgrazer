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
package de.tweerlei.dbgrazer.extension.jdbc;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * JDBC configuration (config.properties or per link)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.backend.jdbc";
	
	/** Fetch limit */
	public static final ConfigKey<Integer> MAX_ROWS = ConfigKey.create(PACKAGE_NAME, "maxRows", Integer.class, 100);
	
	/** Fetch size */
	public static final ConfigKey<Integer> FETCH_SIZE = ConfigKey.create(PACKAGE_NAME, "fetchSize", Integer.class, 100);
	
	/** Query timeout */
	public static final ConfigKey<Integer> QUERY_TIMEOUT = ConfigKey.create(PACKAGE_NAME, "timeout", Integer.class, 60);
	
	/** Max. levels for ERM graphs */
	public static final ConfigKey<Integer> ERM_LEVELS = ConfigKey.create(PACKAGE_NAME, "ermLevels", Integer.class, 4);
	
	/** Rows per MERGE statement */
	public static final ConfigKey<Integer> MERGE_ROWS = ConfigKey.create(PACKAGE_NAME, "mergeRows", Integer.class, 100);
	
	/*
	 * Backend settings (config.properties only)
	 */
	
	/** Module prefix for the datasource factory impl. */
	public static final ConfigKey<String> DATASOURCE_FACTORY = ConfigKey.create(PACKAGE_NAME, "dataSourceFactory", String.class, "simple");
	
	/** Module prefix for the metadata loader impl. */
	public static final ConfigKey<String> METADATA_LOADER = ConfigKey.create(PACKAGE_NAME, "metadataLoader", String.class, "dummy");
	
	/** Whether to cache metadata */
	public static final ConfigKey<Boolean> METADATA_CACHE = ConfigKey.create(PACKAGE_NAME, "metadataCache", Boolean.class, Boolean.FALSE);
	
	
	private ConfigKeys()
		{
		}
	}
