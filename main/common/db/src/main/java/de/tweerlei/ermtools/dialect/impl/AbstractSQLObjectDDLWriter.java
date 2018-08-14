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

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.ermtools.dialect.SQLObjectDDLWriter;

/**
 * Base implementation that provides for TableDescriptions
 * 
 * @author Robert Wruck
 */
public abstract class AbstractSQLObjectDDLWriter implements SQLObjectDDLWriter
	{
	private static final TableDescription findObjectsTableDescription;
	private static final TableDescription findObjectSourceTableDescription;
	private static final TableDescription findObjectPrivilegesTableDescription;
	
	static
		{
		final List<String> pkColumns = new ArrayList<String>(4);
		pkColumns.add("CATALOG");
		pkColumns.add("SCHEMA");
		pkColumns.add("NAME");
		pkColumns.add("TYPE");
		final PrimaryKeyDescription pk = new PrimaryKeyDescription(null, pkColumns);
		
		final List<ColumnDescription> columns = new ArrayList<ColumnDescription>(6);
		columns.add(new ColumnDescription("CATALOG", null, Types.VARCHAR, null, 30, 0, true, null));
		columns.add(new ColumnDescription("SCHEMA", null, Types.VARCHAR, null, 30, 0, true, null));
		columns.add(new ColumnDescription("NAME", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("TYPE", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("LENGTH", null, Types.BIGINT, null, 19, 0, false, null));
		columns.add(new ColumnDescription("HASH", null, Types.VARCHAR, null, 32767, 0, false, null));
		
		findObjectsTableDescription = new TableDescription(null, null, null, null, TableDescription.TABLE, pk, columns, null, null, null, null);
		}
	
	static
		{
		final List<String> pkColumns = new ArrayList<String>(4);
		pkColumns.add("CATALOG");
		pkColumns.add("SCHEMA");
		pkColumns.add("NAME");
		pkColumns.add("TYPE");
		final PrimaryKeyDescription pk = new PrimaryKeyDescription(null, pkColumns);
		
		final List<ColumnDescription> columns = new ArrayList<ColumnDescription>(5);
		columns.add(new ColumnDescription("CATALOG", null, Types.VARCHAR, null, 30, 0, true, null));
		columns.add(new ColumnDescription("SCHEMA", null, Types.VARCHAR, null, 30, 0, true, null));
		columns.add(new ColumnDescription("NAME", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("TYPE", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("LINE", null, Types.VARCHAR, null, 32767, 0, false, null));
		
		findObjectSourceTableDescription = new TableDescription(null, null, null, null, TableDescription.TABLE, pk, columns, null, null, null, null);
		}
	
	static
		{
		final List<String> pkColumns = new ArrayList<String>(4);
		pkColumns.add("CATALOG");
		pkColumns.add("SCHEMA");
		pkColumns.add("NAME");
		pkColumns.add("TYPE");
		pkColumns.add("GRANTEE");
		pkColumns.add("PRIVILEGE");
		final PrimaryKeyDescription pk = new PrimaryKeyDescription(null, pkColumns);
		
		final List<ColumnDescription> columns = new ArrayList<ColumnDescription>(5);
		columns.add(new ColumnDescription("CATALOG", null, Types.VARCHAR, null, 30, 0, true, null));
		columns.add(new ColumnDescription("SCHEMA", null, Types.VARCHAR, null, 30, 0, true, null));
		columns.add(new ColumnDescription("NAME", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("TYPE", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("GRANTEE", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("PRIVILEGE", null, Types.VARCHAR, null, 30, 0, false, null));
		columns.add(new ColumnDescription("IS_GRANTABLE", null, Types.VARCHAR, null, 30, 0, false, null));
		
		findObjectPrivilegesTableDescription = new TableDescription(null, null, null, null, TableDescription.TABLE, pk, columns, null, null, null, null);
		}
	
	public final TableDescription getFindObjectsTableDescription()
		{
		return findObjectsTableDescription;
		}
	
	public final TableDescription getFindObjectSourceTableDescription()
		{
		return findObjectSourceTableDescription;
		}
	
	public final TableDescription getFindObjectPrivilegesTableDescription()
		{
		return findObjectPrivilegesTableDescription;
		}
	}
