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

/**
 * A visitor for Result structures
 * 
 * @author Robert Wruck
 */
public interface ResultVisitor
	{
	/**
	 * Start visiting a Result
	 * @param r Result
	 * @return true to continue with the Result's RowSets, false to abort
	 */
	public boolean startResult(Result r);
	
	/**
	 * End visiting a Result
	 * @param r Result
	 */
	public void endResult(Result r);
	
	/**
	 * Start visiting a RowSet
	 * @param rs RowSet
	 * @return true to continue with the RowSet's rows, false to abort
	 */
	public boolean startRowSet(RowSet rs);
	
	/**
	 * End visiting a RowSet
	 * @param rs RowSet
	 */
	public void endRowSet(RowSet rs);
	
	/**
	 * Start visiting a ResultRow
	 * @param row ResultRow
	 * @param level Hierarchy level
	 * @return true to continue with the ResultRow's child rows, false to abort
	 */
	public boolean startRow(ResultRow row, int level);
	
	/**
	 * End visiting a ResultRow
	 * @param row ResultRow
	 * @param level Hierarchy level
	 */
	public void endRow(ResultRow row, int level);
	}
