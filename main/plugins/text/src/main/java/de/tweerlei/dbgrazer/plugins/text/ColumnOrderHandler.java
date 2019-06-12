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
package de.tweerlei.dbgrazer.plugins.text;

import java.util.List;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;

/**
 * RowHandler that re-orders columns
 * 
 * @author Robert Wruck
 */
public class ColumnOrderHandler implements RowHandler
	{
	private static class ColumnMapping
		{
		public final String name;
		public final int sourceColumn;
		public final String value;
		
		public ColumnMapping(String name, int sourceColumn, String value)
			{
			this.name = name;
			this.sourceColumn = sourceColumn;
			this.value = value;
			}
		}
	
	private final String recipe;
	private ColumnMapping[] columnMapping;
	
	/**
	 * Constructor
	 * @param recipe Transformation recipe
	 */
	public ColumnOrderHandler(String recipe)
		{
		this.recipe = recipe;
		}
	
	@Override
	public void startRows(List<ColumnDef> columns)
		{
		columnMapping = parseRecipe(recipe, columns);
		
		final ColumnDef[] defs = columns.toArray(new ColumnDef[columns.size()]);
		columns.clear();
		for (ColumnMapping m : columnMapping)
			{
			if (m.value != null)
				columns.add(new ColumnDefImpl(m.name, ColumnType.STRING, null, null, null, null));
			else
				{
				final ColumnDef def = defs[m.sourceColumn];
				columns.add(new ColumnDefImpl(m.name, def.getType(), def.getTypeName(), def.getTargetQuery(), def.getSourceObject(), def.getSourceColumn()));
				}
			}
		}
	
	private static ColumnMapping[] parseRecipe(String recipe, List<ColumnDef> columns)
		{
		final String[] cols = recipe.split("\\s+");
		final ColumnMapping[] ret = new ColumnMapping[cols.length];
		
		for (int i = 0; i < cols.length; i++)
			{
			final String[] fields1 = cols[i].split("!", 2);
			if (fields1.length == 2)
				{
				ret[i] = new ColumnMapping(fields1[0], 0, fields1[1]);
				continue;
				}
			
			final String[] fields2 = cols[i].split("#", 2);
			final String expr;
			if (fields2.length == 2)
				expr = fields2[1];
			else
				expr = fields2[0];
			
			int index = i;
			try	{
				final int n = Integer.parseInt(expr);
				if ((n >= 0) && (n < columns.size()))
					index = n;
				}
			catch (NumberFormatException e)
				{
				int n = 0;
				for (ColumnDef c : columns)
					{
					if (c.getName().equalsIgnoreCase(expr))
						{
						index = n;
						break;
						}
					n++;
					}
				}
			
			final String name;
			if (fields2.length == 2)
				name = fields2[0];
			else
				name = columns.get(index).getName();
			
			ret[i] = new ColumnMapping(name, index, null);
			}
		
		return (ret);
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		final List<Object> l = row.getValues();
		final Object[] values = l.toArray();
		l.clear();
		
		for (ColumnMapping m : columnMapping)
			{
			if (m.value != null)
				l.add(m.value);
			else
				l.add(values[m.sourceColumn]);
			}
		
		return (true);
		}
	
	@Override
	public void endRows()
		{
		}
	
	@Override
	public void error(RuntimeException e)
		{
		}
	}
