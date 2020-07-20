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

import de.tweerlei.common.textdata.CSVWriter;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Write CSV lines for ResultRows
 * 
 * @author Robert Wruck
 */
public class CsvRowSetHandler implements RowSetHandler
	{
	private final CSVWriter writer;
	private final DataFormatter fmt;
	private final SQLDialect dialect;
	private int count;
	
	/**
	 * Constructor
	 * @param w CSVWriter
	 * @param fmt DataFormatter
	 * @param dialect SQLDialect
	 */
	public CsvRowSetHandler(CSVWriter w, DataFormatter fmt, SQLDialect dialect)
		{
		this.writer = w;
		this.fmt = fmt;
		this.dialect = dialect;
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
	public void handleRowSet(TableDescription info, RowSet rs)
		{
		try	{
			final ColumnType[] cols = new ColumnType[rs.getColumns().size()];
			final String[] data = new String[rs.getColumns().size() + 1];
			
			int i = 0;
			data[0] = "";
			for (ColumnDef c : rs.getColumns())
				{
				data[i + 1] = c.getName();
				cols[i] = c.getType();
				i++;
				}
			
			writer.writeData(data);
			
			data[0] = dialect.getQualifiedTableName(info.getName());
			for (ResultRow row : rs.getRows())
				{
				i = 0;
				for (Object o : row.getValues())
					{
					data[i + 1] = fmt.format(cols[i], o);
					i++;
					}
				
				writer.writeData(data);
				}
			
			count++;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	}
