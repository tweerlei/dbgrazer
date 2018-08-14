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
package de.tweerlei.dbgrazer.common.file.impl;

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
	
	private static final String PACKAGE_NAME = "dbgrazer.storage";
	
	/** Path to the SVN executable, used by the SvnBackedFileQueryLoader */
	public static final ConfigKey<String> SVN_COMMAND = ConfigKey.create(PACKAGE_NAME, "svn.command", String.class, "svn");
	
	/** SVNServe command line for running a standalone SVN server */
	public static final ConfigKey<String> SVNSERVE_COMMAND = ConfigKey.create(PACKAGE_NAME, "svn.server.command", String.class, null);
	
	/** Path to the executable, used by the ExternalFileQueryLoader */
	public static final ConfigKey<String> EXTERNAL_COMMAND = ConfigKey.create(PACKAGE_NAME, "external.command", String.class, null);
	
	
	private ConfigKeys()
		{
		}
	}
