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

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.OrderBy;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowIterator;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.web.model.CompareProgressMonitor;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
public interface ResultCompareService
	{
	/**
	 * Control which types of statements to generate
	 */
	public static final class CompareFlags
		{
		/** Use INSERT */
		public final boolean useInsert;
		/** Use UPDATE */
		public final boolean useUpdate;
		/** Use DELETE */
		public final boolean useDelete;
		/** Use MERGE instead of INSERT or UPDATE */
		public final boolean useMerge;
		
		/**
		 * Constructor
		 * @param useInsert Use INSERT
		 * @param useUpdate Use UPDATE
		 * @param useDelete Use DELETE
		 * @param useMerge Use MERGE instead of INSERT or UPDATE
		 */
		public CompareFlags(boolean useInsert, boolean useUpdate, boolean useDelete, boolean useMerge)
			{
			this.useInsert = useInsert;
			this.useUpdate = useUpdate;
			this.useDelete = useDelete;
			this.useMerge = useMerge;
			}
		}
	
	/**
	 * Compare two ResultSets, returning SQL statements to transform l to r
	 * @param l LHS
	 * @param r RHS
	 * @param h StatementHandler for generated DML statements
	 * @param monitor CompareProgressMonitor
	 * @param tableDesc TableDescription
	 * @param dialect SQLDialect
	 * @param flags Control which types of statements to generate
	 */
	public void compareResults(RowSet l, RowSet r, StatementHandler h, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect, CompareFlags flags);
	
	/**
	 * Compare two ResultSets, returning differing rows.
	 * Each returned row is prefixed with a marker column that contains one of ADDITION, DELETION or CHANGE.
	 * @param l LHS
	 * @param r RHS
	 * @param monitor CompareProgressMonitor
	 * @param tableDesc TableDescription
	 * @param dialect SQLDialect
	 * @return Differing rows
	 */
	public RowSet compareResults(RowSet l, RowSet r, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect);
	
	/**
	 * Compare two ResultSets matching rows by PK and returning SQL statements to transform l to r
	 * @param l LHS
	 * @param r RHS
	 * @param h StatementHandler for generated DML statements
	 * @param monitor CompareProgressMonitor
	 * @param tableDesc TableDescription
	 * @param dialect SQLDialect
	 * @param order Compare mode
	 * @param flags Control which types of statements to generate
	 */
	public void compareResultsByPK(RowIterator l, RowIterator r, StatementHandler h, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect, OrderBy order, CompareFlags flags);
	
	/**
	 * Compare two ResultSets matching rows by PK passing differing rows to the appropriate RowHandler
	 * @param l LHS
	 * @param r RHS
	 * @param insert RowHandler for inserting
	 * @param update RowHandler for updating
	 * @param delete RowHandler for deleting
	 * @param monitor CompareProgressMonitor
	 * @param tableDesc TableDescription
	 * @param dialect SQLDialect
	 */
	public void compareResultsByPK(RowIterator l, RowIterator r, RowHandler insert, RowHandler update, RowHandler delete, CompareProgressMonitor monitor, TableDescription tableDesc, SQLDialect dialect);
	
	/**
	 * Compare two sets of DDL objects. The returned columns are expected to conform to SQLObjectDDLWriter.findObjectSource
	 * @param l LHS
	 * @param r RHS
	 * @param h StatementHandler for generated DML statements
	 * @param monitor CompareProgressMonitor
	 * @param dialect SQLDialect
	 */
	public void compareDDLSource(RowIterator l, RowIterator r, StatementHandler h, CompareProgressMonitor monitor, SQLDialect dialect);
	
	/**
	 * Compare two sets of DDL object privileges. The returned columns are expected to conform to SQLObjectDDLWriter.findObjectPrivileges
	 * @param l LHS
	 * @param r RHS
	 * @param h StatementHandler for generated DML statements
	 * @param monitor CompareProgressMonitor
	 * @param dialect SQLDialect
	 */
	public void compareDDLPrivileges(RowIterator l, RowIterator r, StatementHandler h, CompareProgressMonitor monitor, SQLDialect dialect);
	}
