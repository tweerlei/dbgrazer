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
package de.tweerlei.dbgrazer.common.service;

import java.io.File;

/**
 * Access configuration files
 * 
 * @author Robert Wruck
 */
public interface ConfigFileStore
	{
	/**
	 * Get the relative path to the main configuration file
	 * @return Path
	 */
	public String getConfigFilePath();
	
	/**
	 * Create an absolute File object for a configuration file
	 * @param path File path
	 * @return File object (never null but not checked for existence)
	 */
	public File getFileLocation(String path);
	
	/**
	 * Get the encoding to use for configuration files
	 * @return Encoding name
	 */
	public String getFileEncoding();
	}
