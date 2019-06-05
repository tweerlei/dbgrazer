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
package de.tweerlei.dbgrazer.plugins.text;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import de.tweerlei.common.io.ByteOrderMarkReader;
import de.tweerlei.common.textdata.CSVReader;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.DataExtractor;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;

/**
 * DataExtractor for CSV
 * 
 * @author Robert Wruck
 */
public class AbstractFieldDataExtractor extends NamedBase implements DataExtractor
	{
	private final String fieldDelimiter;
	private final String textDelimiter;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param d Feldtrenner
	 * @param t Textbegrenzung
	 */
	public AbstractFieldDataExtractor(String name, String d, String t)
		{
		super(name);
		this.fieldDelimiter = d;
		this.textDelimiter = t;
		}
	
	@Override
	public RowSet extractData(Reader in)
		{
		final CSVReader r = new CSVReader(new LineNumberReader(new ByteOrderMarkReader(in)),
				fieldDelimiter, textDelimiter, 0, false);
		
		String[] headings = null;
		RowSet rs = null;
		try	{
			for (;;)
				{
				final String[] fields = r.readData();
				if (rs == null)
					{
					if (headings == null)
						{
						headings = fields;
						continue;
						}
					else
						{
						final List<ColumnDef> cd = new ArrayList<ColumnDef>(headings.length);
						for (int i = 0; i < headings.length; i++)
							{
							if (i < fields.length)
								{
								if (parseInt(fields[i]) != null)
									cd.add(new ColumnDefImpl(headings[i], ColumnType.INTEGER, null, null, null, null));
								else
									cd.add(new ColumnDefImpl(headings[i], ColumnType.STRING, null, null, null, null));
								}
							else
								cd.add(new ColumnDefImpl(headings[i], ColumnType.STRING, null, null, null, null));
							}
						rs = new RowSetImpl(null, 0, cd);
						}
					}
				
				final int n = rs.getColumns().size();
				final ResultRow row = new DefaultResultRow(n);
				for (int i = 0; i < n; i++)
					{
					if (i < fields.length)
						{
						if (rs.getColumns().get(i).getType() == ColumnType.INTEGER)
							row.getValues().add(parseInt(fields[i]));
						else
							row.getValues().add(fields[i]);
						}
					else
						row.getValues().add(null);
					}
				rs.getRows().add(row);
				}
			}
		catch (IOException e)
			{
			// ignore
			}
		
		return (rs);
		}
	
	private Integer parseInt(String value)
		{
		try	{
			return (Integer.valueOf(value));
			}
		catch (NumberFormatException e)
			{
			return (null);
			}
		}
	}
