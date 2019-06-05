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
package de.tweerlei.dbgrazer.plugins.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import de.tweerlei.common.textdata.JSONReader;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.DataExtractor;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;

/**
 * Extract data from a JSON array containing objects that represent individual rows
 * 
 * @author Robert Wruck
 */
@Service
public class JSONDataExtractor implements DataExtractor
	{
	@Override
	public String getName()
		{
		return ("JSON");
		}
	
	@Override
	public RowSet extractData(Reader in)
		{
		final JSONReader r = new JSONReader(in);
		
		RowSet rs = null;
		try	{
			final Object o = r.read();
			if (o instanceof List)
				{
				final List<?> l = (List<?>) o;
				for (Object o2 : l)
					{
					if (o2 instanceof Map)
						{
						final Map<?, ?> m = (Map<?, ?>) o2;
						
						if (rs == null)
							{
							final List<ColumnDef> cd = new ArrayList<ColumnDef>(m.size());
							for (Map.Entry<?, ?> ent : m.entrySet())
								{
								if (ent.getValue() instanceof Number)
									cd.add(new ColumnDefImpl(String.valueOf(ent.getKey()), ColumnType.INTEGER, null, null, null, null));
								else
									cd.add(new ColumnDefImpl(String.valueOf(ent.getKey()), ColumnType.STRING, null, null, null, null));
								}
							rs = new RowSetImpl(null, 0, cd);
							}
						
						final ResultRow row = new DefaultResultRow(rs.getColumns().size());
						for (ColumnDef cd : rs.getColumns())
							row.getValues().add(m.get(cd.getName()));
						rs.getRows().add(row);
						}
					}
				}
			}
		catch (IOException e)
			{
			// ignore
			}
		
		return (rs);
		}
	}
