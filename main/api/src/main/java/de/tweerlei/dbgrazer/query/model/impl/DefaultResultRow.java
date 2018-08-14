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
import java.util.Collections;
import java.util.List;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.RowIterator;

/**
 * ArrayList based impl.
 * 
 * @author Robert Wruck
 */
public class DefaultResultRow implements ResultRow
	{
	private final List<Object> values;
	
	/**
	 * Constructor
	 * @param capacity Initial capacity
	 */
	public DefaultResultRow(int capacity)
		{
		this.values = new ArrayList<Object>(capacity);
		}
	
	/**
	 * Constructor
	 * @param values Initial values
	 */
	public DefaultResultRow(Object... values)
		{
		this(values.length);
		
		for (Object o : values)
			this.values.add(o);
		}
	
	@Override
	public List<ColumnDef> getColumns()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<ResultRow> getRows()
		{
		return (Collections.emptyList());
		}
	
	@Override
	public List<Object> getValues()
		{
		return (values);
		}
	
	@Override
	public RowIterator iterator()
		{
		return (new RowSetIterator(this));
		}
	
	@Override
	public void accept(ResultVisitor v, int level)
		{
		if (v.startRow(this, level))
			v.endRow(this, level);
		}
	
	@Override
	public DefaultResultRow clone()
		{
		final DefaultResultRow ret = new DefaultResultRow(values.size());
		ret.getValues().addAll(values);
		return (ret);
		}
	}
