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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.ermtools.schema.DifferenceHandler;

/**
 * Collects schema differences and write them to a StatementHandler in useful order
 * 
 * @author Robert Wruck
 */
public class StatementDifferenceHandler implements DifferenceHandler, StatementProducer
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
	public StatementDifferenceHandler(SQLDialect d)
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
	
	private void removeRelationship(String s)
		{
		del_relationships.add(s);
		}
	
	private void removeIndex(String s)
		{
		del_indexes.add(s);
		}
	
	private void modifyTable(String s)
		{
		tables.add(s);
		}
	
	private void modifyPrivilege(String s)
		{
		privs.add(s);
		}
	
	private void modifyStructure(String s)
		{
		structures.add(s);
		}
	
	private void modifyIndex(String s)
		{
		add_indexes.add(s);
		}
	
	private void modifyRelationship(String s)
		{
		add_relationships.add(s);
		}
	
	@Override
	public void tableAdded(TableDescription t)
		{
		modifyTable(dialect.createTable(t));
		for (Iterator<IndexDescription> j = t.getIndices().iterator(); j.hasNext(); )
			indexAdded(t, j.next());
		for (Iterator<ForeignKeyDescription> j = t.getReferencedKeys().iterator(); j.hasNext(); )
			fkAdded(t, j.next());
		for (Iterator<PrivilegeDescription> j = t.getPrivileges().iterator(); j.hasNext(); )
			privilegeAdded(t, j.next());
		}
	
	@Override
	public void tableRemoved(TableDescription t)
		{
		modifyTable(dialect.dropTable(t));
		// Indices and FKs will be dropped along
		}
	
	@Override
	public void startTable(TableDescription cOld, TableDescription cNew)
		{
		}
	
	@Override
	public void endTable(TableDescription cOld, TableDescription cNew)
		{
		}
	
	@Override
	public void columnAdded(TableDescription t, ColumnDescription c)
		{
		modifyStructure(dialect.addColumn(t, c));
		}
	
	@Override
	public void columnChanged(TableDescription t, ColumnDescription c, ColumnDescription old)
		{
		modifyStructure(dialect.modifyColumn(t, c, old));
		}
	
	@Override
	public void columnRemoved(TableDescription t, ColumnDescription c)
		{
		modifyStructure(dialect.removeColumn(t, c));
		}
	
	@Override
	public void indexAdded(TableDescription t, IndexDescription ix)
		{
		modifyIndex(dialect.createIndex(t, ix));
		}
	
	@Override
	public void indexRemoved(TableDescription t, IndexDescription ix)
		{
		removeIndex(dialect.dropIndex(t, ix));
		}
	
	@Override
	public void pkAdded(TableDescription t, PrimaryKeyDescription k)
		{
		modifyIndex(dialect.addPrimaryKey(t, k));
		}
	
	@Override
	public void pkChanged(TableDescription t, PrimaryKeyDescription k, PrimaryKeyDescription old)
		{
		pkRemoved(t, old);
		pkAdded(t, k);
		}
	
	@Override
	public void pkRemoved(TableDescription t, PrimaryKeyDescription k)
		{
		removeIndex(dialect.removePrimaryKey(t, k));
		}
	
	@Override
	public void fkAdded(TableDescription t, ForeignKeyDescription fk)
		{
		modifyRelationship(dialect.addForeignKey(t, fk));
		}
	
	@Override
	public void fkRemoved(TableDescription t, ForeignKeyDescription fk)
		{
		removeRelationship(dialect.removeForeignKey(t, fk));
		}
	
	@Override
	public void privilegeAdded(TableDescription t, PrivilegeDescription i)
		{
		modifyPrivilege(dialect.grantPrivilege(t, i));
		}
	
	@Override
	public void privilegeRemoved(TableDescription t, PrivilegeDescription i)
		{
		modifyPrivilege(dialect.revokePrivilege(t, i));
		}
	
	@Override
	public void produceStatements(StatementHandler h)
		{
		if (!del_relationships.isEmpty())
			{
			h.comment("Drop obsolete relationships");
			for (String s : del_relationships)
				h.statement(s);
			}
		if (!del_indexes.isEmpty())
			{
			h.comment("Drop obsolete indexes");
			for (String s : del_indexes)
				h.statement(s);
			}
		if (!tables.isEmpty())
			{
			h.comment("Add / remove tables");
			for (String s : tables)
				h.statement(s);
			}
		if (!privs.isEmpty())
			{
			h.comment("Add / remove privileges");
			for (String s : privs)
				h.statement(s);
			}
		if (!structures.isEmpty())
			{
			h.comment("Update table structures");
			for (String s : structures)
				h.statement(s);
			}
		if (!add_indexes.isEmpty())
			{
			h.comment("Create indexes");
			for (String s : add_indexes)
				h.statement(s);
			}
		if (!add_relationships.isEmpty())
			{
			h.comment("Create relationships");
			for (String s : add_relationships)
				h.statement(s);
			}
		}
	
	@Override
	public String getPrepareStatement()
		{
		return (null);
		}
	
	@Override
	public String getCleanupStatement()
		{
		return (null);
		}
	}
