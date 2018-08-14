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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.dbgrazer.common.file.FileAccess;

/**
 * Common file access functions
 * 
 * @author Robert Wruck
 */
public abstract class AbstractFileAccess implements FileAccess
	{
	@Override
	public List<File> listFiles(File dir) throws IOException
		{
		final File[] files = dir.listFiles();
		if (files == null)
			throw new IOException("Could not list files for " + dir.getAbsolutePath());
		
		final List<File> ret = new ArrayList<File>(files.length);
		
		for (File f : files)
			{
			if (f.isFile() && !f.isHidden())
				ret.add(f);
			}
		
		return (ret);
		}
	
	@Override
	public List<File> listDirectories(File dir) throws IOException
		{
		final File[] files = dir.listFiles();
		if (files == null)
			throw new IOException("Could not list files for " + dir.getAbsolutePath());
		
		final List<File> ret = new ArrayList<File>(files.length);
		
		for (File f : files)
			{
			if (f.isDirectory() && !f.isHidden())
				ret.add(f);
			}
		
		return (ret);
		}
	
	@Override
	public void readFile(File file, StreamReader reader) throws IOException
		{
		final FileInputStream fis = new FileInputStream(file);
		try	{
			reader.read(fis);
			}
		finally
			{
			StreamUtils.closeQuietly(fis);
			}
		}
	
	@Override
	public final void createFile(String user, StreamWriter writer, File file) throws IOException
		{
		createFile(user, writer, getTempFile(file), file);
		}
	
	@Override
	public final void writeFile(String user, StreamWriter writer, File oldFile, File newFile) throws IOException
		{
		writeFile(user, writer, oldFile, getTempFile(newFile), newFile);
		}
	
	@Override
	public final void renameFileOrDirectory(String user, File oldFile, File newFile) throws IOException
		{
		if (oldFile.getName().equals(newFile.getName()))
			return;
		
		renameFileOrDirectory(user, oldFile, getTempFile(newFile), newFile);
		}
	
	private File getTempFile(File file)
		{
		return (new File(file.getParentFile(), "." + file.getName()));
		}
	
	/**
	 * Create a file
	 * @param user User name
	 * @param writer StreamWriter
	 * @param tempFile Temp file for writing to
	 * @param file Final file name
	 * @throws IOException on error
	 */
	protected void createFile(String user, StreamWriter writer, File tempFile, File file) throws IOException
		{
		writeFile(user, writer, file, tempFile, file);
		}
	
	/**
	 * Update/rename a file
	 * @param user User name
	 * @param writer StreamWriter
	 * @param oldFile Existing file name
	 * @param tempFile Temp file for writing to
	 * @param newFile Final file name
	 * @throws IOException on error
	 */
	protected abstract void writeFile(String user, StreamWriter writer, File oldFile, File tempFile, File newFile) throws IOException;
	
	/**
	 * Rename a file
	 * @param user User name
	 * @param oldFile Existing file name
	 * @param tempFile Temp file for writing to
	 * @param newFile Final file name
	 * @throws IOException on error
	 */
	protected abstract void renameFileOrDirectory(String user, File oldFile, File tempFile, File newFile) throws IOException;
	}
