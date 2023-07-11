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
package de.tweerlei.ermtools.dialect.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.MetadataReader;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDataType;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;
import de.tweerlei.ermtools.dialect.impl.CommonSQLDialect;

/**
 * MySQL
 * 
 * @author Robert Wruck
 */
public class MySQLDialect extends CommonSQLDialect
	{
	private static final Map<Integer, SQLDataType> TYPE_MAP;
	
	static
		{
		TYPE_MAP = new HashMap<Integer, SQLDataType>();
		
		TYPE_MAP.put(Types.BIT, new SQLDataType("BIT", false, false));
		TYPE_MAP.put(Types.BOOLEAN, new SQLDataType("TINYINT", false, false));
		TYPE_MAP.put(Types.TINYINT, new SQLDataType("TINYINT", false, false));
		TYPE_MAP.put(Types.SMALLINT, new SQLDataType("SMALLINT", false, false));
		TYPE_MAP.put(Types.INTEGER, new SQLDataType("INT", false, false));
		TYPE_MAP.put(Types.BIGINT, new SQLDataType("BIGINT", false, false));
		
		TYPE_MAP.put(Types.REAL, new SQLDataType("REAL", false, false));
		TYPE_MAP.put(Types.DOUBLE, new SQLDataType("DOUBLE", false, false));
		TYPE_MAP.put(Types.FLOAT, new SQLDataType("FLOAT", false, false));
		
		TYPE_MAP.put(Types.DECIMAL, new SQLDataType("DECIMAL", false, false));
		TYPE_MAP.put(Types.NUMERIC, new SQLDataType("NUMERIC", false, false));
		
		TYPE_MAP.put(Types.DATE, new SQLDataType("DATE", false, false));
		TYPE_MAP.put(Types.TIME, new SQLDataType("TIME", false, false));
		TYPE_MAP.put(Types.TIMESTAMP, new SQLDataType("DATETIME", false, false));
		
		TYPE_MAP.put(Types.CHAR, new SQLDataType("CHAR", true, false));
		TYPE_MAP.put(Types.VARCHAR, new SQLDataType("VARCHAR", true, false));
		TYPE_MAP.put(Types.LONGVARCHAR, new SQLDataType("TEXT", true, false));
		TYPE_MAP.put(Types.CLOB, new SQLDataType("TEXT", true, false));
		
		TYPE_MAP.put(Types.BINARY, new SQLDataType("BINARY", true, false));
		TYPE_MAP.put(Types.VARBINARY, new SQLDataType("VARBINARY", true, false));
		TYPE_MAP.put(Types.LONGVARBINARY, new SQLDataType("VARBINARY", true, false));
		TYPE_MAP.put(Types.BLOB, new SQLDataType("VARBINARY", true, false));
		}
	
	/**
	 * Constructor
	 */
	public MySQLDialect()
		{
		super(TYPE_MAP);
		}
	
	@Override
	public String quoteIdentifier(String c)
		{
		final int len = c.length();
		final StringBuilder sb = new StringBuilder(len + 2);
		boolean needsQuote = false;
		sb.append('`');
		for (int i = 0; i < len; i++)
			{
			final char ch = c.charAt(i);
			if ((ch != '_') && (ch < '0' || ch > '9') && (ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z'))
				needsQuote = true;
			sb.append(ch);
			}
		sb.append('`');
		if (needsQuote)
			return (sb.toString());
		else
			return (c);
		}
	
	public String createTable(TableDescription t)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append(" (\n\t");
		
		boolean first = true;
		for (Iterator<ColumnDescription> i = t.getColumns().iterator(); i.hasNext(); )
			{
			final ColumnDescription c = i.next();
			if (first)
				first = false;
			else
				sb.append(",\n\t");
			sb.append(quoteIdentifier(c.getName()));
			sb.append(" ");
			sb.append(dataTypeToString(c.getType()));
			sb.append(c.isNullable() ? " NULL" : " NOT NULL");
			if (c.getDefaultValue() != null)
				{
				sb.append(" DEFAULT '");
				sb.append(c.getDefaultValue());
				sb.append("'");
				}
			}
		
		final PrimaryKeyDescription pk = t.getPrimaryKey();
		if (pk != null)
			{
			sb.append(",\n\tPRIMARY KEY (");
			
			first = true;
			for (Iterator<String> i = pk.getColumns().iterator(); i.hasNext(); )
				{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(quoteIdentifier(i.next()));
				}
			
			sb.append(")");
			}
		
		sb.append("\n\t)");
		
		return (sb.toString());
		}
	
	public String modifyTable(TableDescription old, TableDescription t)
		{
		return ("RENAME TABLE " + getQualifiedTableName(old.getName()) + " TO " + getQualifiedTableName(t.getName()));
		}
	
	public String addColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tADD ");
		sb.append(quoteIdentifier(c.getName()));
		sb.append(" ");
		sb.append(dataTypeToString(c.getType()));
		sb.append(c.isNullable() ? " NULL" : " NOT NULL");
		if (c.getDefaultValue() != null)
			{
			sb.append(" DEFAULT '");
			sb.append(c.getDefaultValue());
			sb.append("'");
			}
		return (sb.toString());
		}

	public String modifyColumn(TableDescription t, ColumnDescription c, ColumnDescription old)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		if (!old.getName().equals(c.getName()))
			{
			sb.append("\n\tCHANGE COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" ");
			sb.append(quoteIdentifier(c.getName()));
			}
		else
			{
			sb.append("\n\tMODIFY COLUMN ");
			sb.append(quoteIdentifier(c.getName()));
			}
		sb.append(" ");
		sb.append(dataTypeToString(c.getType()));
		sb.append(c.isNullable() ? " NULL" : " NOT NULL");
		if (c.getDefaultValue() != null)
			{
			sb.append(" DEFAULT '");
			sb.append(c.getDefaultValue());
			sb.append("'");
			}
		return (sb.toString());
		}

	public String removeColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tDROP COLUMN ");
		sb.append(quoteIdentifier(c.getName()));
		return (sb.toString());
		}

	public String createIndex(TableDescription t, IndexDescription ix)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		if (ix.isUnique())
			sb.append("\n\tADD UNIQUE KEY ");
		else
			sb.append("\n\tADD KEY ");
		sb.append(quoteIdentifier(ix.getName()));
		sb.append(" (");
		
		boolean first = true;
		for (Iterator<String> i = ix.getColumns().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(quoteIdentifier(i.next()));
			}
		
		sb.append(")");
		return (sb.toString());
		}

	public String dropIndex(TableDescription t, IndexDescription ix)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tDROP INDEX ");
		sb.append(quoteIdentifier(ix.getName()));
		return (sb.toString());
		}

	public String addPrimaryKey(TableDescription t, PrimaryKeyDescription k)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tADD PRIMARY KEY (");
		
		boolean first = true;
		for (Iterator<String> i = k.getColumns().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(quoteIdentifier(i.next()));
			}
		
		sb.append(")");
		return (sb.toString());
		}

	public String removePrimaryKey(TableDescription t, PrimaryKeyDescription k)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tDROP PRIMARY KEY");
		return (sb.toString());
		}

	public String addForeignKey(TableDescription t, ForeignKeyDescription fk)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tADD ");
		if (!StringUtils.empty(fk.getName()))
			sb.append("CONSTRAINT ").append(fk.getName()).append("\n\t");
		sb.append("FOREIGN KEY (");
		
		boolean first = true;
		for (Iterator<String> i = fk.getColumns().keySet().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(quoteIdentifier(i.next()));
			}
		
		sb.append(")\n\tREFERENCES ");
		sb.append(getQualifiedTableName(fk.getTableName()));
		sb.append(" (");
		
		first = true;
		for (Iterator<String> i = fk.getColumns().values().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(quoteIdentifier(i.next()));
			}
		
		sb.append(")");
		return (sb.toString());
		}

	public String removeForeignKey(TableDescription t, ForeignKeyDescription fk)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tDROP FOREIGN KEY ");
		sb.append(fk.getName());
		return (sb.toString());
		}
	
	@Override
	public MetadataReader getMetadataReader(Connection c) throws SQLException
		{
		return (new MySQLMetadataReader(c.getMetaData()));
		}
	
	@Override
	public SQLStatementAnalyzer getStatementAnalyzer(Connection c) throws SQLException
		{
		return (new MySQLStatementAnalyzer(c));
		}
	}
