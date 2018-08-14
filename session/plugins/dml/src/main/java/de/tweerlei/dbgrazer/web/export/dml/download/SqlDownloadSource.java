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

import java.util.Set;

import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Download a RowSet as SQL INSERT statements
 * 
 * @author Robert Wruck
 */
public class SqlDownloadSource extends AbstractSqlDownloadSource
	{
	private final String tableName;
	private final String header;
	private final String noDataFound;
	private final RowSet rs;
	private final Set<Integer> pk;
	private final int blockSize;
	
	/**
	 * Constructor
	 * @param rs RowSet
	 * @param tableName Table name for INSERT
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param pk Optional PK column indices for MERGE
	 * @param blockSize Block size for MERGE
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param dialect SQLDialect
	 */
	public SqlDownloadSource(RowSet rs, String tableName, String header, String noDataFound, Set<Integer> pk, int blockSize,
			DataFormatterFactory dataFormatterFactory, SQLDialect dialect)
		{
		super(rs.getQuery().getName(), dataFormatterFactory, dialect, pk != null);
		
		this.tableName = tableName;
		this.header = header;
		this.noDataFound = noDataFound;
		this.pk = pk;
		this.rs = rs;
		this.blockSize = blockSize;
		}
	
	@Override
	protected void writeSql(SQLWriter sw)
		{
		sw.writeComment(header);
		
		if ((rs == null) || rs.getRows().isEmpty())
			sw.writeComment(noDataFound);
		else
			exportRows(sw);
		}
	
	private void exportRows(SQLWriter sw)
		{
		if ((pk != null) && (blockSize > 0))
			{
			final int n = rs.getRows().size();
			for (int i = 0; i < n; i += blockSize)
				sw.writeMerge(tableName, rs.getColumns(), rs.getRows().subList(i, Math.min(i + blockSize, n)), pk);
			}
		else
			{
			for (ResultRow row : rs.getRows())
				sw.writeInsert(tableName, rs.getColumns(), row);
			}
		}
	}
