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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Group of queries
 * 
 * @author Robert Wruck
 */
public final class QueryGroup
	{
	private final List<Query> lists;		// take no parameters and are not recursive
	private final List<Query> queries;		// take parameters and are not recursive
	private final List<Query> views;		// take parameters and are recursive
	private final List<Query> listviews;	// take no parameters and are recursive
	private final List<Query> subqueries;	// are referenced as subquery (and should take parameters)
	private final List<Query> actions;		// manipulate data
	
	/**
	 * Constructor
	 */
	public QueryGroup()
		{
		this.queries = new LinkedList<Query>();
		this.views = new LinkedList<Query>();
		this.lists = new LinkedList<Query>();
		this.listviews = new LinkedList<Query>();
		this.subqueries = new LinkedList<Query>();
		this.actions = new LinkedList<Query>();
		}
	
	/**
	 * Check whether this QueryGroup is empty
	 * @return true if empty
	 */
	public boolean isEmpty()
		{
		return (queries.isEmpty() && views.isEmpty() && lists.isEmpty() && listviews.isEmpty() && subqueries.isEmpty() && actions.isEmpty());
		}
	
	/**
	 * Accept a QueryGroupVisitor
	 * @param v QueryGroupVisitor
	 */
	public void accept(QueryGroupVisitor v)
		{
		for (Iterator<Query> i = lists.iterator(); i.hasNext(); )
			{
			final Query q = i.next();
			if (v.visitList(q))
				i.remove();
			}
		for (Iterator<Query> i = queries.iterator(); i.hasNext(); )
			{
			final Query q = i.next();
			if (v.visitQuery(q))
				i.remove();
			}
		for (Iterator<Query> i = views.iterator(); i.hasNext(); )
			{
			final Query q = i.next();
			if (v.visitView(q))
				i.remove();
			}
		for (Iterator<Query> i = listviews.iterator(); i.hasNext(); )
			{
			final Query q = i.next();
			if (v.visitListView(q))
				i.remove();
			}
		for (Iterator<Query> i = subqueries.iterator(); i.hasNext(); )
			{
			final Query q = i.next();
			if (v.visitSubquery(q))
				i.remove();
			}
		for (Iterator<Query> i = actions.iterator(); i.hasNext(); )
			{
			final Query q = i.next();
			if (v.visitAction(q))
				i.remove();
			}
		}
	
	/**
	 * Get the queries
	 * @return the queries
	 */
	public List<Query> getQueries()
		{
		return queries;
		}

	/**
	 * Get the views
	 * @return the views
	 */
	public List<Query> getViews()
		{
		return views;
		}

	/**
	 * Get the lists
	 * @return the lists
	 */
	public List<Query> getLists()
		{
		return lists;
		}

	/**
	 * Get the listviews
	 * @return the listviews
	 */
	public List<Query> getListViews()
		{
		return listviews;
		}

	/**
	 * Get the subqueries
	 * @return the subqueries
	 */
	public List<Query> getSubqueries()
		{
		return subqueries;
		}

	/**
	 * Get the subqueries
	 * @return the subqueries
	 */
	public List<Query> getActions()
		{
		return actions;
		}
	}
