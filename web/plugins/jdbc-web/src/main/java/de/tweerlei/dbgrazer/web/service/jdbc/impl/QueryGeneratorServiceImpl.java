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
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
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
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.model.StatementWriter;
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
		final String stmt = sqlGenerator.generatePKSelect(t, Style.INDENTED, dialect);
		
		return (new QueryImpl("", null, null, stmt, null, queryService.findQueryType(JdbcConstants.QUERYTYPE_MULTIPLE), getPKParameters(t, dialect), null, null));
		}
	
	@Override
	public Query createInsertQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt, String pkExpr, Map<Integer, String> values)
		{
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
				pkColumns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, null, null));
			else if (values == null)
				{
				columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, null, null));
				String fkTable = null;
				for (ForeignKeyDescription fk : t.getReferencedKeys())
					{
					if ((fk.getColumns().size() == 1) && fk.getColumns().keySet().iterator().next().equals(c.getName()))
						fkTable = fk.getTableName().toString();
					}
				params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName(), dialect), type, fkTable));
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
					columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, null, null));
					params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName(), dialect), type, null));
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
			sqlWriter.writeInsert(t.getName(), columns, null, pkColumns, Collections.<Object>singletonList(pkExpr));
		else
			sqlWriter.writeInsert(t.getName(), columns, null);
		
		final QueryType tp;
		if (pk.isEmpty() || pkFilled)
			tp = queryService.findQueryType(JdbcConstants.QUERYTYPE_DML);
		else
			tp = queryService.findQueryType(JdbcConstants.QUERYTYPE_DML_KEY);
		
		return (new QueryImpl("", null, null, sw.toString(), null, tp, params, null, null));
		}
	
	@Override
	public Query createUpdateQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt, boolean includePK)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(t.getColumns().size());
		final List<ParameterDef> params = new ArrayList<ParameterDef>(t.getColumns().size());
		final Set<Integer> pk = t.getPKColumns();
		int i = 0;
		for (ColumnDescription c : t.getColumns())
			{
			final ColumnType type = ColumnType.forSQLType(c.getType());
			columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, null, null));
			if (!pk.contains(i))
				{
				String fkTable = null;
				for (ForeignKeyDescription fk : t.getReferencedKeys())
					{
					if ((fk.getColumns().size() == 1) && fk.getColumns().keySet().iterator().next().equals(c.getName()))
						fkTable = fk.getTableName().toString();
					}
				params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName(), dialect), type, fkTable));
				}
			i++;
			}
		
		if (includePK)
			params.addAll(getPKParameters(t, dialect));
		
		final StringWriter sw = new StringWriter();
		final SQLWriter sqlWriter = factory.getSQLWriter(new StatementWriter(sw), dialect, true);
		
		sqlWriter.writeUpdate(t.getName(), columns, null, null, t.getPKColumns());
		
		return (new QueryImpl("", null, null, sw.toString(), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), params, null, null));
		}
	
	@Override
	public Query createDeleteQuery(TableDescription t, SQLDialect dialect, DataFormatter fmt)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(t.getColumns().size());
		for (ColumnDescription c : t.getColumns())
			{
			final ColumnType type = ColumnType.forSQLType(c.getType());
			columns.add(new ColumnDefImpl(c.getName(), type, dialect.dataTypeToString(c.getType()), null, null, null));
			}
		
		final StringWriter sw = new StringWriter();
		final SQLWriter sqlWriter = factory.getSQLWriter(new StatementWriter(sw), dialect, true);
		
		sqlWriter.writeDelete(t.getName(), columns, null, t.getPKColumns());
		
		return (new QueryImpl("", null, null, sw.toString(), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), getPKParameters(t, dialect), null, null));
		}
	
	private List<ParameterDef> getPKParameters(TableDescription t, SQLDialect dialect)
		{
		final Set<Integer> pk = t.getPKColumns();
		final List<ParameterDef> params = new ArrayList<ParameterDef>(pk.size());
		for (Integer i : pk)
			{
			final ColumnDescription c = t.getColumns().get(i);
			final ColumnType type = ColumnType.forSQLType(c.getType());
			params.add(new ParameterDefImpl(sqlGenerator.formatColumnName(c.getName(), dialect), type, null));
			}
		return (params);
		}
	
	@Override
	public Query createAddColumnQuery(TableDescription t, SQLDialect dialect, ColumnDescription c)
		{
		return (new QueryImpl("", null, null, dialect.addColumn(t, c), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createAlterColumnQuery(TableDescription t, SQLDialect dialect, ColumnDescription prev, ColumnDescription c)
		{
		return (new QueryImpl("", null, null, dialect.modifyColumn(t, c, prev), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createDropColumnQuery(TableDescription t, SQLDialect dialect, ColumnDescription c)
		{
		return (new QueryImpl("", null, null, dialect.removeColumn(t, c), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createAddIndexQuery(TableDescription t, SQLDialect dialect, IndexDescription i)
		{
		return (new QueryImpl("", null, null, dialect.createIndex(t, i), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createDropIndexQuery(TableDescription t, SQLDialect dialect, IndexDescription i)
		{
		return (new QueryImpl("", null, null, dialect.dropIndex(t, i), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createAddPrimaryKeyQuery(TableDescription t, SQLDialect dialect, PrimaryKeyDescription i)
		{
		return (new QueryImpl("", null, null, dialect.addPrimaryKey(t, i), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createDropPrimaryKeyQuery(TableDescription t, SQLDialect dialect, PrimaryKeyDescription i)
		{
		return (new QueryImpl("", null, null, dialect.removePrimaryKey(t, i), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createAddForeignKeyQuery(TableDescription t, SQLDialect dialect, ForeignKeyDescription f)
		{
		return (new QueryImpl("", null, null, dialect.addForeignKey(t, f), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createDropForeignKeyQuery(TableDescription t, SQLDialect dialect, ForeignKeyDescription f)
		{
		return (new QueryImpl("", null, null, dialect.removeForeignKey(t, f), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createGrantQuery(TableDescription t, SQLDialect dialect, PrivilegeDescription p)
		{
		return (new QueryImpl("", null, null, dialect.grantPrivilege(t, p), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createRevokeQuery(TableDescription t, SQLDialect dialect, PrivilegeDescription p)
		{
		return (new QueryImpl("", null, null, dialect.revokePrivilege(t, p), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createCreateTableQuery(TableDescription t, SQLDialect dialect)
		{
		return (new QueryImpl("", null, null, dialect.createTable(t), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createAlterTableQuery(TableDescription prev, SQLDialect dialect, TableDescription t)
		{
		return (new QueryImpl("", null, null, dialect.modifyTable(prev, t), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	
	@Override
	public Query createDropTableQuery(TableDescription t, SQLDialect dialect)
		{
		return (new QueryImpl("", null, null, dialect.dropTable(t), null, queryService.findQueryType(JdbcConstants.QUERYTYPE_DML), null, null, null));
		}
	}
