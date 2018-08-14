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

import de.tweerlei.dbgrazer.query.model.impl.AbstractVisualizationQueryType;
import de.tweerlei.dbgrazer.visualization.service.ChartScaling;
import de.tweerlei.dbgrazer.visualization.service.ChartType;

/**
 * Results returned by a subquery per data row
 * 
 * @author Robert Wruck
 */
@Service
@Order(1008)
public class ChartQueryType extends AbstractVisualizationQueryType
	{
	/** The NAME */
	public static final String NAME = "CHART";
	
	private final Map<String, Class<?>> attributes;
	
	/**
	 * Constructor
	 */
	public ChartQueryType()
		{
		super(NAME);
		
		final Map<String, Class<?>> m = new LinkedHashMap<String, Class<?>>();
		m.put(ChartType.class.getSimpleName(), ChartType.class);
		m.put(ChartScaling.class.getSimpleName(), ChartScaling.class);
		attributes = Collections.unmodifiableMap(m);
		}
	
	@Override
	public Map<String, Class<?>> getSupportedAttributes()
		{
		return (attributes);
		}
	}
