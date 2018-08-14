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
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Write DBUnit XML tags for ResultRows
 * 
 * @author Robert Wruck
 */
public class DBUnitRowSetHandler implements RowSetHandler
	{
	private final XMLWriter writer;
	private final DataFormatter fmt;
	private final SQLDialect dialect;
	
	/**
	 * Constructor
	 * @param w XMLWriter
	 * @param fmt DataFormatter
	 * @param dialect SQLDialect
	 */
	public DBUnitRowSetHandler(XMLWriter w, DataFormatter fmt, SQLDialect dialect)
		{
		this.writer = w;
		this.fmt = fmt;
		this.dialect = dialect;
		}
	
	@Override
	public void handleRowSet(TableDescription info, RowSet rs)
		{
		try	{
			final Map<String, String> attrs = new LinkedHashMap<String, String>(rs.getColumns().size());
			final String tableName = dialect.getQualifiedTableName(info.getName());
			
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
				
				writer.writeText("\n\t");
				writer.writeElement(tableName, attrs);
				}
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	}
