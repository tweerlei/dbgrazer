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

import de.tweerlei.dbgrazer.query.model.impl.AbstractMultilevelQueryType;

/**
 * Results returned by a subquery per level
 * 
 * @author Robert Wruck
 */
@Service
@Order(1006)
public class MultilevelQueryType extends AbstractMultilevelQueryType
	{
	/** The NAME */
	public static final String NAME = "MULTILEVEL";
	
	private final Map<String, Class<?>> attributes;
	
	/**
	 * Constructor
	 */
	public MultilevelQueryType()
		{
		super(NAME);
		
		final Map<String, Class<?>> m = new LinkedHashMap<String, Class<?>>();
		m.put("hideId", Boolean.class);
		attributes = Collections.unmodifiableMap(m);
		}
	
	@Override
	public boolean isExplorer()
		{
		return true;
		}
	
	@Override
	public Map<String, Class<?>> getSupportedAttributes()
		{
		return (attributes);
		}
	}
