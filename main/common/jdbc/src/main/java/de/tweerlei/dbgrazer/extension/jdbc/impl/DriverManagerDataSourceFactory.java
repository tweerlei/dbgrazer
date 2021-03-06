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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import de.tweerlei.common.jdbc.ConnectionFactory;
import de.tweerlei.common.jdbc.ConnectionInfo;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.extension.jdbc.DataSourceFactory;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * DataSourceFactory that creates connections via DriverManager
 * 
 * @author Robert Wruck
 */
@Service("simpleDataSourceFactory")
public class DriverManagerDataSourceFactory implements DataSourceFactory
	{
	@Override
	public DataSource createDataSource(LinkDef c, ConfigAccessor config)
		{
		final ConnectionFactory cf;
		if (StringUtils.empty(c.getUsername()))
			cf = new ConnectionFactory(new ConnectionInfo(c.getDriver(), c.getUrl(), null, null, c.getProperties()), !c.isWritable());
		else
			cf = new ConnectionFactory(new ConnectionInfo(c.getDriver(), c.getUrl(), c.getUsername(), c.getPassword(), c.getProperties()), !c.isWritable());
		
		try	{
			return (cf.getDataSource());
			}
		catch (SQLException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public int getConnectionCount(DataSource ds)
		{
		return (0);
		}
	
	@Override
	public void cleanup(DataSource ds)
		{
		}
	}
