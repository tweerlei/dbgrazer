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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataRetrievalFailureException;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowIterator;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;

/**
 * RowIterator for a ResultSet
 * 
 * @author Robert Wruck
 */
public class ResultSetIterator implements RowIterator
	{
	private final ResultSet rs;
	private final ResultSetAccessor accessor;
	private List<ColumnDef> columns;
	private ResultRow row;
	
	/**
	 * Constructor
	 * @param rs ResultSet
	 * @param accessor ResultSetAccessor
	 */
	public ResultSetIterator(ResultSet rs, ResultSetAccessor accessor)
		{
		this.rs = rs;
		this.accessor = accessor;
		}
	
	@Override
	public boolean hasNext()
		{
		if (row != null)
			return (true);
		
		try	{
			if (!rs.next())
				return (false);
			
			if (columns == null)
				{
				final ResultSetMetaData rsmd = rs.getMetaData();
				final int c = rsmd.getColumnCount();
				columns = new ArrayList<ColumnDef>(c);
				for (int i = 1; i <= c; i++)
					columns.add(accessor.getColumnDef(rs, i, null));
				}
			
			final int c = columns.size();
			row = new DefaultResultRow(c);
			for (int i = 1; i <= c; i++)
				row.getValues().add(accessor.getObject(rs, i));
			
			return (true);
			}
		catch (SQLException e)
			{
			throw new DataRetrievalFailureException("hasNext", e);
			}
		}
	
	@Override
	public ResultRow next()
		{
		if (row == null)
			throw new NoSuchElementException();
		
		final ResultRow ret = row;
		row = null;
		return (ret);
		}
	
	@Override
	public void remove()
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public List<ColumnDef> getColumns()
		{
		if (columns == null)
			hasNext();
		
		return (columns);
		}
	
	@Override
	public void abort()
		{
		}
	}
