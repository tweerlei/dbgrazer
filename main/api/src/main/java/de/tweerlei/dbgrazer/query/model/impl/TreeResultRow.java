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

import java.util.LinkedList;
import java.util.List;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;

/**
 * ArrayList based impl.
 * 
 * @author Robert Wruck
 */
public class TreeResultRow extends DefaultResultRow
	{
	private final List<ColumnDef> columns;
	private final List<ResultRow> rows;
	
	/**
	 * Constructor
	 * @param capacity Initial capacity
	 * @param columns Child row columns
	 */
	public TreeResultRow(int capacity, List<ColumnDef> columns)
		{
		super(capacity);
		this.columns = columns;
		this.rows = new LinkedList<ResultRow>();
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
	public void accept(ResultVisitor v, int level)
		{
		if (v.startRow(this, level))
			{
			for (ResultRow row : rows)
				row.accept(v, level + 1);
			v.endRow(this, level);
			}
		}
	
	@Override
	public TreeResultRow clone()
		{
		final TreeResultRow ret = new TreeResultRow(getValues().size(), columns);
		for (ResultRow row : rows)
			ret.getRows().add(row.clone());
		ret.getValues().addAll(getValues());
		return (ret);
		}
	}
