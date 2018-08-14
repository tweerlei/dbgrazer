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
import java.util.Iterator;

import de.tweerlei.common.textdata.CSVWriter;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Download a RowSet as CSV
 * 
 * @author Robert Wruck
 */
public class CsvDownloadSource extends AbstractCsvDownloadSource
	{
	private final RowSet rs;
	private final String noDataFound;
	private final String nameLabel;
	private final String valueLabel;
	private final DataFormatter fmt;
	
	/**
	 * Constructor
	 * @param params CSVParameters
	 * @param rs RowSet
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param nameLabel Label for the name column
	 * @param valueLabel Label for the value column
	 * @param fmt DataFormatter
	 */
	public CsvDownloadSource(CSVParameters params, RowSet rs, String noDataFound, String nameLabel, String valueLabel, DataFormatter fmt)
		{
		super(rs.getQuery().getName(), params);
		
		this.rs = rs;
		this.noDataFound = noDataFound;
		this.nameLabel = nameLabel;
		this.valueLabel = valueLabel;
		this.fmt = fmt;
		}
	
	@Override
	protected void writeCsv(CSVWriter cw) throws IOException
		{
		if ((rs == null) || rs.getRows().isEmpty())
			{
			cw.writeData(new String[] { noDataFound });
			}
		else if (rs.getQuery().getType().getResultType() == ResultType.SINGLE)
			{
			exportColumn(cw);
			}
		else if (rs.getQuery().getType().getResultType() == ResultType.ROW)
			{
			exportRow(cw);
			}
		else
			{
			exportRows(cw);
			}
		}
	
	private void exportColumn(CSVWriter cw) throws IOException
		{
		final StringBuilder sb = new StringBuilder();
		final ColumnType type = rs.getColumns().get(0).getType();
		
		for (Object o : rs.getFirstColumn())
			sb.append(fmt.format(type, o));
		
		cw.writeData(new String[] { sb.toString() });
		}
	
	private void exportRow(CSVWriter cw) throws IOException
		{
		final String[] data = new String[] { nameLabel, valueLabel };
		
		cw.writeData(data);
		final Iterator<ColumnDef> ci = rs.getColumns().iterator();
		final Iterator<Object> oi = rs.getFirstRow().getValues().iterator();
		
		while (ci.hasNext() && oi.hasNext())
			{
			final ColumnDef cd = ci.next();
			final Object value = oi.next();
			data[0] = cd.getName();
			data[1] = fmt.format(cd.getType(), value);
			cw.writeData(data);
			}
		}
	
	private void exportRows(CSVWriter cw) throws IOException
		{
		final ColumnType[] cols = new ColumnType[rs.getColumns().size()];
		final String[] data = new String[rs.getColumns().size()];
		
		int i = 0;
		for (ColumnDef c : rs.getColumns())
			{
			cols[i] = c.getType();
			data[i] = c.getName();
			i++;
			}
		cw.writeData(data);
		
		for (ResultRow row : rs.getRows())
			{
			i = 0;
			for (Object o : row.getValues())
				{
				data[i] = fmt.format(cols[i], o);
				i++;
				}
			cw.writeData(data);
			}
		}
	}
