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
package de.tweerlei.dbgrazer.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultVisitorAdapter;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;

/**
 * Build a new ResultSet from differences, prefixing each row with a column indicating addition, removal or change
 * 
 * @author Robert Wruck
 */
public class DiffCompareHandler implements CompareHandler
	{
	/** diffResults and compareResults: Value inserted into first column for added rows */
	public static final String ADDITION = "+";
	/** diffResults and compareResults: Value inserted into first column for deleted rows */
	public static final String DELETION = "-";
	/** compareResults: Value inserted into first column for changed rows */
	public static final String CHANGE = "*";
	
	private final int n;
	private final RowSetImpl rs;
	private final ResultVisitor v;
	
	/**
	 * Constructor
	 * @param r RowSet
	 * @param v Visitor for processing rows
	 */
	public DiffCompareHandler(RowSet r, ResultVisitor v)
		{
		final List<ColumnDef> effectiveColumns = r.getColumns();
		this.n = effectiveColumns.size() + 1;
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(this.n);
		columns.add(new ColumnDefImpl("Diff", ColumnType.STRING, null, null, null, null));
		columns.addAll(effectiveColumns);
		
		this.rs = new RowSetImpl(r.getQuery(), r.getSubQueryIndex(), columns);
		this.rs.setMoreAvailable(r.isMoreAvailable());
		this.rs.setQueryTime(r.getQueryTime());
		this.rs.getAttributes().put(RowSetConstants.ATTR_DIFF, Boolean.TRUE);
		
		this.v = v;
		
		this.v.startRowSet(rs);
		}
	
	/**
	 * Constructor
	 * @param r RowSet
	 */
	public DiffCompareHandler(RowSet r)
		{
		this(r, new ResultVisitorAdapter());
		}
	
	/**
	 * Get the created RowSet
	 * @return RowSet
	 */
	public RowSet getRowSet()
		{
		return (rs);
		}
	
	@Override
	public void rowAdded(String tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
		{
		final ResultRow row = new DefaultResultRow(n);
		row.getValues().add(ADDITION);
		row.getValues().addAll(values.getValues());
		v.startRow(row, 0);
		rs.getRows().add(row);
		}
	
	@Override
	public boolean rowChanged(String tableName, List<ColumnDef> columns, ResultRow oldValues, ResultRow newValues, Set<Integer> pk)
		{
		final ResultRow row = new DefaultResultRow(n);
		row.getValues().add(CHANGE);
		row.getValues().addAll(newValues.getValues());
		v.startRow(row, 0);
		rs.getRows().add(row);
		return (true);
		}
	
	@Override
	public void rowRemoved(String tableName, List<ColumnDef> columns, ResultRow values, Set<Integer> pk)
		{
		final ResultRow row = new DefaultResultRow(n);
		row.getValues().add(DELETION);
		row.getValues().addAll(values.getValues());
		v.startRow(row, 0);
		rs.getRows().add(row);
		}
	
	@Override
	public void flush()
		{
		v.endRowSet(rs);
		}
	}
