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
package de.tweerlei.dbgrazer.plugins.jdbc.transformer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowContainer;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ResultVisitorAdapter;
import de.tweerlei.dbgrazer.query.model.impl.TreeResultRow;

/**
 * Transforms a RowSet to a tree structure
 * Tree levels are generated from matching column contents starting with the first column
 * until there are no two matching columns on the same level.
 * 
 * @author Robert Wruck
 */
public class ColumnTreeTransformer extends ResultVisitorAdapter
	{
	private static final class TreeLevelBuilder
		{
		private final int level;
		private List<ColumnDef> columns;
		private Map<Object, List<ResultRow>> entries;
		private int rows;
		
		public TreeLevelBuilder(int level, List<ColumnDef> columns)
			{
			this.level = level;
			this.columns = new ArrayList<ColumnDef>(columns);
			this.entries = new LinkedHashMap<Object, List<ResultRow>>();
			this.rows = 0;
			}
		
		public void addRow(ResultRow row)
			{
			final Object key = row.getValues().get(level);
			List<ResultRow> l = entries.get(key);
			if (l == null)
				{
				l = new LinkedList<ResultRow>();
				entries.put(key, l);
				}
			l.add(row);
			rows++;
			}
		
		public boolean hasEntries()
			{
			return (entries.size() < rows);
			}
		
		public void build(RowContainer<ResultRow> rs)
			{
			if (entries.isEmpty())
				return;
			
			if ((entries.size() == rows) || (level + 1 == columns.size()))
				fillLeaves(rs);
			else
				buildSubtree(rs);
			}
		
		// Create rows of the form (ID, LevelName) and apply TreeLevelBuilders to the child ResultRows
		private void buildSubtree(RowContainer<ResultRow> rs)
			{
			rs.getColumns().clear();
			rs.getColumns().add(new ColumnDefImpl("ID", ColumnType.INTEGER, null, null, null, null));
			rs.getColumns().add(columns.get(level));
			
			rs.getRows().clear();
			int id = 1;
			for (Map.Entry<Object, List<ResultRow>> ent : entries.entrySet())
				{
				final TreeLevelBuilder builder = new TreeLevelBuilder(level + 1, columns);
				for (ResultRow r : ent.getValue())
					builder.addRow(r);
				
				final TreeResultRow row = new TreeResultRow(2, new ArrayList<ColumnDef>(2));
				row.getValues().add(id);
				row.getValues().add(ent.getKey());
				
				builder.build(row);
				rs.getRows().add(row);
				id++;
				}
			}
		
		// Create rows containing the remaining columns with a leading ID column
		private void fillLeaves(RowContainer<ResultRow> rs)
			{
			final int n = columns.size();
			
			rs.getColumns().clear();
			rs.getColumns().add(new ColumnDefImpl("ID", ColumnType.INTEGER, null, null, null, null));
			for (int i = level; i < n; i++)
				rs.getColumns().add(columns.get(i));
			
			rs.getRows().clear();
			int id = 1;
			for (Map.Entry<Object, List<ResultRow>> ent : entries.entrySet())
				{
				for (ResultRow r : ent.getValue())
					{
					final TreeResultRow row = new TreeResultRow(n - level + 1, null);
					row.getValues().add(id);
					for (int i = level; i < n; i++)
						row.getValues().add(r.getValues().get(i));
					rs.getRows().add(row);
					id++;
					}
				}
			}
		}
	
	private TreeLevelBuilder root;
	
	@Override
	public boolean startRowSet(RowSet rs)
		{
		final int n = rs.getColumns().size();
		
		if (n < 2)
			return (false);
		
		root = new TreeLevelBuilder(0, rs.getColumns());
		return (true);
		}
	
	@Override
	public boolean startRow(ResultRow row, int level)
		{
		root.addRow(row);
		
		// we only handle top level rows
		return (false);
		}
	
	@Override
	public void endRowSet(RowSet rs)
		{
		if (root.hasEntries())
			root.build(rs);
		}
	}
