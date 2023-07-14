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
package de.tweerlei.dbgrazer.plugins.wiki.types;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.builders.MapBuilder;
import de.tweerlei.dbgrazer.query.model.impl.AbstractSingleQueryType;
import de.tweerlei.dbgrazer.text.backend.TextFormatter;

/**
 * Read file contents
 * 
 * @author Robert Wruck
 */
@Service
@Order(600)
public class WikiQueryType extends AbstractSingleQueryType
	{
	private static final String NAME = "WIKI";
	
	/**
	 * Constructor
	 */
	public WikiQueryType()
		{
		super(NAME, null, MapBuilder.<String, Class<?>>ordered()
				.put(QueryTypeAttributes.ATTR_FORMATTER, TextFormatter.class)
				.build());
		}
	}
