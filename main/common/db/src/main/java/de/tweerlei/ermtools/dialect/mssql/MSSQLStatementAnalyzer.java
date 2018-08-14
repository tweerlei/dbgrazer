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
package de.tweerlei.ermtools.dialect.mssql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class MSSQLStatementAnalyzer implements SQLStatementAnalyzer
	{
	private final Connection conn;
	
	/**
	 * Constructor
	 * @param c Connection
	 */
	public MSSQLStatementAnalyzer(Connection c)
		{
		conn = c;
		}
	
	public SQLExecutionPlan analyzeStatement(String stmt, List<Object> params) throws SQLException
		{
		execute("SET SHOWPLAN_ALL ON");
		try	{
			final Map<Integer, SQLExecutionPlan> rows = new HashMap<Integer, SQLExecutionPlan>();
			
			// T-SQL does not natively support query parameters.
			// PreparedStatement thus employs 'EXECUTE sp_executesql' to execute the statement.
			// Unfortunately, SQL server does not return plan information for procedure calls.
			// In addition, getParameterMetadata does not work when SHOWPLAN is ON,
			// because the underlying query won't return metadata but a plan instead.
			// 
			// Summary: It's not possible to analyze parametrized queries with MSSQL.
			final Statement s1 = conn.createStatement();
			try	{
				if (s1.execute(stmt))
					{
					final ResultSet rs = s1.getResultSet();
					try	{
						while (rs.next())
							{
							if (!"PLAN_ROW".equals(rs.getString("Type")))
								continue;
							
							final int id = rs.getInt("NodeId");
							final int parentId = rs.getInt("Parent");
							
							StringJoiner sb;
							final SQLExecutionPlan plan = new SQLExecutionPlan();
							plan.setId(id);
							
							sb = new StringJoiner(" ");
							sb.append(rs.getString("PhysicalOp")).append(rs.getString("LogicalOp")).append(rs.getString("Argument"));
							plan.setOperation(sb.toString());
							
							plan.setObjectName(rs.getString("DefinedValues"));
							plan.setOther(rs.getBoolean("Parallel") ? "PARALLEL" : "");
//							plan.setCost(convertCost(rs.getFloat("EstimateIO")) + convertCost(rs.getFloat("EstimateCPU")));
							plan.setCost(convertCost(rs.getFloat("TotalSubtreeCost")));
							plan.setRows(rs.getLong("EstimateRows"));
							
							plan.setFilter(rs.getString("OutputList"));
							
							final SQLExecutionPlan parent = rows.get(parentId);
							if (parent != null)
								parent.getChildren().add(plan);
							
							rows.put(id, plan);
							}
						}
					finally
						{
						rs.close();
						}
					}
				}
			finally
				{
				s1.close();
				}
			
			final SQLExecutionPlan ret;
			if (!rows.isEmpty())
				ret = rows.get(2);	// Row ID 1 (with parentId 0) is not a PLAN_ROW but the statement text
			else
				{
				ret = new SQLExecutionPlan();
				ret.setOperation("No plan returned");
				}
			return (ret);
			}
		finally
			{
			execute("SET SHOWPLAN_ALL OFF");
			}
		}
	
	private long convertCost(Float cost) {
		if (cost == null)
			return (0);
		
		return ((long) (cost.doubleValue() * 1000000));
	}
	
	private boolean execute(String sql) throws SQLException
		{
		final Statement stmt = conn.createStatement();
		try	{
			return (stmt.execute(sql));
			}
		finally
			{
			stmt.close();
			}
		}
	}
