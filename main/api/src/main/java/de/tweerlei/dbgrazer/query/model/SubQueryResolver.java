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

/**
 * Resolve subqueries
 * 
 * @author Robert Wruck
 */
public interface SubQueryResolver
	{
	/**
	 * Resolve subqueries
	 * @param mainQuery Main query
	 * @param params Query parameters
	 * @param subQueries Loaded subqueries
	 * @param targetQueries Loaded target queries
	 * @return Resolved subqueries
	 */
	public List<SubQueryInfo> resolve(Query mainQuery, List<Object> params, List<SubQueryInfo> subQueries, List<SubQueryInfo> targetQueries);
	
	/**
	 * Get the additional parameters used to resolve subqueries
	 * @param mainQuery Main query
	 * @return Parameter definitions ADDITIONAL to mainQuery.getParameters
	 */
	public List<ParameterDef> getAdditionalParameters(Query mainQuery);
	}
