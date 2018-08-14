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
package de.tweerlei.dbgrazer.query.service.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.model.impl.StatementWriter;
import de.tweerlei.dbgrazer.query.service.ResultBuilderService;

/**
 * Service for constructing Result objects
 * 
 * @author Robert Wruck
 */
@Service
public class ResultBuilderServiceImpl implements ResultBuilderService
	{
	@Override
	public RowSetImpl createEmptyRowSet(Query query, int subQueryIndex, long time)
		{
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, Collections.<ColumnDef>emptyList());
		rs.setQueryTime(time);
		
		return (rs);
		}
	
	@Override
	public RowSetImpl createSingletonRowSet(Query query, int subQueryIndex, String columnName, Object value, long time)
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(1);
		columns.add(new ColumnDefImpl(columnName, ColumnType.forObject(value), null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		rs.setQueryTime(time);
		rs.getRows().add(new DefaultResultRow(value));
		
		return (rs);
		}
	
	@Override
	public ResultImpl createSingletonResult(Query query, int subQueryIndex, String columnName, Object value, long time)
		{
		final ResultImpl res = new ResultImpl(query);
		res.getRowSets().put(query.getName(), createSingletonRowSet(query, subQueryIndex, columnName, value, time));
		return (res);
		}
	
	@Override
	public <T> RowSetImpl createRowSet(Query query, int subQueryIndex, String columnName, Collection<T> values, long time)
		{
		if (values.isEmpty())
			return (createEmptyRowSet(query, subQueryIndex, time));
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(1);
		columns.add(new ColumnDefImpl(columnName, ColumnType.forObject(values.iterator().next()), null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		rs.setQueryTime(time);
		
		for (Object value : values)
			rs.getRows().add(new DefaultResultRow(value));
		
		return (rs);
		}
	
	@Override
	public <K, V> RowSetImpl createMapRowSet(Query query, int subQueryIndex, String keyColumnName, String valueColumnName, Map<K, V> values, long time)
		{
		if (values.isEmpty())
			return (createEmptyRowSet(query, subQueryIndex, time));
		
		final Set<Map.Entry<K, V>> entries = values.entrySet();
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(2);
		columns.add(new ColumnDefImpl(keyColumnName, ColumnType.forObject(entries.iterator().next().getKey()), null, null, null, null));
		columns.add(new ColumnDefImpl(valueColumnName, ColumnType.forObject(entries.iterator().next().getValue()), null, null, null, null));
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		rs.setQueryTime(time);
		
		for (Map.Entry<K, V> entry : entries)
			rs.getRows().add(new DefaultResultRow(entry.getKey(), entry.getValue()));
		
		return (rs);
		}
	
	@Override
	public RowSetImpl createMapRowSet(Query query, int subQueryIndex, Map<String, Object> values, long time)
		{
		if (values.isEmpty())
			return (createEmptyRowSet(query, subQueryIndex, time));
		
		final List<ColumnDef> columns = new ArrayList<ColumnDef>(values.size());
		final DefaultResultRow row = new DefaultResultRow(values.size());
		
		for (Map.Entry<String, Object> ent : values.entrySet())
			{
			columns.add(new ColumnDefImpl(ent.getKey(), ColumnType.forObject(ent.getValue()), null, null, null, null));
			row.getValues().add(ent.getValue());
			}
		
		final RowSetImpl rs = new RowSetImpl(query, subQueryIndex, columns);
		rs.setQueryTime(time);
		rs.getRows().add(row);
		
		return (rs);
		}
	
	@Override
	public String writeScript(StatementProducer producer, String header, String separator)
		{
		final StringWriter sw = new StringWriter();
		final StatementHandler h = new StatementWriter(sw, separator);
		if (!StringUtils.empty(header))
			h.comment(header);
		producer.produceStatements(h);
		
		return (sw.toString());
		}
	}
