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
package de.tweerlei.common5.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Execute PreparedStatements on a Connection
 * 
 * @author Robert Wruck
 */
public class PreparedStatementExecutor
	{
	/**
	 * Read results from a ResultSet
	 */
	public static interface ResultHandler
		{
		/**
		 * Read results from a ResultSet
		 * @param rs ResultSet
		 * @throws SQLException on error
		 */
		public void handleResult(ResultSet rs) throws SQLException;
		}
	
	/**
	 * Read results from a ResultSet
	 */
	public static abstract class ResultRowHandler implements ResultHandler
		{
		public final void handleResult(ResultSet rs) throws SQLException
			{
			while (rs.next())
				handleRow(rs);
			}
		
		/**
		 * Read the current data from a ResultSet. MUST not call next()
		 * @param rs ResultSet
		 * @throws SQLException on error
		 */
		public abstract void handleRow(ResultSet rs) throws SQLException;
		}
	
	private final Connection conn;
	
	/**
	 * Constructor
	 * @param conn Connection
	 */
	public PreparedStatementExecutor(Connection conn)
		{
		this.conn = conn;
		}
	
	/**
	 * Execute a statement and process results
	 * @param sql SQL statement
	 * @param params Parameters
	 * @param handler ResultHandler
	 * @throws SQLException on error
	 */
	public void executeQuery(String sql, Object[] params, ResultHandler handler) throws SQLException
		{
		final PreparedStatement stmt = conn.prepareStatement(sql);
		try	{
			if (params != null)
				{
				int i = 1;
				for (Object p : params)
					stmt.setObject(i++, p);
				}
			
			final ResultSet rs = stmt.executeQuery();
			try	{
				handler.handleResult(rs);
				}
			finally
				{
				try	{
					rs.close();
					}
				catch (SQLException e)
					{
					// ignore
					}
				}
			}
		finally
			{
			try	{
				stmt.close();
				}
			catch (SQLException e)
				{
				// ignore
				}
			}
		}
	}
