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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.RowIterator;
import de.tweerlei.dbgrazer.query.model.RowSet;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public class RowSetImpl implements RowSet
	{
	private final Query query;
	private final int subQueryIndex;
	private final List<String> parameters;
	private final List<ColumnDef> columns;
	private final List<ResultRow> rows;
	private final Map<String, Object> attributes;
	private long time;
	private int affectedRows;
	private boolean moreAvailable;
	
	/**
	 * Constructor
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 * @param columns Column definitions
	 */
	public RowSetImpl(Query query, int subQueryIndex, List<ColumnDef> columns)
		{
		this.query = query;
		this.subQueryIndex = subQueryIndex;
		if (columns == null)
			this.columns = new ArrayList<ColumnDef>(0);
		else
			this.columns = columns;
		this.rows = new LinkedList<ResultRow>();
		this.attributes = new HashMap<String, Object>();
		this.parameters = new ArrayList<String>();
		this.affectedRows = -1;
		}
	
	@Override
	public Query getQuery()
		{
		return (query);
		}
	
	@Override
	public List<String> getParameterValues()
		{
		return (parameters);
		}
	
	@Override
	public int getSubQueryIndex()
		{
		return (subQueryIndex);
		}
	
	@Override
	public List<ColumnDef> getColumns()
		{
		return (columns);
		}
	
	@Override
	public List<ResultRow> getRows()
		{
		return (rows);
		}
	
	@Override
	public RowIterator iterator()
		{
		return (new RowSetIterator(this));
		}
	
	@Override
	public ResultRow getFirstRow()
		{
		if (rows.isEmpty())
			return (EmptyResultRow.getInstance());
		
		return (rows.get(0));
		}
	
	@Override
	public List<Object> getFirstColumn()
		{
		final List<Object> ret = new ArrayList<Object>(rows.size());
		
		for (ResultRow row : rows)
			ret.add(row.getValues().get(0));
		
		return (ret);
		}
	
	@Override
	public Object getFirstValue()
		{
		if (rows.isEmpty())
			return (null);
		
		return (rows.get(0).getValues().get(0));
		}
	
	@Override
	public int getAffectedRows()
		{
		if (affectedRows < 0)
			return (rows.size());
		else
			return (affectedRows);
		}
	
	/**
	 * Set the affected rows
	 * @param affectedRows affected row count
	 */
	public void setAffectedRows(int affectedRows)
		{
		this.affectedRows = affectedRows;
		}
	
	@Override
	public boolean isMoreAvailable()
		{
		return moreAvailable;
		}
	
	/**
	 * Set the moreAvailable
	 * @param moreAvailable the moreAvailable to set
	 */
	public void setMoreAvailable(boolean moreAvailable)
		{
		this.moreAvailable = moreAvailable;
		}
	
	@Override
	public long getQueryTime()
		{
		return (time);
		}
	
	/**
	 * Set the time taken
	 * @param time Milliseconds
	 */
	public void setQueryTime(long time)
		{
		this.time = time;
		}
	
	@Override
	public Map<String, Object> getAttributes()
		{
		return (attributes);
		}
	
	@Override
	public void accept(ResultVisitor v, int level)
		{
		if (v.startRowSet(this))
			{
			for (ResultRow row : rows)
				row.accept(v, level);
			v.endRowSet(this);
			}
		}
	
	@Override
	public RowSetImpl clone()
		{
		final RowSetImpl ret = new RowSetImpl(query, subQueryIndex, new ArrayList<ColumnDef>(columns));
		
		ret.setAffectedRows(affectedRows);
		ret.setMoreAvailable(moreAvailable);
		ret.setQueryTime(time);
		ret.getParameterValues().addAll(parameters);
		ret.getAttributes().putAll(attributes);
		for (ResultRow row : rows)
			ret.getRows().add(row.clone());
		
		return (ret);
		}
	}
