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
package de.tweerlei.dbgrazer.web.service.jdbc;

import java.util.Set;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Export data from SQLSchema objects
 * 
 * @author Robert Wruck
 */
public interface SchemaDataExportService
	{
	/** Table traversal mode */
	public static enum TraversalMode
		{
		/** Starting table only */
		STARTING_ONLY,
		/** Starting table and parent tables */
		PARENTS,
		/** Starting table, parents and child tables */
		CHILDREN,
		/** Starting table, parents, children and other children of parents */
		SIBLINGS,
		/** Starting table, parents, children, children of parents, children of parents of children */
		STEPCHILDREN
		}
	
	/**
	 * Export data from multiple tables
	 * @param link Link name
	 * @param dialect SQLDialect
	 * @param infos TableDescriptions
	 * @param startTable Starting table
	 * @param where WHERE condition for starting table
	 * @param handler RowSetHandler
	 * @param mode TraversalMode
	 * @return Number of Rows produced
	 * @throws PerformQueryException on error
	 */
	public int export(String link, SQLDialect dialect, Set<TableDescription> infos, QualifiedName startTable, String where, TraversalMode mode, RowSetHandler handler) throws PerformQueryException;
	}
