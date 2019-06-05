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
package de.tweerlei.dbgrazer.query.model.impl;

import java.io.StringReader;

import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.service.DataExtractorService;

/**
 * ResultVisitor that uses a DataExtractor to extract data from the first row's first field
 * 
 * @author Robert Wruck
 */
public class DataExtractorVisitor extends ResultVisitorAdapter
	{
	/**
	 * The Query attribute for the DataExtractor name
	 */
	public static final String EXTRACTOR_NAME_ATTRIBUTE = "dataExtractor";
	
	private final DataExtractorService extractorService;
	
	/**
	 * Constructor
	 * @param extractorService DataExtractorService
	 */
	public DataExtractorVisitor(DataExtractorService extractorService)
		{
		this.extractorService = extractorService;
		}
	
	@Override
	public boolean startRowSet(RowSet rs)
		{
		final Object value = rs.getFirstValue();
		
		if (value != null)
			{
			final String extractorName = rs.getQuery().getAttributes().get(EXTRACTOR_NAME_ATTRIBUTE);
			if (extractorName != null)
				{
				final RowSet extracted = extractorService.extractData(new StringReader(value.toString()), extractorName);
				if ((extracted != null) && !extracted.getColumns().isEmpty())
					{
					rs.getColumns().clear();
					rs.getColumns().addAll(extracted.getColumns());
					
					rs.getRows().clear();
					rs.getRows().addAll(extracted.getRows());
					}
				}
			}
		
		return (false);
		}
	}
