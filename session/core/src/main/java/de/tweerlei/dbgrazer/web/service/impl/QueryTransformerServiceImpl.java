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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.func.predicate.Predicate;
import de.tweerlei.common5.func.unary.UnaryFunction;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.model.QueryGroupVisitor;
import de.tweerlei.dbgrazer.query.model.impl.SimpleQueryGroupVisitor;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.dbgrazer.visualization.model.GraphEdge;
import de.tweerlei.dbgrazer.visualization.model.GraphNode;
import de.tweerlei.dbgrazer.visualization.service.GraphBuilder;
import de.tweerlei.dbgrazer.visualization.service.GraphStyle;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.QueryTransformerService;

/**
 * Transform query meta data
 * 
 * @author Robert Wruck
 */
@Service
public class QueryTransformerServiceImpl implements QueryTransformerService
	{
	private static abstract class SplittingQueryGroupVisitor implements QueryGroupVisitor
		{
		private final SortedMap<String, QueryGroup> groups;
		
		protected SplittingQueryGroupVisitor()
			{
			this.groups = new TreeMap<String, QueryGroup>();
			}
		
		public final SortedMap<String, QueryGroup> getGroups()
			{
			return (groups);
			}
		
		protected final QueryGroup getQueryGroup(String groupName)
			{
			QueryGroup g = groups.get(groupName);
			if (g == null)
				{
				g = new QueryGroup();
				groups.put(groupName, g);
				}
			return (g);
			}
		}
	
	private final QueryService queryService;
	private final GraphBuilder graphBuilder;
	private final GraphStyle graphStyle;
	
	/**
	 * Constructor
	 * @param queryService QueryService
	 * @param graphBuilder GraphBuilder
	 * @param graphStyle GraphStyle
	 */
	@Autowired
	public QueryTransformerServiceImpl(QueryService queryService, GraphBuilder graphBuilder, GraphStyle graphStyle)
		{
		this.queryService = queryService;
		this.graphBuilder = graphBuilder;
		this.graphStyle = graphStyle;
		}
	
	@Override
	public void filterQueryGroup(QueryGroup group, final Predicate<Query> pred)
		{
		group.accept(new SimpleQueryGroupVisitor()
			{
			@Override
			public boolean visitQuery(Query q)
				{
				return !pred.evaluate(q);
				}
			});
		}
	
	@Override
	public SortedMap<String, QueryGroup> splitQueryGroup(QueryGroup group, final UnaryFunction<Query, String> func)
		{
		final SplittingQueryGroupVisitor v = new SplittingQueryGroupVisitor()
			{
			@Override
			public boolean visitView(Query q)
				{
				final String key = func.applyTo(q);
				if (key != null)
					getQueryGroup(key).getViews().add(q);
				return false;
				}
			
			@Override
			public boolean visitSubquery(Query q)
				{
				final String key = func.applyTo(q);
				if (key != null)
					getQueryGroup(key).getSubqueries().add(q);
				return false;
				}
			
			@Override
			public boolean visitQuery(Query q)
				{
				final String key = func.applyTo(q);
				if (key != null)
					getQueryGroup(key).getQueries().add(q);
				return false;
				}
			
			@Override
			public boolean visitListView(Query q)
				{
				final String key = func.applyTo(q);
				if (key != null)
					getQueryGroup(key).getListViews().add(q);
				return false;
				}
			
			@Override
			public boolean visitList(Query q)
				{
				final String key = func.applyTo(q);
				if (key != null)
					getQueryGroup(key).getLists().add(q);
				return false;
				}
			
			@Override
			public boolean visitAction(Query q)
				{
				final String key = func.applyTo(q);
				if (key != null)
					getQueryGroup(key).getActions().add(q);
				return false;
				}
			};
		
		group.accept(v);
		
		return (v.getGroups());
		}
	
	@Override
	public SortedMap<String, QueryGroup> splitQueryGroupMulti(QueryGroup group, final UnaryFunction<Query, Set<String>> func)
		{
		final SplittingQueryGroupVisitor v = new SplittingQueryGroupVisitor()
			{
			@Override
			public boolean visitView(Query q)
				{
				final Set<String> keys = func.applyTo(q);
				if (keys != null)
					{
					for (String key : keys)
						getQueryGroup(key).getViews().add(q);
					}
				return false;
				}
			
			@Override
			public boolean visitSubquery(Query q)
				{
				final Set<String> keys = func.applyTo(q);
				if (keys != null)
					{
					for (String key : keys)
						getQueryGroup(key).getSubqueries().add(q);
					}
				return false;
				}
			
			@Override
			public boolean visitQuery(Query q)
				{
				final Set<String> keys = func.applyTo(q);
				if (keys != null)
					{
					for (String key : keys)
						getQueryGroup(key).getQueries().add(q);
					}
				return false;
				}
			
			@Override
			public boolean visitListView(Query q)
				{
				final Set<String> keys = func.applyTo(q);
				if (keys != null)
					{
					for (String key : keys)
						getQueryGroup(key).getListViews().add(q);
					}
				return false;
				}
			
			@Override
			public boolean visitList(Query q)
				{
				final Set<String> keys = func.applyTo(q);
				if (keys != null)
					{
					for (String key : keys)
						getQueryGroup(key).getLists().add(q);
					}
				return false;
				}
			
			@Override
			public boolean visitAction(Query q)
				{
				final Set<String> keys = func.applyTo(q);
				if (keys != null)
					{
					for (String key : keys)
						getQueryGroup(key).getActions().add(q);
					}
				return false;
				}
			};
		
		group.accept(v);
		
		return (v.getGroups());
		}
	
	@Override
	public Visualization buildGraph(String link, String query, String name, String nodeLink)
		{
		final Map<String, Integer> inMap = new HashMap<String, Integer>();
		final Map<String, Integer> outMap = new HashMap<String, Integer>();
		final Set<GraphNode> nodeSet = new HashSet<GraphNode>();
		final Set<GraphEdge> edgeSet = new HashSet<GraphEdge>();
		
		final String mainTableStyle = "color=\"" + graphStyle.getHighlightForegroundColor() + "\",fillcolor=\"" + graphStyle.getHighlightBackgroundColor() + "\",fontcolor=\"" + graphStyle.getHighlightForegroundColor() + "\"";
		
		int self = 0;
		int i = 0;
		
		// Add all referenced queries
		for (Query q : queryService.findReferencedQueries(link, query, false))
			{
			if (!outMap.containsKey(q.getName()))
				{
				outMap.put(q.getName(), i);
				if (q.getName().equals(query))
					{
					self = i;
					nodeSet.add(new GraphNode(String.valueOf(i), writeNode(q), null, mainTableStyle));
					}
				else
					nodeSet.add(new GraphNode(String.valueOf(i), writeNode(q), null, "URL=\"" + nodeLink + q.getName() + "\""));
				i++;
				}
			}
		for (Query q : queryService.findReferencingQueries(link, query))
			{
			if (!outMap.containsKey(q.getName()) && !inMap.containsKey(q.getName()))
				{
				inMap.put(q.getName(), i);
				nodeSet.add(new GraphNode(String.valueOf(i), writeNode(q), null, "URL=\"" + nodeLink + q.getName() + "\""));
				i++;
				}
			}
		
		for (Integer ix : outMap.values())
			{
			if (ix.intValue() != self)
				edgeSet.add(new GraphEdge(String.valueOf(self), ix.toString(), null, null));
			}
		for (Integer ix : inMap.values())
			edgeSet.add(new GraphEdge(ix.toString(), String.valueOf(self), null, null));
		
		return (new Visualization(VisualizationSettings.GRAPH_QUERY_TYPE, name,
				graphBuilder.buildGraph(name, graphBuilder.getGraphType(VisualizationSettings.ERM_GRAPH_TYPE), graphStyle, null, null, null, nodeSet, edgeSet, null)));
		}
	
	private String writeNode(Query q)
		{
		if (!StringUtils.empty(q.getGroupName()))
			return (q.getName() + "\\n(" + q.getGroupName() + ")");
		else
			return (q.getName());
		}
	}
