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
package de.tweerlei.dbgrazer.query.model;

import java.io.Serializable;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Column definition
 * 
 * @author Robert Wruck
 */
public interface ColumnDef extends Named, Serializable
	{
	/**
	 * Get the type
	 * @return the type
	 */
	public ColumnType getType();
	
	/**
	 * Get the SQL type name
	 * @return the SQL type name
	 */
	public String getTypeName();
	
	/**
	 * Get the sourceObject
	 * @return the sourceObject
	 */
	public QualifiedName getSourceObject();
	
	/**
	 * Get the sourceColumn
	 * @return the sourceColumn
	 */
	public String getSourceColumn();
	
	/**
	 * Get target query
	 * @return TargetDef or null
	 */
	public TargetDef getTargetQuery();
	}
