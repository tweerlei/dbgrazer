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
 * RowCallbackHandler that maps rows to RowSets named by the first column's value
 * 
 * @author Robert Wruck
 */
public class GroupingRowSetMapper extends RowSetMapper
	{
	private final Query query;
	private final Map<String, RowSetImpl> rowSets;
	private int subQueryIndex;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 * @param timeZone TimeZone to use for temporal results
	 * @param query Query
	 * @param subQueryIndex Base subquery index
	 */
	public GroupingRowSetMapper(SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone, Query query, int subQueryIndex)
		{
		super(sqlGenerator, dialect, timeZone);
		this.query = query;
		this.rowSets = new LinkedHashMap<String, RowSetImpl>();
		this.subQueryIndex = subQueryIndex;
		}
	
	@Override
	public Map<String, RowSetImpl> getRowSets()
		{
		return (rowSets);
		}
	
	@Override
	public void processRow(ResultSet rs, ResultSetAccessor accessor) throws SQLException
		{
		final String key = String.valueOf(accessor.getObject(rs, 1));
		RowSetImpl rowSet = rowSets.get(key);
		if (rowSet == null)
			{
			final int c = accessor.getColumnCount(rs);
			final List<ColumnDef> columns = new ArrayList<ColumnDef>(c - 1);
			for (int i = 2; i <= c; i++)
				columns.add(accessor.getColumnDef(rs, i, query.getTargetQueries().get(i - 1)));
			
			rowSet = new RowSetImpl(query, subQueryIndex++, columns);
			rowSets.put(key, rowSet);
			}
		
		final int c = rowSet.getColumns().size();
		final ResultRow row = new DefaultResultRow(c);
		for (int i = 1; i <= c; i++)
			row.getValues().add(accessor.getObject(rs, i + 1));
		
		rowSet.getRows().add(row);
		}
	}
