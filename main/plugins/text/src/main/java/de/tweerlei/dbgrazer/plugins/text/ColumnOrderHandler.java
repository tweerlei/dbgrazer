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
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;

/**
 * RowHandler that re-orders columns
 * 
 * @author Robert Wruck
 */
public class ColumnOrderHandler implements RowHandler
	{
	private final String recipe;
	private int[] columnMapping;
	
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
		columnMapping = new int[columns.size()];
		
		final String[] cols = recipe.split("\\s*,\\s*");
		for (int i = 0; i < columnMapping.length; i++)
			{
			columnMapping[i] = i;
			if (i >= cols.length)
				continue;
			
			try	{
				final int n = Integer.parseInt(cols[i]);
				if ((n >= 0) && (n < columnMapping.length))
					columnMapping[i] = n;
				}
			catch (NumberFormatException e)
				{
				int n = 0;
				for (ColumnDef c : columns)
					{
					if (c.getName().equalsIgnoreCase(cols[i]))
						{
						columnMapping[i] = n;
						break;
						}
					n++;
					}
				}
			}
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		final List<Object> l = row.getValues();
		final Object[] values = row.getValues().toArray();
		
		for (int i = 0; i < columnMapping.length; i++)
			l.set(i, values[columnMapping[i]]);
		
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
