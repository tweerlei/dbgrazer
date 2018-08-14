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
package de.tweerlei.dbgrazer.web.service;

import java.util.Set;

import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.CompareHandler;
import de.tweerlei.dbgrazer.web.model.CompareProgressMonitor;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
public interface ResultDiffService
	{
	/**
	 * Compare and format two ResultSets, returning only the differing rows.
	 * Each returned row is prefixed with a marker column that contains one of ADDITION or DELETION.
	 * @param l LHS
	 * @param r RHS
	 * @param fmt DataFormatter
	 * @param trim Remove columns with only NULL values
	 * @return Difference RowSet
	 */
	public RowSet diffResults(RowSet l, RowSet r, DataFormatter fmt, boolean trim);
	
	/**
	 * Compare two ResultSets, returning a RowSet with a single value that is formatted as diff
	 * @param l LHS
	 * @param r RHS
	 * @param ln Label for rows only found in LHS
	 * @param rn Label for rows only found in RHS
	 * @param fmt DataFormatter
	 * @return Difference RowSet
	 */
	public RowSet diffTextResults(RowSet l, RowSet r, String ln, String rn, DataFormatter fmt);
	
	/**
	 * Compare two RowSets, passing differences to a CompareHandler
	 * @param l LHS
	 * @param r RHS
	 * @param h CompareHandler
	 * @param monitor CompareProgressMonitor
	 * @param tableName Table name to pass to the CompareHandler
	 * @param pk PK to pass to the CompareHandler
	 */
	public void compareResults(RowSet l, RowSet r, CompareHandler h, CompareProgressMonitor monitor, String tableName, Set<Integer> pk);
	}
