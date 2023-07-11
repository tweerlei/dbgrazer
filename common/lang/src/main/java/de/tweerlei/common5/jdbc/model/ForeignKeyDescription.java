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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Foreign key description
 * 
 * @author Robert Wruck
 */
public class ForeignKeyDescription
	{
	private final String name;
	private final QualifiedName tableName;
	private final Map<String, String> columns;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param tableCatalog Reference table catalog
	 * @param tableSchema Reference table schema
	 * @param tableName Reference table name
	 * @param columns Map: Source column -> destination column
	 */
	public ForeignKeyDescription(String name, String tableCatalog, String tableSchema, String tableName, Map<String, String> columns)
		{
		this.name = name;
		this.tableName = new QualifiedName(tableCatalog, tableSchema, tableName);
		this.columns = (columns == null) ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<String, String>(columns));
		}

	/**
	 * Get the name
	 * @return the name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * Get the tableName
	 * @return the tableName
	 */
	public QualifiedName getTableName()
		{
		return tableName;
		}

	/**
	 * Get the columns. Iterating over the map will return the columns as ordered in the foreign key.
	 * @return the columns
	 */
	public Map<String, String> getColumns()
		{
		return columns;
		}

	/**
	 * Accept a visitor
	 * @param v Visitor
	 */
	public void accept(TableVisitor v)
		{
		v.visitForeignKey(this);
		}
	
	/**
	 * Check whether this object is "equal" to another ForeignKeyDescription, given local and remote table names
	 * @param r Remote ForeignKeyDescription
	 * @param localTableName Local table name
	 * @param remoteTableName Remote table name
	 * @return true if equal
	 */
	public boolean equals(ForeignKeyDescription r, QualifiedName localTableName, QualifiedName remoteTableName)
		{
		if (tableName.hasSameSchema(localTableName) && r.tableName.hasSameSchema(remoteTableName))
			{
			if (!tableName.getObjectName().equals(r.tableName.getObjectName()))
				return (false);
			}
		else
			{
			if (!tableName.equals(r.tableName))
				return (false);
			}
		return (columns.equals(r.columns));
		}
	
	@Override
	public int hashCode()
		{
		return (columns.hashCode());
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == null)
			return (false);
		if (o == this)
			return (true);
		if (!(o instanceof ForeignKeyDescription))
			return (false);
		final ForeignKeyDescription i = (ForeignKeyDescription) o;
		return (equals(i, tableName, i.tableName));
		}
	}
