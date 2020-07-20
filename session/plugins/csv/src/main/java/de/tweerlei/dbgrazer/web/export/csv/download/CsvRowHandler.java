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
package de.tweerlei.dbgrazer.web.export.csv.download;

import java.io.IOException;
import java.util.List;

import de.tweerlei.common.textdata.CSVWriter;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Write CSV lines for ResultRows
 * 
 * @author Robert Wruck
 */
public class CsvRowHandler implements RowHandler
	{
	private final CSVWriter writer;
	private final DataFormatter fmt;
	private ColumnType[] cols;
	private String[] data;
	private int count;
	
	/**
	 * Constructor
	 * @param w CSVWriter
	 * @param fmt DataFormatter
	 */
	public CsvRowHandler(CSVWriter w, DataFormatter fmt)
		{
		this.writer = w;
		this.fmt = fmt;
		this.data = null;
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
		data = new String[columns.size()];
		cols = new ColumnType[columns.size()];
		
		int i = 0;
		for (ColumnDef c : columns)
			{
			data[i] = c.getName();
			cols[i] = c.getType();
			i++;
			}
		
		try	{
			writer.writeData(data);
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
			data[i] = fmt.format(cols[i], o);
			i++;
			}
		
		try	{
			writer.writeData(data);
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
