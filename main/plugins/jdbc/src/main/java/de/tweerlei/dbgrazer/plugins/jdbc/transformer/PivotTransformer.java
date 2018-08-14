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
package de.tweerlei.dbgrazer.plugins.jdbc.transformer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultVisitorAdapter;

/**
 * Performs a Pivot transformation on a RowSet.
 * 1st column is interpreted as row ID, 2nd column as column ID.
 * The values in the 3rd column will be placed in the pivot table.
 * 
 * @author Robert Wruck
 */
public class PivotTransformer extends ResultVisitorAdapter
	{
	private static final int ROW_KEY_INDEX = 0;
	private static final int COL_KEY_INDEX = 1;
	private static final int DATA_INDEX = 2;
	
	private Map<String, Integer> columnMapping;
	private Map<String, ResultRow> rowMapping;
	private int columnCount;
	
	@Override
	public boolean startRowSet(RowSet rs)
		{
		final int n = rs.getColumns().size();
		
		if (n <= DATA_INDEX)
			return (false);
		
		columnMapping = new HashMap<String, Integer>();
		rowMapping = new LinkedHashMap<String, ResultRow>();
		
		// Remove all columns except first, save 3rd column description for creating data columns
		final List<ColumnDef> columns = rs.getColumns();
		final ColumnDef dataColumn = columns.get(DATA_INDEX);
		while (columns.size() > 1)
			columns.remove(1);
		
		// Extract all data column names
		for (ResultRow row : rs.getRows())
			{
			final Object colKey = row.getValues().get(COL_KEY_INDEX);
			
			if (colKey != null)
				{
				final String columnName = colKey.toString();
				final Integer columnIndex = columnMapping.get(columnName);
				if (columnIndex == null)
					{
					columnMapping.put(columnName, columns.size());
					columns.add(new ColumnDefImpl(columnName, dataColumn.getType(), dataColumn.getTypeName(), dataColumn.getTargetQuery(), dataColumn.getSourceObject(), dataColumn.getSourceColumn()));
					}
				}
			}
		
		columnCount = columns.size();
		
		return (true);
		}
	
	@Override
	public boolean startRow(ResultRow row, int level)
		{
		final List<Object> values = row.getValues();
		
		final Object rowKey = values.get(ROW_KEY_INDEX);
		final Object colKey = values.get(COL_KEY_INDEX);
		final Object data = values.get(DATA_INDEX);
		
		if (rowKey != null)
			{
			final String rowName = rowKey.toString();
			
			ResultRow target = rowMapping.get(rowName);
			if (target == null)
				{
				target = new DefaultResultRow(columnCount);
				target.getValues().add(rowKey);
				for (int i = 1; i < columnCount; i++)
					target.getValues().add(null);
				rowMapping.put(rowName, target);
				}
			
			if (colKey != null)
				{
				final String columnName = colKey.toString();
				final Integer columnIndex = columnMapping.get(columnName);
				target.getValues().set(columnIndex, data);
				}
			}
		
		// we only handle top level rows
		return (false);
		}
	
	@Override
	public void endRowSet(RowSet rs)
		{
		rs.getRows().clear();
		
		for (ResultRow row : rowMapping.values())
			rs.getRows().add(row);
		}
	}
