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
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;

/**
 * Download a RowSet as CSV
 * 
 * @author Robert Wruck
 */
public class JsonDownloadSource extends AbstractJsonDownloadSource
	{
	private final RowSet rs;
	private final String header;
	private final String noDataFound;
	
	/**
	 * Constructor
	 * @param rs RowSet
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 */
	public JsonDownloadSource(RowSet rs, String header, String noDataFound)
		{
		super(rs.getQuery().getName());
		
		this.rs = rs;
		this.header = header;
		this.noDataFound = noDataFound;
		}
	
	@Override
	protected void writeJson(JSONWriter cw) throws IOException
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
	
	private void exportRows(JSONWriter cw) throws IOException
		{
		final String[] names = new String[rs.getColumns().size()];
		final Map<String, Object> data = new LinkedHashMap<String, Object>();
		
		int i = 0;
		for (ColumnDef c : rs.getColumns())
			{
			names[i] = c.getName();
			i++;
			}
		
		cw.startArray();
		for (ResultRow row : rs.getRows())
			{
			i = 0;
			for (Object o : row.getValues())
				{
				data.put(names[i], o);
				i++;
				}
			cw.appendArray(data);
			}
		cw.endArray();
		}
	}
