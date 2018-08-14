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
package de.tweerlei.dbgrazer.extension.file;

import java.io.IOException;

import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;

/**
 * Access remote resources
 * 
 * @author Robert Wruck
 */
public interface RemoteResourceService
	{
	/**
	 * Read a file
	 * @param link Link name
	 * @param path Resource path
	 * @param reader StreamReader
	 * @throws IOException on error
	 */
	public void readResource(String link, String path, StreamReader reader) throws IOException;
	
	/**
	 * Create a directory
	 * @param link Link name
	 * @param path Resource path
	 * @param user User name
	 * @throws IOException on error
	 */
	public void createPath(String link, String path, String user) throws IOException;
	
	/**
	 * Create a file
	 * @param link Link name
	 * @param path Resource path
	 * @param writer StreamWriter
	 * @param user User name
	 * @throws IOException on error
	 */
	public void createResource(String link, String path, StreamWriter writer, String user) throws IOException;
	
	/**
	 * Update/rename a file
	 * @param link Link name
	 * @param path Resource path
	 * @param writer StreamWriter
	 * @param user User name
	 * @throws IOException on error
	 */
	public void updateResource(String link, String path, StreamWriter writer, String user) throws IOException;
	
	/**
	 * Rename a file
	 * @param link Link name
	 * @param path Resource path
	 * @param newPath New resource path
	 * @param user User name
	 * @throws IOException on error
	 */
	public void renameResource(String link, String path, String newPath, String user) throws IOException;
	
	/**
	 * Remove a file
	 * @param link Link name
	 * @param path Resource path
	 * @param user User name
	 * @throws IOException on error
	 */
	public void removeResource(String link, String path, String user) throws IOException;
	}
