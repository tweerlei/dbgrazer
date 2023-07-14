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
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Download a RowSet as CSV
 * 
 * @author Robert Wruck
 */
public class LdifDownloadSource extends AbstractLdifDownloadSource
	{
	private static final String VALUE_SEPARATOR = "\n";
	private static final String DN_ATTRIBUTE = "dn";
	private static final String RDN_ATTRIBUTE = "rdn";
	
	private final RowSet rs;
	private final String header;
	private final String noDataFound;
	private final DataFormatter fmt;
	
	/**
	 * Constructor
	 * @param rs RowSet
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param fmt DataFormatter
	 */
	public LdifDownloadSource(RowSet rs, String header, String noDataFound, DataFormatter fmt)
		{
		super(rs.getQuery().getName());
		
		this.rs = rs;
		this.header = header;
		this.noDataFound = noDataFound;
		this.fmt = fmt;
		}
	
	@Override
	protected void writeLdif(LDIFWriter cw) throws IOException
		{
		cw.writeComment(header);
		
		if ((rs == null) || rs.getRows().isEmpty())
			{
			cw.writeComment(noDataFound);
			}
		else
			{
			exportRows(cw);
			}
		}
	
	private void exportRows(LDIFWriter cw) throws IOException
		{
		final ColumnType[] cols = new ColumnType[rs.getColumns().size()];
		final String[] names = new String[rs.getColumns().size()];
		final Map<String, Object> data = new LinkedHashMap<String, Object>();
		int dnIndex = -1;
		int rdnIndex = -1;
		
		int i = 0;
		for (ColumnDef c : rs.getColumns())
			{
			cols[i] = c.getType();
			names[i] = c.getName();
			if (DN_ATTRIBUTE.equalsIgnoreCase(names[i]))
				dnIndex = i;
			else if (RDN_ATTRIBUTE.equalsIgnoreCase(names[i]))
				rdnIndex = i;
			i++;
			}
		
		for (ResultRow row : rs.getRows())
			{
			Object dn = null;
			i = 0;
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
			cw.write(String.valueOf(dn), data);
			}
		}
	}
