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
package de.tweerlei.ermtools.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import de.tweerlei.ermtools.util.LoggingSupport;

/**
 * F�hrt DDL-Statements aus
 * 
 * @author Robert Wruck
 */
public class DBSchemaWriter extends LoggingSupport
	{
	private final Connection conn;
	private final boolean ignoreErrors;
	private Statement stmt;
	
	/**
	 * Konstruktor
	 * @param c Connection
	 * @param i Ignore errors
	 */
	public DBSchemaWriter(Connection c, boolean i)
		{
		conn = c;
		ignoreErrors = i;
		}
	
	/**
	 * Prepare writing
	 * @throws SQLException on error
	 */
	public void open() throws SQLException
		{
		stmt = conn.createStatement();
		}

	/**
	 * Execute
	 * @param statements SQL statements
	 * @throws SQLException on error
	 */
	public void executeStatements(List<String> statements) throws SQLException
		{
		for (String sql : statements)
			{
			try	{
				log(sql);
				
				final int rc = stmt.executeUpdate(sql);
				
				log(rc + " rows affected");
				}
			catch (SQLException e)
				{
				if (ignoreErrors)
					log(e.getMessage());
				else
					throw e;
				}
			}
		}
	
	/**
	 * Finish writing
	 * @throws SQLException on error
	 */
	public void close() throws SQLException
		{
		stmt.close();
		stmt = null;
		}
	}
