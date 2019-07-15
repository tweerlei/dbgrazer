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
package de.tweerlei.dbgrazer.plugins.jdbc.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.plugins.jdbc.impl.ResultSetAccessor;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * RowCallbackHandler that maps rows to a single RowSet per column
 * 
 * @author Robert Wruck
 */
public class ColumnSplitMapper extends RowSetMapper
	{
	private final Query query;
	private final Map<String, RowSetImpl> rowSets;
	private int subQueryIndex;
	private boolean first;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 * @param timeZone TimeZone to use for temporal results
	 * @param query Query
	 * @param subQueryIndex Base subquery index
	 */
	public ColumnSplitMapper(SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone, Query query, int subQueryIndex)
		{
		super(sqlGenerator, dialect, timeZone);
		this.query = query;
		this.rowSets = new LinkedHashMap<String, RowSetImpl>();
		this.subQueryIndex = subQueryIndex;
		this.first = true;
		}
	
	@Override
	public Map<String, RowSetImpl> getRowSets()
		{
		return (rowSets);
		}
	
	@Override
	public void processRow(ResultSet rs, ResultSetAccessor accessor) throws SQLException
		{
		if (first)
			{
			first = false;
			final int c = accessor.getColumnCount(rs);
			if (c >= 2)
				{
				final ColumnDef firstColumn = accessor.getColumnDef(rs, 1, query.getTargetQueries().get(0));
				for (int i = 2; i <= c; i++)
					{
					final ColumnDef currentColumn = accessor.getColumnDef(rs, i, query.getTargetQueries().get(i - 1));
					final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
					columns.add(firstColumn);
					columns.add(currentColumn);
					final RowSetImpl rowSet = new RowSetImpl(query, subQueryIndex++, columns);
					rowSets.put(currentColumn.getName(), rowSet);
					}
				}
			}
		
		final Object key = accessor.getObject(rs, 1);
		int i = 2;
		for (RowSetImpl rowSet : rowSets.values())
			{
			final ResultRow row = new DefaultResultRow(key, accessor.getObject(rs, i++));
			rowSet.getRows().add(row);
			}
		}
	}
