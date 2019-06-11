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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import de.tweerlei.ermtools.dialect.SQLExecutionPlan;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;

/**
 * Dummy impl.
 * 
 * @author Robert Wruck
 */
public class PostgreSQLStatementAnalyzer implements SQLStatementAnalyzer
	{
	private final Connection conn;
	
	/**
	 * Constructor
	 * @param c Connection
	 */
	public PostgreSQLStatementAnalyzer(Connection c)
		{
		conn = c;
		}
	
	public SQLExecutionPlan analyzeStatement(String stmt, List<Object> params) throws SQLException
		{
		final SQLExecutionPlan ret = new SQLExecutionPlan();
		ret.setOperation("Plan");
		int id = 1;
		
		final String explainStmt = "EXPLAIN VERBOSE " + stmt;
		
		final PreparedStatement s1 = conn.prepareStatement(explainStmt);
		try	{
			// Set any parameters found in the explained statement
			final int paramCount = (params == null) ? 0 : params.size();
			final int n = s1.getParameterMetaData().getParameterCount();
			for (int i = 1; i <= n; i++)
				{
				if (i <= paramCount)
					{
					final Object value = params.get(i - 1);
					// Hack: Translate java.util.Date to java.sql.Timestamp
					if (value instanceof Date)
						s1.setObject(i, new Timestamp(((Date) value).getTime()));
					else
						s1.setObject(i, value);
					}
				else
					s1.setObject(i, null);
				}
			
			final ResultSet rs = s1.executeQuery();
			try	{
				while (rs.next())
					{
					final SQLExecutionPlan plan = new SQLExecutionPlan();
					plan.setId(id++);
					
					plan.setOperation(rs.getString(1));
					
					ret.getChildren().add(plan);
					}
				}
			finally
				{
				rs.close();
				}
			}
		finally
			{
			s1.close();
			}
		
		return (ret);
		}
	}
