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
package de.tweerlei.dbgrazer.plugins.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.visualization.model.GraphDefinition;
import de.tweerlei.dbgrazer.visualization.model.GraphEdge;
import de.tweerlei.dbgrazer.visualization.model.GraphNode;
import de.tweerlei.dbgrazer.visualization.service.GraphBuilder;
import de.tweerlei.dbgrazer.visualization.service.GraphDirection;
import de.tweerlei.dbgrazer.visualization.service.GraphStyle;
import de.tweerlei.dbgrazer.visualization.service.GraphType;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service("graphBuilder")
public class GraphBuilderImpl implements GraphBuilder
	{
	private static final String START_NODE = "start";
	private static final String END_NODE = "end";
	
	private final Map<String, GraphType> types;
	
	/**
	 * Constructor
	 * @param types GraphTypes
	 */
	@Autowired
	public GraphBuilderImpl(Set<GraphType> types)
		{
		this.types = Collections.unmodifiableMap(new NamedMap<GraphType>(types));
		
		final Logger logger = Logger.getLogger(getClass().getCanonicalName());
		logger.log(Level.INFO, "Graph types: " + this.types);
		}
	
	@Override
	public GraphDefinition buildGraph(String name, GraphType type, GraphStyle style, String title, String subtitle, String attrs, Set<GraphNode> nodes, Set<GraphEdge> edges, String nodeLink)
		{
		final String def;
		if (type.getDirection() == GraphDirection.UNDIRECTED)
			def = buildUndirectedGraph(name, style, title, subtitle, attrs, nodes, edges, nodeLink);
		else
			def = buildDirectedGraph(name, style, type.getDirection(), title, subtitle, attrs, nodes, edges, nodeLink);
		
		return (new GraphDefinition(name, type, def));
		}
	
	private String buildDirectedGraph(String name, GraphStyle style, GraphDirection direction, String title, String subtitle, String attrs, Set<GraphNode> nodes, Set<GraphEdge> edges, String nodeLink)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("digraph ").append(name).append(" {\n");
		
		final Map<String, String> def = new HashMap<String, String>(style.getDirectedGraphAttributes());
		def.put("rankdir", getDirection(direction));
		if (!StringUtils.empty(title))
			{
			if (!StringUtils.empty(subtitle))
				def.put("label", title + "<br/><font point-size=\"" + style.getSubtitleSize() + "\">" + subtitle + "</font>");
			else
				def.put("label", title);
			def.put("labelloc", "t");
			}
		appendGraphAttributes(sb, def);
		if (attrs != null)
			sb.append(attrs).append("\n");
		sb.append("\n");
		
		appendNodesAndEdges(sb, style, nodes, edges, direction, nodeLink);
		
		sb.append("}\n");
		
		return (sb.toString());
		}
	
	private String buildUndirectedGraph(String name, GraphStyle style, String title, String subtitle, String attrs, Set<GraphNode> nodes, Set<GraphEdge> edges, String nodeLink)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("graph ").append(name).append(" {\n");
		
		final Map<String, String> def = new HashMap<String, String>(style.getUndirectedGraphAttributes());
		if (!StringUtils.empty(title))
			{
			if (!StringUtils.empty(subtitle))
				def.put("label", title + "<br/><font point-size=\"" + style.getSubtitleSize() + "\">" + subtitle + "</font>");
			else
				def.put("label", title);
			def.put("labelloc", "t");
			}
		appendGraphAttributes(sb, def);
		if (attrs != null)
			sb.append(attrs).append("\n");
		sb.append("\n");
		
		appendNodesAndEdges(sb, style, nodes, edges, GraphDirection.UNDIRECTED, nodeLink);
		
		sb.append("}\n");
		
		return (sb.toString());
		}
	
	private void appendNodesAndEdges(StringBuilder sb, GraphStyle style, Set<GraphNode> nodes, Set<GraphEdge> edges, GraphDirection dir, String nodeLink)
		{
		if (!style.getNodeAttributes().isEmpty())
			{
			appendNode(sb, "node", style.getNodeAttributes());
			sb.append("\n");
			}
		if (!style.getEdgeAttributes().isEmpty())
			{
			appendNode(sb, "edge", style.getEdgeAttributes());
			sb.append("\n");
			}
		
		boolean hasStart = false;
		boolean hasEnd = false;
		for (GraphEdge edge : edges)
			{
			if (edge.getStartNode() == null)
				hasStart = true;
			if (edge.getEndNode() == null)
				hasEnd = true;
			}
		
		if (hasStart)
			appendNode(sb, START_NODE, style.getStartNodeAttributes());
		if (hasEnd)
			appendNode(sb, END_NODE, style.getEndNodeAttributes());
		
		for (GraphNode node : nodes)
			{
			sb.append("n").append(node.getId());
			if (node.getName().startsWith("<"))
				sb.append(" [label=<").append(node.getName()).append(">");
			else
				sb.append(" [label=\"").append(node.getName().replace("\"", "").replace("\n", "\\n")).append("\"");
			if (node.getAttrs() != null)
				sb.append(",").append(node.getAttrs());
			if ((nodeLink != null) && (node.getLink() != null))
				sb.append(",URL=\"").append(nodeLink).append(node.getLink()).append("\"");
			sb.append("]\n");
			}
		
		sb.append("\n");
		
		for (GraphEdge edge : edges)
			{
			if (edge.getStartNode() == null)
				sb.append(START_NODE);
			else
				sb.append("n").append(edge.getStartNode()).append(getTailPos(dir));
			sb.append(getEdgeDir(dir));
			if (edge.getEndNode() == null)
				sb.append(END_NODE);
			else
				sb.append("n").append(edge.getEndNode()).append(getHeadPos(dir));
			if ((edge.getName() != null) || (edge.getAttrs() != null))
				{
				sb.append(" [");
				if (edge.getName() != null)
					{
					if (edge.getName().startsWith("<"))
						sb.append("label=<").append(edge.getName()).append(">");
					else
						sb.append("label=\"").append(edge.getName().replace("\"", "").replace("\n", "\\n")).append("\"");
					}
				if (edge.getAttrs() != null)
					{
					if (edge.getName() != null)
						sb.append(",");
					sb.append(edge.getAttrs());
					}
				sb.append("]");
				}
			sb.append("\n");
			}
		}
	
	private void appendGraphAttributes(StringBuilder sb, Map<String, String> attrs)
		{
		for (Map.Entry<String, String> ent : attrs.entrySet())
			{
			if (ent.getKey().equals("label"))
				{
				sb.append(ent.getKey());
				sb.append("=<");
				sb.append(ent.getValue());
				sb.append(">");
				}
			else
				{
				sb.append(ent.getKey());
				sb.append("=\"");
				sb.append(ent.getValue());
				sb.append("\"");
				}
			sb.append("\n");
			}
		}
	
	private void appendNode(StringBuilder sb, String node, Map<String, String> attrs)
		{
		sb.append(node);
		
		if (attrs.isEmpty())
			return;
		
		sb.append(" [");
		boolean first = true;
		for (Map.Entry<String, String> ent : attrs.entrySet())
			{
			if (first)
				first = false;
			else
				sb.append(",");
			sb.append(ent.getKey());
			sb.append("=\"");
			sb.append(ent.getValue());
			sb.append("\"");
			}
		sb.append("]\n");
		}

	private String getDirection(GraphDirection dir)
		{
		switch (dir)
			{
			case LEFT_TO_RIGHT:
				return ("LR");
			case RIGHT_TO_LEFT:
				return ("RL");
			case TOP_TO_BOTTOM:
				return ("TB");
			case BOTTOM_TO_TOP:
				return ("BT");
			default:
				return (null);
			}
		}
	
	private String getEdgeDir(GraphDirection dir)
		{
		if (dir == GraphDirection.UNDIRECTED)
			return (" -- ");
		else
			return (" -> ");
		}
	
	private String getTailPos(GraphDirection dir)
		{
		switch (dir)
			{
			case LEFT_TO_RIGHT:
				return (":e");
			case RIGHT_TO_LEFT:
				return (":w");
			case TOP_TO_BOTTOM:
				return (":s");
			case BOTTOM_TO_TOP:
				return (":n");
			default:
				return ("");
			}
		}
	
	private String getHeadPos(GraphDirection dir)
		{
		switch (dir)
			{
			case LEFT_TO_RIGHT:
				return (":w");
			case RIGHT_TO_LEFT:
				return (":e");
			case TOP_TO_BOTTOM:
				return (":n");
			case BOTTOM_TO_TOP:
				return (":s");
			default:
				return ("");
			}
		}
	
	@Override
	public GraphType getGraphType(String name)
		{
		return (types.get(name));
		}
	
	@Override
	public Collection<GraphType> getGraphTypes()
		{
		return (types.values());
		}
	}
