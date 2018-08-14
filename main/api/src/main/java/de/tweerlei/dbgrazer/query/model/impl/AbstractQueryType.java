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

import org.springframework.validation.Errors;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryErrorKeys;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public abstract class AbstractQueryType extends BaseQueryType
	{
	/**
	 * Constructor for query types
	 * @param name Name
	 * @param linkType LinkType
	 */
	protected AbstractQueryType(String name, LinkType linkType)
		{
		super(name, linkType);
		}
	
	@Override
	public void validate(Query query, Errors errors)
		{
		if (StringUtils.empty(query.getName()))
			errors.rejectValue("name", QueryErrorKeys.EMPTY_NAME);
		
		if (StringUtils.empty(query.getStatement()))
			errors.rejectValue("statement", QueryErrorKeys.EMPTY_STATEMENT);
		}
	}
