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
package de.tweerlei.common5.jdbc.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.ParameterDescription;
import de.tweerlei.common5.jdbc.model.ParameterDescription.Direction;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.ProcedureDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Wrap DatabaseMetaData in useful data structures
 * 
 * @author Robert Wruck
 */
public class JdbcMetadataReader implements MetadataReader
	{
	private final DatabaseMetaData md;
	
	/**
	 * Constructor
	 * @param md DatabaseMetaData
	 */
	public JdbcMetadataReader(DatabaseMetaData md)
		{
		this.md = md;
		}
	
	/**
	 * Get the DatabaseMetaData
	 * @return DatabaseMetaData
	 */
	protected final DatabaseMetaData getMetaData()
		{
		return (md);
		}
	
	public String getDefaultCatalogName() throws SQLException
		{
		return (md.getConnection().getCatalog());
		}
	
	public String getDefaultSchemaName() throws SQLException
		{
		throw new SQLException("Connection.getSchema() is only available on JDK 1.7");
//		return (md.getConnection().getSchema());
		}
	
	public List<String> getCatalogNames() throws SQLException
		{
		final List<String> ret = new LinkedList<String>();
		
		final ResultSet rs = md.getCatalogs();
		try	{
			while (rs.next())
				ret.add(rs.getString(1));
			}
		finally
			{
			rs.close();
			}
		
		if (ret.isEmpty())
			ret.add("");
		
		return (ret);
		}
	
	public List<String> getSchemaNames() throws SQLException
		{
		final List<String> ret = new LinkedList<String>();
		
		final ResultSet rs = md.getSchemas();
		try	{
			while (rs.next())
				ret.add(rs.getString(1));
			}
		finally
			{
			rs.close();
			}
		
		if (ret.isEmpty())
			ret.add("");
		
		return (ret);
		}
	
	public Map<String, String> getTables(String catalog, String schema) throws SQLException
		{
		final Map<String, String> ret = new LinkedHashMap<String, String>();
		
		final ResultSet rs = md.getTables(catalog, schema, "%", null);
		try	{
			while (rs.next())
				ret.put(rs.getString(3), rs.getString(4));
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	public Map<String, String> getProcedures(String catalog, String schema) throws SQLException
		{
		final Map<String, String> ret = new LinkedHashMap<String, String>();
		
		final ResultSet rs = md.getProcedures(catalog, schema, "%");
		try	{
			while (rs.next())
				ret.put(rs.getString(3), (rs.getShort(8) == DatabaseMetaData.procedureReturnsResult) ? ProcedureDescription.FUNCTION : ProcedureDescription.PROCEDURE);
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	public Map<String, Integer> getDatatypes(String catalog, String schema) throws SQLException
		{
		final Map<String, Integer> ret = new LinkedHashMap<String, Integer>();
		
		final ResultSet rs = md.getUDTs(catalog, schema, "%", null);
		try	{
			while (rs.next())
				ret.put(rs.getString(3), rs.getInt(5));
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	public TableDescription getTableDescription(String catalog, String schema, String table) throws SQLException
		{
		String type = "";
		String comment = "";
		
		final ResultSet rs = md.getTables(catalog, schema, table, null);
		try	{
			if (!rs.next())
				throw new SQLException("Table not found: " + catalog + ", " + schema + ", " + table);
			
			type = rs.getString(4);
			comment = notNull(rs.getString(5));
			}
		finally
			{
			rs.close();
			}
		
		return (new TableDescription(catalog, schema, table, comment, type,
				getPrimaryKey(catalog, schema, table),
				getColumns(catalog, schema, table),
				getIndices(catalog, schema, table),
				getReferencedKeys(catalog, schema, table),
				getReferencingKeys(catalog, schema, table),
				getTablePrivileges(catalog, schema, table)
				));
		}
	
	private PrimaryKeyDescription getPrimaryKey(String catalog, String schema, String table) throws SQLException
		{
		PrimaryKeyDescription ret = null;
		
		final ResultSet rs = md.getPrimaryKeys(catalog, schema, table);
		try	{
			String name = null;
			// getPrimaryKeys returns columns ordered by name, not ordinal position
			final SortedMap<Short, String> columns = new TreeMap<Short, String>();
			while (rs.next())
				{
				if (name == null)
					name = notNull(rs.getString(6));
				columns.put(rs.getShort(5), rs.getString(4));
				}
			
			if (!columns.isEmpty())
				ret = new PrimaryKeyDescription(name, columns.values());
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	private List<ColumnDescription> getColumns(String catalog, String schema, String table) throws SQLException
		{
		final List<ColumnDescription> ret = new LinkedList<ColumnDescription>();
		
		final ResultSet rs = md.getColumns(catalog, schema, table, "%");
		try	{
			while (rs.next())
				{
				final String colName = rs.getString(4);
				ret.add(new ColumnDescription(
						colName,
						notNull(rs.getString(12)),
						rs.getInt(5),
						rs.getString(6),
						rs.getInt(7),
						rs.getInt(9),
						rs.getInt(11) != DatabaseMetaData.columnNoNulls,
						rs.getString(13)
						));
				}
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	private List<IndexDescription> getIndices(String catalog, String schema, String table) throws SQLException
		{
		final List<IndexDescription> ret = new LinkedList<IndexDescription>();
		
		final ResultSet rs = md.getIndexInfo(catalog, schema, table, false, true);
		try	{
			String name = null;
			boolean nonunique = false;
			final List<String> columns = new LinkedList<String>();
			while (rs.next())
				{
				if (rs.getShort(7) == DatabaseMetaData.tableIndexStatistic)
					continue;
				
				final String s = notNull(rs.getString(6));
				if ((name != null) && !s.equals(name))
					{
					ret.add(new IndexDescription(name, !nonunique, columns));
					columns.clear();	// Column names are copied, so we can reuse the list
					}
				name = s;
				nonunique = rs.getBoolean(4);
				columns.add(rs.getString(9));
				}
			
			if (name != null)
				ret.add(new IndexDescription(name, !nonunique, columns));
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	private List<ForeignKeyDescription> getReferencedKeys(String catalog, String schema, String table) throws SQLException
		{
		final List<ForeignKeyDescription> ret = new LinkedList<ForeignKeyDescription>();
		
		final ResultSet rs = md.getImportedKeys(catalog, schema, table);
		try	{
			String name = null;
			String tableCatalog = null;
			String tableSchema = null;
			String tableName = null;
			final Map<String, String> columns = new LinkedHashMap<String, String>();
			while (rs.next())
				{
				final int ix = rs.getInt(9);
				if ((name != null) && (ix == 1))
					{
					ret.add(new ForeignKeyDescription(name, tableCatalog, tableSchema, tableName, columns));
					columns.clear();	// Column names are copied, so we can reuse the map
					}
				name = notNull(rs.getString(12));
				tableCatalog = rs.getString(1);
				tableSchema = rs.getString(2);
				tableName = rs.getString(3);
				columns.put(rs.getString(8), rs.getString(4));
				}
			
			if (name != null)
				ret.add(new ForeignKeyDescription(name, tableCatalog, tableSchema, tableName, columns));
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	private List<ForeignKeyDescription> getReferencingKeys(String catalog, String schema, String table) throws SQLException
		{
		final List<ForeignKeyDescription> ret = new LinkedList<ForeignKeyDescription>();
		
		final ResultSet rs = md.getExportedKeys(catalog, schema, table);
		try	{
			String name = null;
			String tableCatalog = null;
			String tableSchema = null;
			String tableName = null;
			final Map<String, String> columns = new LinkedHashMap<String, String>();
			while (rs.next())
				{
				final int ix = rs.getInt(9);
				if ((name != null) && (ix == 1))
					{
					ret.add(new ForeignKeyDescription(name, tableCatalog, tableSchema, tableName, columns));
					columns.clear();	// Column names are copied, so we can reuse the map
					}
				name = notNull(rs.getString(12));
				tableCatalog = rs.getString(5);
				tableSchema = rs.getString(6);
				tableName = rs.getString(7);
				columns.put(rs.getString(8), rs.getString(4));
				}
			
			if (name != null)
				ret.add(new ForeignKeyDescription(name, tableCatalog, tableSchema, tableName, columns));
			}
		finally
			{
			rs.close();
			}
		
		return (ret);
		}
	
	private List<PrivilegeDescription> getTablePrivileges(String catalog, String schema, String table)
		{
		final List<PrivilegeDescription> ret = new LinkedList<PrivilegeDescription>();
		
		try	{
			final ResultSet rs = md.getTablePrivileges(catalog, schema, table);
			try	{
				while (rs.next())
					{
					ret.add(new PrivilegeDescription(
							rs.getString(4),
							rs.getString(5),
							rs.getString(6),
							"YES".equals(rs.getString(7))
							));
					}
				}
			finally
				{
				rs.close();
				}
			}
		catch (SQLException e)
			{
			// Some DBMSs don't support reading privileges and they are not crucial for a TableDescription
			}
		
		return (ret);
		}
	
	public ProcedureDescription getProcedureDescription(String catalog, String schema, String proc) throws SQLException
		{
		String type = ProcedureDescription.PROCEDURE;
		String comment = "";
		
		final ResultSet rs0 = md.getProcedures(catalog, schema, proc);
		try	{
			if (!rs0.next())
				throw new SQLException("Procedure not found: " + catalog + ", " + schema + ", " + proc);
			
			type = (rs0.getShort(8) == DatabaseMetaData.procedureReturnsResult) ? ProcedureDescription.FUNCTION : ProcedureDescription.PROCEDURE;
			comment = notNull(rs0.getString(7));
			}
		finally
			{
			rs0.close();
			}
		
		final List<ParameterDescription> params = new LinkedList<ParameterDescription>();
		final List<ParameterDescription> results = new LinkedList<ParameterDescription>();
		
		final ResultSet rs = md.getProcedureColumns(catalog, schema, proc, "%");
		try	{
			while (rs.next())
				{
				switch (rs.getShort(5))
					{
					case DatabaseMetaData.procedureColumnResult:
					case DatabaseMetaData.procedureColumnReturn:
						results.add(new ParameterDescription(Direction.OUT, rs.getString(4), notNull(rs.getString(13)), rs.getInt(6), rs.getString(7), Math.max(rs.getInt(8), rs.getInt(9)), rs.getInt(10), rs.getInt(12) != DatabaseMetaData.columnNoNulls));
						break;
					case DatabaseMetaData.procedureColumnIn:
						params.add(new ParameterDescription(Direction.IN, rs.getString(4), notNull(rs.getString(13)), rs.getInt(6), rs.getString(7), Math.max(rs.getInt(8), rs.getInt(9)), rs.getInt(10), rs.getInt(12) != DatabaseMetaData.columnNoNulls));
						break;
					case DatabaseMetaData.procedureColumnOut:
						params.add(new ParameterDescription(Direction.OUT, rs.getString(4), notNull(rs.getString(13)), rs.getInt(6), rs.getString(7), Math.max(rs.getInt(8), rs.getInt(9)), rs.getInt(10), rs.getInt(12) != DatabaseMetaData.columnNoNulls));
						break;
					case DatabaseMetaData.procedureColumnInOut:
					default:
						params.add(new ParameterDescription(Direction.INOUT, rs.getString(4), notNull(rs.getString(13)), rs.getInt(6), rs.getString(7), Math.max(rs.getInt(8), rs.getInt(9)), rs.getInt(10), rs.getInt(12) != DatabaseMetaData.columnNoNulls));
						break;
					}
				}
			}
		finally
			{
			rs.close();
			}
		
		return (new ProcedureDescription(catalog, schema, proc, comment, type, params, results));
		}
	
	private String notNull(String s)
		{
		return ((s == null) ? "" : s);
		}
	}
