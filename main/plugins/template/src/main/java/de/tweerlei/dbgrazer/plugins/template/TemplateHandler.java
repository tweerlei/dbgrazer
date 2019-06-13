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
package de.tweerlei.dbgrazer.plugins.template;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
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
public class TemplateHandler implements RowHandler
	{
	private static class ColumnMapping
		{
		public final String name;
		public final int sourceColumn;
		public final ColumnType type;
		public final ValueExpression expression;
		
		public ColumnMapping(String name, int sourceColumn, ColumnType type, ValueExpression expression)
			{
			this.name = name;
			this.sourceColumn = sourceColumn;
			this.type = type;
			this.expression = expression;
			}
		}
	
	private final String recipe;
	private ExpressionFactory el;
	private ELContext ctx;
	private ValueExpression valuesExpr;
	private ColumnDef[] sourceColumns;
	private ColumnMapping[] columnMapping;
	
	/**
	 * Constructor
	 * @param recipe Transformation recipe
	 */
	public TemplateHandler(String recipe)
		{
		this.recipe = recipe;
		}
	
	@Override
	public void startRows(List<ColumnDef> columns)
		{
		el = new ExpressionFactoryImpl();
		ctx = createContext();
		valuesExpr = el.createValueExpression(ctx, "${values}", Object.class);
		
		columnMapping = parseRecipe(recipe, columns, el, ctx);
		
		sourceColumns = columns.toArray(new ColumnDef[columns.size()]);
		columns.clear();
		for (ColumnMapping m : columnMapping)
			{
			if (m.type != null)
				columns.add(new ColumnDefImpl(m.name, m.type, null, null, null, null));
			else
				{
				final ColumnDef def = sourceColumns[m.sourceColumn];
				columns.add(new ColumnDefImpl(m.name, def.getType(), def.getTypeName(), def.getTargetQuery(), def.getSourceObject(), def.getSourceColumn()));
				}
			}
		}
	
	private ELContext createContext()
		{
		final SimpleContext ret = new SimpleContext();
//		ret.setFunction("fn", "format", ELFunctions.class.getMethod("format", String.class, Object.class));
		ret.setVariable("fn", el.createValueExpression(new ELFunctions(), Object.class));
		return (ret);
		}
	
	private static ColumnMapping[] parseRecipe(String recipe, List<ColumnDef> columns, ExpressionFactory factory, ELContext context)
		{
		final String[] cols = recipe.split("\\s+");
		final ColumnMapping[] ret = new ColumnMapping[cols.length];
		
		for (int i = 0; i < cols.length; i++)
			{
			final String[] fields1 = cols[i].split("!", 3);
			if (fields1.length == 3)
				{
				try	{
					final ColumnType t = ColumnType.valueOf(fields1[1]);
					final Class<?> resultClass;
					switch (t)
						{
						case BOOLEAN:
							resultClass = Boolean.class;
							break;
						case DATE:
							resultClass = Date.class;
							break;
						case FLOAT:
							resultClass = Double.class;
							break;
						case INTEGER:
							resultClass = Long.class;
							break;
						default:
							resultClass = String.class;
							break;
						}
					final ValueExpression expr = factory.createValueExpression(context, fields1[2], resultClass);
					ret[i] = new ColumnMapping(fields1[0], 0, t, expr);
					}
				catch (IllegalArgumentException e)
					{
					final ValueExpression expr = factory.createValueExpression(context, fields1[2], String.class);
					ret[i] = new ColumnMapping(fields1[0], 0, ColumnType.STRING, expr);
					}
				continue;
				}
			if (fields1.length == 2)
				{
				final ValueExpression expr = factory.createValueExpression(context, fields1[1], String.class);
				ret[i] = new ColumnMapping(fields1[0], 0, ColumnType.STRING, expr);
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
			
			ret[i] = new ColumnMapping(name, index, null, null);
			}
		
		return (ret);
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		final List<Object> l = row.getValues();
		final Map<Object, Object> values = new HashMap<Object, Object>(2 * l.size());
		for (int i = 0; i < sourceColumns.length; i++)
			{
			final Object value = l.get(i);
			values.put(Long.valueOf(i), value);
			values.put(sourceColumns[i].getName(), value);
			}
		l.clear();
		
		valuesExpr.setValue(ctx, values);
		for (ColumnMapping m : columnMapping)
			{
			if (m.type != null)
				l.add(m.expression.getValue(ctx));
			else
				l.add(values.get(Long.valueOf(m.sourceColumn)));
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
