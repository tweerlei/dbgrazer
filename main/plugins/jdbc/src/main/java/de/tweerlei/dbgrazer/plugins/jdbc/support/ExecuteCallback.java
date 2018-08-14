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
package de.tweerlei.dbgrazer.plugins.jdbc.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

/**
 * StatementCallback that employs Statement.execute.
 * Mostly copied from JdbcTemplate.query(), but returns the update count.
 * 
 * @author Robert Wruck
 */
public class ExecuteCallback implements StatementCallback
	{
	private final String sql;
	private final ResultSetExtractor rse;
	private final NativeJdbcExtractor nje;
	
	/**
	 * Constructor
	 * @param sql SQL statement to execute
	 * @param rse ResultSetExtractor
	 * @param nje NativeJdbcExtractor
	 */
	public ExecuteCallback(String sql, ResultSetExtractor rse, NativeJdbcExtractor nje)
		{
		this.sql = sql;
		this.rse = rse;
		this.nje = nje;
		}
	
	@Override
	public Object doInStatement(Statement ps) throws SQLException
		{
		ResultSet rs = null;
		try {
			final boolean b = ps.execute(sql);
			if (b)
				{
				rs = ps.getResultSet();
				ResultSet rsToUse = rs;
				if (nje != null)
					rsToUse = nje.getNativeResultSet(rs);
				return rse.extractData(rsToUse);
				}
			else
				{
				final int updateCount = ps.getUpdateCount();
				return (updateCount < 0) ? null : updateCount;
				}
			}
		finally
			{
			JdbcUtils.closeResultSet(rs);
			}
		}
	}
