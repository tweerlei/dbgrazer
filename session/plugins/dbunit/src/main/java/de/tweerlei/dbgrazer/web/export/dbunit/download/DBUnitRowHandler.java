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
package de.tweerlei.dbgrazer.web.export.dbunit.download;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Write DBUnit XML tags for ResultRows
 * 
 * @author Robert Wruck
 */
public class DBUnitRowHandler implements RowHandler
	{
	private final XMLWriter writer;
	private final String tableName;
	private final DataFormatter fmt;
	private List<ColumnDef> cols;
	private Map<String, String> attrs;
	private int count;
	
	/**
	 * Constructor
	 * @param w XMLWriter
	 * @param tableName Table (i.e. XML tag) name
	 * @param fmt DataFormatter
	 */
	public DBUnitRowHandler(XMLWriter w, String tableName, DataFormatter fmt)
		{
		this.writer = w;
		this.tableName = tableName;
		this.fmt = fmt;
		this.attrs = null;
		this.count = 0;
		}
	
	/**
	 * Get the number of rows handled
	 * @return Count
	 */
	public int getCount()
		{
		return (count);
		}
	
	@Override
	public void startRows(List<ColumnDef> columns)
		{
		cols = columns;
		attrs = new LinkedHashMap<String, String>(columns.size());
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		final Iterator<Object> oi;
		final Iterator<ColumnDef> ci;
		for (oi = row.getValues().iterator(), ci = cols.iterator(); oi.hasNext() && ci.hasNext(); )
			{
			final Object value = oi.next();
			final ColumnDef cd = ci.next();
			attrs.put(cd.getName(), fmt.format(cd.getType(), value));
			}
		
		try	{
			writer.writeText("\n\t");
			writer.writeElement(tableName, attrs);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		
		count++;
		
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
