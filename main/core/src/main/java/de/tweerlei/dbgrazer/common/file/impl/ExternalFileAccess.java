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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.CopyStreamReader;
import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamWriter;
import de.tweerlei.common.util.ProcessUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * FileAccess via an external program
 * 
 * @author Robert Wruck
 */
@Service("externalFileAccess")
public class ExternalFileAccess extends AbstractFileAccess
	{
	private final ConfigFileStore store;
	private final ConfigAccessor configService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param store ConfigFileStore
	 * @param configService ConfigAccessor
	 */
	@Autowired
	public ExternalFileAccess(ConfigFileStore store, ConfigAccessor configService)
		{
		this.store = store;
		this.configService = configService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public synchronized void createDirectory(String user, File dir) throws IOException
		{
		final File wd = dir.getParentFile();
		
		exec(wd, null, null, "mkdir", user, dir.getName());
		}
	
	@Override
	public synchronized void createFile(String user, StreamWriter writer, File tempFile, File file) throws IOException
		{
		final File wd = file.getParentFile();
		
		exec(wd, writer, null, "create", user, tempFile.getName(), file.getName());
		}
	
	@Override
	public synchronized void writeFile(String user, StreamWriter writer, File oldFile, File tempFile, File newFile) throws IOException
		{
		final File wd = newFile.getParentFile();
		
		exec(wd, writer, null, "update", user, oldFile.getName(), tempFile.getName(), newFile.getName());
		}
	
	@Override
	public synchronized void renameFileOrDirectory(String user, File oldFile, File tempFile, File newFile) throws IOException
		{
		final File wd = newFile.getParentFile();
		
		exec(wd, null, null, "rename", user, oldFile.getName(), tempFile.getName(), newFile.getName());
		}
	
	@Override
	public synchronized void removeFileOrDirectory(String user, File file) throws IOException
		{
		final File wd = file.getParentFile();
		
		exec(wd, null, null, "remove", user, file.getName());
		}
	
	@Override
	public List<HistoryEntry> getFileHistory(File file, int limit) throws IOException
		{
		final File wd = file.getParentFile();
		final HistoryParser p = new HistoryParser();
		
		exec(wd, null, p, "history", String.valueOf(limit), file.getName());
		
		return (p.getHistory());
		}
	
	@Override
	public void getFileVersion(File file, String version, StreamReader reader) throws IOException
		{
		final File wd = file.getParentFile();
		
		exec(wd, null, reader, "get", file.getName(), version);
		}
	
	@Override
	public void updateFileOrDirectory(File file, String version, StreamReader reader) throws IOException
		{
		final File wd = file.getParentFile();
		
		exec(wd, null, reader, "fetch", file.getName(), version);
		}
	
	private void exec(File workingDir, StreamWriter writer, StreamReader reader, String... args) throws IOException
		{
		final String cmd = configService.get(ConfigKeys.EXTERNAL_COMMAND);
		final File path = store.getFileLocation(cmd);
		final String[] cmdarray = new String[args.length + 1];
		System.arraycopy(args, 0, cmdarray, 1, args.length);
		cmdarray[0] = path.getAbsolutePath();
		
		logger.log(Level.FINE, StringUtils.join(cmdarray, " "));
		
		final ByteArrayOutputStream err = new ByteArrayOutputStream();
		
		final int rc = ProcessUtils.exec(cmdarray, null, workingDir, writer, reader, new CopyStreamReader(err), 0);
		
		if (err.size() > 0)
			logger.log(Level.INFO, err.toString());
		
		if (rc != 0)
			throw new IOException("External command returned " + rc + ", command line was:\n" + StringUtils.join(cmdarray, " "));
		}
	}
