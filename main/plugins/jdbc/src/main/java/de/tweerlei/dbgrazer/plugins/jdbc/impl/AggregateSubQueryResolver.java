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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.ListBuilder;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.AggregateColumn;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.AggregationMode;
import de.tweerlei.dbgrazer.plugins.jdbc.types.MultipleQueryType;
import de.tweerlei.dbgrazer.plugins.jdbc.types.QueryTypeAttributes;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.SubQueryInfo;
import de.tweerlei.dbgrazer.query.model.SubQueryResolver;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;

/**
 * Create subqueries by aggregating the original query
 * 
 * @author Robert Wruck
 */
@Service
public class AggregateSubQueryResolver implements SubQueryResolver
	{
	private static final class AggregateFunction
		{
		private final Pattern pattern;
		private final AggregationMode mode;
		
		public AggregateFunction(String pattern, AggregationMode mode)
			{
			this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			this.mode = mode;
			}
		
		public AggregateColumn extractColumn(String s)
			{
			final Matcher m = pattern.matcher(s);
			if (m.matches())
				return (new AggregateColumn(m.group(1), null, mode));
			
			return (null);
			}
		}
	
	private static final List<AggregateFunction> AGGREGATE_FUNCTIONS = new ListBuilder<AggregateFunction>()
			.add(new AggregateFunction("COUNT\\((\\w+)\\)", AggregationMode.COUNT))
			.add(new AggregateFunction("COUNT\\(DISTINCT (\\w+)\\)", AggregationMode.COUNT_DISTINCT))
			.add(new AggregateFunction("MIN\\((\\w+)\\)", AggregationMode.MIN))
			.add(new AggregateFunction("MAX\\((\\w+)\\)", AggregationMode.MAX))
			.add(new AggregateFunction("SUM\\((\\w+)\\)", AggregationMode.SUM))
			.add(new AggregateFunction("AVG\\((\\w+)\\)", AggregationMode.AVG))
			.buildReadOnly();
	
	private static final String SUM_TAB = "$sumTab";
	
	private final KeywordService keywordService;
	private final SQLGeneratorService sqlGenerator;
	private final QueryType defaultQueryType;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 * @param sqlGenerator SQLGeneratorService
	 * @param defaultQueryType Default query type for generated subqueries
	 */
	@Autowired
	public AggregateSubQueryResolver(KeywordService keywordService, SQLGeneratorService sqlGenerator, MultipleQueryType defaultQueryType)
		{
		this.keywordService = keywordService;
		this.sqlGenerator = sqlGenerator;
		this.defaultQueryType = defaultQueryType;
		}
	
	@Override
	public List<SubQueryInfo> resolve(Query mainQuery, List<Object> params, List<SubQueryInfo> subQueries, List<SubQueryInfo> targetQueries)
		{
		final List<String> dims = keywordService.extractWords(mainQuery.getAttributes().get(QueryTypeAttributes.ATTR_DIMENSIONS));
		final List<String> extras = keywordService.extractWords(mainQuery.getAttributes().get(QueryTypeAttributes.ATTR_RESULTS));
		final int n = mainQuery.getParameters().size();
		final List<Object> baseParams = params.subList(0, n);
		
		final List<AggregateColumn> columns = new ArrayList<AggregateColumn>(dims.size() + extras.size());
		int i = 0;
		boolean free = false;
		for (String dim : dims)
			{
			final Object p = (n + i < params.size()) ? params.get(n + i) : null;
			if (p == null)
				{
				free = true;
				columns.add(new AggregateColumn(dim, null, AggregationMode.NONE));
				}
			else
				columns.add(new AggregateColumn(dim, p.toString(), AggregationMode.HIDDEN));
			i++;
			}
		for (String extra : extras)
			{
			for (AggregateFunction f : AGGREGATE_FUNCTIONS)
				{
				final AggregateColumn c = f.extractColumn(extra);
				if (c != null)
					columns.add(c);
				}
			}
		
		final List<SubQueryInfo> ret = new ArrayList<SubQueryInfo>(i + 1);
		if (free)
			{
			// There are free dimensions, run a subquery for each
			i = 0;
			for (AggregateColumn c : columns)
				{
				if (c.getMode() == AggregationMode.NONE)
					{
					final List<AggregateColumn> cols = new ArrayList<AggregateColumn>(columns.size());
					final List<Object> qParams = new ArrayList<Object>(dims.size() + n);
					qParams.addAll(baseParams);
					cols.add(c);
					for (AggregateColumn c2 : columns)
						{
						if (c2.getMode() != AggregationMode.NONE)
							{
							cols.add(c2);
							if (!StringUtils.empty(c2.getCondition()))	// Empty condition will generate IS NULL, so no param needed
								qParams.add(c2.getCondition());
							}
						}
					
					final String q = sqlGenerator.createVariableAggregateQuery(mainQuery.getStatement(), cols);
					
					final Query subQuery = new QueryImpl(mainQuery.getName(), mainQuery.getSourceSchema(), mainQuery.getGroupName(), q, null, defaultQueryType, mainQuery.getParameters(),
							Collections.<Integer, TargetDef>emptyMap(), Collections.<String, String>singletonMap(QueryTypeAttributes.ATTR_DIMENSION, String.valueOf(i + n)));
					
					ret.add(new SubQueryInfo(subQuery, c.getName(), null, null, qParams));
					}
				i++;
				}
			
			// Add a COUNT query over all HIDDEN columns
			final List<AggregateColumn> cols = new ArrayList<AggregateColumn>(columns.size());
			final List<Object> qParams = new ArrayList<Object>(dims.size() + n);
			qParams.addAll(baseParams);
			for (AggregateColumn c2 : columns)
				{
				if (c2.getMode() != AggregationMode.NONE)
					{
					cols.add(c2);
					if (!StringUtils.empty(c2.getCondition()))	// Empty condition will generate IS NULL, so no param needed
						qParams.add(c2.getCondition());
					}
				}
			
			final String q = sqlGenerator.createVariableAggregateQuery(mainQuery.getStatement(), cols);
			
			final Query subQuery = new QueryImpl(mainQuery.getName(), mainQuery.getSourceSchema(), mainQuery.getGroupName(), q, null, defaultQueryType, mainQuery.getParameters(),
					Collections.<Integer, TargetDef>emptyMap(), Collections.<String, String>emptyMap());
			
			ret.add(new SubQueryInfo(subQuery, SUM_TAB, null, null, qParams));
			}
		else
			{
			// There are no free dimensions
			if (!targetQueries.isEmpty())
				{
				// Run the first target query with all parameters
				final Query tq = targetQueries.get(0).getQuery();
				if (params.size() >= tq.getParameters().size())
					{
					final List<Object> qParams = new ArrayList<Object>(tq.getParameters().size());
					for (Object v : params.subList(0, tq.getParameters().size()))
						{
						if (v == SubQueryInfo.IS_NULL)
							qParams.add(null);
						else
							qParams.add(v);
						}
					ret.add(new SubQueryInfo(tq, tq.getName(), null, null, qParams));
					return (ret);
					}
				}
			
			// Create a single COUNT query
			final List<Object> qParams = new ArrayList<Object>(dims.size() + n);
			qParams.addAll(baseParams);
			for (AggregateColumn c2 : columns)
				{
				if (!StringUtils.empty(c2.getCondition()))	// Empty condition will generate IS NULL, so no param needed
					qParams.add(c2.getCondition());
				}
			
			final String q = sqlGenerator.createVariableAggregateQuery(mainQuery.getStatement(), columns);
			
			final Query subQuery = new QueryImpl(mainQuery.getName(), mainQuery.getSourceSchema(), mainQuery.getGroupName(), q, null, defaultQueryType, mainQuery.getParameters(),
					Collections.<Integer, TargetDef>emptyMap(), Collections.<String, String>emptyMap());
			
			ret.add(new SubQueryInfo(subQuery, SUM_TAB, null, null, qParams));
			}
		
		return (ret);
		}
	
	@Override
	public List<ParameterDef> getAdditionalParameters(Query mainQuery)
		{
		final List<String> dims = keywordService.extractWords(mainQuery.getAttributes().get(QueryTypeAttributes.ATTR_DIMENSIONS));
		final List<ParameterDef> ret = new ArrayList<ParameterDef>(dims.size());
		
		for (String dim : dims)
			ret.add(new ParameterDefImpl(dim, ColumnType.STRING, null));
		
		return (ret);
		}
	}
