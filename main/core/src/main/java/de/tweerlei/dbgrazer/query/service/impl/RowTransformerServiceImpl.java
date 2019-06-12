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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.RowTransformer;
import de.tweerlei.dbgrazer.query.service.RowTransformerService;

/**
 * Transform ResultRows
 * 
 * @author Robert Wruck
 */
@Service
public class RowTransformerServiceImpl implements RowTransformerService
	{
	private final Logger logger;
	private final Map<String, RowTransformer> formats;
	
	/**
	 * Constructor
	 * @param formats RowTransformers
	 */
	@Autowired(required = false)
	public RowTransformerServiceImpl(Set<RowTransformer> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<RowTransformer>(formats));
		
		this.logger.log(Level.INFO, "Row transformers: " + this.formats);
		}
	
	/**
	 * Constructor used when no FileUploader instances are available
	 */
	public RowTransformerServiceImpl()
		{
		this(Collections.<RowTransformer>emptySet());
		}
	
	@Override
	public Set<String> getSupportedFormats()
		{
		return (formats.keySet());
		}
	
	@Override
	public RowHandler createHandler(String recipe, String format)
		{
		final RowTransformer c = formats.get(format);
		if (c != null)
			return (c.createHandler(recipe));
		
		return (null);
		}
	}
