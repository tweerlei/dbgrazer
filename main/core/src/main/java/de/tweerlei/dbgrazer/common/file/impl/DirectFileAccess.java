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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import de.tweerlei.common.io.FileUtils;
import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;

/**
 * Direct file access
 * 
 * @author Robert Wruck
 */
@Service("directFileAccess")
public class DirectFileAccess extends AbstractFileAccess
	{
	@Override
	public synchronized void createDirectory(String user, File dir) throws IOException
		{
		FileUtils.mkdir(dir);
		}
	
	@Override
	public synchronized void writeFile(String user, StreamWriter writer, File oldFile, File tempFile, File newFile) throws IOException
		{
		final FileOutputStream fos = new FileOutputStream(tempFile);
		try	{
			writer.write(fos);
			}
		finally
			{
			fos.close();
			}
		
		FileUtils.delete(oldFile);
		FileUtils.delete(newFile);
		FileUtils.rename(tempFile, newFile);
		}
	
	@Override
	public synchronized void renameFileOrDirectory(String user, File oldFile, File tempFile, File newFile) throws IOException
		{
		FileUtils.rmdir(tempFile);
		FileUtils.rename(oldFile, tempFile);
		FileUtils.rmdir(newFile);
		FileUtils.rename(tempFile, newFile);
		}
	
	@Override
	public synchronized void removeFileOrDirectory(String user, File file) throws IOException
		{
		FileUtils.rmdir(file);
		}
	
	@Override
	public List<HistoryEntry> getFileHistory(File file, int limit) throws IOException
		{
		final HistoryEntry ent = new HistoryEntry("", new Date(file.lastModified()), "Last modified");
		return (CollectionUtils.list(ent));
		}
	
	@Override
	public void getFileVersion(File file, String version, StreamReader reader) throws IOException
		{
		readFile(file, reader);
		}
	
	@Override
	public void updateFileOrDirectory(File file, String version, StreamReader reader) throws IOException
		{
		}
	}
