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
package de.tweerlei.common5.jdbc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Table description
 * 
 * @author Robert Wruck
 */
public class TableDescription
	{
	/** Type name for tables */
	public static final String TABLE = "TABLE";
	/** Type name for views */
	public static final String VIEW = "VIEW";
	
	private final QualifiedName name;
	private final String comment;
	private final String type;
	private final PrimaryKeyDescription pk;
	private final List<ColumnDescription> columns;
	private final List<IndexDescription> indices;
	private final List<ForeignKeyDescription> referencedKeys;
	private final List<ForeignKeyDescription> referencingKeys;
	private final List<PrivilegeDescription> privileges;
	private Set<Integer> pkIndices;
	
	/**
	 * Constructor
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param name Name
	 * @param comment Table comment
	 * @param type Table type
	 * @param pk Primary key description
	 * @param columns Column descriptions
	 * @param indices Index descriptions
	 * @param referencedKeys Referenced key descriptions
	 * @param referencingKeys Referencing key descriptions
	 * @param privileges Privilege descriptions
	 */
	public TableDescription(String catalog, String schema, String name, String comment, String type,
			PrimaryKeyDescription pk,
			Collection<ColumnDescription> columns, Collection<IndexDescription> indices,
			Collection<ForeignKeyDescription> referencedKeys, Collection<ForeignKeyDescription> referencingKeys,
			Collection<PrivilegeDescription> privileges)
		{
		this.name = new QualifiedName(catalog, schema, name);
		this.comment = comment;
		this.type = type;
		this.pk = pk;
		this.columns = (columns == null) ? Collections.<ColumnDescription>emptyList() : Collections.unmodifiableList(new ArrayList<ColumnDescription>(columns));
		this.indices = (indices == null) ? Collections.<IndexDescription>emptyList() : Collections.unmodifiableList(new ArrayList<IndexDescription>(indices));
		this.referencedKeys = (referencedKeys == null) ? Collections.<ForeignKeyDescription>emptyList() : Collections.unmodifiableList(new ArrayList<ForeignKeyDescription>(referencedKeys));
		this.referencingKeys = (referencingKeys == null) ? Collections.<ForeignKeyDescription>emptyList() : Collections.unmodifiableList(new ArrayList<ForeignKeyDescription>(referencingKeys));
		this.privileges = (privileges == null) ? Collections.<PrivilegeDescription>emptyList() : Collections.unmodifiableList(new ArrayList<PrivilegeDescription>(privileges));
		this.pkIndices = null;
		}
	
	/**
	 * Get the columns
	 * @return the columns
	 */
	public List<ColumnDescription> getColumns()
		{
		return columns;
		}

	/**
	 * Get a column by name
	 * @param n Column name
	 * @return ColumnDescription
	 */
	public ColumnDescription getColumn(String n)
		{
		for (ColumnDescription c : columns)
			{
			if (c.getName().equals(n))
				return (c);
			}
		return (null);
		}
	
	/**
	 * Get the numbers of the columns that are part of the PK.
	 * The returned set will reflect the column order in the PK.
	 * @return Set of column numbers
	 */
	public Set<Integer> getPKColumns()
		{
		if (pkIndices == null)
			{
			if (pk == null)
				pkIndices = Collections.emptySet();
			else
				{
				final Set<Integer> tmp = new LinkedHashSet<Integer>();
				for (String s : pk.getColumns())
					{
					int i = 0;
					for (ColumnDescription c : columns)
						{
						if (c.getName().equals(s))
							{
							tmp.add(i);
							break;
							}
						i++;
						}
					}
				
				pkIndices = Collections.unmodifiableSet(tmp);
				}
			}
		return (pkIndices);
		}
	
	/**
	 * Get the indices
	 * @return the indices
	 */
	public List<IndexDescription> getIndices()
		{
		return indices;
		}

	/**
	 * Get the referencedKeys
	 * @return the referencedKeys
	 */
	public List<ForeignKeyDescription> getReferencedKeys()
		{
		return referencedKeys;
		}

	/**
	 * Get the referencingKeys
	 * @return the referencingKeys
	 */
	public List<ForeignKeyDescription> getReferencingKeys()
		{
		return referencingKeys;
		}

	/**
	 * Get the privileges
	 * @return the privileges
	 */
	public List<PrivilegeDescription> getPrivileges()
		{
		return privileges;
		}

	/**
	 * Get the name
	 * @return the name
	 */
	public QualifiedName getName()
		{
		return name;
		}

	/**
	 * Get the comment
	 * @return the comment
	 */
	public String getComment()
		{
		return comment;
		}

	/**
	 * Get the type
	 * @return the type
	 */
	public String getType()
		{
		return type;
		}

	/**
	 * Get the pk
	 * @return the pk
	 */
	public PrimaryKeyDescription getPrimaryKey()
		{
		return pk;
		}
	
	/**
	 * Accept a visitor
	 * @param v Visitor
	 */
	public void accept(TableVisitor v)
		{
		v.beginTable(this);
		
		if (pk != null)
			pk.accept(v);
		
		v.beginColumns();
		for (ColumnDescription c : columns)
			c.accept(v);
		v.endColumns();
		
		v.beginIndices();
		for (IndexDescription i : indices)
			i.accept(v);
		v.endIndices();
		
		v.beginForeignKeys();
		for (ForeignKeyDescription f : referencedKeys)
			f.accept(v);
/*
		for (ForeignKeyDescription f : referencingKeys)
			f.accept(v);
*/
		v.endForeignKeys();
		
		v.beginPrivileges();
		for (PrivilegeDescription p : privileges)
			p.accept(v);
		v.endPrivileges();
		
		v.endTable(this);
		}

	@Override
	public int hashCode()
		{
		return (name.hashCode());
		}
	
	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableDescription other = (TableDescription) obj;
		if (!name.equals(other.name))
			return false;
		if (!type.equals(other.type))
			return false;
		return true;
		}
	
	@Override
	public String toString()
		{
		return (name.toString());
		}
	}
