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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.plugins.jdbc.impl.AggregateSubQueryResolver;
import de.tweerlei.dbgrazer.plugins.jdbc.impl.JdbcLinkType;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.SubQueryResolver;
import de.tweerlei.dbgrazer.query.model.impl.AbstractTableQueryType;

/**
 * Multiple rows, multiple columns
 * 
 * @author Robert Wruck
 */
@Service
@Order(114)
public class CubeQueryType extends AbstractTableQueryType
	{
	private static final String NAME = "CUBE";
	
	private final Map<String, Class<?>> attributes;
	private final SubQueryResolver subQueryResolver;
	
	/**
	 * Constructor
	 * @param linkType LinkType
	 * @param subQueryResolver SubQueryResolver
	 */
	@Autowired
	public CubeQueryType(JdbcLinkType linkType, AggregateSubQueryResolver subQueryResolver)
		{
		super(NAME, linkType, ResultMapMode.SINGLE);
		
		final Map<String, Class<?>> m = new LinkedHashMap<String, Class<?>>();
		m.put(QueryTypeAttributes.ATTR_DIMENSIONS, String.class);
		m.put(QueryTypeAttributes.ATTR_RESULTS, String.class);
		attributes = Collections.unmodifiableMap(m);
		
		this.subQueryResolver = subQueryResolver;
		}

	@Override
	public Map<String, Class<?>> getSupportedAttributes()
		{
		return (attributes);
		}
	
	@Override
	public SubQueryResolver getSubQueryResolver()
		{
		return (subQueryResolver);
		}
	}
