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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.CopyStreamReader;
import de.tweerlei.common.io.FileUtils;
import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.common.util.ProcessUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * QueryLoader that checks query definitions into a Subversion repository
 * using the external "svn" client program
 * 
 * @author Robert Wruck
 */
@Service("svnFileAccess")
public class SvnFileAccess extends AbstractFileAccess
	{
	private final ConfigAccessor configService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 */
	@Autowired
	public SvnFileAccess(ConfigAccessor configService)
		{
		this.configService = configService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public synchronized void createDirectory(String user, File dir) throws IOException
		{
		final File wd = dir.getParentFile();
		
		FileUtils.mkdir(wd);
		
		svn(wd, null, "mkdir", "--parents", dir.getName());
		
		try	{
			svn(wd, null, "commit", "-m", user + " created subdir " + dir.getName());
			}
		catch (IOException e)
			{
			// commit failed, move dir out of the way
			svn(wd, null, "rm", dir.getName());
			throw e;
			}
		}
	
	@Override
	public synchronized void writeFile(String user, StreamWriter writer, File oldFile, File tempFile, File newFile) throws IOException
		{
		final boolean create = !oldFile.exists();
		final boolean rename = !oldFile.getName().equals(newFile.getName());
		final File wd = newFile.getParentFile();
		
		if (rename)
			svn(wd, null, "mv", oldFile.getName(), tempFile.getName());
		
		final FileOutputStream fos = new FileOutputStream(tempFile);
		try	{
			writer.write(fos);
			}
		finally
			{
			fos.close();
			}
		
		if (rename)
			{
			svn(wd, null, "mv", tempFile.getName(), newFile.getName());
			
			try	{
				svn(wd, null, "commit", "-m", user + " renamed " + oldFile.getName() + " to " + newFile.getName());
				}
			catch (IOException e)
				{
				// commit failed, rename file back
				svn(wd, null, "mv", newFile.getName(), tempFile.getName());
				svn(wd, null, "mv", tempFile.getName(), oldFile.getName());
				throw e;
				}
			}
		else
			{
			FileUtils.delete(oldFile);
			FileUtils.delete(newFile);
			FileUtils.rename(tempFile, newFile);
			
			if (create)
				svn(wd, null, "add", newFile.getName());
			
			try	{
				svn(wd, null, "commit", "-m", user + (create ? " created " : " changed ") + newFile.getName());
				}
			catch (IOException e)
				{
				if (create)
					{
					// commit failed, move file out of the way
					svn(wd, null, "rm", newFile.getName());
					}
				throw e;
				}
			}
		}
	
	@Override
	public synchronized void renameFileOrDirectory(String user, File oldFile, File tempFile, File newFile) throws IOException
		{
		final File wd = newFile.getParentFile();
		
		svn(wd, null, "mv", oldFile.getName(), tempFile.getName());
		svn(wd, null, "mv", tempFile.getName(), newFile.getName());
		
		try	{
			svn(wd, null, "commit", "-m", user + " renamed " + oldFile.getName() + " to " + newFile.getName());
			}
		catch (IOException e)
			{
			// commit failed, rename file back
			svn(wd, null, "mv", newFile.getName(), tempFile.getName());
			svn(wd, null, "mv", tempFile.getName(), oldFile.getName());
			throw e;
			}
		}
	
	@Override
	public synchronized void removeFileOrDirectory(String user, File file) throws IOException
		{
		final File wd = file.getParentFile();
		
		svn(wd, null, "rm", file.getName());
		svn(wd, null, "commit", "-m", user + " deleted " + file.getName());
		
//		FileUtils.delete(file);
		}
	
	@Override
	public List<HistoryEntry> getFileHistory(File file, int limit) throws IOException
		{
		final File wd = file.getParentFile();
		final HistoryParser p = new HistoryParser();
		
		svn(wd, p, "log", "--xml", "-l", String.valueOf(limit), file.getName());
		
		return (p.getHistory());
		}
	
	@Override
	public void getFileVersion(File file, String version, StreamReader reader) throws IOException
		{
		final File wd = file.getParentFile();
		
		svn(wd, reader, "cat", "-r", version, file.getName());
		}
	
	@Override
	public void updateFileOrDirectory(File file, String version, StreamReader reader) throws IOException
		{
		final File wd = file.getParentFile();
		
		if (version != null)
			svn(wd, reader, "up", "-r", version, file.getName());
		else
			svn(wd, reader, "up", file.getName());
		}
	
	private void svn(File workingDir, StreamReader reader, String... args) throws IOException
		{
		final String[] cmdarray = new String[args.length + 1];
		System.arraycopy(args, 0, cmdarray, 1, args.length);
		cmdarray[0] = configService.get(ConfigKeys.SVN_COMMAND);
		
		logger.log(Level.FINE, StringUtils.join(cmdarray, " "));
		
		final ByteArrayOutputStream err = new ByteArrayOutputStream();
		
		final int rc = ProcessUtils.exec(cmdarray, null, workingDir, null, reader, new CopyStreamReader(err), 0);
		
		if (err.size() > 0)
			logger.log(Level.INFO, err.toString());
		
		if (rc != 0)
			throw new IOException("SVN returned " + rc + ", command line was:\n" + StringUtils.join(cmdarray, " "));
		}
	}
