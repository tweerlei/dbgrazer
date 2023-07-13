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
package de.tweerlei.dbgrazer.extension.http;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Well known configuration keys
 * 
 * @author Robert Wruck
 */
public final class ConfigKeys
	{
	/*
	 * HTTP settings for authentication and webservice queries (config.properties only)
	 */
	
	private static final String PACKAGE_NAME = "dbgrazer.backend.http";
	
	/** Module prefix for the HTTP client impl. */
	public static final ConfigKey<String> HTTP_CLIENT = ConfigKey.create(PACKAGE_NAME, "httpClient", String.class, "jdk");
	
	/** HTTP: Connect timeout in millis */
	public static final ConfigKey<Integer> HTTP_CONNECT_TIMEOUT = ConfigKey.create(PACKAGE_NAME, "connectTimeout", Integer.class, 1000);
	
	/** HTTP: Read timeout in millis */
	public static final ConfigKey<Integer> HTTP_READ_TIMEOUT = ConfigKey.create(PACKAGE_NAME, "readTimeout", Integer.class, 1000);
	
	/** HTTP: Proxy host name */
	public static final ConfigKey<String> HTTP_PROXY_HOST = ConfigKey.create(PACKAGE_NAME, "proxyHost", String.class, null);
	
	/** HTTP: Proxy port */
	public static final ConfigKey<Integer> HTTP_PROXY_PORT = ConfigKey.create(PACKAGE_NAME, "proxyPort", Integer.class, 3128);
	
	/** HTTP: Proxy user name */
	public static final ConfigKey<String> HTTP_PROXY_USERNAME = ConfigKey.create(PACKAGE_NAME, "proxyUsername", String.class, null);
	
	/** HTTP: Proxy password */
	public static final ConfigKey<String> HTTP_PROXY_PASSWORD = ConfigKey.create(PACKAGE_NAME, "proxyPassword", String.class, null);
	
	/** HTTP: Create multipart requests that are MIME compliant or "browser compatible" */
	public static final ConfigKey<Boolean> HTTP_STRICT_MODE = ConfigKey.create(PACKAGE_NAME, "strictMode", Boolean.class, Boolean.TRUE);
	
	/** HTTP: Link name for external requests (e.g. loading referenced XML schemas) */
	public static final ConfigKey<String> HTTP_ANON_LINK = ConfigKey.create(PACKAGE_NAME, "anonymousLinkName", String.class, null);
	
	
	private ConfigKeys()
		{
		}
	}
