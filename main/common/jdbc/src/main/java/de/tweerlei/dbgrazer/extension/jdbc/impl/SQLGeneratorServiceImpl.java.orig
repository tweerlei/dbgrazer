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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.sql.SQLFormat;
import de.tweerlei.dbgrazer.extension.sql.handler.MultilineSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.handler.SimpleSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.handler.TokenListSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLConsumer;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLParser;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.util.OrderedSet;

/**
 * Generate SQL statement fragments
 *
 * @author Robert Wruck
 */
@Service
public class SQLGeneratorServiceImpl implements SQLGeneratorService
	{
	private static final String NAME_SEPARATOR = ".";	// TODO: Get from SQLDialect
	private static final String INNER_TABLE_NAME = "a_";
	
	private final SQLFormat sqlFormat;
	
	/**
	 * Constructor
	 * @param formats All known SQLFormats
	 */
	@Autowired(required = false)
	public SQLGeneratorServiceImpl(List<SQLFormat> formats)
		{
		final OrderedSet<SQLFormat> orderedFormats = new OrderedSet<SQLFormat>(formats);
		if (orderedFormats.isEmpty())
			this.sqlFormat = null;
		else
			this.sqlFormat = orderedFormats.last();
		}
	
	/**
	 * Constructor used when no SQLFormat instances are available
	 */
	public SQLGeneratorServiceImpl()
		{
		this(Collections.<SQLFormat>emptyList());
		}
	
	@Override
	public final String formatColumnName(String name)
		{
		if (StringUtils.empty(name))
			return ("");
		
		final int l = name.length();
		final StringBuilder sb = new StringBuilder(l);
		
		boolean upper = true;
		for (int i = 0; i < l; i++)
			{
			final char c = name.charAt(i);
			if (Character.isLetter(c))
				{
				sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
				upper = false;
				}
			else
				{
				sb.append(c);
				upper = true;
				}
			}
		
		return (sb.toString());
		}
	
	@Override
	public String generateSelect(TableDescription t, Style style, Joins joins, String where, OrderBy orderBy, SQLDialect dialect)
		{
		return (generateSelect(t, style, joins, false, where, orderBy, dialect));
		}
	
	@Override
	public String generatePKSelect(TableDescription t, Style style, SQLDialect dialect)
		{
		return (generateSelect(t, style, Joins.NONE, true, null, OrderBy.NONE, dialect));
		}
	
	private String generateSelect(TableDescription t, Style style, Joins joins, boolean where, String addWhere, OrderBy orderBy, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		final Set<Integer> pk = t.getPKColumns();
		
		boolean first;
		int i;
		
		sb.appendName("SELECT");
		first = true;
		i = 0;
		for (ColumnDescription c : t.getColumns())
			{
			if (!where || !pk.contains(i))
				{
				if (first)
					first = false;
				else
					sb.appendOperator(",");
				if (joins != Joins.NONE)
					sb.appendName("t0").appendOperator(NAME_SEPARATOR);
				sb.appendName(c.getName());
				if (joins != Joins.NONE)
					sb.appendName("AS").appendName(c.getName());
				}
			i++;
			}
		if (first)
			{
			// select a dummy column
			sb.appendName("NULL");
			}
		sb.appendName("FROM").appendName(dialect.getQualifiedTableName(t.getName()));
		
		if (joins != Joins.NONE)
			{
			sb.appendName("t0");
			i = 1;
			for (ForeignKeyDescription fk : t.getReferencedKeys())
				{
				boolean nullable = false;
				for (String cn : fk.getColumns().keySet())
					{
					for (ColumnDescription c : t.getColumns())
						{
						if (cn.equals(c.getName()))
							{
							if (c.isNullable())
								nullable = true;
							break;
							}
						}
					}
				if (nullable)
					{
					if (joins == Joins.INNER)
						continue;
					sb.appendName("LEFT").appendName("JOIN");
					}
				else
					sb.appendName("INNER").appendName("JOIN");
				sb.appendName(dialect.getQualifiedTableName(fk.getTableName())).appendName("t" + i);
				sb.appendName("ON").openBrace();
				first = true;
				for (Map.Entry<String, String> ent : fk.getColumns().entrySet())
					{
					if (first)
						first = false;
					else
						sb.appendName("AND");
					sb.appendName("t0").appendOperator(NAME_SEPARATOR).appendName(ent.getKey()).appendOperator("=").appendName("t" + i).appendOperator(NAME_SEPARATOR).appendName(ent.getValue());
					}
				sb.closeBrace();
				i++;
				}
			}
		
		if (where)
			{
			if (!pk.isEmpty())
				{
				sb.appendName("WHERE");
				first = true;
				i = 0;
				for (ColumnDescription c : t.getColumns())
					{
					if (pk.contains(i))
						{
						if (first)
							first = false;
						else
							sb.appendName("AND");
						if (joins != Joins.NONE)
							sb.appendName("t0").appendOperator(NAME_SEPARATOR);
						sb.appendName(c.getName());
						sb.appendOperator("=");
						sb.appendName("?");
						}
					i++;
					}
				}
			}
		else
			{
			final List<String> whereTokens = parseSQL(addWhere);
			if (whereTokens != null)
				{
				sb.appendName("WHERE");
				appendSQL(sb, whereTokens);
				}
			}
		
		if ((orderBy == OrderBy.PK) && (t.getPrimaryKey() != null))
			{
			sb.appendName("ORDER").appendName("BY");
			first = true;
			for (String c : t.getPrimaryKey().getColumns())
				{
				if (first)
					first = false;
				else
					sb.appendOperator(",");
				if (joins != Joins.NONE)
					sb.appendName("t0").appendOperator(NAME_SEPARATOR);
				sb.appendName(c);
				}
			}
		else if ((orderBy == OrderBy.DATA) && (pk.size() < t.getColumns().size()))
			{
			sb.appendName("ORDER").appendName("BY");
			first = true;
			i = 0;
			for (ColumnDescription c : t.getColumns())
				{
				if (!pk.contains(i))
					{
					if (first)
						first = false;
					else
						sb.appendOperator(",");
					sb.appendName(c.getName());
					}
				i++;
				}
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String generateSelectStats(TableDescription t, Style style, String where, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		
		sb.appendName("SELECT").appendString("Nulls").appendName("AS").appendName("Name");
		sb.appendOperator(",").appendName("COUNT").openBrace().appendName("*").closeBrace();
		for (ColumnDescription c : t.getColumns())
			sb.appendOperator(",").appendName("COUNT").openBrace().appendName("*").closeBrace().appendOperator("-").appendName("COUNT").openBrace().appendName(c.getName()).closeBrace().appendName("AS").appendName(c.getName());
		sb.appendName("FROM").appendName(dialect.getQualifiedTableName(t.getName()));
		final List<String> whereTokens = parseSQL(where);
		if (whereTokens != null)
			{
			sb.appendName("WHERE");
			appendSQL(sb, whereTokens);
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String generateSelect(TableDescription t, Style style, String where, String order, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		
		sb.appendName("SELECT").appendName("*").appendName("FROM");
		sb.appendName(dialect.getQualifiedTableName(t.getName()));
		final List<String> whereTokens = parseSQL(where);
		if (whereTokens != null)
			{
			sb.appendName("WHERE");
			appendSQL(sb, whereTokens);
			}
		final List<String> orderTokens = parseSQL(order);
		if (orderTokens != null)
			{
			sb.appendName("ORDER").appendName("BY");
			appendSQL(sb, orderTokens);
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String generateSelectIn(TableDescription t, Style style, Set<Integer> critColumns, int critCount, OrderBy orderBy, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		
		sb.appendName("SELECT").appendName("DISTINCT");
		
		boolean first = true;
		boolean first2 = true;
		for (ColumnDescription c : t.getColumns())
			{
			if (first)
				first = false;
			else
				sb.appendOperator(",");
			sb.appendName(c.getName());
			}
		
		sb.appendName("FROM").appendName(dialect.getQualifiedTableName(t.getName()));
		
		switch (critColumns.size())
			{
			case 0:
				// no where clause
				break;
			case 1:
				sb.appendName("WHERE");
				for (Integer i : critColumns)
					sb.appendName(t.getColumns().get(i).getName());
				sb.appendName("IN").openBrace();
				first = true;
				for (int i = 0; i < critCount; i++)
					{
					if (first)
						first = false;
					else
						sb.appendOperator(",");
					sb.appendName("?");
					}
				sb.closeBrace();
				break;
			default:
				sb.appendName("WHERE").openBrace();
				first = true;
				for (Integer i : critColumns)
					{
					if (first)
						first = false;
					else
						sb.appendOperator(",");
					sb.appendName(t.getColumns().get(i).getName());
					}
				sb.closeBrace().appendName("IN").openBrace();
				first = true;
				for (int i = 0; i < critCount; i++)
					{
					if (first)
						first = false;
					else
						sb.appendOperator(",");
					sb.openBrace();
					first2 = true;
					for (Integer j : critColumns)
						{
						if (first2)
							first2 = false;
						else
							sb.appendOperator(",");
						t.getColumns().get(j);
						sb.appendName("?");
						}
					sb.closeBrace();
					}
				sb.closeBrace();
				break;
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String generateSelectCount(QualifiedName t, Style style, String where, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		
		sb.appendName("SELECT").appendName("COUNT").openBrace().appendName("*").closeBrace().appendName("FROM");
		sb.appendName(dialect.getQualifiedTableName(t));
		final List<String> whereTokens = parseSQL(where);
		if (whereTokens != null)
			{
			sb.appendName("WHERE");
			appendSQL(sb, whereTokens);
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String generateDelete(QualifiedName t, Style style, String where, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		
		sb.appendName("DELETE").appendName("FROM");
		sb.appendName(dialect.getQualifiedTableName(t));
		final List<String> whereTokens = parseSQL(where);
		if (whereTokens != null)
			{
			sb.appendName("WHERE");
			appendSQL(sb, whereTokens);
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String generateTruncate(QualifiedName t, Style style, SQLDialect dialect)
		{
		final SQLConsumer sb = createConsumer(style);
		
		sb.appendName("TRUNCATE").appendName("TABLE");
		sb.appendName(dialect.getQualifiedTableName(t));
		
		return (sb.finish().toString());
		}
	
	@Override
	public String createAggregateQuery(String query, List<AggregateColumn> columns)
		{
		final SQLConsumer sb = createConsumer(Style.MULTILINE);
		
		sb.appendName("SELECT").appendName("COUNT").openBrace().appendName("*").closeBrace();
		int gc = 0;
		for (AggregateColumn col : columns)
			{
			sb.appendOperator(",");
			
			switch (col.getMode())
				{
				case HIDDEN:
					sb.appendString("");
					break;
				case NONE:
					sb.appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName());
					gc++;
					break;
				case MIN:
					sb.appendName("MIN").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					break;
				case MAX:
					sb.appendName("MAX").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					break;
				case COUNT:
					sb.appendName("COUNT").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					break;
				case COUNT_DISTINCT:
					sb.appendName("COUNT").openBrace().appendName("DISTINCT").appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					break;
				case SUM:
					sb.appendName("SUM").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					break;
				case AVG:
					sb.appendName("AVG").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					break;
				}
			
			sb.appendName("AS");
			sb.appendName(col.getName());
			}
		
		sb.appendName("FROM").openBrace();
		sb.appendName(query);
		sb.closeBrace();
		sb.appendName(INNER_TABLE_NAME);
		
		sb.appendName("WHERE").appendNumber("0").appendOperator("=").appendNumber("0");
		for (AggregateColumn col : columns)
			{
			final List<String> tokens = parseSQL(col.getCondition());
			if (tokens != null)
				{
				sb.appendName("AND").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName());
				appendSQL(sb, tokens);
				sb.closeBrace();
				}
			}
		
		if (gc > 0)
			{
			sb.appendName("GROUP").appendName("BY");
			boolean first = true;
			for (AggregateColumn col : columns)
				{
				if (col.getMode() == AggregationMode.NONE)
					{
					if (first)
						first = false;
					else
						sb.appendOperator(",");
					
					sb.appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName());
					}
				}
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String createVariableAggregateQuery(String query, List<AggregateColumn> columns)
		{
		final SQLConsumer sb = createConsumer(Style.MULTILINE);
		
		sb.appendName("SELECT");
		int gc = 0;
		for (AggregateColumn col : columns)
			{
			switch (col.getMode())
				{
				case HIDDEN:
					continue;
				case NONE:
					sb.appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName());
					sb.appendName("AS").appendName(col.getName());
					gc++;
					break;
				case MIN:
					sb.appendName("MIN").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					sb.appendName("AS").appendName("MIN_" + col.getName());
					break;
				case MAX:
					sb.appendName("MAX").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					sb.appendName("AS").appendName("MAX_" + col.getName());
					break;
				case COUNT:
					sb.appendName("COUNT").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					sb.appendName("AS").appendName("COUNT_" + col.getName());
					break;
				case COUNT_DISTINCT:
					sb.appendName("COUNT").openBrace().appendName("DISTINCT").appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					sb.appendName("AS").appendName("DISTINCT_" + col.getName());
					break;
				case SUM:
					sb.appendName("SUM").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					sb.appendName("AS").appendName("SUM_" + col.getName());
					break;
				case AVG:
					sb.appendName("AVG").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).closeBrace();
					sb.appendName("AS").appendName("AVG_" + col.getName());
					break;
				}
			
			sb.appendOperator(",");
			}
		
		sb.appendName("COUNT").openBrace().appendName("*").closeBrace();
		sb.appendName("FROM").openBrace();
		sb.appendName(query);
		sb.closeBrace();
		sb.appendName(INNER_TABLE_NAME);
		
		sb.appendName("WHERE").appendNumber("0").appendOperator("=").appendNumber("0");
		for (AggregateColumn col : columns)
			{
			if (col.getCondition() != null)
				{
				if (StringUtils.empty(col.getCondition()))
					sb.appendName("AND").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).appendName("IS").appendName("NULL").closeBrace();
				else
					sb.appendName("AND").openBrace().appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName()).appendOperator("=").appendName("?").closeBrace();
				}
			}
		
		if (gc > 0)
			{
			sb.appendName("GROUP").appendName("BY");
			boolean first = true;
			for (AggregateColumn col : columns)
				{
				if (col.getMode() == AggregationMode.NONE)
					{
					if (first)
						first = false;
					else
						sb.appendOperator(",");
					
					sb.appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName());
					}
				}
			sb.appendName("ORDER").appendName("BY").appendName("COUNT").openBrace().appendName("*").closeBrace().appendName("DESC");
			for (AggregateColumn col : columns)
				{
				if (col.getMode() == AggregationMode.NONE)
					{
					sb.appendOperator(",");
					sb.appendName(INNER_TABLE_NAME).appendOperator(NAME_SEPARATOR).appendName(col.getName());
					}
				}
			}
		
		return (sb.finish().toString());
		}
	
	@Override
	public String createRowCountQuery(String query)
		{
		return ("SELECT COUNT(*) AS Rows FROM ( " + query + " ) " + INNER_TABLE_NAME);
		}
	
	private List<String> parseSQL(String sql)
		{
		try	{
			final TokenListSQLHandler h = new TokenListSQLHandler();
			new SQLParser(h).parse(sql);
			if (!h.getTokens().isEmpty())
				return (h.getTokens());
			}
		catch (RuntimeException e)
			{
			}
		return (null);
		}
	
	@Override
	public SQLConsumer createConsumer(Style style)
		{
		switch (style)
			{
			case SIMPLE:
				return (new SQLConsumer(new SimpleSQLHandler()));
			case MULTILINE:
				return (new SQLConsumer(new MultilineSQLHandler()));
			case INDENTED:
				return (new SQLConsumer(sqlFormat.createHandler()));
			}
		
		throw new RuntimeException("Unknown style: " + style);
		}
	
	private void appendSQL(SQLConsumer c, List<String> l)
		{
		for (String token : l)
			c.appendName(token);
		}
	
	@Override
	public List<String> parseScript(String script, SQLDialect dialect)
		{
		try	{
			return (sqlFormat.parseScript(script, dialect.dmlRequiresTerminator()));
			}
		catch (RuntimeException e)
			{
			throw new DataIntegrityViolationException("Error parsing script", e);
			}
		}
	}
