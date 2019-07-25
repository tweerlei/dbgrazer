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
package de.tweerlei.dbgrazer.plugins.jdbc.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.MapBuilder;
import de.tweerlei.dbgrazer.plugins.jdbc.impl.JdbcLinkType;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.impl.AbstractTableQueryType;

/**
 * Multiple rows, multiple columns
 * 
 * @author Robert Wruck
 */
@Service
@Order(100)
public class MultipleQueryType extends AbstractTableQueryType
	{
	private static final String NAME = "MULTIPLE";
	
	/**
	 * Constructor
	 * @param linkType LinkType
	 */
	@Autowired
	public MultipleQueryType(JdbcLinkType linkType)
		{
		super(NAME, linkType, ResultMapMode.SINGLE, MapBuilder.<String, Class<?>>ordered()
				.put(QueryTypeAttributes.ATTR_COLORIZE, Boolean.class)
				.put(QueryTypeAttributes.ATTR_SUM_ROW, Boolean.class)
				.build());
		}
	}
