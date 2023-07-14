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
package de.tweerlei.dbgrazer.query.backend.impl;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.tweerlei.common5.collections.builders.MapBuilder;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryErrorKeys;
import de.tweerlei.dbgrazer.query.model.impl.AbstractVisualizationQueryType;

/**
 * Results returned by a subqueries
 * 
 * @author Robert Wruck
 */
@Service
@Order(1010)
public class SVGQueryType extends AbstractVisualizationQueryType
	{
	/** The NAME */
	public static final String NAME = "SVG";
	
	/**
	 * Constructor
	 */
	public SVGQueryType()
		{
		super(NAME, MapBuilder.<String, Class<?>>ordered()
				.put("showSubqueries", Boolean.class)
				.build());
		}
	
	@Override
	public void validate(Query query, Errors errors)
		{
		super.validate(query, errors);
		
		if (query.getSubQueries().size() < 1)
			errors.rejectValue("subQueries", QueryErrorKeys.VIEW_WITH_WRONG_COUNT);
		}
	}
