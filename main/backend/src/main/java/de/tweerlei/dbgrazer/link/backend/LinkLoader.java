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
package de.tweerlei.dbgrazer.link.backend;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.link.model.LinkDef;

/**
 * Load link definitions
 * 
 * @author Robert Wruck
 */
public interface LinkLoader
	{
	/**
	 * Load link definitions
	 * @return Loaded links
	 */
	public SortedMap<String, LinkDef> loadLinks();
	
	/**
	 * Create a link definition
	 * @param user User name
	 * @param name Link name
	 * @param conn Link definition
	 * @throws IOException on error
	 */
	public void createLink(String user, String name, LinkDef conn) throws IOException;
	
	/**
	 * Update a link definition
	 * @param user User name
	 * @param name Link name
	 * @param newName New link name
	 * @param conn Link definition
	 * @throws IOException on error
	 */
	public void updateLink(String user, String name, String newName, LinkDef conn) throws IOException;
	
	/**
	 * Remove a link definition
	 * @param user User name
	 * @param name Link name
	 * @throws IOException on error
	 */
	public void removeLink(String user, String name) throws IOException;
	
	/**
	 * Get the modification history for a link definition
	 * @param name Link name
	 * @param limit List at most this number of newest entries
	 * @return History
	 * @throws IOException on error
	 */
	public List<HistoryEntry> getHistory(String name, int limit) throws IOException;
	}
