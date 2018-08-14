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
package de.tweerlei.ermtools.dialect.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.common.util.StringJoiner;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;

/**
 * Dummy impl.
 * 
 * @author Robert Wruck
 */
public class OracleStatementAnalyzer implements SQLStatementAnalyzer
	{
	private static final String STATEMENT_ID = OracleStatementAnalyzer.class.getSimpleName();
	
	private final Connection conn;
	
	/**
	 * Constructor
	 * @param c Connection
	 */
	public OracleStatementAnalyzer(Connection c)
		{
		conn = c;
		}
	
	public SQLExecutionPlan analyzeStatement(String stmt, List<Object> params) throws SQLException
		{
		try	{
			final String explainStmt = "EXPLAIN PLAN"
					+ " SET statement_id = '" + STATEMENT_ID + "'"
					+ " FOR " + stmt;
			
			final int paramCount = (params == null) ? 0 : params.size();
			
			final PreparedStatement s1 = conn.prepareStatement(explainStmt);
			try	{
				// Set any parameters found in the explained statement
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
				
				s1.execute();
				}
			finally
				{
				s1.close();
				}
			
			final SQLExecutionPlan ret;
			
			final String selectStmt = "SELECT id, parent_id, operation, options, optimizer, object_owner, object_name, other_tag, cost, cardinality, access_predicates, filter_predicates"
					+ " FROM plan_table"
					+ " WHERE statement_id = '" + STATEMENT_ID + "'"
					+ " ORDER BY parent_id NULLS FIRST, position";
			
			final PreparedStatement s2 = conn.prepareStatement(selectStmt);
			try	{
				final ResultSet rs = s2.executeQuery();
				try	{
					final Map<Integer, SQLExecutionPlan> rows = new HashMap<Integer, SQLExecutionPlan>();
					
					while (rs.next())
						{
						final int id = rs.getInt(1);
						final int parentId = rs.getInt(2);
						final SQLExecutionPlan parent;
						if (rs.wasNull())
							parent = null;
						else
							parent = rows.get(parentId);
						
						StringJoiner sb;
						final SQLExecutionPlan plan = new SQLExecutionPlan();
						plan.setId(id);
						
						sb = new StringJoiner(" ");
						sb.append(rs.getString(3)).append(rs.getString(4)).append(rs.getString(5));
						plan.setOperation(sb.toString());
						
						sb = new StringJoiner(".");
						sb.append(rs.getString(6)).append(rs.getString(7));
						plan.setObjectName(sb.toString());
						plan.setOther(rs.getString(8));
						plan.setCost(rs.getLong(9));
						plan.setRows(rs.getLong(10));
						
						sb = new StringJoiner(" ");
						sb.append(rs.getString(11)).append(rs.getString(12));
						plan.setFilter(sb.toString());
						
						if (parent != null)
							parent.getChildren().add(plan);
						
						rows.put(id, plan);
						}
					
					if (!rows.isEmpty())
						ret = rows.get(0);
					else
						{
						ret = new SQLExecutionPlan();
						ret.setOperation("No plan returned");
						}
					}
				finally
					{
					rs.close();
					}
				}
			finally
				{
				s2.close();
				}
			
			return (ret);
			}
		finally
			{
			// clear plan_table
			conn.rollback();
			}
		}
	}
