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
package de.tweerlei.dbgrazer.query.model.impl;

import java.util.List;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowIterator;

/**
 * Iterate over ResultRows produced by another thread
 * 
 * @author Robert Wruck
 */
public class AsyncRowIterator implements RowHandler, RowIterator
	{
	private final AsyncQueue<ResultRow> queue;
	private List<ColumnDef> cols;
	private RuntimeException lastError;
	
	/**
	 * Constructor
	 */
	public AsyncRowIterator()
		{
		this.queue = new AsyncQueue<ResultRow>(EmptyResultRow.getInstance());
		}
	
	/*
	 * Producer side
	 */
	
	@Override
	public void startRows(List<ColumnDef> columns)
		{
		cols = columns;
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		queue.add(row);
		return (true);
		}
	
	@Override
	public void endRows()
		{
		queue.done();
		}
	
	@Override
	public void error(RuntimeException e)
		{
		lastError = e;
		}
	
	/*
	 * Consumer side
	 */
	
	@Override
	public boolean hasNext()
		{
		final boolean b = queue.hasNext();	// may block
		
		if (lastError != null)
			{
			abort();
			throw lastError;
			}
		
		return (b);
		}
	
	@Override
	public ResultRow next()
		{
		final ResultRow row = queue.next();	// may block
		
		if (lastError != null)
			{
			abort();
			throw lastError;
			}
		
		return (row);
		}
	
	@Override
	public void remove()
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public List<ColumnDef> getColumns()
		{
		if (lastError != null)
			{
			abort();
			throw lastError;
			}
		
		return (cols);
		}
	
	@Override
	public void abort()
		{
		queue.abort();
		}
	}
