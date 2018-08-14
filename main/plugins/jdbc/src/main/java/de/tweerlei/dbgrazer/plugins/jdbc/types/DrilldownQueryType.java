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

import de.tweerlei.dbgrazer.plugins.jdbc.impl.JdbcLinkType;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.ResultOrientation;
import de.tweerlei.dbgrazer.query.model.impl.AbstractTableQueryType;

/**
 * Multiple rows, multiple columns in a single panel with linked queries shown in additional panels
 * 
 * @author Robert Wruck
 */
@Service
@Order(116)
public class DrilldownQueryType extends AbstractTableQueryType
	{
	private static final String NAME = "DRILLDOWN";
	
	private final Map<String, Class<?>> attributes;
	
	/**
	 * Constructor
	 * @param linkType LinkType
	 */
	@Autowired
	public DrilldownQueryType(JdbcLinkType linkType)
		{
		super(NAME, linkType, ResultMapMode.SINGLE);
		
		final Map<String, Class<?>> m = new LinkedHashMap<String, Class<?>>();
		m.put(QueryTypeAttributes.ATTR_COLORIZE, Boolean.class);
		attributes = Collections.unmodifiableMap(m);
		}
	
	@Override
	public ResultOrientation getOrientation()
		{
		return ResultOrientation.DOWN;
		}
	
	@Override
	public Map<String, Class<?>> getSupportedAttributes()
		{
		return (attributes);
		}
	}
