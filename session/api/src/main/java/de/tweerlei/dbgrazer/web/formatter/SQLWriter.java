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
package de.tweerlei.dbgrazer.web.formatter;

import java.util.List;
import java.util.Set;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;

/**
 * Generate SQL statements for mass data
 * 
 * @author Robert Wruck
 */
public interface SQLWriter
	{
	/**
	 * Write an INSERT statement for the given data
	 * @param tableName Table name
	 * @param columns Columns
	 * @param values Values (same number and order as columns, null to write JDBC placeholders)
	 */
	public void writeInsert(QualifiedName tableName, List<ColumnDef> columns, ResultRow values);
	
	/**
	 * Write an INSERT statement for the given data
	 * @param tableName Table name
	 * @param columns Columns
	 * @param values Values (same number and order as columns, null to write JDBC placeholders)
	 * @param pkColumns Primary key columns
	 * @param pkValues PK column values as raw SQL expressions
	 */
	public void writeInsert(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, List<ColumnDef> pkColumns, List<Object> pkValues);
	
	/**
	 * Write an UPDATE statement for the given data
	 * @param tableName Table name
	 * @param columns Columns
	 * @param oldValues Original values (same number and order as columns, null to write JDBC placeholders)
	 * @param newValues New values (same number and order as columns, null to write JDBC placeholders)
	 * @param pk Primary key column indices. If not empty, only these columns will be included in the WHERE clause.
	 * @return true if a statement was written, false if newValues matched oldValues
	 */
	public boolean writeUpdate(QualifiedName tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk);
	
	/**
	 * Check whether an update is necessary, i.e. newValues do not match oldValues.
	 * This is the same result as would be returned by writeUpdate, except that no actual statement is written.
	 * @param oldValues Original values
	 * @param newValues New values
	 * @param pk Primary key column indices. If not empty, only these columns will be included in the WHERE clause.
	 * @return true if a statement would be written, false if newValues matched oldValues
	 */
	public boolean checkUpdate(ResultRow oldValues, ResultRow newValues, Set<Integer> pk);
	
	/**
	 * Write a DELETE statement for the given data
	 * @param tableName Table name
	 * @param columns Columns
	 * @param values Values (same number and order as columns, null to write JDBC placeholders)
	 * @param pk Primary key column indices. If not empty, only these columns will be included in the WHERE clause.
	 */
	public void writeDelete(QualifiedName tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk);
	
	/**
	 * Write a MERGE statement for the given data
	 * @param tableName Table name
	 * @param columns Columns
	 * @param rows Rows of values (same number and order as columns, null to write JDBC placeholders)
	 * @param pk Primary key column indices
	 */
	public void writeMerge(QualifiedName tableName, List<ColumnDef> columns, List<ResultRow> rows, Set<Integer> pk);
	
	/**
	 * Write a comment
	 * @param comment Comment text
	 */
	public void writeComment(String comment);
	}
