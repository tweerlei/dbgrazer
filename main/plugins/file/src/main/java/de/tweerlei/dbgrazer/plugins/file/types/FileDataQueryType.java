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
package de.tweerlei.dbgrazer.plugins.file.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.MapBuilder;
import de.tweerlei.dbgrazer.plugins.file.impl.FilesystemLinkType;
import de.tweerlei.dbgrazer.query.model.DataExtractor;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.impl.AbstractTableQueryType;
import de.tweerlei.dbgrazer.query.model.impl.DataExtractorVisitor;
import de.tweerlei.dbgrazer.query.service.DataExtractorService;
import de.tweerlei.dbgrazer.query.service.RowTransformerService;

/**
 * Read file contents
 * 
 * @author Robert Wruck
 */
@Service
@Order(303)
public class FileDataQueryType extends AbstractTableQueryType
	{
	private static final String NAME = "FILE_DATA";
	
	private final DataExtractorService extractorService;
	private final RowTransformerService transformerService;
	
	/**
	 * Constructor
	 * @param linkType LinkType
	 * @param extractorService DataExtractorService
	 * @param transformerService RowTransformerService
	 */
	@Autowired
	public FileDataQueryType(FilesystemLinkType linkType,
			DataExtractorService extractorService, RowTransformerService transformerService)
		{
		super(NAME, linkType, ResultMapMode.SINGLE, MapBuilder.<String, Class<?>>ordered()
				.put(DataExtractorVisitor.EXTRACTOR_NAME_ATTRIBUTE, DataExtractor.class)
				.build());
		this.extractorService = extractorService;
		this.transformerService = transformerService;
		}
	
	@Override
	public ResultVisitor getPostProcessor()
		{
		return (new DataExtractorVisitor(extractorService, transformerService));
		}
	}
