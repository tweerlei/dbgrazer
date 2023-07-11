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

import java.util.ArrayList;
import java.util.List;

import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.model.SQLSchema;
import de.tweerlei.ermtools.model.SQLVisitorAdapter;

/**
 * Move all objects to a different schema
 * 
 * @author Robert Wruck
 */
public class SchemaChangeVisitor extends SQLVisitorAdapter
	{
	private final SQLSchema resultSchema;
	
	/**
	 * Constructor
	 * @param catalog New catalog name
	 * @param schema New schema name
	 */
	public SchemaChangeVisitor(String catalog, String schema)
		{
		this.resultSchema = new SQLSchema(catalog, schema);
		}
	
	/**
	 * Get the resulting schema
	 * @return SQLSchema
	 */
	public SQLSchema getSchema()
		{
		return (resultSchema);
		}
	
	@Override
	public void beginTable(TableDescription table)
		{
		resultSchema.addTable(new TableDescription(
				resultSchema.getCatalog(),
				resultSchema.getSchema(),
				table.getName().getObjectName(),
				table.getComment(),
				table.getType(),
				table.getPrimaryKey(),
				table.getColumns(),
				table.getIndices(),
				mapForeignKeys(table.getName(), table.getReferencedKeys()),
				mapForeignKeys(table.getName(), table.getReferencingKeys()),
				table.getPrivileges()
				));
		}
	
	private List<ForeignKeyDescription> mapForeignKeys(QualifiedName tableName, List<ForeignKeyDescription> keys)
		{
		if (keys.isEmpty())
			return (keys);
		
		final List<ForeignKeyDescription> ret = new ArrayList<ForeignKeyDescription>(keys.size());
		for (ForeignKeyDescription fk : keys)
			{
			if (fk.getTableName().hasSameCatalog(tableName) && fk.getTableName().hasSameSchema(tableName))
				{
				ret.add(new ForeignKeyDescription(
						fk.getName(),
						resultSchema.getCatalog(),
						resultSchema.getSchema(),
						fk.getTableName().getObjectName(),
						fk.getColumns()
						));
				}
			}
		return (ret);
		}
	}
