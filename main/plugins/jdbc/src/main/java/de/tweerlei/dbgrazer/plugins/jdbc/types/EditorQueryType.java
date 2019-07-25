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
 * Multiple rows, multiple columns, first column specifies PK for DML operations
 * 
 * @author Robert Wruck
 */
@Service
@Order(117)
public class EditorQueryType extends AbstractTableQueryType
	{
	private static final String NAME = "EDITOR";
	
	/**
	 * Constructor
	 * @param linkType LinkType
	 */
	@Autowired
	public EditorQueryType(JdbcLinkType linkType)
		{
		super(NAME, linkType, ResultMapMode.SINGLE, MapBuilder.<String, Class<?>>ordered()
				.put(QueryTypeAttributes.ATTR_CATALOG, String.class)
				.put(QueryTypeAttributes.ATTR_SCHEMA, String.class)
				.put(QueryTypeAttributes.ATTR_TABLE, String.class)
				.put(QueryTypeAttributes.ATTR_PK_SELECT, String.class)
				.build());
		}
	}
