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

import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Produce MERGE statements for rows
 * 
 * @author Robert Wruck
 */
public class MergeStatementProducer implements StatementProducer
	{
	private final DataFormatterFactory factory;
	private final RowProducer producer;
	private final String tableName;
	private final Set<Integer> pk;
	private final int blockSize;
	private final SQLDialect dialect;
	private final String prepare;
	private final String cleanup;
	
	/**
	 * Constructor
	 * @param factory DataFormatterFactory
	 * @param producer RowProducer
	 * @param tableName Destination table name
	 * @param pk PK column indices
	 * @param blockSize MERGE block size
	 * @param dialect SQLDialect
	 * @param prepare Prepate statement
	 * @param cleanup Cleanup statement
	 */
	public MergeStatementProducer(DataFormatterFactory factory, RowProducer producer, String tableName, Set<Integer> pk, int blockSize, SQLDialect dialect, String prepare, String cleanup)
		{
		this.factory = factory;
		this.producer = producer;
		this.tableName = tableName;
		this.pk = pk;
		this.blockSize = blockSize;
		this.dialect = dialect;
		this.prepare = prepare;
		this.cleanup = cleanup;
		}
	
	@Override
	public void produceStatements(StatementHandler h)
		{
		final SQLWriter sw = factory.getSQLWriter(h, dialect, false);
		final RowHandler handler = new MergeRowHandler(tableName, pk, blockSize, sw);
		
		producer.produceRows(handler);
		}
	
	@Override
	public String getPrepareStatement()
		{
		return (prepare);
		}
	
	@Override
	public String getCleanupStatement()
		{
		return (cleanup);
		}
	}
