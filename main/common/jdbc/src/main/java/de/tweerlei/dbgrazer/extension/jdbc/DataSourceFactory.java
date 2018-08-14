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
package de.tweerlei.dbgrazer.extension.jdbc;

import javax.sql.DataSource;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.spring.config.ConfigAccessor;

/**
 * Create DataSources from link definitions
 * 
 * @author Robert Wruck
 */
public interface DataSourceFactory
	{
	/**
	 * Create a DataSource
	 * @param c Link definition
	 * @param config ConfigAccessor
	 * @return DataSource
	 */
	public DataSource createDataSource(LinkDef c, ConfigAccessor config);
	
	/**
	 * Get the number of physical connections for a DataSource created by this DataSourceFactory
	 * @param ds DataSource
	 * @return connection count
	 */
	public int getConnectionCount(DataSource ds);
	
	/**
	 * Close a DataSources created by this factory
	 * @param ds DataSource
	 */
	public void cleanup(DataSource ds);
	}
