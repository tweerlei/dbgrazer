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
package de.tweerlei.ermtools.dialect.impl;

import java.util.Collections;
import java.util.Iterator;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDataType;

/**
 * Generic dialect that uses JDBC type names
 * 
 * @author Robert Wruck
 */
public class GenericDialect extends CommonSQLDialect
	{
	/**
	 * Constructor
	 */
	public GenericDialect()
		{
		super(Collections.<Integer, SQLDataType>emptyMap());
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
	
	public String modifyTable(TableDescription old, TableDescription t)
		{
		return ("ALTER TABLE " + getQualifiedTableName(old.getName()) + " RENAME TO " + getQualifiedTableName(t.getName()));
		}
	
	public String addColumn(TableDescription t, ColumnDescription c)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tADD COLUMN ");
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
		return (sb.toString());
		}

	public String modifyColumn(TableDescription t, ColumnDescription c, ColumnDescription old)
		{
		final StringBuffer sb = new StringBuffer();
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tMODIFY COLUMN ");
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
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		if (ix.isUnique())
			sb.append("\n\tADD UNIQUE INDEX ");
		else
			sb.append("\n\tADD INDEX ");
		sb.append(ix.getName());
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
		sb.append("ALTER TABLE ");
		sb.append(t.getName().getObjectName());
		sb.append("\n\tDROP INDEX ");
		sb.append(ix.getName());
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
	}
