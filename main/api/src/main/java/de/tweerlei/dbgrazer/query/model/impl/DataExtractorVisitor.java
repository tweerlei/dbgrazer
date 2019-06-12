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

import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.service.DataExtractorService;
import de.tweerlei.dbgrazer.query.service.RowTransformerService;

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
	
	/**
	 * The Query attribute for the RowTransformer name
	 */
	public static final String TRANSFORMER_NAME_ATTRIBUTE = "rowTransformer";
	
	/**
	 * The Query attribute for the RowTransformer recipe
	 */
	public static final String TRANSFORMER_RECIPE_ATTRIBUTE = "rowTransformation";
	
	private final DataExtractorService extractorService;
	private final RowTransformerService transformerService;
	private RowHandler handler;
	
	/**
	 * Constructor
	 * @param extractorService DataExtractorService
	 * @param transformerService RowTransformerService
	 */
	public DataExtractorVisitor(DataExtractorService extractorService, RowTransformerService transformerService)
		{
		this.extractorService = extractorService;
		this.transformerService = transformerService;
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
					
					final String transformerName = rs.getQuery().getAttributes().get(TRANSFORMER_NAME_ATTRIBUTE);
					final String transformerRecipe = rs.getQuery().getAttributes().get(TRANSFORMER_RECIPE_ATTRIBUTE);
					if ((transformerName != null) && (transformerRecipe != null))
						{
						handler = transformerService.createHandler(transformerRecipe, transformerName);
						if (handler != null)
							{
							handler.startRows(rs.getColumns());
							return (true);
							}
						}
					}
				}
			}
		
		return (false);
		}
	
	@Override
	public void endRowSet(RowSet rs)
		{
		handler.endRows();
		}
	
	@Override
	public boolean startRow(ResultRow row, int level)
		{
		handler.handleRow(row);
		
		return (false);
		}
	}
