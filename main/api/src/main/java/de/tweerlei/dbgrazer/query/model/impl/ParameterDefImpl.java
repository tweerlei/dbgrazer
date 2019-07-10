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

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;

/**
 * Definition of a query parameter
 * 
 * @author Robert Wruck
 */
public class ParameterDefImpl extends NamedBase implements ParameterDef
	{
	private final ColumnType type;
	private final String valueQuery;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param type Type
	 * @param valueQuery Query name that returns possible values
	 */
	public ParameterDefImpl(String name, ColumnType type, String valueQuery)
		{
		super(name);
		this.type = type;
		this.valueQuery = StringUtils.empty(valueQuery) ? null : valueQuery;
		}

	@Override
	public ColumnType getType()
		{
		return type;
		}
	
	@Override
	public String getValueQuery()
		{
		return valueQuery;
		}
	}
