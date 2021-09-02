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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TypeDescription;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Access ResultSet contents
 * 
 * @author Robert Wruck
 */
public class ResultSetAccessor
	{
	private static final class ResultColumnInfo
		{
		public final String label;
		public final TypeDescription type;
		public final QualifiedName sourceObject;
		public final String sourceColumn;
		
		public ResultColumnInfo(String label, TypeDescription type, QualifiedName sourceObject, String sourceColumn)
			{
			this.label = label;
			this.type = type;
			this.sourceObject = sourceObject;
			this.sourceColumn = sourceColumn;
			}
		}
	
	private final SQLGeneratorService sqlGenerator;
	private final SQLDialect dialect;
	private final Calendar calendar;
	private List<ResultColumnInfo> columns;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 * @param timeZone TimeZone
	 */
	public ResultSetAccessor(SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone)
		{
		this.sqlGenerator = sqlGenerator;
		this.dialect = dialect;
		this.calendar = Calendar.getInstance(timeZone);
		}
	
	private List<ResultColumnInfo> getColumnInfo(ResultSet rs) throws SQLException
		{
		if (columns == null)
			{
			final ResultSetMetaData rsmd = rs.getMetaData();
			final int n = rsmd.getColumnCount();
			columns = new ArrayList<ResultColumnInfo>(n);
			for (int i = 1; i <= n; i++)
				{
				final String name = rsmd.getColumnName(i);
				final String label = rsmd.getColumnLabel(i);
				final String tn = rsmd.getTableName(i);
				final QualifiedName qn = StringUtils.empty(tn) ? null : new QualifiedName(rsmd.getCatalogName(i), rsmd.getSchemaName(i), tn);
				final TypeDescription type = new TypeDescription(rsmd.getColumnTypeName(i), rsmd.getColumnType(i), rsmd.getPrecision(i), rsmd.getScale(i));
				
				columns.add(new ResultColumnInfo(label, type, qn, name));
				}
			}
		return (columns);
		}
	
	private ResultColumnInfo getColumnInfo(ResultSet rs, int column) throws SQLException
		{
		return (getColumnInfo(rs).get(column - 1));
		}
	
	/**
	 * Get the number of columns
	 * @param rs ResultSet
	 * @return Number of columns
	 * @throws SQLException on error
	 */
	public int getColumnCount(ResultSet rs) throws SQLException
		{
		return (getColumnInfo(rs).size());
		}
	
	/**
	 * Get a ColumnDef for a result column
	 * @param rs ResultSet
	 * @param column Column index (1-based)
	 * @param target TargetDef
	 * @return ColumnDef
	 * @throws SQLException on error
	 */
	public ColumnDef getColumnDef(ResultSet rs, int column, TargetDef target) throws SQLException
		{
		final ResultColumnInfo info = getColumnInfo(rs, column);
		
		return (new ColumnDefImpl(
				sqlGenerator.formatColumnName(info.label, dialect),
				ColumnType.forSQLType(info.type),
				dialect.dataTypeToString(info.type),
				target,
				info.sourceObject,
				info.sourceColumn
				));
		}
	
	/**
	 * Get the vaule of the given column of the ResultSet's current row
	 * @param rs ResultSet
	 * @param column Column index (1-based)
	 * @return Object
	 * @throws SQLException on error
	 */
	public Object getObject(ResultSet rs, int column) throws SQLException
		{
		final ResultColumnInfo info = getColumnInfo(rs, column);
		
		switch (info.type.getType())
			{
			case Types.DATE:
				return (rs.getDate(column, calendar));
			case Types.TIME:
				return (rs.getTime(column, calendar));
			case Types.TIMESTAMP:
				return (rs.getTimestamp(column, calendar));
			default:
				return (rs.getObject(column));
			}
		}
	}
