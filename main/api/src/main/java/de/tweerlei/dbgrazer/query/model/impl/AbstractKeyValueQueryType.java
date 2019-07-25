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

import java.util.Map;

import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.ResultType;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public abstract class AbstractKeyValueQueryType extends AbstractQueryType
	{
	/**
	 * Constructor for query types
	 * @param name Name
	 * @param linkType LinkType
	 * @param attributes Supported attributes
	 */
	protected AbstractKeyValueQueryType(String name, LinkType linkType, Map<String, Class<?>> attributes)
		{
		super(name, linkType, attributes);
		}
	
	@Override
	public final ResultType getResultType()
		{
		return ResultType.KEYVALUE;
		}
	
	@Override
	public final ResultMapMode getMapMode()
		{
		return ResultMapMode.SINGLE;
		}
	}
