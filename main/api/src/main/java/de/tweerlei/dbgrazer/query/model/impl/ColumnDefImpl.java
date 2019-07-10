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
package de.tweerlei.dbgrazer.query.model.impl;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.TargetDef;

/**
 * Column definition
 * 
 * @author Robert Wruck
 */
public class ColumnDefImpl extends NamedBase implements ColumnDef
	{
	private final ColumnType type;
	private final String typeName;
	private final QualifiedName sourceObject;
	private final String sourceColumn;
	private final TargetDef target;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param type ColumnType
	 * @param typeName SQL type name
	 * @param target Target query
	 * @param sourceObject Source object name or null
	 * @param sourceColumn Source column name or null
	 */
	public ColumnDefImpl(String name, ColumnType type, String typeName, TargetDef target, QualifiedName sourceObject, String sourceColumn)
		{
		super(name);
		this.type = type;
		this.typeName = typeName;
		this.target = target;
		this.sourceObject = sourceObject;
		this.sourceColumn = sourceColumn;
		}

	@Override
	public ColumnType getType()
		{
		return type;
		}
	
	@Override
	public String getTypeName()
		{
		return typeName;
		}
	
	@Override
	public QualifiedName getSourceObject()
		{
		return sourceObject;
		}
	
	@Override
	public String getSourceColumn()
		{
		return (sourceColumn);
		}
	
	@Override
	public TargetDef getTargetQuery()
		{
		return (target);
		}
	}
