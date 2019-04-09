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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.web.constant.RowSetConstants;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.service.ResultTransformerService;
import de.tweerlei.dbgrazer.web.service.TextTransformerService;

/**
 * Transform query results
 *
 * @author Robert Wruck
 */
@Service
public class ResultTransformerServiceImpl implements ResultTransformerService
	{
	/** Required number of columns for maps */
	private static final int MAP_MIN_COLUMNS = 2;
	/** Index of the column containing the key */
	private static final int MAP_KEY_INDEX = 0;
	/** Index of the column containing the value */
	private static final int MAP_VALUE_INDEX = 1;
	
	private final TextTransformerService textFormatterService;
	
	/**
	 * Constructor
	 * @param textFormatterService TextFormatterService
	 */
	@Autowired
	public ResultTransformerServiceImpl(TextTransformerService textFormatterService)
		{
		this.textFormatterService = textFormatterService;
		}
	
	@Override
	public void translateRowSet(RowSet rs, DataFormatter fmt)
		{
		translateRowSet(rs, fmt, false, false, false);
		}
	
	@Override
	public void translateRowSet(RowSet rs, DataFormatter fmt, boolean trim, boolean depth, boolean sum)
		{
		final TranslateVisitor v = new TranslateVisitor(fmt, trim, sum);
		rs.accept(v, 0);
		
		if (depth)
			rs.getAttributes().put(RowSetConstants.ATTR_DEPTH, v.getDepth());
		if (sum)
			rs.getAttributes().put(RowSetConstants.ATTR_SUM_VALUES, v.getSumRow());
		}
	
	@Override
	public void formatRowSet(RowSet rs, DataFormatter fmt, String format, Set<TextTransformerService.Option> options)
		{
		if (rs.getRows().isEmpty())
			return;
		
		final String value = convertToString(rs, fmt);
		
		final Set<TextTransformerService.Option> opts = EnumSet.copyOf(options);
		
		final String attrFormatting = getRowSetOrQueryAttribute(rs, RowSetConstants.ATTR_FORMATTING);
		if (attrFormatting != null && Boolean.parseBoolean(attrFormatting))
			opts.add(TextTransformerService.Option.FORMATTING);
		final String attrSyntaxColoring = getRowSetOrQueryAttribute(rs, RowSetConstants.ATTR_SYNTAX_COLORING);
		if (attrSyntaxColoring != null && Boolean.parseBoolean(attrSyntaxColoring))
			opts.add(TextTransformerService.Option.SYNTAX_COLORING);
		final String attrLineNumbers = getRowSetOrQueryAttribute(rs, RowSetConstants.ATTR_LINE_NUMBERS);
		if (attrLineNumbers != null && Boolean.parseBoolean(attrLineNumbers))
			opts.add(TextTransformerService.Option.LINE_NUMBERS);
		
		final String attrFormatter = getRowSetOrQueryAttribute(rs, RowSetConstants.ATTR_FORMATTER);
		final String formatter;
		if (attrFormatter != null)
			formatter = attrFormatter.toString();
		else
			formatter = format;
		
		final String result = textFormatterService.format(value, formatter, opts);
		
		rs.getRows().clear();
		rs.getRows().add(new DefaultResultRow(result));
		}
	
	private String getRowSetOrQueryAttribute(RowSet rs, String key)
		{
		final String queryValue = rs.getQuery().getAttributes().get(key);
		if (queryValue != null)
			return (queryValue);
		
		final Object value = rs.getAttributes().get(key);
		if (value != null)
			return (value.toString());
		
		return (null);
		}
	
	@Override
	public void addRowsWithPrefix(Result r, Result newRows, String prefixName, ColumnType prefixType, Object prefix, int maxRows)
		{
		for (Map.Entry<String, RowSet> ent : newRows.getRowSets().entrySet())
			{
			final RowSet rsNew = ent.getValue();
			if (rsNew.getColumns().isEmpty())
				continue;
			
			RowSet rs = r.getRowSets().get(ent.getKey());
			if (rs == null)
				{
				final List<ColumnDef> columns = new ArrayList<ColumnDef>(rsNew.getColumns());
				columns.set(0, new ColumnDefImpl(prefixName, prefixType, null, null, null, null));
				rs = new RowSetImpl(rsNew.getQuery(), rsNew.getSubQueryIndex(), columns);
				r.getRowSets().put(ent.getKey(), rs);
				}
			
			if (rs.getColumns().size() != rsNew.getColumns().size())
				continue;
			
			for (ResultRow l : rsNew.getRows())
				{
				final ResultRow row = l.clone();
				row.getValues().set(0, prefix);
				rs.getRows().add(row);
				}
			
			for (int n = rs.getRows().size(); n > maxRows; n--)
				rs.getRows().remove(0);
			}
		}
	
	@Override
	public Map<String, String> convertToMap(RowSet rs, DataFormatter fmt)
		{
		if (rs.getColumns().isEmpty())
			return (null);
		
		final int valueIndex = (rs.getColumns().size() < MAP_MIN_COLUMNS) ? MAP_KEY_INDEX : MAP_VALUE_INDEX;
		
		final Map<String, String> map = new LinkedHashMap<String, String>();
		for (ResultRow row : rs.getRows())
			{
			final Object key = row.getValues().get(MAP_KEY_INDEX);
			final Object value = row.getValues().get(valueIndex);
			if ((key != null) && (value != null))
				{
				map.put(
						fmt.format(rs.getColumns().get(MAP_KEY_INDEX).getType(), key),
						fmt.format(rs.getColumns().get(valueIndex).getType(), value)
						);
				}
			}
		
		return (map);
		}
	
	@Override
	public String convertToString(RowSet rs, DataFormatter fmt)
		{
		final StringBuilder sb = new StringBuilder();
		
		if (!rs.getColumns().isEmpty())
			{
			final ColumnType type = rs.getColumns().get(0).getType();
			
			for (Object o : rs.getFirstColumn())
				sb.append(fmt.format(type, o));
			}
		
		return (sb.toString());
		}
	}
