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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Download query results as DBUnit flat XML dataset
 * 
 * @author Robert Wruck
 */
public class DBUnitMultiStreamDownloadSource extends AbstractXmlDownloadSource
	{
	private final RowSetProducer producer;
	private final SQLDialect dialect;
	private final String header;	
	private final String noDataFound;
	private final DataFormatter fmt;
	
	/**
	 * Constructor
	 * @param producer RowProducer
	 * @param fileName File base name
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param fmt DataFormatter
	 * @param dialect SQLDialect
	 */
	public DBUnitMultiStreamDownloadSource(RowSetProducer producer, String fileName,
			String header, String noDataFound, DataFormatter fmt, SQLDialect dialect)
		{
		super(fileName);
		
		this.producer = producer;
		this.header = header;
		this.noDataFound = noDataFound;
		this.fmt = fmt;
		this.dialect = dialect;
		}
	
	@Override
	protected void writeXml(XMLWriter xw) throws IOException
		{
		xw.writeXMLDeclaration("UTF-8", true);
		if (header != null)
			xw.writeComment("\n" + header + "\n");
		xw.startElement("dataset");
		
		final DBUnitRowSetHandler handler = new DBUnitRowSetHandler(xw, fmt, dialect);
		try	{
			producer.produceRowSets(handler);
			if (handler.getCount() == 0)
				{
				xw.writeText("\n\t");
				xw.writeComment(noDataFound);
				}
			}
		catch (RuntimeException e)
			{
			Logger.getLogger(getClass().getCanonicalName()).log(Level.SEVERE, "writeXml", e);
			if (e.getMessage() != null)
				xw.writeComment(e.getMessage());
			}
		
		xw.writeText("\n");
		xw.endElement("dataset");
		xw.writeText("\n");
		}
	}
