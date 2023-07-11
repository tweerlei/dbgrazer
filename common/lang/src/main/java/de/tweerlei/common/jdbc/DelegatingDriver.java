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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * JDBC driver that delegates all operations to a driver loaded from external JARs
 * 
 * @author Robert Wruck
 */
public class DelegatingDriver implements Driver
	{
	private final Driver target;
	
	/**
	 * Constructor
	 * @param t Delegate Driver
	 */
	public DelegatingDriver(Driver t)
		{
		target = t;
		}
	
	@Override
	public boolean acceptsURL(String url) throws SQLException
		{
		return (target.acceptsURL(url));
		}
	
	@Override
	public Connection connect(String url, Properties info) throws SQLException
		{
		return (target.connect(url, info));
		}
	
	@Override
	public int getMajorVersion()
		{
		return (target.getMajorVersion());
		}
	
	@Override
	public int getMinorVersion()
		{
		return (target.getMinorVersion());
		}
	
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
		{
		return (target.getPropertyInfo(url, info));
		}
	
	@Override
	public boolean jdbcCompliant()
		{
		return (target.jdbcCompliant());
		}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException
		{
		return null;
		}
	}
