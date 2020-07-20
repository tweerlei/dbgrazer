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
package de.tweerlei.dbgrazer.web.export.dml.download;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;

/**
 * Write MERGE statements for ResultRows
 * 
 * @author Robert Wruck
 */
public class MergeRowHandler implements RowHandler
	{
	private final SQLWriter sqlWriter;
	private final String tableName;
	private List<ColumnDef> cols;
	private final Set<Integer> pk;
	private final int blockSize;
	private final List<ResultRow> rows;
	private int count;
	
	/**
	 * Constructor
	 * @param tableName Table name
	 * @param pk PK column indices
	 * @param blockSize MERGE block size
	 * @param sqlWriter SQLWriter
	 */
	public MergeRowHandler(String tableName, Set<Integer> pk, int blockSize, SQLWriter sqlWriter)
		{
		this.sqlWriter = sqlWriter;
		this.tableName = tableName;
		this.pk = pk;
		this.blockSize = blockSize;
		this.rows = new ArrayList<ResultRow>(blockSize);
		this.count = 0;
		}
	
	/**
	 * Get the number of rows handled
	 * @return Count
	 */
	public int getCount()
		{
		return (count);
		}
	
	@Override
	public void startRows(List<ColumnDef> columns)
		{
		cols = columns;
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		rows.add(row.clone());
		if (rows.size() >= blockSize)
			endRows();
		
		count++;
		
		return (true);
		}
	
	@Override
	public void endRows()
		{
		sqlWriter.writeMerge(tableName, cols, rows, pk);
		rows.clear();
		}
	
	@Override
	public void error(RuntimeException e)
		{
		}
	}
