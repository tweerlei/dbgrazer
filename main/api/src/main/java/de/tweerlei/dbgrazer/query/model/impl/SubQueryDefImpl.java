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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tweerlei.dbgrazer.query.model.SubQueryDef;

/**
 * Query impl.
 * 
 * @author Robert Wruck
 */
public class SubQueryDefImpl implements SubQueryDef
	{
	private final String name;
	private final List<String> parameters;
	
	/**
	 * Constructor
	 * @param name Query name
	 * @param parameters Parameter values
	 */
	public SubQueryDefImpl(String name, List<String> parameters)
		{
		this.name = name;
		this.parameters = (parameters == null) ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<String>(parameters));
		}
	
	@Override
	public String getName()
		{
		return name;
		}
	
	@Override
	public List<String> getParameterValues()
		{
		return parameters;
		}
	}
