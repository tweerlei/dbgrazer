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
package de.tweerlei.dbgrazer.web.export.json.download;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.common.textdata.JSONWriter;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;

/**
 * Write CSV lines for ResultRows
 * 
 * @author Robert Wruck
 */
public class JsonRowHandler implements RowHandler
	{
	private final JSONWriter writer;
	private final Map<String, Object> data;
	private String[] names;
	private int count;
	
	/**
	 * Constructor
	 * @param w JSONWriter
	 */
	public JsonRowHandler(JSONWriter w)
		{
		this.writer = w;
		this.data = new LinkedHashMap<String, Object>();
		this.names = null;
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
		names = new String[columns.size()];
		
		int i = 0;
		for (ColumnDef c : columns)
			{
			names[i] = c.getName();
			i++;
			}
		
		try	{
			writer.startArray();
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		int i = 0;
		for (Object o : row.getValues())
			{
			data.put(names[i], o);
			i++;
			}
		
		try	{
			writer.appendArray(data);
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
		try	{
			writer.endArray();
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void error(RuntimeException e)
		{
		}
	}
