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
package de.tweerlei.dbgrazer.common.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;

/**
 * File access service
 * 
 * @author Robert Wruck
 */
public interface FileAccess
	{
	/**
	 * List files
	 * @param dir Directory name
	 * @return Files
	 * @throws IOException on error
	 */
	public List<File> listFiles(File dir) throws IOException;
	
	/**
	 * List directories
	 * @param dir Directory name
	 * @return Files
	 * @throws IOException on error
	 */
	public List<File> listDirectories(File dir) throws IOException;
	
	/**
	 * Read a file
	 * @param file Source file name
	 * @param reader StreamReader
	 * @throws IOException on error
	 */
	public void readFile(File file, StreamReader reader) throws IOException;
	
	/**
	 * Create a directory
	 * @param user User name
	 * @param dir Directory name
	 * @throws IOException on error
	 */
	public void createDirectory(String user, File dir) throws IOException;
	
	/**
	 * Create a file
	 * @param user User name
	 * @param writer StreamWriter
	 * @param file Final file name
	 * @throws IOException on error
	 */
	public void createFile(String user, StreamWriter writer, File file) throws IOException;
	
	/**
	 * Update/rename a file
	 * @param user User name
	 * @param writer StreamWriter
	 * @param oldFile Existing file name
	 * @param newFile Final file name
	 * @throws IOException on error
	 */
	public void writeFile(String user, StreamWriter writer, File oldFile, File newFile) throws IOException;
	
	/**
	 * Rename a file or directory
	 * @param user User name
	 * @param oldFile Existing file name
	 * @param newFile Final file name
	 * @throws IOException on error
	 */
	public void renameFileOrDirectory(String user, File oldFile, File newFile) throws IOException;
	
	/**
	 * Remove a file or directory
	 * @param user User name
	 * @param file Existing file name
	 * @throws IOException on error
	 */
	public void removeFileOrDirectory(String user, File file) throws IOException;
	
	/**
	 * Get the file history
	 * @param file File
	 * @param limit History size limit
	 * @return File history
	 * @throws IOException on error
	 */
	public List<HistoryEntry> getFileHistory(File file, int limit) throws IOException;
	
	/**
	 * Get a previous version of a file
	 * @param file File
	 * @param version Version
	 * @param reader StreamReader
	 * @throws IOException on error
	 */
	public void getFileVersion(File file, String version, StreamReader reader) throws IOException;
	
	/**
	 * Update a file or directory to a given version
	 * @param file File
	 * @param version Version
	 * @param reader StreamReader for status messages
	 * @throws IOException on error
	 */
	public void updateFileOrDirectory(File file, String version, StreamReader reader) throws IOException;
	}
