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
package de.tweerlei.dbgrazer.web.backend.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.ByteOrderMarkWriter;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.visualization.model.GraphDefinition;
import de.tweerlei.dbgrazer.visualization.model.GraphEdge;
import de.tweerlei.dbgrazer.visualization.model.GraphNode;
import de.tweerlei.dbgrazer.visualization.service.GraphBuilder;
import de.tweerlei.dbgrazer.visualization.service.GraphService;
import de.tweerlei.dbgrazer.visualization.service.GraphStyle;
import de.tweerlei.dbgrazer.visualization.service.GraphType;
import de.tweerlei.dbgrazer.web.backend.Visualizer;
import de.tweerlei.dbgrazer.web.constant.ErrorKeys;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;

/**
 * Visualize a Result
 * 
 * @author Robert Wruck
 */
@Service
@Order(2)
public class GraphResultVisualizer extends NamedBase implements Visualizer
	{
	private static final String GRAPHTYPE_KEY = GraphType.class.getSimpleName();
	
	private static final Set<String> OPTION_NAMES = Collections.singleton(GRAPHTYPE_KEY);
	
	/** Required number of columns for the graph nodes */
	private static final int GRAPH_NODE_MIN_COLUMNS = 2;
	/** Index of the column containing the node ID */
	private static final int GRAPH_NODE_ID_INDEX = 0;
	/** Index of the column containing the node name */
	private static final int GRAPH_NODE_NAME_INDEX = 1;
	/** Index of the column containing the node attributes */
	private static final int GRAPH_NODE_ATTR_INDEX = 2;
	
	/** Required number of columns for the graph edges */
	private static final int GRAPH_EDGE_MIN_COLUMNS = 2;
	/** Index of the column containing the edge start node ID */
	private static final int GRAPH_EDGE_START_INDEX = 0;
	/** Index of the column containing the edge end node ID */
	private static final int GRAPH_EDGE_END_INDEX = 1;
	/** Index of the column containing the edge attributes */
	private static final int GRAPH_EDGE_ATTR_INDEX = 2;
	
	/** Settings for the graph source code */
	private static final String CHARSET = "UTF-8";
	private static final String CONTENT_TYPE = "text/plain; charset=" + CHARSET;
	private static final String EXTENSION = ".gv";
	
	private final GraphBuilder graphBuilder;
	private final GraphService graphService;
	private final GraphStyle graphStyle;
	private final FrontendHelperService frontendHelper;
	private final FrontendNotificationService frontendNotificationService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param graphBuilder GraphBuilder
	 * @param graphService GraphService
	 * @param graphStyle GraphStyle
	 * @param frontendHelper FrontendHelperService
	 * @param frontendNotificationService FrontendNotificationService
	 */
	@Autowired
	public GraphResultVisualizer(GraphBuilder graphBuilder, GraphService graphService, GraphStyle graphStyle,
			FrontendHelperService frontendHelper, FrontendNotificationService frontendNotificationService)
		{
		super("Graph");
		this.graphBuilder = graphBuilder;
		this.graphService = graphService;
		this.graphStyle = graphStyle;
		this.frontendHelper = frontendHelper;
		this.frontendNotificationService = frontendNotificationService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public boolean supports(String type)
		{
		return (VisualizationSettings.GRAPH_QUERY_TYPE.equals(type));
		}
	
	@Override
	public Set<String> getOptionNames()
		{
		return (OPTION_NAMES);
		}
	
	@Override
	public Set<String> getOptionValues(String option, int code)
		{
		final Set<String> ret = new LinkedHashSet<String>();
		if (GRAPHTYPE_KEY.equals(option))
			{
			for (GraphType t : graphBuilder.getGraphTypes())
				ret.add(t.getName());
			}
		return (ret);
		}
	
	@Override
	public Visualization build(Result r, DataFormatter fmt, String name, String subtitle, String nodeLink, Map<String, String> options)
		{
		final Iterator<RowSet> it = r.getRowSets().values().iterator();
		final RowSet nodes = it.next();
		final RowSet edges = it.hasNext() ? it.next() : null;
		
		if (nodes.getRows().isEmpty())
			return (null);
		
		if (nodes.getColumns().size() < GRAPH_NODE_MIN_COLUMNS)
			{
			frontendNotificationService.logError(ErrorKeys.TOO_FEW_COLUMNS, nodes.getQuery().getName());
			return (null);
			}
		
		final Map<String, String> nodeMap = new HashMap<String, String>();
		final Set<GraphNode> nodeSet = new HashSet<GraphNode>();
		final Set<GraphEdge> edgeSet = new HashSet<GraphEdge>();
		
		final boolean hasNodeAttrs = (nodes.getColumns().size() > GRAPH_NODE_ATTR_INDEX);
		int nodeCount = 0;
		for (ResultRow row : nodes.getRows())
			{
			final String nodeIdText = fmt.format(nodes.getColumns().get(GRAPH_NODE_ID_INDEX).getType(), row.getValues().get(GRAPH_NODE_ID_INDEX));
			final String nodeId = String.valueOf(nodeCount);
			
			if (!nodeMap.containsKey(nodeIdText))
				{
				nodeSet.add(new GraphNode(
						String.valueOf(nodeId),
						fmt.format(nodes.getColumns().get(GRAPH_NODE_NAME_INDEX).getType(), row.getValues().get(GRAPH_NODE_NAME_INDEX)),
						frontendHelper.paramEncode(nodeIdText, false),
						hasNodeAttrs ? fmt.format(nodes.getColumns().get(GRAPH_NODE_ATTR_INDEX).getType(), row.getValues().get(GRAPH_NODE_ATTR_INDEX)) : null
						));
				nodeMap.put(nodeIdText, nodeId);
				nodeCount++;
				}
			}
		
		if ((edges != null) && !edges.getRows().isEmpty())
			{
			if (edges.getColumns().size() < GRAPH_EDGE_MIN_COLUMNS)
				{
				frontendNotificationService.logError(ErrorKeys.TOO_FEW_COLUMNS, edges.getQuery().getName());
				return (null);
				}
			
			final boolean hasEdgeAttrs = (edges.getColumns().size() > GRAPH_EDGE_ATTR_INDEX);
			for (ResultRow row : edges.getRows())
				{
				final String startIdText = fmt.format(edges.getColumns().get(GRAPH_EDGE_START_INDEX).getType(), row.getValues().get(GRAPH_EDGE_START_INDEX));
				final String endIdText = fmt.format(edges.getColumns().get(GRAPH_EDGE_END_INDEX).getType(), row.getValues().get(GRAPH_EDGE_END_INDEX));
				final String startId = nodeMap.get(startIdText);	// may be null
				final String endId = nodeMap.get(endIdText);	// may be null
				
				edgeSet.add(new GraphEdge(
						startId,
						endId,
						null,
						hasEdgeAttrs ? fmt.format(edges.getColumns().get(GRAPH_EDGE_ATTR_INDEX).getType(), row.getValues().get(GRAPH_EDGE_ATTR_INDEX)) : null
						));
				}
			}
		
		final GraphType type = getGraphType(options);
		
		return (new Visualization(r.getQuery().getType().getName(), name,
				graphBuilder.buildGraph(name, type, graphStyle, r.getQuery().getName(), subtitle, null, nodeSet, edgeSet, nodeLink)));
		}
	
	private GraphType getGraphType(Map<String, String> options)
		{
		final String value = options.get(GRAPHTYPE_KEY);
		
		final GraphType t = graphBuilder.getGraphType(value);
		if (t != null)
			return (t);
		
		return (graphBuilder.getGraphTypes().iterator().next());
		}
	
	@Override
	public String getImageContentType()
		{
		return (graphService.getImageContentType());
		}
	
	@Override
	public String getImageFileExtension()
		{
		return (graphService.getFileExtension());
		}
	
	@Override
	public void writeImageTo(Visualization obj, OutputStream stream) throws IOException
		{
		final GraphDefinition def = (GraphDefinition) obj.getDefinition();
		
		graphService.createImage(def, stream);
		}
	
	@Override
	public boolean supportsSourceText()
		{
		return (true);
		}
	
	@Override
	public String getSourceTextContentType()
		{
		return (CONTENT_TYPE);
		}
	
	@Override
	public String getSourceTextFileExtension()
		{
		return (EXTENSION);
		}
	
	@Override
	public void writeSourceTextTo(Visualization obj, OutputStream stream) throws IOException
		{
		final GraphDefinition def = (GraphDefinition) obj.getDefinition();
		
		@SuppressWarnings("resource")
		final Writer osw = new ByteOrderMarkWriter(new OutputStreamWriter(stream, CHARSET));
		try	{
			osw.write(graphService.getGraphSource(def));
			}
		finally
			{
			osw.flush();
			}
		}
	
	@Override
	public boolean supportsSourceSVG()
		{
		return (true);
		}
	
	@Override
	public String getSourceSVG(Visualization obj)
		{
		final GraphDefinition def = (GraphDefinition) obj.getDefinition();
		
		try	{
			return (graphService.createSVG(def));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createSVG for graph " + def.getName(), e);
			return ("");
			}
		}
	
	@Override
	public String getHtmlMap(Visualization obj)
		{
		final GraphDefinition def = (GraphDefinition) obj.getDefinition();
		
		try	{
			return (graphService.createHTMLMap(def));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createHTMLMap for graph " + def.getName(), e);
			return ("");
			}
		}
	}
