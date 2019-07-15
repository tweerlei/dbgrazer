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
package de.tweerlei.dbgrazer.query.service;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;

/**
 * Perform recursive queries
 * 
 * @author Robert Wruck
 */
public interface RecursiveQueryRunnerService
	{
	/**
	 * Perform a recursive query, returning a separate Result per direct subquery
	 * @param link Link name
	 * @param query Query
	 * @param params Parameters
	 * @param timeZone TimeZone to use for temporal results
	 * @param level Maximum recursion depth (0 = top level query only)
	 * @param limit Fetch limit
	 * @param showEmpty Return empty results
	 * @return A single result if the query is not recursive, otherwise a result per immediate subquery but at least a single empty Result
	 * @throws PerformQueryException on error
	 */
	public Map<String, Result> performRecursiveQuery(String link, Query query, List<Object> params, TimeZone timeZone, int level, int limit, boolean showEmpty) throws PerformQueryException;
	
	/**
	 * Perform a non-recursive query, returning a single Result
	 * @param link Link name
	 * @param query Query
	 * @param params Parameters
	 * @param timeZone TimeZone to use for temporal results
	 * @param limit Fetch limit
	 * @param showEmpty Return empty results
	 * @return Result
	 * @throws PerformQueryException on error
	 */
	public Result performQuery(String link, Query query, List<Object> params, TimeZone timeZone, int limit, boolean showEmpty) throws PerformQueryException;
	}
