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
package de.tweerlei.ermtools.schema;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ColumnType;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.model.SQLSchema;
import de.tweerlei.ermtools.model.SQLVisitor;

/**
 * Besucher, der ein Schema als XML ausgibt
 * 
 * @author Robert Wruck
 */
public class XMLVisitor implements SQLVisitor
	{
	private final Writer writer;
	
	/**
	 * Konstruktor
	 * @param w Writer
	 */
	public XMLVisitor(Writer w)
		{
		this.writer = w;
		}
	
	private void write(String s)
		{
		if (s != null)
			{
			try	{
				writer.write(s);
				}
			catch (IOException e)
				{
				throw new RuntimeException(e);
				}
			}
		}
	
	public void beginSchema(SQLSchema schema)
		{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<DBSchema xmlns=\"http://www.tweerlei.de/schema/ermtools/dbschema\" catalog=\"");
		if (schema.getCatalog() != null)
			write(schema.getCatalog());
		write("\" schema=\"");
		if (schema.getSchema() != null)
			write(schema.getSchema());
		write("\">\n");
		}
	
	public void endSchema(SQLSchema schema)
		{
		write("</DBSchema>\n");
		}
	
	public void beginTable(TableDescription table)
		{
		write("<Table name=\"");
		write(table.getName().getObjectName());
		write("\">\n");
		}
	
	public void endTable(TableDescription table)
		{
		write("</Table>\n");
		}
	
	public void beginColumns()
		{
		write("\t<Columns>\n");
		}
	
	public void visitColumn(ColumnDescription column)
		{
		write("\t\t<Column name=\"");
		write(column.getName());
		write("\" type=\"");
		write(ColumnType.getTypeName(column.getType().getType()));
		write("\" width=\"");
		write(String.valueOf(column.getType().getLength()));
		if (column.getType().getDecimals() > 0)
			{
			write("\" precision=\"");
			write(String.valueOf(column.getType().getDecimals()));
			}
		write("\" nullable=\"");
		write(XMLWriter.printBoolean(column.isNullable()));
		if (column.getDefaultValue() != null)
			{
			write("\" default=\"");
			write(column.getDefaultValue());
			}
		write("\"/>\n");
		}
	
	public void endColumns()
		{
		write("\t</Columns>\n");
		}
	
	public void visitPrimaryKey(PrimaryKeyDescription pk)
		{
		write("\t<PrimaryKey name=\"");
		write(pk.getName());
		write("\">\n");
		for (Iterator<String> i = pk.getColumns().iterator(); i.hasNext(); )
			{
			final String k = i.next();
			write("\t\t<ColumnRef name=\"");
			write(k);
			write("\"/>\n");
			}
		write("\t</PrimaryKey>\n");
		}
	
	public void beginIndices()
		{
		write("\t<Indices>\n");
		}
	
	public void visitIndex(IndexDescription index)
		{
		write("\t\t<Index name=\"");
		write(index.getName());
		write("\" unique=\"");
		write(XMLWriter.printBoolean(index.isUnique()));
		write("\">\n");
		for (Iterator<String> i = index.getColumns().iterator(); i.hasNext(); )
			{
			final String k = i.next();
			write("\t\t\t<ColumnRef name=\"");
			write(k);
			write("\"/>\n");
			}
		write("\t\t</Index>\n");
		}
	
	public void endIndices()
		{
		write("\t</Indices>\n");
		}
	
	public void beginForeignKeys()
		{
		write("\t<ForeignKeys>\n");
		}
	
	public void visitForeignKey(ForeignKeyDescription fk)
		{
		write("\t\t<ForeignKey name=\"");
		write(fk.getName());
		write("\" catalog=\"");
		write(fk.getTableName().getCatalogName());
		write("\" schema=\"");
		write(fk.getTableName().getSchemaName());
		write("\" table=\"");
		write(fk.getTableName().getObjectName());
		write("\">\n");
		for (Map.Entry<String, String> ent : fk.getColumns().entrySet())
			{
			write("\t\t\t<ColumnJoin source=\"");
			write(ent.getKey());
			write("\" target=\"");
			write(ent.getValue());
			write("\"/>\n");
			}
		write("\t\t</ForeignKey>\n");
		}
	
	public void endForeignKeys()
		{
		write("\t</ForeignKeys>\n");
		}
	
	public void beginPrivileges()
		{
		write("\t<Privileges>\n");
		}
	
	public void visitPrivilege(PrivilegeDescription p)
		{
		write("\t\t<Privilege privilege=\"");
		write(p.getPrivilege());
		write("\" grantee=\"");
		write(p.getGrantee());
		write("\" grantable=\"");
		write(XMLWriter.printBoolean(p.isGrantable()));
		write("\"/>\n");
		}
	
	public void endPrivileges()
		{
		write("\t</Privileges>\n");
		}
	}
