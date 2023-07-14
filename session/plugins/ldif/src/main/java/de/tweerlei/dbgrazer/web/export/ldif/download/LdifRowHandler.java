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
package de.tweerlei.dbgrazer.web.export.ldif.download;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.common.textdata.LDIFWriter;
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
public class LdifRowHandler implements RowHandler
	{
	private static final String VALUE_SEPARATOR = "\n";
	private static final String DN_ATTRIBUTE = "dn";
	private static final String RDN_ATTRIBUTE = "rdn";
	
	private final LDIFWriter writer;
	private final DataFormatter fmt;
	private final Map<String, Object> data;
	private ColumnType[] cols;
	private String[] names;
	private int dnIndex;
	private int rdnIndex;
	private int count;
	
	/**
	 * Constructor
	 * @param w LDIFWriter
	 * @param fmt DataFormatter
	 */
	public LdifRowHandler(LDIFWriter w, DataFormatter fmt)
		{
		this.writer = w;
		this.fmt = fmt;
		this.data = new LinkedHashMap<String, Object>();
		this.cols = null;
		this.names = null;
		this.dnIndex = -1;
		this.rdnIndex = -1;
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
		cols = new ColumnType[columns.size()];
		dnIndex = -1;
		rdnIndex = -1;
		
		int i = 0;
		for (ColumnDef c : columns)
			{
			names[i] = c.getName();
			cols[i] = c.getType();
			if (DN_ATTRIBUTE.equalsIgnoreCase(names[i]))
				dnIndex = i;
			else if (RDN_ATTRIBUTE.equalsIgnoreCase(names[i]))
				rdnIndex = i;
			i++;
			}
		}
	
	@Override
	public boolean handleRow(ResultRow row)
		{
		Object dn = null;
		int i = 0;
		data.clear();
		for (Object o : row.getValues())
			{
			if (i == dnIndex)
				dn = o;
			else if ((i != rdnIndex) && (o != null))
				{
				switch (cols[i])
					{
					case STRING:
					case TEXT:
						// Assume that multiple values are separated by newlines
						// (see LdapQueryRunner.RowSetMapper)
						data.put(names[i], o.toString().split(VALUE_SEPARATOR));
						break;
					case BINARY:
						// Pass binary data unformatted for Base64 encoding
						data.put(names[i], o);
						break;
					default:
						// Preformat any other data
						data.put(names[i], fmt.format(cols[i], o));
						break;
					}
				}
			i++;
			}
		
		try	{
			writer.write(String.valueOf(dn), data);
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
