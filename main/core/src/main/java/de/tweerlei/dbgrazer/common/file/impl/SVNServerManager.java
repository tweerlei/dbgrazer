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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.ProcessUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;

/**
 * Start a private SVN server
 * 
 * @author Robert Wruck
 */
@Service("svnServerManager")
public class SVNServerManager implements ConfigListener
	{
	private static class ServerThread extends Thread
		{
		private final SVNServerManager manager;
		private final String command;
		
		public ServerThread(SVNServerManager manager, String command)
			{
			super("SVN server thread");
			this.manager = manager;
			this.command = command;
			}
		
		public String getCommand()
			{
			return (command);
			}
		
		@Override
		public void run()
			{
			try	{
				final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
				final int rc = ProcessUtils.exec(command.split("\\s+"), null, null, null, null, stderr, 0);
				manager.serverDied(rc, stderr.toString());
				}
			catch (IOException e)
				{
				manager.serverDied(-1, e.getMessage());
				}
			}
		}
	
	private final ConfigService configService;
	private final Logger logger;
	private ServerThread serverThread;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 */
	@Autowired
	public SVNServerManager(ConfigService configService)
		{
		this.configService = configService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	/**
	 * Shut down the server
	 */
	@PreDestroy
	public void cleanup()
		{
		stopServer();
		}
	
	@Override
	public void configChanged()
		{
		final String command = configService.get(ConfigKeys.SVNSERVE_COMMAND);
		
		if (command != null)
			startServer(command);
		else
			stopServer();
		}
	
	/**
	 * Notify about thread termination
	 * @param rc Return code
	 * @param message Error message
	 */
	public void serverDied(int rc, String message)
		{
		logger.log(Level.INFO, "SVN server exited: " + rc);
		if (message != null)
			logger.log(Level.WARNING, "SVN server error: " + message);
		
		// Since this is called from the ServerThread, we can't join it here
		}
	
	private synchronized void startServer(String command)
		{
		if (serverThread != null && serverThread.isAlive() && serverThread.getCommand().equals(command))
			return;
		
		stopServer();
		
		logger.log(Level.INFO, "Starting SVN server: " + command);
		
		serverThread = new ServerThread(this, command);
		serverThread.start();
		}
	
	private synchronized void stopServer()
		{
		if (serverThread != null)
			{
			logger.log(Level.INFO, "Stopping SVN server: " + serverThread.getCommand());
			serverThread.interrupt();
			try	{
				serverThread.join();
				}
			catch (InterruptedException e)
				{
				logger.log(Level.WARNING, "Interrupted while waiting for SVN server to stop");
				}
			serverThread = null;
			}
		}
	}
