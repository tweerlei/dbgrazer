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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.JdbcConstants;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.StatementWriter;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.service.jdbc.QueryGeneratorService;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Generate SQL statement fragments
 *
 * @author Robert Wruck
 */
@Service
public class QueryGeneratorServiceImpl implements QueryGeneratorService
	{
	private final QueryService queryService;
	private final SQLGeneratorService sqlGenerator;
	private final DataFormatterFactory factory;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param sqlGenerator SQLGeneratorService
	 * @param factory DataFormatterFactory
	 */
	@Autowired
	public QueryGeneratorServiceImpl(QueryService queryService, SQLGeneratorService sqlGenerator,
			DataFormatterFactory factory)
		{
		this.queryService = queryService;
		this.sqlGenerator = sqlGenerator;
		this.factory = factory;
		}
	
	@Override
	public Query createSelectQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt)
		{
		final List<ParameterDef> params = new ArrayList<ParameterDef>(t.getColumns().size());
		final Set<Integer> pk = t.getPKColumns();
		int i = 0;
		for (ColumnDescription c : t.getColumns())
			{
			final ColumnType type = ColumnType.forSQLType(c.getType());
			if (pk.contains(i))
				params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName()), type, null));
			i++;
			}
		
		final String stmt = sqlGenerator.generatePKSelect(t, Style.INDENTED, dialect);
		
		return (new QueryImpl("select", null, null, stmt, queryService.findQueryType(JdbcConstants.QUERYTYPE_MULTIPLE), params, null, null));
		}
	
	@Override
	public Query createInsertQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt, String pkExpr, Map<Integer, String> values)
		{
		final String tableName = dialect.getQualifiedTableName(t.getName());
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(t.getColumns().size());
		final List<ParameterDef> params = new ArrayList<ParameterDef>(t.getColumns().size());
		final Set<Integer> pk = t.getPKColumns();
		final List<ColumnDef> pkColumns = new ArrayList<ColumnDef>(1);
		final boolean hasPK = (pk.size() == 1) && !StringUtils.empty(pkExpr);
		boolean pkFilled = false;
		int i = 0;
		int j = 0;
		for (ColumnDescription c : t.getColumns())
			{
			final ColumnType type = ColumnType.forSQLType(c.getType());
			if (hasPK && pk.contains(i))
				pkColumns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, t.getName(), c.getName()));
			else if (values == null)
				{
				columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, t.getName(), c.getName()));
				String fkTable = null;
				for (ForeignKeyDescription fk : t.getReferencedKeys())
					{
					if ((fk.getColumns().size() == 1) && fk.getColumns().keySet().iterator().next().equals(c.getName()))
						fkTable = fk.getTableName().toString();
					}
				params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName()), type, fkTable));
				j++;
				}
			else
				{
				// Only include non-NULL columns in generated statement; reorder values to match the included columns
				final String value = values.get(i);
				if (j != i)
					{
					if (value == null)
						values.remove(j);
					else
						values.put(j, value);
					values.remove(i);
					}
				if ((type == ColumnType.BOOLEAN) || (fmt.parse(type, value) != null))
					{
					columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, t.getName(), c.getName()));
					params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName()), type, null));
					if (pk.contains(i))
						pkFilled = true;
					j++;
					}
				}
			i++;
			}
		
		final StringWriter sw = new StringWriter();
		final SQLWriter sqlWriter = factory.getSQLWriter(new StatementWriter(sw), dialect, true);
		
		if (!pkColumns.isEmpty())
			sqlWriter.writeInsert(tableName, columns, null, pkColumns, Collections.<Object>singletonList(pkExpr));
		else
			sqlWriter.writeInsert(tableName, columns, null);
		
		final QueryType tp;
		if (pk.isEmpty() || pkFilled)
			tp = queryService.findQueryType(JdbcConstants.QUERYTYPE_DML);
		else
			tp = queryService.findQueryType(JdbcConstants.QUERYTYPE_DML_KEY);
		
		return (new QueryImpl("insert", null, null, sw.toString(), tp, params, null, null));
		}
	
	@Override
	public Query createUpdateQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt, boolean includePK)
		{
		final String tableName = dialect.getQualifiedTableName(t.getName());
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(t.getColumns().size());
		final List<ParameterDef> params = new ArrayList<ParameterDef>(t.getColumns().size());
		final List<ParameterDef> idParams = new ArrayList<ParameterDef>();
		final Set<Integer> pk = t.getPKColumns();
		int i = 0;
		for (ColumnDescription c : t.getColumns())
			{
			final ColumnType type = ColumnType.forSQLType(c.getType());
			columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, t.getName(), c.getName()));
			if (pk.contains(i))
				idParams.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName()), type, null));
			else
				{
				String fkTable = null;
				for (ForeignKeyDescription fk : t.getReferencedKeys())
					{
					if ((fk.getColumns().size() == 1) && fk.getColumns().keySet().iterator().next().equals(c.getName()))
						fkTable = fk.getTableName().toString();
					}
				params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName()), type, fkTable));
				}
			i++;
			}
			
		if (includePK)
			params.addAll(idParams);
		
		final StringWriter sw = new StringWriter();
		final SQLWriter sqlWriter = factory.getSQLWriter(new StatementWriter(sw), dialect, true);
		
		sqlWriter.writeUpdate(tableName, columns, null, null, t.getPKColumns());
		
		return (new QueryImpl("update", null, null, sw.toString(), queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), params, null, null));
		}
	
	@Override
	public Query createDeleteQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt)
		{
		final String tableName = dialect.getQualifiedTableName(t.getName());
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(t.getColumns().size());
		final List<ParameterDef> params = new ArrayList<ParameterDef>(t.getColumns().size());
		final Set<Integer> pk = t.getPKColumns();
		int i = 0;
		for (ColumnDescription c : t.getColumns())
			{
			final ColumnType type = ColumnType.forSQLType(c.getType());
			columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, t.getName(), c.getName()));
			if (pk.contains(i))
				params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName()), type, null));
			i++;
			}
		
		final StringWriter sw = new StringWriter();
		final SQLWriter sqlWriter = factory.getSQLWriter(new StatementWriter(sw), dialect, true);
		
		sqlWriter.writeDelete(tableName, columns, null, t.getPKColumns());
		
		return (new QueryImpl("insert", null, null, sw.toString(), queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), params, null, null));
		}
	}
