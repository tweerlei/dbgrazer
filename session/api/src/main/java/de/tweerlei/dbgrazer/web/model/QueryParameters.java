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
package de.tweerlei.dbgrazer.web.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.tweerlei.common5.collections.CollectionUtils;
import de.tweerlei.dbgrazer.query.model.Query;

/**
 * Query with parameters
 * 
 * @author Robert Wruck
 */
public class QueryParameters
	{
	private final Query query;
	private final Map<Integer, String> actualParameters;
	private final List<String> effectiveParameters;
	private final List<String> visibleParameters;
	private final List<String> additionalParameters;
	private final List<String> allParameters;
	
	/**
	 * Constructor
	 * @param query Query
	 * @param actualParameters Actual parameters
	 * @param effectiveParameters Effective parameters
	 * @param visibleParameters Visible parameters
	 * @param additionalParameters Additional parameters
	 */
	public QueryParameters(Query query,
			Map<Integer, String> actualParameters,
			List<String> effectiveParameters,
			List<String> visibleParameters,
			List<String> additionalParameters
			)
		{
		this.query = query;
		this.actualParameters = actualParameters;
		this.effectiveParameters = effectiveParameters;
		this.visibleParameters = visibleParameters;
		this.additionalParameters = additionalParameters;
		this.allParameters = CollectionUtils.concat(effectiveParameters, additionalParameters);
		}

	/**
	 * Constructor
	 * @param query Query
	 */
	public QueryParameters(Query query)
		{
		this(query, Collections.<Integer, String>emptyMap(), Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList());
		}
	
	/**
	 * @return the query
	 */
	public Query getQuery()
		{
		return query;
		}
	
	/**
	 * @return the actualParameters
	 */
	public Map<Integer, String> getActualParameters()
		{
		return actualParameters;
		}
	
	/**
	 * @return the effectiveParameters
	 */
	public List<String> getEffectiveParameters()
		{
		return effectiveParameters;
		}
	
	/**
	 * @return the visibleParameters
	 */
	public List<String> getVisibleParameters()
		{
		return visibleParameters;
		}
	
	/**
	 * @return the additionalParameters
	 */
	public List<String> getAdditionalParameters()
		{
		return additionalParameters;
		}
	
	/**
	 * @return all parameters (effective + additional)
	 */
	public List<String> getAllParameters()
		{
		return (allParameters);
		}
	}
