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
import java.util.Map;

import de.tweerlei.common.textdata.LDIFWriter;
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
public class LdifRowSetHandler implements RowSetHandler
	{
	private static final String VALUE_SEPARATOR = "\n";
	private static final String DN_ATTRIBUTE = "dn";
	private static final String RDN_ATTRIBUTE = "rdn";
	
	private final LDIFWriter writer;
	private final DataFormatter fmt;
	private final SQLDialect dialect;
	private int count;
	
	/**
	 * Constructor
	 * @param w LDIFWriter
	 * @param fmt DataFormatter
	 * @param dialect SQLDialect
	 */
	public LdifRowSetHandler(LDIFWriter w, DataFormatter fmt, SQLDialect dialect)
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
			final String tableName = dialect.getQualifiedTableName(info.getName());
			final Map<String, Object> data = new LinkedHashMap<String, Object>();
			final ColumnType[] cols = new ColumnType[rs.getColumns().size()];
			final String[] names = new String[rs.getColumns().size()];
			int dnIndex = -1;
			int rdnIndex = -1;
			
			int i = 0;
			for (ColumnDef c : rs.getColumns())
				{
				names[i] = c.getName();
				cols[i] = c.getType();
				if (DN_ATTRIBUTE.equalsIgnoreCase(names[i]))
					dnIndex = i;
				else if (RDN_ATTRIBUTE.equalsIgnoreCase(names[i]))
					rdnIndex = i;
				i++;
				}
			
			writer.writeComment(tableName);
			
			for (ResultRow row : rs.getRows())
				{
				Object dn = null;
				i = 0;
				for (Object o : row.getValues())
					{
					if (i == dnIndex)
						dn = o;
					else if (i != rdnIndex)
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
				
				writer.write(String.valueOf(dn), data);
				}
			
			count++;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	}
