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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultVisitorAdapter;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
public class TranslateVisitor extends ResultVisitorAdapter
	{
	private static final class IntSum extends Number
		{
		private long value;
		
		public IntSum()
			{
			value = 0;
			}
		
		public void add(long increment)
			{
			value += increment;
			}
		
		@Override
		public double doubleValue()
			{
			return (value);
			}
	
		@Override
		public float floatValue()
			{
			return (value);
			}
		
		@Override
		public int intValue()
			{
			return ((int) value);
			}
		
		@Override
		public long longValue()
			{
			return (value);
			}
		
		@Override
		public String toString()
			{
			return (String.valueOf(value));
			}
		}
	
	private static final class FloatSum extends Number
		{
		private double value;
		
		public FloatSum()
			{
			value = 0.0;
			}
		
		public void add(double increment)
			{
			value += increment;
			}
		
		@Override
		public double doubleValue()
			{
			return (value);
			}
	
		@Override
		public float floatValue()
			{
			return ((float) value);
			}
		
		@Override
		public int intValue()
			{
			return ((int) Math.round(value));
			}
		
		@Override
		public long longValue()
			{
			return (Math.round(value));
			}
		
		@Override
		public String toString()
			{
			return (String.valueOf(value));
			}
		}
	
	private final DataFormatter fmt;
	private final boolean trim;
	private final boolean sum;
	private List<ColumnDef> columns;
	private boolean[] usedCols;
	private int depth;
	private ResultRow sumRow;
	
	/**
	 * Constructor
	 * @param fmt DataFormatter
	 * @param trim Trim empty columns
	 * @param sum Calculate sum
	 */
	public TranslateVisitor(DataFormatter fmt, boolean trim, boolean sum)
		{
		this.fmt = fmt;
		this.trim = trim;
		this.sum = sum;
		}
	
	/**
	 * Get the maximum depth
	 * @return Depth
	 */
	public int getDepth()
		{
		return (depth);
		}
	
	/**
	 * Get the calculated sum row
	 * @return Sum row
	 */
	public ResultRow getSumRow()
		{
		return (sumRow);
		}
	
	@Override
	public boolean startRowSet(RowSet rs)
		{
		usedCols = new boolean[rs.getColumns().size()];
		columns = rs.getColumns();
		depth = 0;
		if (sum)
			{
			sumRow = new DefaultResultRow(rs.getColumns().size());
			for (ColumnDef c : rs.getColumns())
				{
				if (c.getType() == ColumnType.FLOAT)
					sumRow.getValues().add(new FloatSum());
				else
					sumRow.getValues().add(new IntSum());
				}
			}
		return (true);
		}
	
	@Override
	public boolean startRow(ResultRow row, int level)
		{
		if (level > depth)
			depth = level;
		
		translateRow(row.getValues(), sum && (level == 0));
		
		return (true);
		}
	
	@Override
	public void endRowSet(RowSet rs)
		{
		if (trim)
			trimColumns(rs);
		if (sum)
			translateSumRow(sumRow.getValues());
		}
	
	private void translateRow(List<Object> row, boolean addToSum)
		{
		int i = 0;
		for (ColumnDef c : columns)
			{
			final Object value = row.get(i);
			if (value != null)
				{
				usedCols[i] = true;
				if (addToSum)
					{
					switch (c.getType())
						{
						case FLOAT:
							((FloatSum) sumRow.getValues().get(i)).add(((Number) value).doubleValue());
							break;
						case INTEGER:
							((IntSum) sumRow.getValues().get(i)).add(((Number) value).longValue());
							break;
						default:
							((IntSum) sumRow.getValues().get(i)).add(1);
							break;
						}
					}
				}
			row.set(i, fmt.format(c.getType(), value));
			i++;
			}
		}
	
	private void translateSumRow(List<Object> row)
		{
		int i = 0;
		for (ColumnDef c : columns)
			{
			final Object value = row.get(i);
			if (c.getType() == ColumnType.FLOAT)
				row.set(i, fmt.format(ColumnType.FLOAT, value));
			else
				row.set(i, fmt.format(ColumnType.INTEGER, value));
			i++;
			}
		}
	
	private void trimColumns(RowSet rs)
		{
		int keepCols = usedCols.length;
		int i = 0;
		for (Iterator<ColumnDef> it = rs.getColumns().iterator(); it.hasNext(); )
			{
			it.next();
			// Keep at least a single column
			if ((keepCols > 1) && !usedCols[i])
				{
				it.remove();
				keepCols--;
				}
			i++;
			}
		
		if (keepCols < usedCols.length)
			{
			for (ListIterator<ResultRow> it = rs.getRows().listIterator(); it.hasNext(); )
				{
				final ResultRow oldRow = it.next();
				final ResultRow newRow = new DefaultResultRow(keepCols);
				for (i = 0; i < usedCols.length; i++)
					{
					if (usedCols[i])
						newRow.getValues().add(oldRow.getValues().get(i));
					}
				it.set(newRow);
				}
			}
		
		rs.getAttributes().put(RowSetConstants.ATTR_TRIM, Boolean.TRUE);
		}
	}
