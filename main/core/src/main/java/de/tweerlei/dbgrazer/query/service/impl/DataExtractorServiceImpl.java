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
package de.tweerlei.dbgrazer.query.service.impl;

import java.io.Reader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.query.model.DataExtractor;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.service.DataExtractorService;

/**
 * Extract data from a String
 * 
 * @author Robert Wruck
 */
@Service
public class DataExtractorServiceImpl implements DataExtractorService
	{
	private final Logger logger;
	private final Map<String, DataExtractor> formats;
	
	/**
	 * Constructor
	 * @param formats DataExtractors
	 */
	@Autowired(required = false)
	public DataExtractorServiceImpl(Set<DataExtractor> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<DataExtractor>(formats));
		
		this.logger.log(Level.INFO, "Data extractors: " + this.formats);
		}
	
	/**
	 * Constructor used when no FileUploader instances are available
	 */
	public DataExtractorServiceImpl()
		{
		this(Collections.<DataExtractor>emptySet());
		}
	
	@Override
	public Set<String> getSupportedFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public RowSet extractData(Reader in, String format)
		{
		final DataExtractor c = formats.get(format);
		if (c != null)
			return (c.extractData(in));
		
		return (null);
		}
	}
