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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

/**
 * PreparedStatementCallback that employs PreparedStatement.execute and relies upon parameters being already set on the PreparedStatement.
 * Mostly copied from JdbcTemplate.query()
 * 
 * @author Robert Wruck
 */
public class GenerateKeysCallback implements PreparedStatementCallback
	{
	private final ResultSetExtractor rse;
	private final NativeJdbcExtractor nje;
	
	/**
	 * Constructor
	 * @param rse ResultSetExtractor
	 * @param nje NativeJdbcExtractor
	 */
	public GenerateKeysCallback(ResultSetExtractor rse, NativeJdbcExtractor nje)
		{
		this.rse = rse;
		this.nje = nje;
		}
	
	@Override
	public Object doInPreparedStatement(PreparedStatement ps) throws SQLException
		{
		ResultSet rs = null;
		try {
			final int updateCount = ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			ResultSet rsToUse = rs;
			if (nje != null)
				rsToUse = nje.getNativeResultSet(rs);
			rse.extractData(rsToUse);
			return (updateCount);
			}
		finally
			{
			JdbcUtils.closeResultSet(rs);
			}
		}
	}
