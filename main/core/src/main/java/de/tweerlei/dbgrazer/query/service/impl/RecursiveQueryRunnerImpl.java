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
package de.tweerlei.dbgrazer.query.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.exception.PerformQueryException;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.SubQueryInfo;
import de.tweerlei.dbgrazer.query.model.SubQueryResolver;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ResultImpl;
import de.tweerlei.dbgrazer.query.model.impl.RowSetImpl;
import de.tweerlei.dbgrazer.query.service.QueryRunnerService;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.query.service.RecursiveQueryRunnerService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class RecursiveQueryRunnerImpl implements RecursiveQueryRunnerService
	{
	private final QueryService queryService;
	private final QueryRunnerService runner;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param runner QueryRunnerService
	 */
	@Autowired
	public RecursiveQueryRunnerImpl(QueryService queryService, QueryRunnerService runner)
		{
		this.queryService = queryService;
		this.runner = runner;
		}
	
	@Override
	public Map<String, Result> performRecursiveQuery(String link, Query query, List<Object> params, TimeZone timeZone, int level, int limit, boolean showEmpty) throws PerformQueryException
		{
		final Map<String, Result> ret = new LinkedHashMap<String, Result>(query.getSubQueries().size());
		
		if (query.getType().getResultType() != ResultType.RECURSIVE)
			ret.put(query.getName(), performQuery(link, query, params, timeZone, limit, showEmpty));
		else
			{
		for (SubQueryInfo q : getSubQueries(link, query, params))
			{
				// TODO: Nested RECURSIVE queries should be split into separate Results, too
			final Result r = recurse(link, q.getQuery(), 0, q.getSuffix(), q.getCurried(), q.getParams(), timeZone, level - 1, limit, showEmpty);
			if (!showEmpty && r.getRowSets().isEmpty())
				continue;
			
			prepareResult(r);
			ret.put(getTitle(q.getLabel(), q.getSuffix()), r);
			}
			}
		
		if (ret.isEmpty())
			{
			// Return at least one Result with an empty RowSet
			final Result r = new ResultImpl(query);
			prepareResult(r);
			ret.put(query.getName(), r);
			}
		
		return (ret);
		}
	
	@Override
	public Result performQuery(String link, Query query, List<Object> params, TimeZone timeZone, int limit, boolean showEmpty) throws PerformQueryException
		{
		// Don't skip empty subqueries for visualization queries
		// Allow one level of subqueries for views
		final Result r = recurse(link, query, 0, null, null, params, timeZone, hasSubqueries(query) ? 1 : 0, limit, showEmpty || (query.getType().getResultType() == ResultType.VISUALIZATION));
		prepareResult(r);
		return (r);
		}
	
	private Result recurse(String link, Query query, int subQueryIndex, String suffix, List<String> curried, List<Object> params, TimeZone timeZone, int level, int limit, boolean showEmpty) throws PerformQueryException
		{
		if (level < 0)
			{
//			throw new QueryException(query.getName(), null, new RuntimeException("Deep recursion"));
			return new ResultImpl(query);
			}
		
		// Don't limit visualized data
		final int effectiveLimit;
		if (query.getType().getResultType() == ResultType.VISUALIZATION)
			effectiveLimit = Integer.MAX_VALUE;
		else
			effectiveLimit = limit;
		
		if (!hasSubqueries(query))
			{
			final Result r = runner.performQuery(link, query, subQueryIndex, params, timeZone, effectiveLimit, null);
			if (suffix == null)
				return (r);
			
			final Result mapped = new ResultImpl(r.getQuery());
			for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
				{
				ent.getValue().getParameterValues().addAll(curried);
				mapped.getRowSets().put(getTitle(ent.getKey(), suffix), ent.getValue());
				}
			return (mapped);
			}
		
		final Result r = new ResultImpl(query);
		if (curried != null)
			r.getParameterValues().addAll(curried);
		
		int base = subQueryIndex;
		for (SubQueryInfo q : getSubQueries(link, query, params))
			{
			final Result tmp = recurse(link, q.getQuery(), base, q.getSuffix(), q.getCurried(), q.getParams(), timeZone, level - 1, effectiveLimit, showEmpty);
			for (Map.Entry<String, RowSet> ent : tmp.getRowSets().entrySet())
				{
				if (showEmpty || !ent.getValue().getRows().isEmpty())
					{
					if (q.getQuery().getType().getMapMode() == ResultMapMode.SINGLE)
						{
						r.getRowSets().put(getTitle(q.getLabel(), q.getSuffix()), ent.getValue());
						break;
						}
					else
						r.getRowSets().put(ent.getKey(), ent.getValue());
					base++;
					}
				}
			}
		
		return (r);
		}
	
	private boolean hasSubqueries(Query q)
		{
		return (q.getType().getResultType().isView() || (q.getType().getSubQueryResolver() != null));
		}
	
	private List<SubQueryInfo> getSubQueries(String link, Query query, List<Object> params)
		{
		final List<SubQueryInfo> ret = new ArrayList<SubQueryInfo>(query.getSubQueries().size());
		
		boolean first = true;
		for (SubQueryDef s : query.getSubQueries())
			{
			if (first)
				first = false;
			else if (query.getType().isExplorer() || (query.getType().getResultType() == ResultType.MULTILEVEL))
				{
				// For multilevel and explorer queries, only fetch 1st level
				break;
				}
			
			final Query q = queryService.findQueryByName(link, s.getName());
			if (q != null)
				ret.add(new SubQueryInfo(q, null, null, s.getParameterValues(), null));
			}
		
		final List<SubQueryInfo> trg;
		if (ret.isEmpty())
			{
			trg = new ArrayList<SubQueryInfo>(query.getTargetQueries().size());
			for (TargetDef t : query.getTargetQueries().values())
				{
				if (!t.isParameter())
					{
					final Query q = queryService.findQueryByName(link, t.getQueryName());
					if (q != null)
						trg.add(new SubQueryInfo(q, null, null, null, null));
					}
				}
			}
		else
			trg = Collections.<SubQueryInfo>emptyList();
		
		final SubQueryResolver res = query.getType().getSubQueryResolver();
		if (res == null)
			return (DefaultSubQueryResolver.INSTANCE.resolve(query, params, ret, trg));
		else
			return (res.resolve(query, params, ret, trg));
		}
	
	private String getTitle(String name, String suffix)
		{
		if (suffix == null)
			return (name);
		
		return (name + ": " + suffix);
		}
	
	private void prepareResult(Result r)
		{
		if (r.getRowSets().isEmpty())
			{
			// Return at least one empty RowSet
			r.getRowSets().put(r.getQuery().getName(), new RowSetImpl(r.getQuery(), 0, null));
			}
		}
	}
