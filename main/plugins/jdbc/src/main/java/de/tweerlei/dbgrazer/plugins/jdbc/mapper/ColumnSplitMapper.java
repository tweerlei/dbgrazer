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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
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
	private final int limit;
	private final Map<String, RowSetImpl> rowSets;
	private int subQueryIndex;
	private boolean first;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 * @param query Query
	 * @param subQueryIndex Base subquery index
	 * @param limit Fetch limit
	 */
	public ColumnSplitMapper(SQLGeneratorService sqlGenerator, SQLDialect dialect, Query query, int subQueryIndex, int limit)
		{
		super(sqlGenerator, dialect);
		this.query = query;
		this.limit = limit;
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
	public void processRow(ResultSet rs) throws SQLException
		{
		if (first)
			{
			first = false;
			final ResultSetMetaData rsmd = rs.getMetaData();
			final int c = rsmd.getColumnCount();
			for (int i = 2; i <= c; i++)
				{
				final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
				columns.add(getColumnDef(rsmd, 1, query.getTargetQueries().get(0)));
				columns.add(getColumnDef(rsmd, i, query.getTargetQueries().get(i - 1)));
				final RowSetImpl rowSet = new RowSetImpl(query, subQueryIndex++, columns);
				rowSets.put(rsmd.getColumnLabel(i), rowSet);
				}
			}
		
		final Object key = rs.getObject(1);
		int i = 2;
		for (RowSetImpl rowSet : rowSets.values())
			{
			if (rowSet.getRows().size() < limit)
				{
				final ResultRow row = new DefaultResultRow(
						key,
						rs.getObject(i++));
				rowSet.getRows().add(row);
				}
			else
				rowSet.setMoreAvailable(true);
			}
		}
	}
