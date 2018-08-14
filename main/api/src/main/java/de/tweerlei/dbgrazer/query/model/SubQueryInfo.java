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
package de.tweerlei.dbgrazer.query.model;

import java.util.List;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;

/**
 * Information for running a subquery
 * 
 * @author Robert Wruck
 */
public class SubQueryInfo
	{
	/** Placeholder for a query parameter that is present but null */
	public static final Object IS_NULL = new NamedBase("");
	
	private final Query query;
	private final String label;
	private final String suffix;
	private final List<String> curried;
	private final List<Object> params;
	
	/**
	 * Constructor
	 * @param query Query
	 * @param label Label
	 * @param suffix Label suffix
	 * @param curried Curried extra parameters
	 * @param params Effective parameters
	 */
	public SubQueryInfo(Query query, String label, String suffix, List<String> curried, List<Object> params)
		{
		this.query = query;
		this.label = label;
		this.suffix = suffix;
		this.curried = curried;
		this.params = params;
		}

	/**
	 * Get the query
	 * @return the query
	 */
	public Query getQuery()
		{
		return query;
		}

	/**
	 * Get the label
	 * @return the label
	 */
	public String getLabel()
		{
		return label;
		}

	/**
	 * Get the suffix
	 * @return the suffix
	 */
	public String getSuffix()
		{
		return suffix;
		}

	/**
	 * Get the curried
	 * @return the curried
	 */
	public List<String> getCurried()
		{
		return curried;
		}

	/**
	 * Get the params
	 * @return the params
	 */
	public List<Object> getParams()
		{
		return params;
		}
	}
