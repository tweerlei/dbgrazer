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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Simple DataSource that gets connections from the DriverManager
 * 
 * @author Robert Wruck
 */
public class DriverManagerDataSource implements DataSource
	{
	/** The username property */
	public static final String USERNAME_PROPERTY = "user";
	/** The password property */
	public static final String PASSWORD_PROPERTY = "password";
	
	private final String url;
	private final Properties props;
	private final boolean readOnly;
	private PrintWriter logWriter;
	private int loginTimeout;
	
	/**
	 * Constructor
	 * @param url JDBC URL
	 * @param props Connection properties, including username and password
	 * @param readOnly Set created connections to read only
	 */
	public DriverManagerDataSource(String url, Properties props, boolean readOnly)
		{
		this.url = url;
		this.props = props;
		this.readOnly = readOnly;
		}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException
		{
		return (logWriter);
		}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException
		{
		logWriter = out;
		}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException
		{
		loginTimeout = seconds;
		}
	
	@Override
	public int getLoginTimeout() throws SQLException
		{
		return loginTimeout;
		}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
		{
		return null;
		}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
		{
		return false;
		}
	
	public Logger getParentLogger() throws SQLFeatureNotSupportedException
		{
		return null;
		}
	
	@Override
	public Connection getConnection() throws SQLException
		{
		final Connection c = DriverManager.getConnection(url, props);
		prepare(c);
		return (c);
		}
	
	@Override
	public Connection getConnection(String username, String password) throws SQLException
		{
		final Properties p = new Properties(props);
		p.setProperty(USERNAME_PROPERTY, username);
		p.setProperty(PASSWORD_PROPERTY, password);
		
		final Connection c = DriverManager.getConnection(url, p);
		prepare(c);
		return (c);
		}
	
	private void prepare(Connection c) throws SQLException
		{
		if (readOnly)
			c.setReadOnly(true);
		}
	}
