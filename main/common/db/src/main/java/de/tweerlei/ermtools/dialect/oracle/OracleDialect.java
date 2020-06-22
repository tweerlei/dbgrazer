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
package de.tweerlei.ermtools.dialect.oracle;

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
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDataType;
import de.tweerlei.ermtools.dialect.SQLScriptOutputReader;
import de.tweerlei.ermtools.dialect.SQLStatementAnalyzer;
import de.tweerlei.ermtools.dialect.impl.CommonSQLDialect;

/**
 * Oracle
 * 
 * @author Robert Wruck
 */
public class OracleDialect extends CommonSQLDialect
	{
	private static final Map<Integer, SQLDataType> TYPE_MAP;
	
	static
		{
		TYPE_MAP = new HashMap<Integer, SQLDataType>();
		
		TYPE_MAP.put(Types.BIT, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.BOOLEAN, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.TINYINT, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.SMALLINT, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.INTEGER, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.BIGINT, new SQLDataType("NUMBER", true, true));
		
		TYPE_MAP.put(Types.REAL, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.DOUBLE, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.FLOAT, new SQLDataType("NUMBER", true, true));
		
		TYPE_MAP.put(Types.DECIMAL, new SQLDataType("NUMBER", true, true));
		TYPE_MAP.put(Types.NUMERIC, new SQLDataType("NUMBER", true, true));
		
		TYPE_MAP.put(Types.DATE, new SQLDataType("DATE", false, false));
		TYPE_MAP.put(Types.TIME, new SQLDataType("DATE", false, false));
		TYPE_MAP.put(Types.TIMESTAMP, new SQLDataType("TIMESTAMP", false, false));
		
		TYPE_MAP.put(Types.CHAR, new SQLDataType("CHAR", true, false));
		TYPE_MAP.put(Types.VARCHAR, new SQLDataType("VARCHAR2", true, false));
		TYPE_MAP.put(Types.LONGVARCHAR, new SQLDataType("VARCHAR2", true, false));
		TYPE_MAP.put(Types.CLOB, new SQLDataType("CLOB", false, false));
		
		TYPE_MAP.put(Types.BINARY, new SQLDataType("BINARY", true, false));
		TYPE_MAP.put(Types.VARBINARY, new SQLDataType("VARBINARY", true, false));
		TYPE_MAP.put(Types.LONGVARBINARY, new SQLDataType("VARBINARY", true, false));
		TYPE_MAP.put(Types.BLOB, new SQLDataType("BLOB", false, false));
		}
	
	/**
	 * Constructor
	 */
	public OracleDialect()
		{
		super(TYPE_MAP);
		}
	
	@Override
	public boolean supportsMerge()
		{
		return (true);
		}
	
	@Override
	public String getDefaultTableName()
		{
		return ("DUAL");
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
			sb.append(c.getName());
			sb.append(" ");
			sb.append(dataTypeToString(c.getType()));
			if (c.getDefaultValue() != null)
				{
				sb.append(" DEFAULT ");
				sb.append(c.getDefaultValue());
				}
			sb.append(c.isNullable() ? " NULL" : " NOT NULL");
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
	
	public String modifyTable(TableDescription old, TableDescription t)
		{
		return ("ALTER TABLE " + getQualifiedTableName(old.getName()) + "\n\tRENAME TO " + getQualifiedTableName(t.getName()));
		}
	
	public String addColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tADD ");
		sb.append(c.getName());
		sb.append(" ");
		sb.append(dataTypeToString(c.getType()));
		if (c.getDefaultValue() != null)
			{
			sb.append(" DEFAULT ");
			sb.append(c.getDefaultValue());
			}
		sb.append(c.isNullable() ? " NULL" : " NOT NULL");
		return (sb.toString());
		}

	public String modifyColumn(TableDescription t, ColumnDescription c, ColumnDescription old)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tMODIFY ");
		sb.append(c.getName());
		sb.append(" ");
		sb.append(dataTypeToString(c.getType()));
		if (c.getDefaultValue() != null)
			{
			sb.append(" DEFAULT ");
			sb.append(c.getDefaultValue());
			}
		if (c.isNullable() != old.isNullable())
			sb.append(c.isNullable() ? " NULL" : " NOT NULL");
		return (sb.toString());
		}

	public String removeColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tDROP COLUMN ");
		sb.append(c.getName());
		return (sb.toString());
		}

	public String createIndex(TableDescription t, IndexDescription ix)
		{
		final PrimaryKeyDescription pk = t.getPrimaryKey();
		if (pk != null)
			{
			// Suppress index creation for PK (in most cases, Oracle does this itself)
			if (ix.getColumns().equals(pk.getColumns()))
				return ("BEGIN /* Skipped creation of PK index " + ix.getName() + " on " + t.getName().getObjectName() + " */ NULL; END;");
			}
		
		final StringBuffer sb = new StringBuffer();
		sb.append("CREATE ");
		if (ix.isUnique())
			sb.append("UNIQUE INDEX ");
		else
			sb.append("INDEX ");
		sb.append(ix.getName());
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
			sb.append(i.next());
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
		if (!fk.getTableName().hasSameSchema(t.getName()))
			{
			// Add schema prefix only if not in same schema
			sb.append(fk.getTableName().getSchemaName());
			sb.append(".");
			}
		sb.append(getQualifiedTableName(fk.getTableName()));
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
		sb.append(getQualifiedTableName(t.getName()));
		sb.append("\n\tDROP CONSTRAINT ");
		sb.append(fk.getName());
		return (sb.toString());
		}
	
	@Override
	public String grantPrivilege(TableDescription t, PrivilegeDescription p)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("GRANT ");
		sb.append(p.getPrivilege());
		sb.append(" ON ");
		sb.append(getQualifiedTableName(t.getName()));
		sb.append(" TO ");
		sb.append(p.getGrantee());
		if (p.isGrantable())
			sb.append(" WITH GRANT OPTION");
		
		return (sb.toString());
		}
	
	@Override
	public MetadataReader getMetadataReader(Connection c) throws SQLException
		{
		return (new OracleMetadataReader(c));
		}
	
	@Override
	public SQLStatementAnalyzer getStatementAnalyzer(Connection c) throws SQLException
		{
		return (new OracleStatementAnalyzer(c));
		}
	
	@Override
	public SQLScriptOutputReader getScriptOutputReader(Connection c) throws SQLException
		{
		return (new OracleScriptOutputReader(c));
		}
	}
