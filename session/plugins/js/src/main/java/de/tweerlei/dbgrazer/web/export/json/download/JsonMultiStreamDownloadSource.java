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
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common.textdata.JSONWriter;
import de.tweerlei.dbgrazer.query.model.RowSetProducer;
import de.tweerlei.ermtools.dialect.SQLDialect;

/**
 * Download query results as CSV
 * 
 * @author Robert Wruck
 */
public class JsonMultiStreamDownloadSource extends AbstractJsonDownloadSource
	{
	private final RowSetProducer producer;
	private final SQLDialect dialect;
	private final String header;
	private final String noDataFound;
	
	/**
	 * Constructor
	 * @param producer RowSetProducer
	 * @param fileName File base name
	 * @param header Header comment
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param dialect SQLDialect
	 */
	public JsonMultiStreamDownloadSource(RowSetProducer producer, String fileName, String header, String noDataFound, SQLDialect dialect)
		{
		super(fileName);
		
		this.producer = producer;
		this.header = header;
		this.noDataFound = noDataFound;
		this.dialect = dialect;
		}
	
	@Override
	protected void writeJson(JSONWriter cw) throws IOException
		{
		final JsonRowSetHandler handler = new JsonRowSetHandler(cw, dialect);
		
		try	{
			cw.writeComment(header);
			producer.produceRowSets(handler);
			if (handler.getCount() == 0)
				cw.writeComment(noDataFound);
			}
		catch (RuntimeException e)
			{
			Logger.getLogger(getClass().getCanonicalName()).log(Level.SEVERE, "writeJson", e);
			if (e.getMessage() != null)
				cw.writeComment(e.getMessage());
			}
		}
	}
