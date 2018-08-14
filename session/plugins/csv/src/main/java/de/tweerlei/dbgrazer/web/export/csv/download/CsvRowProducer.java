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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.common.io.ByteOrderMarkReader;
import de.tweerlei.common.textdata.CSVReader;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Download query results as a single Excel sheet
 * 
 * @author Robert Wruck
 */
public class CsvRowProducer implements RowProducer
	{
	private static final String CHARSET = "UTF-8";
	
	private final CSVParameters params;
	private final TableDescription info;
	private final InputStream input;
	private final DataFormatter fmt;
	
	/**
	 * Constructor
	 * @param params CSVParameters
	 * @param info TableDescription
	 * @param input InputStream
	 * @param fmt DataFormatter
	 */
	public CsvRowProducer(CSVParameters params, TableDescription info, InputStream input, DataFormatter fmt)
		{
		this.params = params;
		this.info = info;
		this.input = input;
		this.fmt = fmt;
		}
	
	@Override
	public int produceRows(RowHandler h)
		{
		try	{
			final CSVReader r = new CSVReader(new LineNumberReader(new ByteOrderMarkReader(new InputStreamReader(input, CHARSET))),
					params.getFieldSeparator(), params.getTextDelimiter(), 0, false);
			
			try	{
				final int rows = exportCSV(r, h);
				
				return (rows);
				}
			finally
				{
				r.close();
				}
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		finally
			{
			h.endRows();
			}
		}
	
	private int exportCSV(CSVReader r, RowHandler h) throws IOException
		{
		int rows = 0;
		Map<Integer, ColumnDef> columnMap = null;
		ResultRow row = null;
		
		for (;;)
			{
			try	{
				final String[] data = r.readData();
				final int n = data.length;
				
				if (columnMap == null)
					{
					columnMap = new LinkedHashMap<Integer, ColumnDef>(n);
					final List<ColumnDef> columns = new ArrayList<ColumnDef>(n);
					row = new DefaultResultRow(n);
					
					// Read column names from first row
					for (int i = 0; i < n; i++)
						{
						final String columnName = data[i];
						if (!StringUtils.empty(columnName))
							{
							for (ColumnDescription cd : info.getColumns())
								{
								if (cd.getName().equalsIgnoreCase(columnName))
									{
									final ColumnDef def = new ColumnDefImpl(cd.getName(), ColumnType.forSQLType(cd.getType()), null, null, null, null);
									columnMap.put(i, def);
									columns.add(def);
									}
								}
							}
						}
					
					if (columnMap.isEmpty())
						return (0);
					
					h.startRows(columns);
					}
				else
					{
					row.getValues().clear();
					for (Map.Entry<Integer, ColumnDef> ent : columnMap.entrySet())
						{
						if (ent.getKey().intValue() >= n)
							row.getValues().add(null);
						else
							row.getValues().add(fmt.parse(ent.getValue().getType(), data[ent.getKey()]));
						}
					if (!h.handleRow(row))
						{
						rows++;
						break;
						}
					rows++;
					}
				}
			catch (EOFException e)
				{
				// expected
				break;
				}
			}
		
		return (rows);
		}
	}
