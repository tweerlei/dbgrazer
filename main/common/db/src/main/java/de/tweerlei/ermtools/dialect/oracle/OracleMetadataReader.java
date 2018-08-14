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
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.ProcedureDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * MetadataReader that uses Oracle views
 * 
 * @author Robert Wruck
 */
public class OracleMetadataReader implements MetadataReader
	{
	private static final String DUMMY_CATALOG_NAME = "";
	
	private static final Map<String, Integer> TYPE_MAP;
	static
		{
		TYPE_MAP = new HashMap<String, Integer>();
		TYPE_MAP.put("NUMBER", Types.DECIMAL);
		TYPE_MAP.put("DATE", Types.TIMESTAMP);
		TYPE_MAP.put("VARCHAR2", Types.VARCHAR);
		TYPE_MAP.put("RAW", Types.BINARY);
		TYPE_MAP.put("LONG", Types.LONGVARCHAR);
		TYPE_MAP.put("CHAR", Types.CHAR);
		TYPE_MAP.put("NVARCHAR2", Types.VARCHAR);
		TYPE_MAP.put("NCHAR", Types.CHAR);
		TYPE_MAP.put("BLOB", Types.BLOB);
		TYPE_MAP.put("CLOB", Types.CLOB);
		TYPE_MAP.put("NCLOB", Types.CLOB);
		TYPE_MAP.put("TIMESTAMP(3)", Types.TIMESTAMP);
		TYPE_MAP.put("TIMESTAMP(6)", Types.TIMESTAMP);
		TYPE_MAP.put("ROWID", Types.VARCHAR);
		TYPE_MAP.put("UROWID", Types.VARCHAR);
		TYPE_MAP.put("OBJECT", Types.STRUCT);
		TYPE_MAP.put("COLLECTION", Types.ARRAY);
		}
	
	private static interface ResultHandler
		{
		public void handleResult(ResultSet rs) throws SQLException;
		}
	
	private final Connection conn;
	
	/**
	 * Constructor
	 * @param c Connection
	 */
	public OracleMetadataReader(Connection c)
		{
		conn = c;
		}
	
	public List<String> getCatalogNames() throws SQLException
		{
		// Oracle does not support catalogs
		return Collections.singletonList(DUMMY_CATALOG_NAME);
		}

	public List<String> getSchemaNames() throws SQLException
		{
		final List<String> ret = new ArrayList<String>();
		runQuery("SELECT u.username"
				+" FROM all_users u"
				+" ORDER BY u.username",
				null, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					ret.add(rs.getString(1));
				}
			});
		return (ret);
		}
	
	public Map<String, String> getTables(String catalog, String schema) throws SQLException
		{
		final Map<String, String> ret = new TreeMap<String, String>();
		runQuery("SELECT o.object_name, o.object_type"
				+" FROM all_objects o"
				+" WHERE o.owner = ? AND o.object_type IN ('TABLE', 'VIEW')"
				+" ORDER BY o.object_name",
				new Object[] { schema }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					ret.put(rs.getString(1), rs.getString(2));
				}
			});
		return (ret);
		}
	
	public Map<String, String> getProcedures(String catalog, String schema) throws SQLException
		{
		final Map<String, String> ret = new TreeMap<String, String>();
		runQuery("SELECT o.object_name, o.object_type"
				+" FROM all_objects o"
				+" WHERE o.owner = ? AND o.object_type IN ('FUNCTION', 'PROCEDURE')"
				+" ORDER BY o.object_name",
				new Object[] { schema }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					ret.put(rs.getString(1), rs.getString(2));
				}
			});
		return (ret);
		}

	public Map<String, Integer> getDatatypes(String catalog, String schema) throws SQLException
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>();
		runQuery("SELECT t.type_name, t.typecode"
				+" FROM all_types t"
				+" WHERE t.owner = ?"
				+" ORDER BY t.object_name",
				new Object[] { schema }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					ret.put(rs.getString(1), parseType(rs.getString(2)));
				}
			});
		return (ret);
		}

	public TableDescription getTableDescription(final String catalog, final String schema, final String table) throws SQLException
		{
		final String[] type = { null, null };
		runQuery("SELECT t.table_type, t.comments"
				+" FROM all_tab_comments t"
				+" WHERE t.owner = ? AND t.table_name = ?",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				if (!rs.next())
					throw new SQLException("Table not found: " + catalog + ", " + schema + ", " + table);
				
				type[0] = rs.getString(1);
				type[1] = rs.getString(2);
				}
			});
		
		return (new TableDescription(DUMMY_CATALOG_NAME, schema, table, type[1], type[0],
				getPrimaryKeyDescription(schema, table),
				getColumnDescriptions(schema, table),
				getIndexDescriptions(schema, table),
				getReferencedKeys(schema, table),
				getReferencingKeys(schema, table),
				getTablePrivileges(schema, table)
				));
		}

	private PrimaryKeyDescription getPrimaryKeyDescription(String schema, String table) throws SQLException
		{
		final String[] type = { null };
		final List<String> cols = new ArrayList<String>();
		runQuery("SELECT c.constraint_name, cc.column_name"
				+" FROM all_constraints c"
				+" INNER JOIN all_cons_columns cc ON cc.owner = c.owner and cc.constraint_name = c.constraint_name"
				+" WHERE c.owner = ? AND c.table_name = ? AND c.constraint_type = 'P'"
				+" ORDER BY cc.position",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					{
					type[0] = rs.getString(1);
					cols.add(rs.getString(2));
					}
				}
			});
		
		if (type[0] == null)
			return (null);
		
		return (new PrimaryKeyDescription(type[0], cols));
		}
	
	private List<ColumnDescription> getColumnDescriptions(String schema, String table) throws SQLException
		{
		final List<ColumnDescription> ret = new ArrayList<ColumnDescription>();
		runQuery("SELECT t.column_name, c.comments, t.data_type, DECODE(t.char_used, 'C', t.char_length, NVL(t.data_precision, t.data_length)), t.data_scale, t.nullable, t.data_default"
				+" FROM all_tab_columns t"
				+" LEFT JOIN all_col_comments c ON c.owner = t.owner AND c.table_name = t.table_name AND c.column_name = t.column_name"
				+" WHERE t.owner = ? AND t.table_name = ?"
				+" ORDER BY t.column_id",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					{
					final String cn = rs.getString(1);
					final String cc = rs.getString(2);
					final String tn = rs.getString(3);
					final String def = rs.getString(7);
					ret.add(new ColumnDescription(
							cn,
							cc,
							parseType(tn),
							tn,
							rs.getInt(4),
							rs.getInt(5),
							"Y".equals(rs.getString(6)),
							(def == null) ? null : def.trim()	// Oracle bug: Trailing whitespace (from e.g. "DEFAULT xx NOT NULL") is included in the DEFAULT expression
							));
					}
				}
			});
		return (ret);
		}
	
	private List<IndexDescription> getIndexDescriptions(String schema, String table) throws SQLException
		{
		final List<IndexDescription> ret = new ArrayList<IndexDescription>();
		runQuery("SELECT i.index_name, i.uniqueness, ic.column_name, ie.column_expression"
				+" FROM all_indexes i"
				+" INNER JOIN all_ind_columns ic ON ic.index_owner = i.owner AND ic.index_name = i.index_name"
				+" LEFT JOIN all_ind_expressions ie ON ie.index_owner = ic.index_owner AND ie.index_name = ic.index_name AND ie.column_position = ic.column_position"
				+" WHERE i.table_owner = ? AND i.table_name = ?"
				+" ORDER BY i.index_name, ic.column_position",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				String last = null;
				boolean u = false;
				List<String> columns = new ArrayList<String>();
				while (rs.next())
					{
					final String cn = rs.getString(1);
					if ((last != null) && !last.equals(cn))
						{
						ret.add(new IndexDescription(last, u, columns));
						columns.clear();	// Column names are copied, so we can reuse the list
						}
					last = cn;
					u = "UNIQUE".equals(rs.getString(2));
					final String colName = rs.getString(3);
					final String colExpr = rs.getString(4);
					columns.add((colExpr != null) ? colExpr : colName);
					}
				
				if (last != null)
					ret.add(new IndexDescription(last, u, columns));
				}
			});
		return (ret);
		}
	
	private List<ForeignKeyDescription> getReferencedKeys(String schema, String table) throws SQLException
		{
		final List<ForeignKeyDescription> ret = new ArrayList<ForeignKeyDescription>();
		runQuery("SELECT p.constraint_name, r.owner, r.table_name, pc.column_name, rc.column_name"
				+" FROM all_constraints p"
				+" INNER JOIN all_constraints r ON r.owner = p.r_owner AND r.constraint_name = p.r_constraint_name"
				+" INNER JOIN all_cons_columns pc ON pc.owner = p.owner AND pc.constraint_name = p.constraint_name"
				+" INNER JOIN all_cons_columns rc ON rc.owner = r.owner AND rc.constraint_name = r.constraint_name AND rc.position = pc.position"
				+" WHERE p.owner = ? AND p.table_name = ? AND p.constraint_type = 'R'"
				+" ORDER BY p.constraint_name, pc.position",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				String last = null;
				String rowner = null;
				String rtable = null;
				Map<String, String> columns = new LinkedHashMap<String, String>();
				while (rs.next())
					{
					final String cn = rs.getString(1);
					if ((last != null) && !last.equals(cn))
						{
						ret.add(new ForeignKeyDescription(last, DUMMY_CATALOG_NAME, rowner, rtable, columns));
						columns.clear();	// Column names are copied, so we can reuse the map
						}
					last = cn;
					rowner = rs.getString(2);
					rtable = rs.getString(3);
					columns.put(rs.getString(4), rs.getString(5));
					}
				
				if (last != null)
					ret.add(new ForeignKeyDescription(last, DUMMY_CATALOG_NAME, rowner, rtable, columns));
				}
			});
		return (ret);
		}
	
	private List<ForeignKeyDescription> getReferencingKeys(String schema, String table) throws SQLException
		{
		final List<ForeignKeyDescription> ret = new ArrayList<ForeignKeyDescription>();
		runQuery("SELECT p.constraint_name, p.owner, p.table_name, pc.column_name, rc.column_name"
				+" FROM all_constraints p"
				+" INNER JOIN all_constraints r ON r.owner = p.r_owner AND r.constraint_name = p.r_constraint_name"
				+" INNER JOIN all_cons_columns pc ON pc.owner = p.owner AND pc.constraint_name = p.constraint_name"
				+" INNER JOIN all_cons_columns rc ON rc.owner = r.owner AND rc.constraint_name = r.constraint_name AND rc.position = pc.position"
				+" WHERE r.owner = ? AND r.table_name = ? AND p.constraint_type = 'R'"
				+" ORDER BY p.constraint_name, pc.position",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				String last = null;
				String rowner = null;
				String rtable = null;
				Map<String, String> columns = new LinkedHashMap<String, String>();
				while (rs.next())
					{
					final String cn = rs.getString(1);
					if ((last != null) && !last.equals(cn))
						{
						ret.add(new ForeignKeyDescription(last, DUMMY_CATALOG_NAME, rowner, rtable, columns));
						columns.clear();	// Column names are copied, so we can reuse the map
						}
					last = cn;
					rowner = rs.getString(2);
					rtable = rs.getString(3);
					columns.put(rs.getString(4), rs.getString(5));
					}
				
				if (last != null)
					ret.add(new ForeignKeyDescription(last, DUMMY_CATALOG_NAME, rowner, rtable, columns));
				}
			});
		return (ret);
		}
	
	private List<PrivilegeDescription> getTablePrivileges(String schema, String table) throws SQLException
		{
		final List<PrivilegeDescription> ret = new ArrayList<PrivilegeDescription>();
		runQuery("SELECT p.grantor, p.grantee, p.privilege, p.grantable"
				+" FROM all_tab_privs p"
				+" WHERE p.table_schema = ? AND p.table_name = ?"
				+" ORDER BY p.privilege, p.grantee",
				new Object[] { schema, table }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				while (rs.next())
					ret.add(new PrivilegeDescription(
							rs.getString(1),
							rs.getString(2),
							rs.getString(3),
							"YES".equals(rs.getString(4))
							));
				}
			});
		return (ret);
		}

	public ProcedureDescription getProcedureDescription(final String catalog, final String schema, final String proc) throws SQLException
		{
		final String[] type = { null };
		runQuery("SELECT p.object_type"
				+" FROM all_procedures p"
				+" WHERE p.owner = ? AND p.object_name = ?",
				new Object[] { schema, proc }, new ResultHandler()
			{
			public void handleResult(ResultSet rs) throws SQLException
				{
				if (!rs.next())
					throw new SQLException("Procedure not found: " + catalog + ", " + schema + ", " + proc);
				
				type[0] = rs.getString(1);
				}
			});
		return (new ProcedureDescription(DUMMY_CATALOG_NAME, schema, proc, null, type[0], null, null));
		}
	
	private void runQuery(String sql, Object[] params, ResultHandler handler) throws SQLException
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
	
	private static int parseType(String type)
		{
		final Integer t = TYPE_MAP.get(type);
		if (t == null)
			return (Types.OTHER);
		return (t);
		}
	}
