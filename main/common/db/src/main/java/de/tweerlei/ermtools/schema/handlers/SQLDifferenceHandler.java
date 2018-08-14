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
package de.tweerlei.ermtools.schema.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.schema.DifferenceHandler;

/**
 * Erzeugt SQL-Code für Schemaänderungen
 * 
 * @author Robert Wruck
 */
public class SQLDifferenceHandler implements DifferenceHandler
	{
	private final SQLDialect dialect;
	private final List<String> del_relationships;
	private final List<String> del_indexes;
	private final List<String> tables;
	private final List<String> privs;
	private final List<String> structures;
	private final List<String> add_indexes;
	private final List<String> add_relationships;
	
	/**
	 * Constructor
	 * @param d SQLDialect
	 */
	public SQLDifferenceHandler(SQLDialect d)
		{
		dialect = d;
		del_relationships = new ArrayList<String>();
		del_indexes = new ArrayList<String>();
		tables = new ArrayList<String>();
		privs = new ArrayList<String>();
		structures = new ArrayList<String>();
		add_indexes = new ArrayList<String>();
		add_relationships = new ArrayList<String>();
		}
	
	/**
	 * Fügt ein Statement zum Löschen hinzu
	 * @param s Statement
	 */
	private void removeRelationship(String s)
		{
		del_relationships.add(s);
		}
	
	/**
	 * Fügt ein Statement zum Löschen hinzu
	 * @param s Statement
	 */
	private void removeIndex(String s)
		{
		del_indexes.add(s);
		}
	
	/**
	 * Fügt ein Statement zum Erzeugen einer Tabelle hinzu
	 * @param s Statement
	 */
	private void modifyTable(String s)
		{
		tables.add(s);
		}
	
	/**
	 * Fügt ein Statement zum Erzeugen einer Tabelle hinzu
	 * @param s Statement
	 */
	private void modifyPrivilege(String s)
		{
		privs.add(s);
		}
	
	/**
	 * Fügt ein Statement zum Ändern einer Tabelle hinzu
	 * @param s Statement
	 */
	private void modifyStructure(String s)
		{
		structures.add(s);
		}
	
	/**
	 * Fügt ein Statement zum Ändern einer Tabelle hinzu
	 * @param s Statement
	 */
	private void modifyIndex(String s)
		{
		add_indexes.add(s);
		}
	
	/**
	 * Fügt ein Statement zum Anlegen einer Beziehung hinzu
	 * @param s Statement
	 */
	private void modifyRelationship(String s)
		{
		add_relationships.add(s);
		}
	
	public void tableAdded(TableDescription t)
		{
		modifyTable(dialect.createTable(t));
		for (Iterator<IndexDescription> j = t.getIndices().iterator(); j.hasNext(); )
			indexAdded(t, j.next());
		for (Iterator<ForeignKeyDescription> j = t.getReferencedKeys().iterator(); j.hasNext(); )
			fkAdded(t, j.next());
		}
	
	public void tableRemoved(TableDescription t)
		{
		modifyTable(dialect.dropTable(t));
		// Indices and FKs will be dropped along
		}
	
	public void startTable(TableDescription cOld, TableDescription cNew)
		{
		}
	
	public void endTable(TableDescription cOld, TableDescription cNew)
		{
		}
	
	public void columnAdded(TableDescription t, ColumnDescription c)
		{
		modifyStructure(dialect.addColumn(t, c));
		}
	
	public void columnChanged(TableDescription t, ColumnDescription c, ColumnDescription old)
		{
		modifyStructure(dialect.modifyColumn(t, c, old));
		}
	
	public void columnRemoved(TableDescription t, ColumnDescription c)
		{
		modifyStructure(dialect.removeColumn(t, c));
		}
	
	public void indexAdded(TableDescription t, IndexDescription ix)
		{
		modifyIndex(dialect.createIndex(t, ix));
		}
	
	public void indexRemoved(TableDescription t, IndexDescription ix)
		{
		removeIndex(dialect.dropIndex(t, ix));
		}
	
	public void pkAdded(TableDescription t, PrimaryKeyDescription k)
		{
		modifyIndex(dialect.addPrimaryKey(t, k));
		}
	
	public void pkChanged(TableDescription t, PrimaryKeyDescription k, PrimaryKeyDescription old)
		{
		pkRemoved(t, old);
		pkAdded(t, k);
		}
	
	public void pkRemoved(TableDescription t, PrimaryKeyDescription k)
		{
		removeIndex(dialect.removePrimaryKey(t, k));
		}
	
	public void fkAdded(TableDescription t, ForeignKeyDescription fk)
		{
		modifyRelationship(dialect.addForeignKey(t, fk));
		}
	
	public void fkRemoved(TableDescription t, ForeignKeyDescription fk)
		{
		removeRelationship(dialect.removeForeignKey(t, fk));
		}
	
	public void privilegeAdded(TableDescription t, PrivilegeDescription i)
		{
		modifyPrivilege(dialect.grantPrivilege(t, i));
		}
	
	public void privilegeRemoved(TableDescription t, PrivilegeDescription i)
		{
		modifyPrivilege(dialect.revokePrivilege(t, i));
		}
	
	@Override
	public String toString()
		{
		final StringBuffer sb = new StringBuffer();
		if (!del_relationships.isEmpty())
			{
			sb.append("-- Drop obsolete relationships\n");
			for (Iterator<String> i = del_relationships.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		if (!del_indexes.isEmpty())
			{
			sb.append("-- Drop obsolete indexes\n");
			for (Iterator<String> i = del_indexes.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		if (!tables.isEmpty())
			{
			sb.append("-- Add / remove tables\n");
			for (Iterator<String> i = tables.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		if (!privs.isEmpty())
			{
			sb.append("-- Add / remove privileges\n");
			for (Iterator<String> i = privs.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		if (!structures.isEmpty())
			{
			sb.append("-- Update table structures\n");
			for (Iterator<String> i = structures.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		if (!add_indexes.isEmpty())
			{
			sb.append("-- Create indexes\n");
			for (Iterator<String> i = add_indexes.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		if (!add_relationships.isEmpty())
			{
			sb.append("-- Create relationships\n");
			for (Iterator<String> i = add_relationships.iterator(); i.hasNext(); )
				{
				sb.append(i.next());
				sb.append(dialect.getStatementTerminator());
				}
			sb.append("\n");
			}
		return (sb.toString());
		}
	}
