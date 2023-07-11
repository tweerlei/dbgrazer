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
package de.tweerlei.ermtools.model;

import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.IndexDescription;
import de.tweerlei.common5.jdbc.model.PrimaryKeyDescription;
import de.tweerlei.common5.jdbc.model.PrivilegeDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;

/**
 * Adapter
 * 
 * @author Robert Wruck
 */
public class SQLVisitorAdapter implements SQLVisitor
	{
	public void beginTable(TableDescription table)
		{
		}
	
	public void endTable(TableDescription table)
		{
		}
	
	public void beginColumns()
		{
		}
	
	public void visitColumn(ColumnDescription column)
		{
		}
	
	public void endColumns()
		{
		}
	
	public void visitPrimaryKey(PrimaryKeyDescription pk)
		{
		}
	
	public void beginIndices()
		{
		}
	
	public void visitIndex(IndexDescription index)
		{
		}
	
	public void endIndices()
		{
		}
	
	public void beginForeignKeys()
		{
		}
	
	public void visitForeignKey(ForeignKeyDescription fk)
		{
		}
	
	public void endForeignKeys()
		{
		}
	
	public void beginPrivileges()
		{
		}
	
	public void visitPrivilege(PrivilegeDescription p)
		{
		}
	
	public void endPrivileges()
		{
		}
	
	public void beginSchema(SQLSchema schema)
		{
		}
	
	public void endSchema(SQLSchema schema)
		{
		}
	}
