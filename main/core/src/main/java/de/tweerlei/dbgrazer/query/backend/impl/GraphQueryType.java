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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryErrorKeys;
import de.tweerlei.dbgrazer.query.model.impl.AbstractVisualizationQueryType;
import de.tweerlei.dbgrazer.visualization.service.GraphType;

/**
 * Results returned by 2 subqueries: Nodes and Edges
 * 
 * @author Robert Wruck
 */
@Service
@Order(1007)
public class GraphQueryType extends AbstractVisualizationQueryType
	{
	/** The NAME */
	public static final String NAME = "GRAPH";
	
	private final Map<String, Class<?>> attributes;
	
	/**
	 * Constructor
	 */
	public GraphQueryType()
		{
		super(NAME);
		
		final Map<String, Class<?>> m = new LinkedHashMap<String, Class<?>>();
		m.put(GraphType.class.getSimpleName(), GraphType.class);
		attributes = Collections.unmodifiableMap(m);
		}
	
	@Override
	public Map<String, Class<?>> getSupportedAttributes()
		{
		return (attributes);
		}
	
	@Override
	public void validate(Query query, Errors errors)
		{
		super.validate(query, errors);
		
		if (query.getSubQueries().size() < 2)
			errors.rejectValue("subQueries", QueryErrorKeys.VIEW_WITH_WRONG_COUNT);
		}
	}
