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
package de.tweerlei.dbgrazer.plugins.c3p0;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.jdbc.DataSourceFactory;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * DataSourceFactory that uses C3P0 connection pools
 * 
 * @author Robert Wruck
 */
@Service("c3p0DataSourceFactory")
public class PooledDataSourceFactory implements DataSourceFactory
	{
	private final Logger logger;
	
	/**
	 * Constructor
	 */
	public PooledDataSourceFactory()
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public void cleanup(DataSource ds)
		{
		if ((ds == null) || !(ds instanceof PooledDataSource))
			return;
		
		try	{
			((PooledDataSource) ds).close();
			}
		catch (SQLException e)
			{
			// This actually can't happen, since AbstractPoolBackedDataSource.close() does not declare SQLException
			logger.log(Level.WARNING, "cleanup", e);
			}
		}
	
	@Override
	public DataSource createDataSource(LinkDef c, ConfigAccessor config)
		{
		final DriverManagerDataSource ret = new DriverManagerDataSource();
		
		// Set properties first because this would clear some of the settings below, esp. user/password
		ret.setProperties(c.getProperties());
		
		ret.setDriverClass(c.getDriver());
		ret.setJdbcUrl(c.getUrl());
		if (!StringUtils.empty(c.getUsername()))
			{
			ret.setUser(c.getUsername());
			ret.setPassword(c.getPassword());
			}
		
		return (ret);
		}
	
	@Override
	public int getConnectionCount(DataSource ds)
		{
		if ((ds == null) || !(ds instanceof PooledDataSource))
			return (0);
		
		try	{
			return (((PooledDataSource) ds).getNumConnectionsAllUsers());
			}
		catch (SQLException e)
			{
			logger.log(Level.WARNING, "getConnectionCount", e);
			return (0);
			}
		}
	}
