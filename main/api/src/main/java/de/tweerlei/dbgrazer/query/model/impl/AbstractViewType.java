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

import org.springframework.validation.Errors;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryErrorKeys;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public abstract class AbstractViewType extends BaseQueryType
	{
	/**
	 * Constructor for view types
	 * @param name Name
	 * @param attributes Supported attributes
	 */
	protected AbstractViewType(String name, Map<String, Class<?>> attributes)
		{
		super(name, null, attributes);
		}
	
	@Override
	public final ResultMapMode getMapMode()
		{
		return ResultMapMode.SINGLE;
		}
	
	@Override
	public void validate(Query query, Errors errors)
		{
		if (StringUtils.empty(query.getName()))
			errors.rejectValue("name", QueryErrorKeys.EMPTY_NAME);
		
		if (query.getSubQueries().isEmpty())
			errors.rejectValue("subQueries", QueryErrorKeys.VIEW_WITHOUT_SUBQUERIES);
		}
	}
