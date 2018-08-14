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
import java.util.Map;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Download a RowSet as DBUnit flat XML dataset
 * 
 * @author Robert Wruck
 */
public class DBUnitDownloadSource extends AbstractXmlDownloadSource
	{
	private final RowSet rs;
	private final String tableName;
	private final String header;	
	private final String noDataFound;
	private final DataFormatter fmt;
	
	/**
	 * Constructor
	 * @param rs RowSet
	 * @param tableName Table name
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param fmt DataFormatter
	 */
	public DBUnitDownloadSource(RowSet rs, String tableName,
			String header, String noDataFound, DataFormatter fmt)
		{
		super(rs.getQuery().getName());
		
		this.rs = rs;
		this.tableName = tableName;
		this.header = header;
		this.noDataFound = noDataFound;
		this.fmt = fmt;
		}
	
	@Override
	protected void writeXml(XMLWriter xw) throws IOException
		{
		xw.writeXMLDeclaration("UTF-8", true);
		if (header != null)
			xw.writeComment("\n" + header + "\n");
		xw.startElement("dataset");
		
		final int rows = exportRows(xw);
		if (rows == 0)
			{
			xw.writeText("\n\t");
			xw.writeComment(noDataFound);
			}
		
		xw.writeText("\n");
		xw.endElement("dataset");
		xw.writeText("\n");
		}
	
	private int exportRows(XMLWriter xw) throws IOException
		{
		int rows = 0;
		
		final Map<String, String> attrs = new LinkedHashMap<String, String>();
		
		for (ResultRow row : rs.getRows())
			{
			final Iterator<Object> oi;
			final Iterator<ColumnDef> ci;
			for (oi = row.getValues().iterator(), ci = rs.getColumns().iterator(); oi.hasNext() && ci.hasNext(); )
				{
				final Object value = oi.next();
				final ColumnDef cd = ci.next();
				attrs.put(cd.getName(), fmt.format(cd.getType(), value));
				}
			
			xw.writeText("\n\t");
			xw.writeElement(tableName, attrs);
			rows++;
			}
		
		return (rows);
		}
	}
