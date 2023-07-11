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
package de.tweerlei.ermtools.dialect.postgresql;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tweerlei.common5.jdbc.PreparedStatementExecutor;
import de.tweerlei.common5.jdbc.PreparedStatementExecutor.ResultRowHandler;
import de.tweerlei.common5.jdbc.impl.JdbcMetadataReader;

/**
 * JdbcMetadataReader for MySQL
 * 
 * @author Robert Wruck
 */
public class PostgreSQLMetadataReader extends JdbcMetadataReader
	{
	/**
	 * Constructor
	 * @param md DatabaseMetaData
	 */
	public PostgreSQLMetadataReader(DatabaseMetaData md)
		{
		super(md);
		}
	
	@Override
	public String getDefaultSchemaName() throws SQLException
		{
		final PreparedStatementExecutor executor = new PreparedStatementExecutor(getMetaData().getConnection());
		
		final List<String> ret = new ArrayList<String>();
		executor.executeQuery("SELECT current_schema()",
				null, new ResultRowHandler()
			{
			@Override
			public void handleRow(ResultSet rs) throws SQLException
				{
				ret.add(rs.getString(1));
				}
			});
		return (ret.isEmpty() ? null : ret.get(0));
		}
	}
