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

import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;

/**
 * Write MERGE statements for ResultRows
 * 
 * @author Robert Wruck
 */
public class SqlRowSetHandler implements RowSetHandler
	{
	private final SQLWriter sqlWriter;
	private final int blockSize;
	private int count;
	
	/**
	 * Constructor
	 * @param blockSize MERGE block size
	 * @param sqlWriter SQLWriter
	 */
	public SqlRowSetHandler(int blockSize, SQLWriter sqlWriter)
		{
		this.sqlWriter = sqlWriter;
		this.blockSize = blockSize;
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
	public void handleRowSet(TableDescription info, RowSet rs)
		{
		if (!info.getPKColumns().isEmpty() && (blockSize > 0))
			{
			final int n = rs.getRows().size();
			for (int i = 0; i < n; i += blockSize)
				sqlWriter.writeMerge(info.getName(), rs.getColumns(), rs.getRows().subList(i, Math.min(i + blockSize, n)), info.getPKColumns());
			}
		else
			{
			for (ResultRow row : rs.getRows())
				sqlWriter.writeInsert(info.getName(), rs.getColumns(), row);
			}
		
		count++;
		}
	}
