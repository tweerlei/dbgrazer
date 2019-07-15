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
 * RowCallbackHandler that maps rows to a single RowSet
 * 
 * @author Robert Wruck
 */
public class SingleRowSetMapper extends RowSetMapper
	{
	private final Query query;
	private final int subQueryIndex;
	private final Map<String, RowSetImpl> rowSets;
	
	/**
	 * Constructor
	 * @param sqlGenerator SQLGeneratorService
	 * @param dialect SQLDialect
	 * @param timeZone TimeZone to use for temporal results
	 * @param query Query
	 * @param subQueryIndex Subquery index
	 */
	public SingleRowSetMapper(SQLGeneratorService sqlGenerator, SQLDialect dialect, TimeZone timeZone, Query query, int subQueryIndex)
		{
		super(sqlGenerator, dialect, timeZone);
		this.query = query;
		this.subQueryIndex = subQueryIndex;
		this.rowSets = new LinkedHashMap<String, RowSetImpl>();
		}
	
	@Override
	public Map<String, RowSetImpl> getRowSets()
		{
		return (rowSets);
		}
	
	@Override
	public void processRow(ResultSet rs, ResultSetAccessor accessor) throws SQLException
		{
		RowSetImpl rowSet = rowSets.get(query.getName());
		if (rowSets.isEmpty())
			{
			final int c = accessor.getColumnCount(rs);
			final List<ColumnDef> columns = new ArrayList<ColumnDef>(c);
			for (int i = 1; i <= c; i++)
				columns.add(accessor.getColumnDef(rs, i, query.getTargetQueries().get(i - 1)));
			
			rowSet = new RowSetImpl(query, subQueryIndex, columns);
			rowSets.put(query.getName(), rowSet);
			}
		
		final int c = rowSet.getColumns().size();
		final ResultRow row = new DefaultResultRow(c);
		for (int i = 1; i <= c; i++)
			row.getValues().add(accessor.getObject(rs, i));
		
		rowSet.getRows().add(row);
		}
	}
