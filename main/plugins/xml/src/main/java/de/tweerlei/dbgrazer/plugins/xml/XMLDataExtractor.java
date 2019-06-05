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
package de.tweerlei.dbgrazer.plugins.xml;

import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.tweerlei.common.xml.AbstractXMLParser;
import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.DataExtractor;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;

/**
 * Extract data from XML
 * 
 * @author Robert Wruck
 */
@Service
public class XMLDataExtractor implements DataExtractor
	{
	private static class RowSetXMLParser extends AbstractXMLParser
		{
		private final StringBuilder sb;
		private final Map<String, String> fields;
		private RowSet rs;
		private int level;
		
		public RowSetXMLParser()
			{
			super(true, true);
			this.sb = new StringBuilder();
			this.fields = new LinkedHashMap<String, String>();
			this.rs = null;
			this.level = 0;
			}
		
		public RowSet getRowSet()
			{
			return (rs);
			}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
			{
			level++;
			}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
			{
			if (level == 3)
				{
				fields.put(localName, sb.toString());
				sb.setLength(0);
				}
			else if (level == 2)
				{
				if (rs == null)
					{
					final List<ColumnDef> cd = new ArrayList<ColumnDef>(fields.size());
					for (Map.Entry<String, String> ent : fields.entrySet())
						{
						if (parseInt(ent.getValue()) != null)
							cd.add(new ColumnDefImpl(ent.getKey(), ColumnType.INTEGER, null, null, null, null));
						else
							cd.add(new ColumnDefImpl(ent.getKey(), ColumnType.STRING, null, null, null, null));
						}
					rs = new RowSetImpl(null, 0, cd);
					}
				
				final ResultRow row = new DefaultResultRow();
				for (ColumnDef c : rs.getColumns())
					{
					if (c.getType() == ColumnType.INTEGER)
						row.getValues().add(parseInt(fields.get(c.getName())));
					else
						row.getValues().add(fields.get(c.getName()));
					}
				rs.getRows().add(row);
				
				fields.clear();
				}
			level--;
			}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
			{
			if (level == 3)
				sb.append(ch, start, length);
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
	
	@Override
	public String getName()
		{
		return ("XML");
		}
	
	@Override
	public RowSet extractData(Reader in)
		{
		final RowSetXMLParser p = new RowSetXMLParser();
		
		try	{
			p.parse(in);
			}
		catch (SAXException e)
			{
			// ignore
			}
		
		return (p.getRowSet());
		}
	}
