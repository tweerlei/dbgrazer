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
package de.tweerlei.dbgrazer.plugins.c3p0;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * Connection pool settings for the PooledDataSourceFactory (config.properties or per connection)
	 * These are the names c3p0 uses in its own configuration file.
	 */
	
	private static final String PACKAGE_NAME = "c3p0";
	
	/** Initial pool size per logical connection */
	public static final ConfigKey<Integer> INITIAL_POOL_SIZE = ConfigKey.create(PACKAGE_NAME, "initialPoolSize", Integer.class, 0);
	
	/** Minimum pool size per logical connection */
	public static final ConfigKey<Integer> MIN_POOL_SIZE = ConfigKey.create(PACKAGE_NAME, "minPoolSize", Integer.class, 0);
	
	/** Maximum pool size per logical connection */
	public static final ConfigKey<Integer> MAX_POOL_SIZE = ConfigKey.create(PACKAGE_NAME, "maxPoolSize", Integer.class, 10);
	
	/** Pool size increment */
	public static final ConfigKey<Integer> ACQUIRE_INCREMENT = ConfigKey.create(PACKAGE_NAME, "acquireIncrement", Integer.class, 1);
	
	/** Connection acquire attempts before failure */
	public static final ConfigKey<Integer> ACQUIRE_RETRY_ATTEMPTS = ConfigKey.create(PACKAGE_NAME, "acquireRetryAttempts", Integer.class, 5);
	
	/** Connection acquire delay after failed attempts (msec) */
	public static final ConfigKey<Integer> ACQUIRE_RETRY_DELAY = ConfigKey.create(PACKAGE_NAME, "acquireRetryDelay", Integer.class, 1000);
	
	/** Timeout for acquiring a connection (msec) */
	public static final ConfigKey<Integer> CHECKOUT_TIMEOUT = ConfigKey.create(PACKAGE_NAME, "checkoutTimeout", Integer.class, 5000);
	
	/** Maximum time ANY idle connections will be kept around (sec) */
	public static final ConfigKey<Integer> MAX_IDLE_TIME = ConfigKey.create(PACKAGE_NAME, "maxIdleTime", Integer.class, 0);
	
	/** Maximum time idle connections exceeding minPoolSize will be kept around (sec) */
	public static final ConfigKey<Integer> MAX_IDLE_TIME_EXCESS_CONNECTIONS = ConfigKey.create(PACKAGE_NAME, "maxIdleTimeExcessConnections", Integer.class, 0);
	
	/** Maximum lifetime for a physical connection per logical connection (sec) */
	public static final ConfigKey<Integer> MAX_CONNECTION_AGE = ConfigKey.create(PACKAGE_NAME, "maxConnectionAge", Integer.class, 3600);
	
	/** Size of the statement cache for all connections */
	public static final ConfigKey<Integer> MAX_STATEMENTS = ConfigKey.create(PACKAGE_NAME, "maxStatements", Integer.class, 0);
	
	/** Size of the statement cache per physical connection */
	public static final ConfigKey<Integer> MAX_STATEMENTS_PER_CONNECTION = ConfigKey.create(PACKAGE_NAME, "maxStatementsPerConnection", Integer.class, 0);
	
	/** Interval to test physical connections (sec) */
	public static final ConfigKey<Integer> IDLE_CONNECTION_TEST_PERIOD = ConfigKey.create(PACKAGE_NAME, "idleConnectionTestPeriod", Integer.class, 300);
	
	/** SQL statement for connection testing */
	public static final ConfigKey<String> PREFERRED_TEST_QUERY = ConfigKey.create(PACKAGE_NAME, "preferredTestQuery", String.class, null);
	
	
	private ConfigKeys()
		{
		}
	}
