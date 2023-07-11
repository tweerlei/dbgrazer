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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Hold information necessary for establishing a JDBC connection
 * 
 * @author Robert Wruck
 */
public class ConnectionInfo
	{
	private final List jars;
	private final String driver;
	private final String uri;
	private final String user;
	private final String pass;
	private final Properties props;
	
	/**
	 * Constructor
	 * @param j JAR files
	 * @param d Driver class name
	 * @param a JDBC URI
	 * @param u Username
	 * @param p Password
	 * @param pr Connection properties
	 */
	public ConnectionInfo(Collection j, String d, String a, String u, String p, Properties pr)
		{
		jars = Collections.unmodifiableList(new ArrayList(j));
		driver = d;
		uri = a;
		user = u;
		pass = p;
		props = new Properties(pr);
		}
	
	/**
	 * Constructor
	 * @param d Driver class name
	 * @param a JDBC URI
	 * @param u Username
	 * @param p Password
	 * @param pr Connection properties
	 */
	public ConnectionInfo(String d, String a, String u, String p, Properties pr)
		{
		jars = Collections.EMPTY_LIST;
		driver = d;
		uri = a;
		user = u;
		pass = p;
		props = new Properties(pr);
		}
	
	/**
	 * Get the selected JAR files
	 * @return never null
	 */
	public List getJarFiles()
		{
		return (jars);
		}
	
	/**
	 * Get the driver class name
	 * @return Class name
	 */
	public String getDriverName()
		{
		return (driver);
		}
	
	/**
	 * Get the JDBC URI
	 * @return URI
	 */
	public String getURI()
		{
		return (uri);
		}
	
	/**
	 * Get the user name
	 * @return User name
	 */
	public String getUsername()
		{
		return (user);
		}
	
	/**
	 * Get the password
	 * @return Password
	 */
	public String getPassword()
		{
		return (pass);
		}
	
	/**
	 * Get the properties
	 * @return Properties
	 */
	public Properties getProperties()
		{
		return (props);
		}
	
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((driver == null) ? 0 : driver.hashCode());
		result = prime * result + ((jars == null) ? 0 : jars.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((props == null) ? 0 : props.hashCode());
		return result;
		}

	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionInfo other = (ConnectionInfo) obj;
		if (driver == null)
			{
			if (other.driver != null)
				return false;
			}
		else if (!driver.equals(other.driver))
			return false;
		if (jars == null)
			{
			if (other.jars != null)
				return false;
			}
		else if (!jars.equals(other.jars))
			return false;
		if (uri == null)
			{
			if (other.uri != null)
				return false;
			}
		else if (!uri.equals(other.uri))
			return false;
		if (user == null)
			{
			if (other.user != null)
				return false;
			}
		else if (!user.equals(other.user))
			return false;
		if (props == null)
			{
			if (other.props != null)
				return false;
			}
		else if (!props.equals(other.props))
			return false;
		return true;
		}
	
	public String toString()
		{
		return (uri);
		}
	}
