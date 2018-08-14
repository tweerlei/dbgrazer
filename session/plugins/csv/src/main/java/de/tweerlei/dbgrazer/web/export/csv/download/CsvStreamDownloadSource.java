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
package de.tweerlei.dbgrazer.web.export.csv.download;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tweerlei.common.textdata.CSVWriter;
import de.tweerlei.dbgrazer.query.model.RowProducer;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Download query results as CSV
 * 
 * @author Robert Wruck
 */
public class CsvStreamDownloadSource extends AbstractCsvDownloadSource
	{
	private final RowProducer producer;
	private final String noDataFound;
	private final DataFormatter fmt;
	
	/**
	 * Constructor
	 * @param params CSVParameters
	 * @param producer RowProducer
	 * @param fileName File base name
	 * @param noDataFound Text to return if the RowSet is empty
	 * @param fmt DataFormatter
	 */
	public CsvStreamDownloadSource(CSVParameters params, RowProducer producer, String fileName, String noDataFound, DataFormatter fmt)
		{
		super(fileName, params);
		
		this.producer = producer;
		this.noDataFound = noDataFound;
		this.fmt = fmt;
		}
	
	@Override
	protected void writeCsv(CSVWriter cw) throws IOException
		{
		final CsvRowHandler handler = new CsvRowHandler(cw, fmt);
		try	{
			if (producer.produceRows(handler) == 0)
				cw.writeData(new String[] { noDataFound });
			}
		catch (RuntimeException e)
			{
			Logger.getLogger(getClass().getCanonicalName()).log(Level.SEVERE, "writeCsv", e);
			if (e.getMessage() != null)
				cw.writeData(new String[] { e.getMessage() });
			}
		}
	}
