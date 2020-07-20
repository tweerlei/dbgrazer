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
package de.tweerlei.dbgrazer.web.export.json.download;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import de.tweerlei.common.textdata.JSONWriter;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.RowSetHandler;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Write CSV lines for ResultRows
 * 
 * @author Robert Wruck
 */
public class JsonRowSetHandler implements RowSetHandler
	{
	private final JSONWriter writer;
	private final SQLDialect dialect;
	private int count;
	
	/**
	 * Constructor
	 * @param w JSONWriter
	 * @param dialect SQLDialect
	 */
	public JsonRowSetHandler(JSONWriter w, SQLDialect dialect)
		{
		this.writer = w;
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
			final String[] names = new String[rs.getColumns().size()];
			
			int i = 0;
			for (ColumnDef c : rs.getColumns())
				{
				names[i] = c.getName();
				i++;
				}
			
			writer.writeComment(tableName);
			writer.startArray();
			
			for (ResultRow row : rs.getRows())
				{
				i = 0;
				for (Object o : row.getValues())
					{
					data.put(names[i], o);
					i++;
					}
				
				writer.appendArray(data);
				}
			
			writer.endArray();
			
			count++;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	}
