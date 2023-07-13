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

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jndi.JndiTemplate;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.jdbc.DataSourceFactory;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * DataSourceFactory that looks up DataSources in JNDI
 * 
 * @author Robert Wruck
 */
@Service("jndiDataSourceFactory")
public class JndiDataSourceFactory implements DataSourceFactory
	{
	private final JndiTemplate jndiTemplate;
	
	/**
	 * Constructor
	 */
	public JndiDataSourceFactory()
		{
		this.jndiTemplate = new JndiTemplate();
		}
	
	@Override
	public DataSource createDataSource(LinkDef c, ConfigAccessor config)
		{
		try	{
			return ((DataSource) jndiTemplate.lookup(c.getUrl(), DataSource.class));
			}
		catch (NamingException e)
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
