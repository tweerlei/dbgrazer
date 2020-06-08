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
package de.tweerlei.ermtools.dialect.mssql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
 * Microsoft SQL Server
 * 
 * @author Robert Wruck
 */
public class MSSQLDialect extends CommonSQLDialect
	{
	private static final String DATE_FORMAT = "''yyyy-MM-dd''";
	private static final String TIME_FORMAT = "''HH:mm:ss''";
	private static final String DATETIME_FORMAT = "''yyyy-MM-dd HH:mm:ss''";
	private static final String TIMESTAMP_FORMAT = "''yyyy-MM-dd HH:mm:ss.SSS''";
	
	private static final Map<Integer, SQLDataType> TYPE_MAP;
	
	static
		{
		TYPE_MAP = new HashMap<Integer, SQLDataType>();
		
		TYPE_MAP.put(Types.BIT, new SQLDataType("BIT", false, false));
		TYPE_MAP.put(Types.BOOLEAN, new SQLDataType("BIT", false, false));
		TYPE_MAP.put(Types.TINYINT, new SQLDataType("TINYINT", false, false));
		TYPE_MAP.put(Types.SMALLINT, new SQLDataType("SMALLINT", false, false));
		TYPE_MAP.put(Types.INTEGER, new SQLDataType("INT", false, false));
		TYPE_MAP.put(Types.BIGINT, new SQLDataType("BIGINT", false, false));
		
		TYPE_MAP.put(Types.REAL, new SQLDataType("REAL", false, false));
		TYPE_MAP.put(Types.DOUBLE, new SQLDataType("FLOAT", true, false));
		TYPE_MAP.put(Types.FLOAT, new SQLDataType("FLOAT", true, false));
		
		TYPE_MAP.put(Types.DECIMAL, new SQLDataType("DECIMAL", true, true));
		TYPE_MAP.put(Types.NUMERIC, new SQLDataType("NUMERIC", true, true));
		
		TYPE_MAP.put(Types.DATE, new SQLDataType("DATE", false, false));
		TYPE_MAP.put(Types.TIME, new SQLDataType("TIME", false, false));
		TYPE_MAP.put(Types.TIMESTAMP, new SQLDataType("DATETIME", false, false));
		
		TYPE_MAP.put(Types.CHAR, new SQLDataType("CHAR", true, false));
		TYPE_MAP.put(Types.VARCHAR, new SQLDataType("VARCHAR", true, false));
		TYPE_MAP.put(Types.LONGVARCHAR, new SQLDataType("VARCHAR", true, false));
		TYPE_MAP.put(Types.CLOB, new SQLDataType("VARCHAR", true, false));
		
		TYPE_MAP.put(Types.BINARY, new SQLDataType("BINARY", true, false));
		TYPE_MAP.put(Types.VARBINARY, new SQLDataType("VARBINARY", true, false));
		TYPE_MAP.put(Types.LONGVARBINARY, new SQLDataType("VARBINARY", true, false));
		TYPE_MAP.put(Types.BLOB, new SQLDataType("VARBINARY", true, false));
		}
	
	/**
	 * Constructor
	 */
	public MSSQLDialect()
		{
		super(TYPE_MAP);
		}
	
	@Override
	public String getDateFormat()
		{
		return (DATE_FORMAT);
		}
	
	@Override
	public String getTimeFormat()
		{
		return (TIME_FORMAT);
		}
	
	@Override
	public String getDatetimeFormat()
		{
		return (DATETIME_FORMAT);
		}
	
	@Override
	public String getTimestampFormat()
		{
		return (TIMESTAMP_FORMAT);
		}
	
	@Override
	public boolean supportsMerge()
		{
		return (true);
		}
	
	@Override
	public boolean dmlRequiresTerminator()
		{
		return (true);
		}
	
	@Override
	public String prepareInsert(TableDescription t)
		{
		// SQL Server won't permit INSERTs into identity columns
		if (isIdentityInsert(t))
			return ("SET IDENTITY_INSERT " + getQualifiedTableName(t.getName()) + " ON");
		return (null);
		}
	
	@Override
	public String finishInsert(TableDescription t)
		{
		// SQL Server won't permit INSERTs into identity columns
		if (isIdentityInsert(t))
			return ("SET IDENTITY_INSERT " + getQualifiedTableName(t.getName()) + " OFF");
		return (null);
		}
	
	private boolean isIdentityInsert(TableDescription t)
		{
		final PrimaryKeyDescription pk = t.getPrimaryKey();
		if ((pk != null) && (pk.getColumns().size() == 1))
			{
			final ColumnDescription col = t.getColumn(pk.getColumns().get(0));
			if ((col != null) && col.getType().getName().equalsIgnoreCase("int identity"))
				return (true);
			}
		return (false);
		}
	
	public String createTable(TableDescription t)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append(" (\n\t");
		
		boolean first = true;
		for (Iterator<ColumnDescription> i = t.getColumns().iterator(); i.hasNext(); )
			{
			final ColumnDescription c = i.next();
			if (first)
				first = false;
			else
				sb.append(",\n\t");
			sb.append(c.getName());
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
			sb.append(",\n\tCONSTRAINT ");
			sb.append(pk.getName());
			sb.append(" PRIMARY KEY (");
			
			first = true;
			for (Iterator<String> i = pk.getColumns().iterator(); i.hasNext(); )
				{
				if (first)
					first = false;
				else
					sb.append(", ");
				sb.append(i.next());
				}
			
			sb.append(")");
			}
		
		sb.append("\n\t)");
		
		return (sb.toString());
		}

	public String addColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tADD ");
		sb.append(c.getName());
		sb.append(" ");
		sb.append(dataTypeToString(c.getType()));
		sb.append(c.isNullable() ? " NULL" : " NOT NULL");
		if (c.getDefaultValue() != null)
			{
			sb.append(" DEFAULT ");
			sb.append(c.getDefaultValue());
			}
		return (sb.toString());
		}

	public String modifyColumn(TableDescription t, ColumnDescription c, ColumnDescription old)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tALTER COLUMN ");
		sb.append(c.getName());
		sb.append(" ");
		sb.append(dataTypeToString(c.getType()));
		sb.append(c.isNullable() ? " NULL" : " NOT NULL");
		if (c.getDefaultValue() != null)
			{
			sb.append(" DEFAULT ");
			sb.append(c.getDefaultValue());
			// FIXME: It's not possible to remove a DEFAULT from a column without knowing the constraint name -
			//        but that is not returned from DatabaseMetadata
			}
		return (sb.toString());
		}

	public String removeColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tDROP COLUMN ");
		sb.append(c.getName());
		return (sb.toString());
		}

	public String createIndex(TableDescription t, IndexDescription ix)
		{
		final StringBuffer sb = new StringBuffer();
		if (ix.isUnique())
			sb.append("CREATE UNIQUE INDEX ");
		else
			sb.append("CREATE INDEX ");
		sb.append(ix.getName());
		sb.append("\n\tON ");
		sb.append(t.getName().getObjectName());
		sb.append(" (");
		
		boolean first = true;
		for (Iterator<String> i = ix.getColumns().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(i.next());
			}
		
		sb.append(")");
		return (sb.toString());
		}

	public String dropIndex(TableDescription t, IndexDescription ix)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("DROP INDEX ");
		sb.append(ix.getName());
		sb.append(" ON ");
		sb.append(t.getName().getObjectName());
		return (sb.toString());
		}

	public String addPrimaryKey(TableDescription t, PrimaryKeyDescription k)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tADD CONSTRAINT ");
		sb.append(k.getName());
		sb.append(" PRIMARY KEY (");
		
		boolean first = true;
		for (Iterator<String> i = k.getColumns().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(i.next());
			}
		
		sb.append(")");
		return (sb.toString());
		}

	public String removePrimaryKey(TableDescription t, PrimaryKeyDescription k)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tDROP CONSTRAINT ");
		sb.append(k.getName());
		return (sb.toString());
		}

	public String addForeignKey(TableDescription t, ForeignKeyDescription fk)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tADD CONSTRAINT ");
		sb.append(fk.getName());
		sb.append("\n\tFOREIGN KEY (");
		
		boolean first = true;
		for (Iterator<String> i = fk.getColumns().keySet().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(i.next());
			}
		
		sb.append(")\n\tREFERENCES ");
		sb.append(fk.getTableName().getObjectName());
		sb.append(" (");
		
		first = true;
		for (Iterator<String> i = fk.getColumns().values().iterator(); i.hasNext(); )
			{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(i.next());
			}
		
		sb.append(")");
		return (sb.toString());
		}

	public String removeForeignKey(TableDescription t, ForeignKeyDescription fk)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tDROP CONSTRAINT ");
		sb.append(fk.getName());
		return (sb.toString());
		}
	
	@Override
	public MetadataReader getMetadataReader(Connection c) throws SQLException
		{
		return (new MSSQLMetadataReader(c.getMetaData()));
		}
	
	@Override
	public SQLStatementAnalyzer getStatementAnalyzer(Connection c) throws SQLException
		{
		return (new MSSQLStatementAnalyzer(c));
		}
	}
