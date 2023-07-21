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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.ByteOrderMarkWriter;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.visualization.model.SVGDefinition;
import de.tweerlei.dbgrazer.visualization.model.SVGShape;
import de.tweerlei.dbgrazer.visualization.service.SVGBuilder;
import de.tweerlei.dbgrazer.web.backend.Visualizer;
import de.tweerlei.dbgrazer.web.constant.ErrorKeys;
import de.tweerlei.dbgrazer.web.constant.VisualizationSettings;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.model.Visualization;
import de.tweerlei.dbgrazer.web.service.FrontendNotificationService;

/**
 * Visualize a Result
 * 
 * @author Robert Wruck
 */
@Service
@Order(3)
public class SVGResultVisualizer extends NamedBase implements Visualizer
	{
	/** Required number of columns for the graph nodes */
	private static final int MIN_COLUMNS = 5;
	/** Index of the column containing the shape name */
	private static final int SHAPE_INDEX = 0;
	/** Index of the column containing the first coordinate */
	private static final int COORD1_INDEX = 1;
	/** Index of the column containing the second coordinate */
	private static final int COORD2_INDEX = 2;
	/** Index of the column containing the third coordinate */
	private static final int COORD3_INDEX = 3;
	/** Index of the column containing the fourth coordinate */
	private static final int COORD4_INDEX = 4;
	/** Index of the column containing the shape attributes */
	private static final int ATTR_INDEX = 5;
	
	/** Settings for the graph source code */
	private static final String CHARSET = "UTF-8";
	private static final String CONTENT_TYPE = "image/svg+xml";
	private static final String EXTENSION = ".svg";
	
	private final SVGBuilder svgBuilder;
	private final FrontendNotificationService frontendNotificationService;
	
	/**
	 * Constructor
	 * @param svgBuilder SVGBuilder
	 * @param frontendNotificationService FrontendNotificationService
	 */
	@Autowired
	public SVGResultVisualizer(SVGBuilder svgBuilder, FrontendNotificationService frontendNotificationService)
		{
		super("SVG");
		this.svgBuilder = svgBuilder;
		this.frontendNotificationService = frontendNotificationService;
		}
	
	@Override
	public boolean supports(String type)
		{
		return (VisualizationSettings.SVG_QUERY_TYPE.equals(type));
		}
	
	@Override
	public Set<String> getOptionNames()
		{
		return (Collections.emptySet());
		}
	
	@Override
	public Set<String> getOptionValues(String option, int code)
		{
		return (Collections.emptySet());
		}
	
	@Override
	public Visualization build(Result r, DataFormatter fmt, String name, String subtitle, String nodeLink, Map<String, String> options)
		{
		final RowSet rs = r.getFirstRowSet();
		
		if (rs.getRows().isEmpty())
			return (null);
		
		if (rs.getColumns().size() < MIN_COLUMNS)
			{
			frontendNotificationService.logError(ErrorKeys.TOO_FEW_COLUMNS, rs.getQuery().getName());
			return (null);
			}
		
		if ((rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.INTEGER) && (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.FLOAT)
				|| (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.INTEGER) && (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.FLOAT)
				|| (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.INTEGER) && (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.FLOAT)
				|| (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.INTEGER) && (rs.getColumns().get(COORD1_INDEX).getType() != ColumnType.FLOAT))
			{
			frontendNotificationService.logError(ErrorKeys.UNSUPPORTED_DATA_TYPES, rs.getQuery().getName());
			return (null);
			}
		
		final Set<SVGShape> shapeSet = new LinkedHashSet<SVGShape>();
		
		final boolean hasNodeAttrs = (rs.getColumns().size() > ATTR_INDEX);
		for (ResultRow row : rs.getRows())
			{
			final String shapeName = fmt.format(rs.getColumns().get(SHAPE_INDEX).getType(), row.getValues().get(SHAPE_INDEX));
			final Number c1 = (Number) row.getValues().get(COORD1_INDEX);
			final Number c2 = (Number) row.getValues().get(COORD2_INDEX);
			final Number c3 = (Number) row.getValues().get(COORD3_INDEX);
			final Number c4 = (Number) row.getValues().get(COORD4_INDEX);
			final String attributes = hasNodeAttrs ? fmt.format(rs.getColumns().get(ATTR_INDEX).getType(), row.getValues().get(ATTR_INDEX)) : null;
			
			shapeSet.add(new SVGShape(
					shapeName,
					c1.doubleValue(), c2.doubleValue(), c3.doubleValue(), c4.doubleValue(),
					null,
					attributes
					));
			}
		
		return (new Visualization(r.getQuery().getType().getName(), name,
				svgBuilder.buildSVG(name, r.getQuery().getName(), subtitle, null, shapeSet, nodeLink)));
		}
	
	@Override
	public String getImageContentType()
		{
		return (CONTENT_TYPE);
		}
	
	@Override
	public String getImageFileExtension()
		{
		return (EXTENSION);
		}
	
	@Override
	public void writeImageTo(Visualization obj, OutputStream stream) throws IOException
		{
		final SVGDefinition def = (SVGDefinition) obj.getDefinition();
		
		@SuppressWarnings("resource")
		final Writer osw = new ByteOrderMarkWriter(new OutputStreamWriter(stream, CHARSET));
		try	{
			osw.write(def.getSVG().toString());
			}
		finally
			{
			osw.flush();
			}
		}
	
	@Override
	public boolean supportsSourceText()
		{
		return (false);
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
		writeImageTo(obj, stream);
		}
	
	@Override
	public boolean supportsSourceSVG()
		{
		return (true);
		}
	
	@Override
	public String getSourceSVG(Visualization obj)
		{
		final SVGDefinition def = (SVGDefinition) obj.getDefinition();
		
		return (def.getSVG().toString());
		}
	
	@Override
	public String getHtmlMap(Visualization obj)
		{
		return ("");
		}
	}
