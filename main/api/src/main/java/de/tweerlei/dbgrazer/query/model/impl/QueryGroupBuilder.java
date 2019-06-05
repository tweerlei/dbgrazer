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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.model.ResultType;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;

/**
 * Helper class for building a QueryGroup
 * 
 * @author Robert Wruck
 */
public class QueryGroupBuilder
	{
	private final boolean listviews;
	private final boolean valuequeries;
	private final QueryGroup group;
	private final Set<String> referenced;
	
	/**
	 * Constructor
	 * @param listviews separate listviews from ordinary lists
	 * @param valuequeries separate referenced value queries from ordinary lists
	 */
	public QueryGroupBuilder(boolean listviews, boolean valuequeries)
		{
		this.listviews = listviews;
		this.valuequeries = valuequeries;
		this.group = new QueryGroup();
		this.referenced = new HashSet<String>();
		}
	
	/**
	 * Add a query
	 * @param q Query
	 */
	public void add(Query q)
		{
		if (q.getType().isManipulation())
			group.getActions().add(q);
		else if (q.getParameters().isEmpty())
			{
			if (listviews && (q.getType().getResultType() == ResultType.RECURSIVE))
				group.getListViews().add(q);	// recursive views that don't take parameters
			else
				group.getLists().add(q);	// views and queries that don't take parameters
			}
		else
			{
			if (q.getType().getResultType() == ResultType.RECURSIVE)
				group.getViews().add(q);	// recursive views that take parameters
			else
				group.getQueries().add(q);	// queries and non-recursive views (chart, graph, tree) that take parameters
			}
		
		addRefs(q);
		}
	
	/**
	 * Only add references from the given query, not the query itself
	 * @param q Query
	 */
	public void addRefs(Query q)
		{
		for (SubQueryDef sq : q.getSubQueries())
			referenced.add(sq.getName());
		
		if (valuequeries)
			{
			for (ParameterDef p : q.getParameters())
				{
				if (!StringUtils.empty(p.getValueQuery()))
					referenced.add(p.getValueQuery());
				}
			}
		}
	
	/**
	 * Build the group
	 * @return QueryGroup
	 */
	public QueryGroup build()
		{
		if (!referenced.isEmpty())
			{
			// Move referenced queries to subqueries
			final Set<String> rq = referenced;
			final List<Query> sq = new ArrayList<Query>(referenced.size());
			group.accept(new SimpleQueryGroupVisitor()
				{
				@Override
				public boolean visitQuery(Query q)
					{
					if (rq.contains(q.getName()))
						{
						sq.add(q);
						return (true);
						}
					return (false);
					}
				});
			
			group.getSubqueries().addAll(sq);
			}
		
		return (group);
		}
	}
