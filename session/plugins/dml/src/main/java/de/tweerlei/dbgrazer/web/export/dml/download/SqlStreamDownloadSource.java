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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Download query results as SQL INSERT statements
 * 
 * @author Robert Wruck
 */
public class SqlStreamDownloadSource extends AbstractSqlDownloadSource
	{
	private final RowProducer producer;
	private final QualifiedName tableName;
	private final String header;
	private final String noDataFound;
	private final Set<Integer> pk;
	private final int blockSize;
	
	/**
	 * Constructor
	 * @param producer RowProducer
	 * @param fileName File base name
	 * @param tableName Table name for INSERT
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param pk Optional PK column indices for MERGE
	 * @param blockSize Block size for MERGE
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param dialect SQLDialect
	 */
	public SqlStreamDownloadSource(RowProducer producer, String fileName,
			QualifiedName tableName, String header, String noDataFound, Set<Integer> pk, int blockSize,
			DataFormatterFactory dataFormatterFactory, SQLDialect dialect)
		{
		super(fileName, dataFormatterFactory, dialect, pk != null);
		
		this.producer = producer;
		this.tableName = tableName;
		this.header = header;
		this.noDataFound = noDataFound;
		this.pk = pk;
		this.blockSize = blockSize;
		}
	
	@Override
	protected void writeSql(SQLWriter sw)
		{
		if ((pk != null) && !pk.isEmpty() && (blockSize > 0))
			writeMerge(sw);
		else
			writeInsert(sw);
		}
	
	private void writeMerge(SQLWriter sw)
		{
		final MergeRowHandler handler = new MergeRowHandler(tableName, pk, blockSize, sw);
		
		try	{
			sw.writeComment(header);
			producer.produceRows(handler);
			if (handler.getCount() == 0)
				sw.writeComment(noDataFound);
			}
		catch (RuntimeException e)
			{
			Logger.getLogger(getClass().getCanonicalName()).log(Level.SEVERE, "writeSql", e);
			if (e.getMessage() != null)
				sw.writeComment(e.getMessage());
			}
		}
	
	private void writeInsert(SQLWriter sw)
		{
		final InsertRowHandler handler = new InsertRowHandler(tableName, sw);
		
		try	{
			sw.writeComment(header);
			producer.produceRows(handler);
			if (handler.getCount() == 0)
				sw.writeComment(noDataFound);
			}
		catch (RuntimeException e)
			{
			Logger.getLogger(getClass().getCanonicalName()).log(Level.SEVERE, "writeSql", e);
			if (e.getMessage() != null)
				sw.writeComment(e.getMessage());
			}
		}
	}
