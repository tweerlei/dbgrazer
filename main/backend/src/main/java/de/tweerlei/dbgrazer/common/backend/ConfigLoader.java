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
package de.tweerlei.dbgrazer.common.backend;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;

/**
 * Load user data
 * 
 * @author Robert Wruck
 */
public interface ConfigLoader
	{
	/**
	 * Load configuration properties
	 * @return Properties
	 */
	public Properties loadConfig();
	
	/**
	 * Update configuration properties
	 * @param user User name
	 * @param props Properties
	 * @throws IOException on error
	 */
	public void updateConfig(String user, Properties props) throws IOException;
	
	/**
	 * Get the modification history for a user
	 * @param limit List at most this number of newest entries
	 * @return History
	 * @throws IOException on error
	 */
	public List<HistoryEntry> getHistory(int limit) throws IOException;
	}
