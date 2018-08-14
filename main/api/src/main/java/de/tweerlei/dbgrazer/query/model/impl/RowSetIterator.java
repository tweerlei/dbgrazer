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

import java.util.Iterator;
import java.util.List;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowContainer;
import de.tweerlei.dbgrazer.query.model.RowIterator;

/**
 * Iterate over result set rows
 * 
 * @author Robert Wruck
 */
public class RowSetIterator implements RowIterator
	{
	private final RowContainer<ResultRow> rs;
	private final Iterator<ResultRow> it;
	
	/**
	 * Constructor
	 * @param rs RowSet
	 */
	public RowSetIterator(RowContainer<ResultRow> rs)
		{
		this.rs = rs;
		this.it = rs.getRows().iterator();
		}
	
	@Override
	public boolean hasNext()
		{
		return (it.hasNext());
		}

	@Override
	public ResultRow next()
		{
		return (it.next());
		}
	
	@Override
	public void remove()
		{
		it.remove();
		}
	
	@Override
	public List<ColumnDef> getColumns()
		{
		return (rs.getColumns());
		}
	
	@Override
	public void abort()
		{
		}
	}
