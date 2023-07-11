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

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLNamingStrategy;
import de.tweerlei.ermtools.model.SQLSchema;
import de.tweerlei.ermtools.model.SQLVisitor;
import de.tweerlei.ermtools.schema.matchers.LaxColumnMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxForeignKeyMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxIndexMatcher;
import de.tweerlei.ermtools.schema.matchers.LaxPrivilegeMatcher;
import de.tweerlei.ermtools.schema.matchers.StrictTypeMatcher;
import de.tweerlei.ermtools.schema.naming.StrictNamingStrategy;

/**
 * Compares two SQLSchemas.
 * Will invoke a DifferenceHandler's methods
 * to convert a "current" schema to the visited schema, e.g.
 * a table not present in the current schema but in the visited schema
 * will cause a call to DifferenceHandler.tableAdded
 * 
 * @author Robert Wruck
 */
public class CompareVisitor implements SQLVisitor
	{
	private final SQLSchema other;
	private final DifferenceHandler handler;
	private final Map<String, TableDescription> right;
	
	private final SQLNamingStrategy namingStrategy;
	private final Comparator<ColumnDescription> columnMatcher;
	private final Comparator<IndexDescription> indexMatcher;
	private final Comparator<ForeignKeyDescription> fkMatcher;
	private final Comparator<PrivilegeDescription> privMatcher;
	
	private TableDescription rightTable;
	private Map<String, ColumnDescription> rightCols;
	private Set<IndexDescription> rightIndices;
	private Set<ForeignKeyDescription> rightFKs;
	private Set<PrivilegeDescription> rightPrivs;
	
	/**
	 * Constructor
	 * @param schema Current schema to compare to
	 * @param handler Handler to be notified
	 */
	public CompareVisitor(SQLSchema schema, DifferenceHandler handler)
		{
		this(schema, handler, new StrictNamingStrategy());
		}
	
	/**
	 * Constructor
	 * @param schema Current schema to compare to
	 * @param handler Handler to be notified
	 * @param namingStrategy SchemaNamingStrategy
	 */
	public CompareVisitor(SQLSchema schema, DifferenceHandler handler,
			SQLNamingStrategy namingStrategy)
		{
		this(schema, handler, namingStrategy,
				new LaxColumnMatcher(new StrictTypeMatcher()),
				new LaxIndexMatcher(),
				new LaxForeignKeyMatcher(namingStrategy),
				new LaxPrivilegeMatcher());
		}
	
	/**
	 * Constructor
	 * @param schema Current schema to compare to
	 * @param handler Handler to be notified
	 * @param namingStrategy SchemaNamingStrategy
	 * @param columnMatcher Column matcher
	 * @param indexMatcher Index matcher
	 * @param fkMatcher Foreign key matcher
	 * @param privMatcher Privilege matcher
	 */
	public CompareVisitor(SQLSchema schema, DifferenceHandler handler,
			SQLNamingStrategy namingStrategy,
			Comparator<ColumnDescription> columnMatcher,
			Comparator<IndexDescription> indexMatcher,
			Comparator<ForeignKeyDescription> fkMatcher,
			Comparator<PrivilegeDescription> privMatcher
			)
		{
		this.other = schema;
		this.handler = handler;
		this.namingStrategy = namingStrategy;
		this.columnMatcher = columnMatcher;
		this.indexMatcher = indexMatcher;
		this.fkMatcher = fkMatcher;
		this.privMatcher = privMatcher;
		this.right = new TreeMap<String, TableDescription>();
		}
	
	public void beginSchema(SQLSchema schema)
		{
		for (TableDescription table : other.getTables().values())
			{
			// Compare real tables only (we don't know how to handle views)
			if (table.getType().equals(TableDescription.TABLE))
				right.put(namingStrategy.getQualifiedTableName(table.getName()), table);
			}
		}
	
	public void endSchema(SQLSchema schema)
		{
		if (!right.isEmpty())
			{
			final SQLSchema tmp = new SQLSchema(other.getCatalog(), other.getSchema());
			for (TableDescription a : right.values())
				tmp.addTable(a);
			
			final SchemaChangeVisitor scv = new SchemaChangeVisitor(schema.getCatalog(), schema.getSchema());
			tmp.accept(scv);
			
			for (TableDescription a : scv.getSchema().getTables().values())
				handler.tableRemoved(a);
			right.clear();
			}
		}
	
	public void beginTable(TableDescription table)
		{
		// Compare real tables only (we don't know how to handle views)
		if (!table.getType().equals(TableDescription.TABLE))
			return;
		
		final TableDescription otherTable = right.remove(namingStrategy.getQualifiedTableName(table.getName()));
		if (otherTable == null)
			{
			handler.tableAdded(table);
			return;
			}
		
		rightTable = otherTable;
		
		handler.startTable(rightTable, table);
		
		// Handle PK
		
		if (indexMatcher != null)
			{
			if ((table.getPrimaryKey() != null) && (rightTable.getPrimaryKey() == null))
				handler.pkAdded(rightTable, table.getPrimaryKey());
			else if ((table.getPrimaryKey() != null) && (rightTable.getPrimaryKey() != null))
				{
				if (indexMatcher.compare(table.getPrimaryKey(), rightTable.getPrimaryKey()) != 0)
					handler.pkChanged(rightTable, table.getPrimaryKey(), rightTable.getPrimaryKey());
				}
			else if ((table.getPrimaryKey() == null) && (rightTable.getPrimaryKey() != null))
				handler.pkRemoved(rightTable, rightTable.getPrimaryKey());
			}
		}

	public void endTable(TableDescription table)
		{
		if (rightTable == null)
			return;
		
		handler.endTable(rightTable, table);
		
		rightTable = null;
		}
	
	public void beginColumns()
		{
		if ((rightTable == null) || (columnMatcher == null))
			return;
		
		rightCols = new LinkedHashMap<String, ColumnDescription>();
		for (ColumnDescription c : rightTable.getColumns())
			rightCols.put(namingStrategy.quoteIdentifier(c.getName()), c);
		}
	
	public void visitColumn(ColumnDescription a)
		{
		if (rightCols == null)
			return;
		
		final ColumnDescription b = rightCols.get(namingStrategy.quoteIdentifier(a.getName()));
		if (b == null)
			handler.columnAdded(rightTable, a);
		else
			{
			if (columnMatcher.compare(a, b) != 0)
				handler.columnChanged(rightTable, a, b);
			rightCols.remove(b.getName());
			}
		}
	
	public void endColumns()
		{
		if (rightCols == null)
			return;
		
		for (ColumnDescription c : rightCols.values())
			handler.columnRemoved(rightTable, c);
		
		rightCols = null;
		}
	
	public void visitPrimaryKey(PrimaryKeyDescription pk)
		{
		}
	
	public void beginIndices()
		{
		if ((rightTable == null) || (indexMatcher == null))
			return;
		
		rightIndices = new LinkedHashSet<IndexDescription>(rightTable.getIndices());
		}
	
	public void visitIndex(IndexDescription a)
		{
		if (rightIndices == null)
			return;
		
		boolean found = false;
		for (Iterator<IndexDescription> j = rightIndices.iterator(); j.hasNext(); )
			{
			final IndexDescription b = j.next();
			if (indexMatcher.compare(a, b) == 0)
				{
				j.remove();
				found = true;
				break;
				}
			}
		if (!found)
			handler.indexAdded(rightTable, a);
		}
	
	public void endIndices()
		{
		if (rightIndices == null)
			return;
		
		for (IndexDescription i : rightIndices)
			handler.indexRemoved(rightTable, i);
		
		rightIndices = null;
		}
	
	public void beginForeignKeys()
		{
		if ((rightTable == null) || (fkMatcher == null))
			return;
		
		rightFKs = new LinkedHashSet<ForeignKeyDescription>(rightTable.getReferencedKeys());
		}
	
	public void visitForeignKey(ForeignKeyDescription a)
		{
		if (rightFKs == null)
			return;
		
		boolean found = false;
		for (Iterator<ForeignKeyDescription> j = rightFKs.iterator(); j.hasNext(); )
			{
			final ForeignKeyDescription b = j.next();
			if (fkMatcher.compare(a, b) == 0)
				{
				j.remove();
				found = true;
				break;
				}
			}
		if (!found)
			handler.fkAdded(rightTable, a);
		}
	
	public void endForeignKeys()
		{
		if (rightFKs == null)
			return;
		
		for (ForeignKeyDescription f : rightFKs)
			handler.fkRemoved(rightTable, f);
		
		rightFKs = null;
		}
	
	public void beginPrivileges()
		{
		if ((rightTable == null) || (privMatcher == null))
			return;
		
		rightPrivs = new LinkedHashSet<PrivilegeDescription>(rightTable.getPrivileges());
		}
	
	public void visitPrivilege(PrivilegeDescription a)
		{
		if (rightPrivs == null)
			return;
		
		boolean found = false;
		for (Iterator<PrivilegeDescription> j = rightPrivs.iterator(); j.hasNext(); )
			{
			final PrivilegeDescription b = j.next();
			if (privMatcher.compare(a, b) == 0)
				{
				j.remove();
				found = true;
				break;
				}
			}
		if (!found)
			handler.privilegeAdded(rightTable, a);
		}
	
	public void endPrivileges()
		{
		if (rightPrivs == null)
			return;
		
		for (PrivilegeDescription p : rightPrivs)
			handler.privilegeRemoved(rightTable, p);
	
		rightPrivs = null;
		}
	}
