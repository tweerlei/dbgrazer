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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.util.SortedSet;
import java.util.TreeSet;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.web.model.UserObject;

/**
 * A named set of tables
 * 
 * @author Robert Wruck
 */
public class TableSet extends UserObject
	{
	private final SortedSet<QualifiedName> tableNames;
	
	/**
	 * Constructor
	 * @param tableNames Table names
	 */
	public TableSet(SortedSet<QualifiedName> tableNames)
		{
		this.tableNames = tableNames;
		}
	
	/**
	 * Constructor
	 */
	public TableSet()
		{
		this(new TreeSet<QualifiedName>());
		}

	/**
	 * Get the tableNames
	 * @return the tableNames
	 */
	public SortedSet<QualifiedName> getTableNames()
		{
		return tableNames;
		}
	}
