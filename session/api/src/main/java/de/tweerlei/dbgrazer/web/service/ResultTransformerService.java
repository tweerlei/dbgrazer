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

import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
public interface ResultTransformerService
	{
	/**
	 * Translate raw objects in a RowSet using a given formatter
	 * @param rs RowSet
	 * @param fmt DataFormatter
	 */
	public void translateRowSet(RowSet rs, DataFormatter fmt);
	
	/**
	 * Translate raw objects in a RowSet using a given formatter
	 * @param rs RowSet
	 * @param fmt DataFormatter
	 * @param trim Remove columns with only NULL values
	 * @param depth calculate the RowSet depth and store it as ATTR_DEPTH attribute
	 * @param sum calculate a ResultRow with sums for all numeric columns and store it as ATTR_SUM_VALUES attribute
	 */
	public void translateRowSet(RowSet rs, DataFormatter fmt, boolean trim, boolean depth, boolean sum);
	
	/**
	 * Replace all data in a RowSet with a single value that is formatted in a given format
	 * @param rs RowSet
	 * @param fmt DataFormatter
	 * @param format Format tag
	 * @param options Formatting options
	 */
	public void formatRowSet(RowSet rs, DataFormatter fmt, String format, Set<TextTransformerService.Option> options);
	
	/**
	 * Add rows from a Result to another Result, inserting a prefix as first column
	 * @param r Result to add rows to
	 * @param newRows Result with new rows
	 * @param prefixName Prefix column name
	 * @param prefixType Prefix column type
	 * @param prefix Prefix value
	 * @param maxRows Maximum rows in the result's RowSets (FIFO)
	 */
	public void addRowsWithPrefix(Result r, Result newRows, String prefixName, ColumnType prefixType, Object prefix, int maxRows);
	
	/**
	 * Convert a RowSet with two columns (key, value) into a Map
	 * @param rs RowSet
	 * @param fmt DataFormatter
	 * @return Map
	 */
	public Map<String, String> convertToMap(RowSet rs, DataFormatter fmt);
	
	/**
	 * Convert a RowSet with a single column into a String
	 * @param rs RowSet
	 * @param fmt DataFormatter
	 * @return String
	 */
	public String convertToString(RowSet rs, DataFormatter fmt);
	}
