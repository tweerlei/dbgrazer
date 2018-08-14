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
import java.util.List;

/**
 * A RowSet returned by a database query
 * @param <T> Row type
 * 
 * @author Robert Wruck
 */
public interface RowContainer<T extends RowContainer<?>> extends Serializable, Cloneable, Iterable<ResultRow>
	{
	/**
	 * Get the column names of the child rows
	 * @return Column names
	 */
	public List<ColumnDef> getColumns();
	
	/**
	 * Get the child rows
	 * @return List of Maps: Column name -> Value
	 */
	public List<T> getRows();
	
	@Override
	public RowIterator iterator();
	
	/**
	 * Accept a ResultVisitor
	 * @param v ResultVisitor
	 * @param level Hierarchy level
	 */
	public void accept(ResultVisitor v, int level);
	
	/**
	 * Clone this row
	 * @return New row with same values
	 */
	public RowContainer<T> clone();
	}
