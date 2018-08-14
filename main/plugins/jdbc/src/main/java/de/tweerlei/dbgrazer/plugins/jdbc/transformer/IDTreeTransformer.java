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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ResultVisitorAdapter;
import de.tweerlei.dbgrazer.query.model.impl.TreeResultRow;

/**
 * Transforms a RowSet to a tree structure
 * 1st column is interpreted as row ID, 2nd column as parent row ID.
 * 
 * @author Robert Wruck
 */
public class IDTreeTransformer extends ResultVisitorAdapter
	{
	private static final class RowWithParent
		{
		public final TreeResultRow row;
		public final Object parentId;
		
		public RowWithParent(TreeResultRow row, Object parentId)
			{
			this.row = row;
			this.parentId = parentId;
			}
		}
	
	private List<ColumnDef> columns;
	private Map<Object, RowWithParent> idMap;
	
	@Override
	public boolean startRowSet(RowSet rs)
		{
		final int n = rs.getColumns().size();
		
		if (n < 2)
			return (false);
		
		columns = rs.getColumns();
		columns.remove(1);	// Parent ID is only used for constructing the tree
		
		idMap = new HashMap<Object, RowWithParent>();
		
		return (true);
		}
	
	@Override
	public boolean startRow(ResultRow row, int level)
		{
		final List<Object> values = row.getValues();
		
		final int n = values.size();
		final Object rowId = values.get(0);
		final Object parentId = values.get(1);
		
		final TreeResultRow trow = new TreeResultRow(n - 1, columns);
		final List<Object> tvalues = trow.getValues();
		tvalues.add(rowId);
		for (int i = 2; i < n; i++)	// Skip parent ID
			tvalues.add(values.get(i));
		
		idMap.put(rowId, new RowWithParent(trow, parentId));
		
		// we only handle top level rows
		return (false);
		}
	
	@Override
	public void endRowSet(RowSet rs)
		{
		rs.getRows().clear();
		
		for (RowWithParent child : idMap.values())
			{
			final RowWithParent parent = idMap.get(child.parentId);
			// Rows without parent or with parent == self are added as root rows.
			// Cyclic rows won't be added this way.
			if ((parent == null) || (parent == child))
				rs.getRows().add(child.row);
			else
				parent.row.getRows().add(child.row);
			}
		}
	}
