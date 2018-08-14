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

import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Download query results as SQL INSERT statements
 * 
 * @author Robert Wruck
 */
public class SqlMultiStreamDownloadSource extends AbstractSqlDownloadSource
	{
	private final RowSetProducer producer;
	private final String header;
	private final String noDataFound;
	private final int blockSize;
	private final SQLDialect dialect;
	
	/**
	 * Constructor
	 * @param producer RowSetProducer
	 * @param fileName File base name
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param blockSize Block size for MERGE
	 * @param dataFormatterFactory DataFormatterFactory
	 * @param dialect SQLDialect
	 */
	public SqlMultiStreamDownloadSource(RowSetProducer producer, String fileName,
			String header, String noDataFound, int blockSize,
			DataFormatterFactory dataFormatterFactory, SQLDialect dialect)
		{
		super(fileName, dataFormatterFactory, dialect, false);
		
		this.producer = producer;
		this.header = header;
		this.noDataFound = noDataFound;
		this.blockSize = blockSize;
		this.dialect = dialect;
		}
	
	@Override
	protected void writeSql(SQLWriter sw)
		{
		final SqlRowSetHandler handler = new SqlRowSetHandler(blockSize, sw, dialect);
		
		try	{
			sw.writeComment(header);
			
			if (producer.produceRowSets(handler) == 0)
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
