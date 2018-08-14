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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryCheckResult;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.model.QueryGroupVisitor;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.QueryGroupBuilder;

/**
 * Validate queries
 * 
 * @author Robert Wruck
 */
public class QueryValidator
	{
	private static final String NO_SUBQUERIES = "validation_viewWithoutSubqueries";
	private static final String EMPTY_QUERY_STRING = "validation_emptyQueryString";
	private static final String UNKNOWN_PARAMETER_QUERY = "validation_unknownParameterQuery";
	private static final String PARAMETER_QUERY_NEEDS_PARAMETER = "validation_parameterQueryNeedsParameter";
	private static final String PARAMETER_QUERY_USED_AS_SUBQUERY = "validation_parameterQueryUsedAsSubquery";
	private static final String VIEW_USED_AS_PARAMETER_QUERY = "validation_viewUsedAsParameterQuery";
	private static final String UNKNOWN_SUBQUERY = "validation_unknownSubquery";
	private static final String SUBQUERY_USED_MORE_THAN_ONCE = "validation_subqueryUsedMoreThanOnce";
	private static final String SUBQUERY_ACCEPTS_DIFFERENT_PARAMETERS = "validation_subqueryAcceptsDifferentParameters";
	private static final String QUERY_REFERENCES_SELF = "validation_queryReferencesSelf";
	private static final String VIEW_USED_AS_SUBQUERY = "validation_viewUsedAsSubquery";
	private static final String TARGET_PARAMETER_NOT_ACCEPTED = "validation_targetParameterNotAccepted";
	private static final String UNKNOWN_TARGET_QUERY = "validation_unknownTargetQuery";
	private static final String TARGET_QUERY_ACCEPTS_NO_PARAMETER = "validation_targetQueryAcceptsNoParameter";
	private static final String TARGET_QUERY_USED_AS_SUBQUERY = "validation_targetQueryUsedAsSubquery";
	private static final String DIALECT_QUERY_REFERENCES_SCHEMA_QUERY = "validation_dialectQueryReferencesSchemaQuery";
	private static final String SCHEMA_QUERY_REFERENCES_DIALECT_QUERY = "validation_schemaQueryReferencesDialectQuery";
	private static final String DIALECT_QUERY_REFERENCES_OTHER_DIALECT_QUERY = "validation_dialectQueryReferencesOtherDialectQuery";
	private static final String SUBQUERY_WITH_TOO_MANY_PARAMETERS = "validation_subqueryWithTooManyParameters";
	private static final String SUBQUERY_IN_DIFFERENT_GROUP = "validation_subqueryInDifferentGroup";
	
	private final Map<String, Query> allQueries;
	private final Set<String> params;
	private final Set<String> queries;
	private final Set<String> views;
	private final Set<String> paramlists;
	private final Set<String> lists;
	private final Set<String> listviews;
	private final Set<String> subqueries;
	private final Set<String> actions;
	
	/**
	 * Constructor
	 * @param allQueries All known queries
	 */
	public QueryValidator(Map<String, Query> allQueries)
		{
		this.allQueries = allQueries;
		this.params = new HashSet<String>();
		this.queries = new HashSet<String>();
		this.views = new HashSet<String>();
		this.lists = new HashSet<String>();
		this.paramlists = new HashSet<String>();
		this.listviews = new HashSet<String>();
		this.subqueries = new HashSet<String>();
		this.actions = new HashSet<String>();
		
		final QueryGroupBuilder b = new QueryGroupBuilder(true, false);
		for (Query q : allQueries.values())
			{
			b.add(q);
			
			// Find all possible target parameter names
			if (q.getParameters().size() == 1)
				params.add(q.getParameters().get(0).getName());
			}
		
		// Group queries
		final QueryGroup g = b.build();
		
		final Set<String> views2 = views;
		final Set<String> subqueries2 = subqueries;
		final Set<String> queries2 = queries;
		final Set<String> listviews2 = listviews;
		final Set<String> lists2 = lists;
		final Set<String> paramlists2 = paramlists;
		final Set<String> actions2 = actions;
		g.accept(new QueryGroupVisitor()
			{
			@Override
			public boolean visitView(Query q)
				{
				views2.add(q.getName());
				return false;
				}
			
			@Override
			public boolean visitSubquery(Query q)
				{
				subqueries2.add(q.getName());
				return false;
				}
			
			@Override
			public boolean visitQuery(Query q)
				{
				queries2.add(q.getName());
				return false;
				}
			
			@Override
			public boolean visitListView(Query q)
				{
				listviews2.add(q.getName());
				return false;
				}
			
			@Override
			public boolean visitList(Query q)
				{
				lists2.add(q.getName());
				if (!q.getType().getResultType().isView())
					paramlists2.add(q.getName());
				return false;
				}
			
			@Override
			public boolean visitAction(Query q)
				{
				actions2.add(q.getName());
				return false;
				}
			});
		}
	
	/**
	 * Validate a query
	 * @param q Query
	 * @return Errors
	 */
	public List<QueryCheckResult> validate(Query q)
		{
		final List<QueryCheckResult> l = new ArrayList<QueryCheckResult>();
		
		if (q.getType().getResultType().isView())
			{
			if (q.getSubQueries().isEmpty())
				l.add(new QueryCheckResult(NO_SUBQUERIES, null));
			}
		else
			{
			if (q.getStatement().trim().length() == 0)
				l.add(new QueryCheckResult(EMPTY_QUERY_STRING, null));
			}
		
		for (ParameterDef p : q.getParameters())
			{
			if (!StringUtils.empty(p.getValueQuery()))
				{
				final Query sq = allQueries.get(p.getValueQuery());
				if (sq == null)
					l.add(new QueryCheckResult(UNKNOWN_PARAMETER_QUERY, p.getValueQuery()));
				else if (q.getSourceSchema().isQuerySet())
					{
					if (!sq.getSourceSchema().isQuerySet())
						l.add(new QueryCheckResult(DIALECT_QUERY_REFERENCES_SCHEMA_QUERY, p.getValueQuery()));
					else if (!StringUtils.equals(sq.getSourceSchema().getVersion(), q.getSourceSchema().getVersion()))
						l.add(new QueryCheckResult(DIALECT_QUERY_REFERENCES_OTHER_DIALECT_QUERY, p.getValueQuery()));
					}
				else if (!q.getSourceSchema().isQuerySet() && sq.getSourceSchema().isQuerySet())
					l.add(new QueryCheckResult(SCHEMA_QUERY_REFERENCES_DIALECT_QUERY, p.getValueQuery()));
				
				if (!paramlists.contains(p.getValueQuery()))
					{
					if (queries.contains(p.getValueQuery()) || views.contains(p.getValueQuery()))
						l.add(new QueryCheckResult(PARAMETER_QUERY_NEEDS_PARAMETER, p.getValueQuery()));
					else if (subqueries.contains(p.getValueQuery()))
						l.add(new QueryCheckResult(PARAMETER_QUERY_USED_AS_SUBQUERY, p.getValueQuery()));
					else if (lists.contains(p.getValueQuery()) || listviews.contains(p.getValueQuery()))
						l.add(new QueryCheckResult(VIEW_USED_AS_PARAMETER_QUERY, p.getValueQuery()));
					}
				}
			}
		
		final Set<String> seen = new HashSet<String>();
		for (SubQueryDef sub : q.getSubQueries())
			{
			final String s = sub.getName();
			final Query sq = allQueries.get(s);
			if (sq == null)
				l.add(new QueryCheckResult(UNKNOWN_SUBQUERY, s));
			else if (q.getSourceSchema().isQuerySet())
				{
				if (!sq.getSourceSchema().isQuerySet())
					l.add(new QueryCheckResult(DIALECT_QUERY_REFERENCES_SCHEMA_QUERY, s));
				else if (!StringUtils.equals(sq.getSourceSchema().getVersion(), q.getSourceSchema().getVersion()))
					l.add(new QueryCheckResult(DIALECT_QUERY_REFERENCES_OTHER_DIALECT_QUERY, s));
				}
			else if (!q.getSourceSchema().isQuerySet() && sq.getSourceSchema().isQuerySet())
				l.add(new QueryCheckResult(SCHEMA_QUERY_REFERENCES_DIALECT_QUERY, s));
			
			if (q.getType().getResultType() != ResultType.MULTILEVEL)
				{
				if (seen.contains(s))
					l.add(new QueryCheckResult(SUBQUERY_USED_MORE_THAN_ONCE, s));
				
				if ((sq != null) && (q.getParameters().size() != sq.getParameters().size()))
					l.add(new QueryCheckResult(SUBQUERY_ACCEPTS_DIFFERENT_PARAMETERS, s));
				}
			seen.add(s);
			
			if (StringUtils.equals(s, q.getName()))
				l.add(new QueryCheckResult(QUERY_REFERENCES_SELF, null));
			
			if (views.contains(s) || listviews.contains(s))
				l.add(new QueryCheckResult(VIEW_USED_AS_SUBQUERY, s));
			
			if (sq != null)
				{
				if (sq.getParameters().size() < sub.getParameterValues().size())
					l.add(new QueryCheckResult(SUBQUERY_WITH_TOO_MANY_PARAMETERS, s));
				if (!StringUtils.empty(q.getGroupName()) && !StringUtils.equals(sq.getGroupName(), q.getGroupName()))
					l.add(new QueryCheckResult(SUBQUERY_IN_DIFFERENT_GROUP, s));
				}
			}
		
		for (TargetDef t : q.getTargetQueries().values())
			{
			if (t.isParameter())
				{
				if (!params.contains(t.getParameterName()))
					l.add(new QueryCheckResult(TARGET_PARAMETER_NOT_ACCEPTED, t.getParameterName()));
				}
			else
				{
				final Query sq = allQueries.get(t.getQueryName());
				if (sq == null)
					l.add(new QueryCheckResult(UNKNOWN_TARGET_QUERY, t.getQueryName()));
				else if (q.getSourceSchema().isQuerySet())
					{
					if (!sq.getSourceSchema().isQuerySet())
						l.add(new QueryCheckResult(DIALECT_QUERY_REFERENCES_SCHEMA_QUERY, t.getQueryName()));
					else if (!StringUtils.equals(sq.getSourceSchema().getVersion(), q.getSourceSchema().getVersion()))
						l.add(new QueryCheckResult(DIALECT_QUERY_REFERENCES_OTHER_DIALECT_QUERY, t.getQueryName()));
					}
				else if (!q.getSourceSchema().isQuerySet() && sq.getSourceSchema().isQuerySet())
					l.add(new QueryCheckResult(SCHEMA_QUERY_REFERENCES_DIALECT_QUERY, t.getQueryName()));
				
				if (lists.contains(t.getQueryName()) || listviews.contains(t.getQueryName()))
					l.add(new QueryCheckResult(TARGET_QUERY_ACCEPTS_NO_PARAMETER, t.getQueryName()));
				else if (subqueries.contains(t.getQueryName()))
					l.add(new QueryCheckResult(TARGET_QUERY_USED_AS_SUBQUERY, t.getQueryName()));
				}
			}
		
		return (l);
		}
	}
