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
package de.tweerlei.ermtools.dialect.postgresql;

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
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDataType;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;
import de.tweerlei.ermtools.dialect.SQLStatementWrapper;
import de.tweerlei.ermtools.dialect.impl.CommonSQLDialect;

/**
 * MySQL
 * 
 * @author Robert Wruck
 */
public class PostgreSQLDialect extends CommonSQLDialect
	{
	private static final Map<Integer, SQLDataType> TYPE_MAP;
	
	static
		{
		TYPE_MAP = new HashMap<Integer, SQLDataType>();
		
		TYPE_MAP.put(Types.BIT, new SQLDataType("BIT", false, false));
		TYPE_MAP.put(Types.BOOLEAN, new SQLDataType("BOOL", false, false));
		TYPE_MAP.put(Types.TINYINT, new SQLDataType("INT2", false, false));
		TYPE_MAP.put(Types.SMALLINT, new SQLDataType("INT2", false, false));
		TYPE_MAP.put(Types.INTEGER, new SQLDataType("INT4", false, false));
		TYPE_MAP.put(Types.BIGINT, new SQLDataType("INT8", false, false));
		
		TYPE_MAP.put(Types.REAL, new SQLDataType("FLOAT4", false, false));
		TYPE_MAP.put(Types.DOUBLE, new SQLDataType("FLOAT8", false, false));
		TYPE_MAP.put(Types.FLOAT, new SQLDataType("FLOAT4", false, false));
		
		TYPE_MAP.put(Types.DECIMAL, new SQLDataType("DECIMAL", false, false));
		TYPE_MAP.put(Types.NUMERIC, new SQLDataType("NUMERIC", false, false));
		
		TYPE_MAP.put(Types.DATE, new SQLDataType("DATE", false, false));
		TYPE_MAP.put(Types.TIME, new SQLDataType("TIME", false, false));
		TYPE_MAP.put(Types.TIMESTAMP, new SQLDataType("TIMESTAMP", false, false));
		
		TYPE_MAP.put(Types.CHAR, new SQLDataType("CHAR", true, false));
		TYPE_MAP.put(Types.VARCHAR, new SQLDataType("VARCHAR", true, false));
		TYPE_MAP.put(Types.LONGVARCHAR, new SQLDataType("TEXT", false, false));
		TYPE_MAP.put(Types.CLOB, new SQLDataType("TEXT", false, false));
		
		TYPE_MAP.put(Types.BINARY, new SQLDataType("BYTEA", false, false));
		TYPE_MAP.put(Types.VARBINARY, new SQLDataType("BYTEA", false, false));
		TYPE_MAP.put(Types.LONGVARBINARY, new SQLDataType("BYTEA", false, false));
		TYPE_MAP.put(Types.BLOB, new SQLDataType("BYTEA", false, false));
		}
	
	/**
	 * Constructor
	 */
	public PostgreSQLDialect()
		{
		super(TYPE_MAP);
		}
	
	@Override
	public boolean isCaseSensitive()
		{
		return (true);
		}
	
	@Override
	public String quoteIdentifier(String c)
		{
		return ("\"" + c + "\"");
		}
	
	@Override
	public boolean supportsBoolean()
		{
		return (true);
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
			sb.append(",\n\t");
			if (!StringUtils.empty(pk.getName()))
				sb.append("CONSTRAINT ").append(pk.getName()).append(" ");
			sb.append("PRIMARY KEY (");
			
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
		final StringBuilder sb = new StringBuilder();
		QualifiedName tempName = old.getName();
		
		// Rename object
		if (!old.getName().getObjectName().equals(t.getName().getObjectName()))
			{
			sb.append("ALTER TABLE ").append(getQualifiedTableName(old.getName()));
			sb.append("\n\tRENAME TO ").append(quoteIdentifier(t.getName().getObjectName()));
			
			tempName = new QualifiedName(old.getName().getCatalogName(), old.getName().getSchemaName(), t.getName().getObjectName());
			}
		
		// Move between schemas
		if (!old.getName().getSchemaName().equals(t.getName().getSchemaName()))
			{
			if (sb.length() > 0)
				sb.append(";\n");
		
			sb.append("ALTER TABLE ").append(getQualifiedTableName(tempName));
			sb.append("\n\tSET SCHEMA ").append(quoteIdentifier(t.getName().getSchemaName()));
			}
		
		return (sb.toString());
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
		boolean sep = false;
		
		if (c.isNullable() && !old.isNullable())
			{
			if (sep)
				sb.append(",");
			else
				sep = true;
			sb.append("\n\tALTER COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" DROP NOT NULL");
			}
		else if (!c.isNullable() && old.isNullable())
			{
			if (sep)
				sb.append(",");
			else
				sep = true;
			sb.append("\n\tALTER COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" SET NOT NULL");
			}
	
		if ((c.getDefaultValue() == null) && (old.getDefaultValue() != null))
			{
			if (sep)
				sb.append(",");
			else
				sep = true;
			sb.append("\n\tALTER COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" DROP DEFAULT");
			}
		else if ((c.getDefaultValue() != null)  && !c.getDefaultValue().equals(old.getDefaultValue()))
			{
			if (sep)
				sb.append(",");
			else
				sep = true;
			sb.append("\n\tALTER COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" SET DEFAULT '");
			sb.append(c.getDefaultValue());
			sb.append("'");
			}
		
		final String oldType = dataTypeToString(old.getType());
		final String newType = dataTypeToString(c.getType());
		if (!sep || !newType.equalsIgnoreCase(oldType))
			{
			if (sep)
				sb.append(",");
			else
				sep = true;
			sb.append("\n\tALTER COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" TYPE ");
			sb.append(newType);
			}
		
		if (!old.getName().equals(c.getName()))
			{
			sb.append(";\nALTER TABLE ");
			sb.append(getQualifiedTableName(t.getName()));
			sb.append("\n\tRENAME COLUMN ");
			sb.append(quoteIdentifier(old.getName()));
			sb.append(" TO ");
			sb.append(quoteIdentifier(c.getName()));
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
		final PrimaryKeyDescription pk = t.getPrimaryKey();
		if (pk != null)
			{
			// Suppress index creation for PK (in most cases, PostgreSQL does this itself)
			if (ix.getColumns().equals(pk.getColumns()))
				return ("NULL");
			}
		
		final StringBuffer sb = new StringBuffer();
		sb.append("CREATE ");
		if (ix.isUnique())
			sb.append("UNIQUE INDEX ");
		else
			sb.append("INDEX ");
		sb.append(quoteIdentifier(ix.getName()));
		sb.append("\n\tON ");
		sb.append(getQualifiedTableName(t.getName()));
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
		final QualifiedName qname = new QualifiedName(t.getName().getCatalogName(), t.getName().getSchemaName(), ix.getName());
		
		final StringBuffer sb = new StringBuffer();
		sb.append("DROP INDEX ");
		sb.append(getQualifiedTableName(qname));
		return (sb.toString());
		}

	public String addPrimaryKey(TableDescription t, PrimaryKeyDescription k)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tADD ");
		if (!StringUtils.empty(k.getName()))
			sb.append("CONSTRAINT ").append(k.getName()).append("\n\t");
		sb.append("PRIMARY KEY (");
		
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
		sb.append("\n\tDROP CONSTRAINT ");
		sb.append(k.getName());
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
		sb.append("\n\tDROP CONSTRAINT ");
		sb.append(fk.getName());
		return (sb.toString());
		}
	
	@Override
	public SQLStatementWrapper getStatementWrapper()
		{
		return (PostgreSQLStatementWrapper.INSTANCE);
		}
	
	@Override
	public MetadataReader getMetadataReader(Connection c) throws SQLException
		{
		return (new PostgreSQLMetadataReader(c.getMetaData()));
		}
	
	@Override
	public SQLStatementAnalyzer getStatementAnalyzer(Connection c) throws SQLException
		{
		return (new PostgreSQLStatementAnalyzer(c));
		}
	}
