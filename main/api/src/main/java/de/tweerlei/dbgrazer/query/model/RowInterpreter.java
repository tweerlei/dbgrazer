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

import java.util.List;

/**
 * Transfer data rows from a RowIterator to the right RowHandler
 * 
 * @author Robert Wruck
 */
public interface RowInterpreter
	{
	/**
	 * RowHandler definition
	 */
	public static final class RowHandlerDef
		{
		private final String stmt;
		private final List<ColumnDef> columns;
		private final ResultRowMapper mapper;
		
		/**
		 * Constructor
		 * @param stmt SQL statement
		 * @param columns Parameter types
		 * @param mapper ResultRowMapper
		 */
		public RowHandlerDef(String stmt, List<ColumnDef> columns, ResultRowMapper mapper)
			{
			this.stmt = stmt;
			this.columns = columns;
			this.mapper = mapper;
			}
		
		/**
		 * @return the stmt
		 */
		public String getStmt()
			{
			return stmt;
			}
		
		/**
		 * @return the mapper
		 */
		public ResultRowMapper getMapper()
			{
			return mapper;
			}
		
		/**
		 * @return the columns
		 */
		public List<ColumnDef> getColumns()
			{
			return columns;
			}
		}
	
	/**
	 * Transfer data rows from a RowIterator to a StatementHandler
	 * @param handlers RowHandler for all statements returned by getStatements
	 */
	public void produceRows(List<RowHandler> handlers);
	
	/**
	 * Get the statements to prepare for invoking the transfer method
	 * @return RowHandlerDefs
	 */
	public List<RowHandlerDef> getStatements();
	
	/**
	 * Get the prepare statement that will be executed prior to all statements returned from the iterator
	 * @return Prepare statement or null
	 */
	public String getPrepareStatement();
	
	/**
	 * Get the cleanup statement that will be executed after all statements returned from the iterator and in case of an error
	 * @return Cleanup statement or null
	 */
	public String getCleanupStatement();
	}
