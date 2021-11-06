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
package de.tweerlei.dbgrazer.web.formatter.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.util.ObjectUtils;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public class SQLWriterImpl implements SQLWriter
	{
	private static final Object NO_VALUE = new Object();
	
	private final StatementHandler w;
	private final SQLDialect dialect;
	private final DataFormatter fmt;
	private final boolean pretty;
	
	/**
	 * Constructor
	 * @param w StatementHandler
	 * @param d SQLDialect
	 * @param fmt DataFormatter
	 * @param pretty Pretty print statements
	 */
	public SQLWriterImpl(StatementHandler w, SQLDialect d, DataFormatter fmt, boolean pretty)
		{
		this.w = w;
		this.dialect = d;
		this.fmt = fmt;
		this.pretty = pretty;
		}
	
	@Override
	public void writeDelete(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
		{
		final StringBuilder sb = new StringBuilder();
		
		sb.append("DELETE FROM ");
		sb.append(dialect.getQualifiedTableName(tableName));
		if (pretty)
			sb.append("\nWHERE ");
		else
			sb.append(" WHERE ");
		
		writeWhere(sb, columns, values, pk);
		
		w.statement(sb.toString());
		}
	
	@Override
	public boolean checkUpdate(ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
		{
		final Iterator<Object> oi = oldValues.getValues().iterator();
		final Iterator<Object> ni = newValues.getValues().iterator();
		int i = 0;
		while (oi.hasNext() && ni.hasNext())
			{
			final Object o = oi.next();
			final Object n = ni.next();
			if (!pk.contains(i) && !ObjectUtils.equals(o, n))
				return (true);
			i++;
			}
		
		return (false);
		}
	
	@Override
	public boolean writeUpdate(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
		{
		final StringBuilder sb = new StringBuilder();
		
		sb.append("UPDATE ");
		sb.append(dialect.getQualifiedTableName(tableName));
		if (pretty)
			sb.append("\n   SET ");
		else
			sb.append(" SET ");
		
		final Iterator<Object> oi = (oldValues == null) ? null : oldValues.getValues().iterator();
		final Iterator<Object> ni = (newValues == null) ? null : newValues.getValues().iterator();
		boolean first = true;
		int i = 0;
		for (ColumnDef c : columns)
			{
			final Object o = (oi == null) ? null : oi.next();
			final Object n = (ni == null) ? NO_VALUE : ni.next();
			if (!pk.contains(i) && !ObjectUtils.equals(o, n))
				{
				if (first)
					first = false;
				else if (pretty)
					sb.append(",\n       ");
				else
					sb.append(", ");
				sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
				sb.append(" = ");
				if (n == NO_VALUE)
					sb.append("?");
				else if (n == null)
					sb.append("NULL");
				else
					sb.append(fmt.format(c.getType(), n));
				}
			i++;
			}
		
		if (first)
			return (false);
		
		if (pretty)
			sb.append("\nWHERE ");
		else
			sb.append(" WHERE ");
		
		writeWhere(sb, columns, oldValues, pk);
		
		w.statement(sb.toString());
		return (true);
		}
	
	private void writeWhere(StringBuilder sb, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
		{
		if (!pk.isEmpty())
			{
			boolean first = true;
			for (Integer i : pk)
				{
				final ColumnDef c = columns.get(i);
				if (first)
					first = false;
//				else if (pretty)
//					w.write("\nAND ");
				else
					sb.append(" AND ");
				sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
				if (values == null)
					sb.append(" = ?");
				else
					{
					final Object o = values.getValues().get(i);
					if (o == null)
						sb.append(" IS NULL");
					else
						{
						sb.append(" = ");
						sb.append(fmt.format(c.getType(), o));
						}
					}
				}
			}
		else
			{
			final Iterator<Object> oi = (values == null) ? null : values.getValues().iterator();
			boolean first = true;
			for (ColumnDef c : columns)
				{
				if (first)
					first = false;
//				else if (pretty)
//					w.write("\nAND ");
				else
					sb.append(" AND ");
				sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
				if (oi == null)
					sb.append(" = ?");
				else
					{
					final Object o = oi.next();
					if (o == null)
						sb.append(" IS NULL");
					else
						{
						sb.append(" = ");
						sb.append(fmt.format(c.getType(), o));
						}
					}
				}
			}
		}
	
	@Override
	public void writeInsert(QualifiedName tableName, List<ColumnDef> columns, ResultRow values)
		{
		writeInsert(tableName, columns, values, null, null);
		}
	
	@Override
	public void writeInsert(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, List<ColumnDef> pkColumns, List<Object> pkValues)
		{
		final StringBuilder sb = new StringBuilder();
		
		sb.append("INSERT INTO ");
		sb.append(dialect.getQualifiedTableName(tableName));
		if (pretty)
			sb.append("\n       (");
		else
			sb.append(" (");
		
		if ((pkColumns != null) && !pkColumns.isEmpty())
			{
			writeColumnList(sb, pkColumns, pkValues, "");
			if (!columns.isEmpty())
				sb.append(", ");
			}
		writeColumnList(sb, columns, (values == null) ? null : values.getValues(), "");
		
		if (pretty)
			sb.append(")\nVALUES (");
		else
			sb.append(") VALUES (");
		
		if ((pkColumns != null) && !pkColumns.isEmpty())
			{
			for (int i = 0; i < pkColumns.size(); i++)
				{
				if (i > 0)
					sb.append(", ");
				final Object o = (pkValues == null) ? null : pkValues.get(i);
				if (o == null)
					sb.append("NULL");
				else
					sb.append(o.toString());
				i++;
				}
			if (!columns.isEmpty())
				sb.append(", ");
			}
		writeValueList(sb, columns, (values == null) ? null : values.getValues(), false);
		
		sb.append(")");
		
		w.statement(sb.toString());
		}
	
	@Override
	public void writeMerge(QualifiedName tableName, List<ColumnDef> columns, List<ResultRow> rows, Set<Integer> pk)
		{
		if (rows.isEmpty())
			return;
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append("MERGE INTO ");
		sb.append(dialect.getQualifiedTableName(tableName));
		sb.append(" dst USING (");
		
		boolean first = true;
		for (ResultRow values : rows)
			{
			if (first)
				{
				first = false;
				if (pretty)
					sb.append("\n          SELECT ");
				else
					sb.append("SELECT ");
				}
			else
				{
				if (pretty)
					sb.append("\nUNION ALL SELECT ");
				else
					sb.append(" UNION ALL SELECT ");
				}
			
			writeValueList(sb, columns, (values == null) ? null : values.getValues(), true);
			
			if (dialect.getDefaultTableName() != null)
				{
				sb.append(" FROM ");
				sb.append(dialect.getDefaultTableName());
				}
			}
		
		if (pretty)
			sb.append("\n) src ON (");
		else
			sb.append(") src ON (");
		
		first = true;
		for (Integer i : pk)
			{
			final ColumnDef c = columns.get(i);
			if (first)
				first = false;
//			else if (pretty)
//				w.write("\nAND ");
			else
				sb.append(" AND ");
			sb.append("dst.");
			sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
			sb.append(" = src.");
			sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
			}
		
		sb.append(")");
		
		if (columns.size() > pk.size())
			{
			if (pretty)
				sb.append("\nWHEN MATCHED THEN UPDATE SET ");
			else
				sb.append(" WHEN MATCHED THEN UPDATE SET ");
			
			first = true;
			int i = 0;
			for (ColumnDef c : columns)
				{
				if (!pk.contains(i))
					{
					if (first)
						first = false;
					else
						sb.append(", ");
					
//					w.write("dst.");
					sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
					sb.append(" = src.");
					sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
					}
				i++;
				}
			}
		
		if (pretty)
			sb.append("\nWHEN NOT MATCHED THEN INSERT (");
		else
			sb.append(" WHEN NOT MATCHED THEN INSERT (");
		
//		writeColumnList(columns, null, "dst.");
		writeColumnList(sb, columns, null, "");
		
		if (pretty)
			sb.append(")\nVALUES (");
		else
			sb.append(") VALUES (");
		
		writeColumnList(sb, columns, null, "src.");
		
		sb.append(")");
		
		w.statement(sb.toString());
		}
	
	private void writeValueList(StringBuilder sb, List<ColumnDef> columns, List<Object> values, boolean label)
		{
		boolean first = true;
		Iterator<Object> oi = (values == null) ? null : values.iterator();
		for (ColumnDef c : columns)
			{
			final Object o = (oi == null) ? NO_VALUE : oi.next();
			if (label || (o != null) || (first && !oi.hasNext()))
				{
				if (first)
					first = false;
				else
					sb.append(", ");
				if (o == NO_VALUE)
					sb.append("?");
				else if (o == null)
					sb.append("NULL");
				else
					sb.append(fmt.format(c.getType(), o));
				if (label)
					{
					sb.append(" AS ");
					sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
					}
				}
			}
		}
	
	private void writeColumnList(StringBuilder sb, List<ColumnDef> columns, List<Object> values, String prefix)
		{
		boolean first = true;
		Iterator<Object> oi = (values == null) ? null : values.iterator();
		for (ColumnDef c : columns)
			{
			final Object o = (oi == null) ? NO_VALUE : oi.next();
			if ((o != null) || (first && !oi.hasNext()))
				{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(prefix);
				sb.append((c.getSourceColumn() == null) ? dialect.quoteIdentifier(c.getName()) : c.getSourceColumn());
				}
			}
		}
	
	@Override
	public void writeComment(String comment)
		{
		w.comment(comment);
		}
	}
