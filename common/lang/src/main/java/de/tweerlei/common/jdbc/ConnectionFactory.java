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
package de.tweerlei.common.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import de.tweerlei.common.util.JarClassLoader;

/**
 * Creates JDBC connections
 * 
 * @author Robert Wruck
 */
public class ConnectionFactory
	{
	private static final Set drivers = new HashSet();
	
	private final ConnectionInfo info;
	private final boolean readOnly;
	private boolean driverLoaded;
	
	/**
	 * Constructor
	 * @param ci ConnectionInfo
	 * @param ro Set connections to read only
	 */
	public ConnectionFactory(ConnectionInfo ci, boolean ro)
		{
		info = ci;
		readOnly = ro;
		driverLoaded = false;
		}
	
	/**
	 * Get the ConnectionInfo
	 * @return ConnectionInfo
	 */
	public ConnectionInfo getConnectionInfo()
		{
		return (info);
		}
	
	/**
	 * Get a new connection
	 * @return Connection
	 * @throws SQLException on error
	 */
	public Connection getConnection() throws SQLException
		{
		if (!driverLoaded)
			{
			loadDriver();
			driverLoaded = true;
			}
		
		final Connection c = DriverManager.getConnection(info.getURI(), info.getUsername(), info.getPassword());
		if (readOnly)
			c.setReadOnly(true);
		
		return (c);
		}
	
	/**
	 * Get a DataSource
	 * @return DataSource
	 * @throws SQLException on error
	 */
	public DataSource getDataSource() throws SQLException
		{
		if (!driverLoaded)
			{
			loadDriver();
			driverLoaded = true;
			}
		
		final Properties props = new Properties(info.getProperties());
		props.setProperty(DriverManagerDataSource.USERNAME_PROPERTY, info.getUsername());
		props.setProperty(DriverManagerDataSource.PASSWORD_PROPERTY, info.getPassword());
		
		return (new DriverManagerDataSource(info.getURI(), props, readOnly));
		}
	
	private void loadDriver() throws SQLException
		{
		final List jars = info.getJarFiles();
		if ((jars != null) && !jars.isEmpty())
			{
			load(jars, info.getDriverName());
			}
		else
			{
			try	{
				Class.forName(info.getDriverName());
				}
			catch (ClassNotFoundException e)
				{
				throw new SQLException(e.getMessage());
				}
			}
		}
	
	/**
	 * Load a driver
	 * @param jars External JARs
	 * @param driver Driver class name
	 * @throws SQLException On error
	 */
	public static void load(List jars, String driver) throws SQLException
		{
		if (!drivers.contains(driver))
			{
			final Class clazz;
			try	{
				// Don't use ClassLoader.getSystemClassLoader()
				final JarClassLoader loader = new JarClassLoader(Thread.currentThread().getContextClassLoader());
				for (Iterator i = jars.iterator(); i.hasNext(); )
					loader.addFile((File) i.next());
				clazz = loader.loadClass(driver);
				}
			catch (ClassNotFoundException e2)
				{
				throw new SQLException("Class not found: " + e2.getMessage());
				}
			
			final Driver del;
			try	{
				del = new DelegatingDriver((Driver) clazz.newInstance());
				}
			catch (IllegalAccessException e)
				{
				throw new SQLException(e.getClass().getName() + " (" + driver + "): " + e.getMessage());
				}
			catch (InstantiationException e)
				{
				throw new SQLException(e.getClass().getName() + " (" + driver + "): " + e.getMessage());
				}
			catch (ClassCastException e)
				{
				throw new SQLException(e.getClass().getName() + " (" + driver + "): " + e.getMessage());
				}
			
			DriverManager.registerDriver(del);
			drivers.add(driver);
			}
		}
	}
