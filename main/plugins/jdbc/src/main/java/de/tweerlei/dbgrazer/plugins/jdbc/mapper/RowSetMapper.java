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
package de.tweerlei.dbgrazer.plugins.jdbc.mapper;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowCallbackHandler;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TypeDescription;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * RowCallbackHandler that maps rows to RowSets
 * 
 * @author Robert Wruck
 */
public abstract class RowSetMapper implements RowCallbackHandler
	{
	private final SQLGeneratorService sqlGenerator;
	private final SQLDialect dialect;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 */
	public RowSetMapper(SQLGeneratorService sqlGenerator, SQLDialect dialect)
		{
		this.sqlGenerator = sqlGenerator;
		this.dialect = dialect;
		}
	
	/**
	 * Get the extracted RowSets
	 * @return Map: Name -> RowSet
	 */
	public abstract Map<String, RowSetImpl> getRowSets();
	
	/**
	 * Get a ColumnDef for a result column
	 * @param rsmd ResultSetMetaData
	 * @param column Column index (1-based)
	 * @param target TargetDef
	 * @return ColumnDef
	 * @throws SQLException on error
	 */
	protected final ColumnDef getColumnDef(ResultSetMetaData rsmd, int column, TargetDef target) throws SQLException
		{
		final String tn = rsmd.getTableName(column);
		final QualifiedName qn = StringUtils.empty(tn) ? null : new QualifiedName(rsmd.getCatalogName(column), rsmd.getSchemaName(column), tn);
		final String label = rsmd.getColumnLabel(column);
		final String name = rsmd.getColumnName(column);
		final TypeDescription type = new TypeDescription(rsmd.getColumnTypeName(column), rsmd.getColumnType(column), rsmd.getPrecision(column), rsmd.getScale(column));
		
		return (new ColumnDefImpl(
				sqlGenerator.formatColumnName(label),
				ColumnType.forSQLType(type),
				dialect.dataTypeToString(type),
				target,
				qn,
				name
				));
		}
	}
