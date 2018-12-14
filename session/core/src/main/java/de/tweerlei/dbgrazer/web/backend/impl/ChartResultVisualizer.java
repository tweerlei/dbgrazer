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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import de.tweerlei.common5.collections.Pair;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowSet;
import de.tweerlei.dbgrazer.visualization.model.ChartDataRow;
import de.tweerlei.dbgrazer.visualization.model.ChartDefinition;
import de.tweerlei.dbgrazer.visualization.service.ChartBuilder;
import de.tweerlei.dbgrazer.visualization.service.ChartScaling;
import de.tweerlei.dbgrazer.visualization.service.ChartService;
import de.tweerlei.dbgrazer.visualization.service.ChartStyle;
import de.tweerlei.dbgrazer.visualization.service.ChartType;
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
@Order(1)
public class ChartResultVisualizer extends NamedBase implements Visualizer
	{
	private static final String CHARTTYPE_KEY = ChartType.class.getSimpleName();
	private static final String CHARTSCALING_KEY = ChartScaling.class.getSimpleName();
	
	private static final int OPTION_CODE_CONTINUOUS = 0;
	private static final int OPTION_CODE_CATEGORY = 1;
	private static final int OPTION_CODE_TIME = 2;
	private static final int OPTION_CODE_GANTT = 3;
	private static final int OPTION_CODE_XYZ = 4;
	
	private static final Set<String> OPTION_NAMES;
	static
		{
		final Set<String> s = new LinkedHashSet<String>();
		s.add(CHARTTYPE_KEY);
		s.add(CHARTSCALING_KEY);
		
		OPTION_NAMES = Collections.unmodifiableSet(s);
		}
	
	/** Required number of columns for charts */
	private static final int CHART_MIN_COLUMNS = 2;
	/** Required number of columns for interval charts */
	private static final int CHART_INTERVAL_COLUMNS = 3;
	/** Index of the column containing the domain */
	private static final int CHART_DOMAIN_INDEX = 0;
	/** Index of the column containing the range */
	private static final int CHART_RANGE_INDEX = 1;
	/** Index of the column containing the range */
	private static final int CHART_INTERVAL_RANGE_INDEX = 2;
	
	private final ChartBuilder chartBuilder;
	private final ChartService chartService;
	private final ChartStyle chartStyle;
	private final FrontendNotificationService frontendNotificationService;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param chartBuilder ChartBuilder
	 * @param chartService ChartService
	 * @param chartStyle ChartStyle
	 * @param frontendNotificationService FrontendNotificationService
	 */
	@Autowired
	public ChartResultVisualizer(ChartBuilder chartBuilder, ChartService chartService, ChartStyle chartStyle,
			FrontendNotificationService frontendNotificationService)
		{
		super("Chart");
		this.chartBuilder = chartBuilder;
		this.chartService = chartService;
		this.chartStyle = chartStyle;
		this.frontendNotificationService = frontendNotificationService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	@Override
	public boolean supports(String type)
		{
		return (VisualizationSettings.CHART_QUERY_TYPE.equals(type) || VisualizationSettings.TIMECHART_QUERY_TYPE.equals(type));
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
		if (CHARTTYPE_KEY.equals(option))
			{
			switch (code)
				{
				case OPTION_CODE_CONTINUOUS:
				case OPTION_CODE_TIME:
					for (ChartType t : chartBuilder.getChartTypes())
						{
						if (t.isSupportingContinuousRanges())
							ret.add(t.getName());
						}
					break;
				case OPTION_CODE_XYZ:
					for (ChartType t : chartBuilder.getChartTypes())
						{
						if (t.isSupportingZValues())
							ret.add(t.getName());
						}
					break;
				case OPTION_CODE_CATEGORY:
					for (ChartType t : chartBuilder.getChartTypes())
						{
						if (t.isSupportingCategories())
							ret.add(t.getName());
						}
					break;
				case OPTION_CODE_GANTT:
					for (ChartType t : chartBuilder.getChartTypes())
						{
						if (!t.isSupportingContinuousRanges() && !t.isSupportingCategories() && !t.isSupportingZValues())
							ret.add(t.getName());
						}
					break;
				default:
					for (ChartType t : chartBuilder.getChartTypes())
						ret.add(t.getName());
					break;
				}
			}
		else if (CHARTSCALING_KEY.equals(option))
			{
			switch (code)
				{
				case OPTION_CODE_CONTINUOUS:
				case OPTION_CODE_XYZ:
					for (ChartScaling t : chartBuilder.getChartScalings())
						ret.add(t.getName());
					break;
				case OPTION_CODE_CATEGORY:
				case OPTION_CODE_TIME:
					for (ChartScaling t : chartBuilder.getChartScalings())
						{
						if (!t.isDomainAxisScaled())
							ret.add(t.getName());
						}
					break;
				case OPTION_CODE_GANTT:
					for (ChartScaling t : chartBuilder.getChartScalings())
						{
						if (!t.isDomainAxisScaled() && !t.isRangeAxisScaled())
							ret.add(t.getName());
						}
					break;
				default:
					for (ChartScaling t : chartBuilder.getChartScalings())
						ret.add(t.getName());
					break;
				}
			}
		return (ret);
		}
	
	@Override
	public Visualization build(Result r, DataFormatter fmt, String name, String subtitle, String rowLink, Map<String, String> options)
		{
		final RowSet rs = getFirstFilledRowSet(r);
		
		if (rs == null)
			return (null);
		
		if (rs.getColumns().size() < CHART_MIN_COLUMNS)
			{
			frontendNotificationService.logError(ErrorKeys.TOO_FEW_COLUMNS, rs.getQuery().getName());
			return (null);
			}
		
		final ColumnType domain = rs.getColumns().get(CHART_DOMAIN_INDEX).getType();
		final ColumnType range = rs.getColumns().get(CHART_RANGE_INDEX).getType();
		
		final String xLabel = rs.getColumns().get(CHART_DOMAIN_INDEX).getName();
		final String yLabel = rs.getColumns().get(CHART_RANGE_INDEX).getName();
		
		final ChartType type = getChartType(options);
		final ChartScaling scaling = getChartScaling(options);
		
		switch (domain)
			{
			case STRING:
			case TEXT:
				if (range == ColumnType.INTEGER)
					return buildCategoryChart(r, true, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				else if (range == ColumnType.FLOAT)
					return buildCategoryChart(r, false, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				break;
			case INTEGER:
				if (range == ColumnType.INTEGER)
					return buildNumberChart(r, true, true, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				else if (range == ColumnType.FLOAT)
					return buildNumberChart(r, true, false, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				break;
			case FLOAT:
				if (range == ColumnType.INTEGER)
					return buildNumberChart(r, false, true, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				else if (range == ColumnType.FLOAT)
					return buildNumberChart(r, false, false, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				break;
			case DATE:
				if (range == ColumnType.INTEGER)
					return buildTimeChart(r, fmt, true, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				else if (range == ColumnType.FLOAT)
					return buildTimeChart(r, fmt, false, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				else if (range == ColumnType.DATE)
					return buildIntervalChart(r, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
				break;
			default:
				break;
			}
		
		frontendNotificationService.logError(ErrorKeys.UNSUPPORTED_DATA_TYPES, rs.getQuery().getName(), domain, range);
		return (null);
		}
	
	private ChartType getChartType(Map<String, String> options)
		{
		final String value = options.get(CHARTTYPE_KEY);
		
		final ChartType t = chartBuilder.getChartType(value);
		if (t != null)
			return (t);
		
		return (chartBuilder.getChartTypes().iterator().next());
		}
	
	private ChartScaling getChartScaling(Map<String, String> options)
		{
		final String value = options.get(CHARTSCALING_KEY);
		
		final ChartScaling t = chartBuilder.getChartScaling(value);
		if (t != null)
			return (t);
		
		return (chartBuilder.getChartScalings().iterator().next());
		}
	
	private Visualization buildNumberChart(Result r, boolean xDiscrete, boolean yDiscrete, String name, String subtitle, String xLabel, String yLabel, ChartType type, ChartScaling scaling, String rowLink)
		{
		final RowSet rs = getFirstFilledRowSet(r);
		
		if (rs.getColumns().size() >= CHART_INTERVAL_COLUMNS)
			{
			final List<ChartDataRow<Pair<Number, Number>, Number>> rows = extractZRows(Number.class, r, Integer.valueOf(0));
			
			return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_XYZ,
					chartBuilder.buildZChart(name, rows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
					yLabel, yDiscrete, xLabel, xDiscrete, rowLink)));
			}
		else
			{
			final List<ChartDataRow<Number, Number>> rows = extractRows(Number.class, r);
			
			return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_CONTINUOUS,
					chartBuilder.buildNumberChart(name, rows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
					yLabel, yDiscrete, xLabel, xDiscrete, rowLink)));
			}
		}
	
	private Visualization buildTimeChart(Result r, DataFormatter fmt, boolean discrete, String name, String subtitle, String xLabel, String yLabel, ChartType type, ChartScaling scaling, String rowLink)
		{
		final List<ChartDataRow<Date, Number>> rows = extractRows(Date.class, r);
		
		// If no date value has a time component, create a timerange chart
		final List<ChartDataRow<Pair<Date, Date>, Number>> irows = convertToIntervalRows(rows, fmt);
		if (irows != null)
			{
			return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_TIME,
					chartBuilder.buildTimerangeChart(name, irows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
					yLabel, discrete, xLabel, rowLink)));
			}
		
		return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_TIME,
				chartBuilder.buildTimeChart(name, rows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
				yLabel, discrete, xLabel, rowLink)));
		}
	
	private Visualization buildIntervalChart(Result r, String name, String subtitle, String xLabel, String yLabel, ChartType type, ChartScaling scaling, String rowLink)
		{
		final RowSet rs = getFirstFilledRowSet(r);
		
		if (rs.getColumns().size() >= CHART_INTERVAL_COLUMNS)
			{
			final ColumnType range2 = rs.getColumns().get(CHART_INTERVAL_RANGE_INDEX).getType();
			final String yLabel2 = rs.getColumns().get(CHART_INTERVAL_RANGE_INDEX).getName();
			if (range2 == ColumnType.INTEGER)
				return buildTimerangeChart(r, true, name, subtitle, xLabel, yLabel2, type, scaling, rowLink);
			else if (range2 == ColumnType.FLOAT)
				return buildTimerangeChart(r, false, name, subtitle, xLabel, yLabel2, type, scaling, rowLink);
			}
		
		return buildTimeSeriesChart(r, false, name, subtitle, xLabel, yLabel, type, scaling, rowLink);
		}
	
	private Visualization buildCategoryChart(Result r, boolean discrete, String name, String subtitle, String xLabel, String yLabel, ChartType type, ChartScaling scaling, String rowLink)
		{
		final List<ChartDataRow<String, Number>> rows = extractRows(String.class, r);
		
		return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_CATEGORY,
				chartBuilder.buildCategoryChart(name, rows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
				yLabel, discrete, xLabel, rowLink)));
		}
	
	private Visualization buildTimeSeriesChart(Result r, boolean discrete, String name, String subtitle, String xLabel, String yLabel, ChartType type, ChartScaling scaling, String rowLink)
		{
		final List<ChartDataRow<Pair<Date, Date>, String>> rows = extractIntervalRows(String.class, r, null);
		
		return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_GANTT,
				chartBuilder.buildTimeSeriesChart(name, rows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
				yLabel, discrete, xLabel, rowLink)));
		}
	
	private Visualization buildTimerangeChart(Result r, boolean discrete, String name, String subtitle, String xLabel, String yLabel, ChartType type, ChartScaling scaling, String rowLink)
		{
		final List<ChartDataRow<Pair<Date, Date>, Number>> rows = extractIntervalRows(Number.class, r, Integer.valueOf(0));
		
		return (new Visualization(r.getQuery().getType().getName(), name, OPTION_CODE_TIME,
				chartBuilder.buildTimerangeChart(name, rows, type, scaling, chartStyle, r.getQuery().getName(), subtitle,
				yLabel, discrete, xLabel, rowLink)));
		}
	
	private <T> List<ChartDataRow<T, Number>> extractRows(Class<T> keyType, Result r)
		{
		final List<ChartDataRow<T, Number>> rows = new ArrayList<ChartDataRow<T, Number>>();
		final RowSet rs = getFirstFilledRowSet(r);
		
		for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
			{
			if (ent.getValue().getRows().isEmpty())
				continue;
			
			if (!checkColumns(rs, ent.getValue(), CHART_MIN_COLUMNS))
				{
				frontendNotificationService.logError(ErrorKeys.DATA_TYPES_MISMATCH, ent.getValue().getQuery().getName());
				continue;
				}
			
			final Map<T, Number> map = new LinkedHashMap<T, Number>();
			for (ResultRow l : ent.getValue().getRows())
				{
				final Object key = l.getValues().get(CHART_DOMAIN_INDEX);
				final Object value = l.getValues().get(CHART_RANGE_INDEX);
				if ((key != null) && (value != null))
					{
					try	{
						map.put(keyType.cast(key), (Number) value);
						}
					catch (ClassCastException e)
						{
						frontendNotificationService.logError(ErrorKeys.DATA_CONVERSION_ERROR, key.getClass().getName(), key, value.getClass().getName(), value);
						break;
						}
					}
				}
			
			rows.add(new ChartDataRow<T, Number>(ent.getKey(), map));
			}
		
		return (rows);
		}
	
	private <T> List<ChartDataRow<Pair<Date, Date>, T>> extractIntervalRows(Class<T> valueType, Result r, T def)
		{
		final List<ChartDataRow<Pair<Date, Date>, T>> rows = new ArrayList<ChartDataRow<Pair<Date, Date>, T>>();
		final RowSet rs = getFirstFilledRowSet(r);
		
		final boolean hasValue = rs.getColumns().size() >= CHART_INTERVAL_COLUMNS;
		
		for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
			{
			if (ent.getValue().getRows().isEmpty())
				continue;
			
			if (!checkColumns(rs, ent.getValue(), hasValue ? CHART_INTERVAL_COLUMNS : CHART_MIN_COLUMNS))
				{
				frontendNotificationService.logError(ErrorKeys.DATA_TYPES_MISMATCH, ent.getValue().getQuery().getName());
				continue;
				}
			
			final Map<Pair<Date, Date>, T> map = new LinkedHashMap<Pair<Date, Date>, T>();
			for (ResultRow l : ent.getValue().getRows())
				{
				final Object start = l.getValues().get(CHART_DOMAIN_INDEX);
				final Object end = l.getValues().get(CHART_RANGE_INDEX);
				final Object value = hasValue ? l.getValues().get(CHART_INTERVAL_RANGE_INDEX) : def;
				if ((start != null) && (end != null))
					{
					try	{
						map.put(new Pair<Date, Date>((Date) start, (Date) end), valueType.cast(value));
						}
					catch (ClassCastException e)
						{
						frontendNotificationService.logError(ErrorKeys.RANGE_CONVERSION_ERROR, start.getClass().getName(), start, end.getClass().getName(), end, (value == null) ? "null" : value.getClass().getName(), value);
						break;
						}
					}
				}
			
			rows.add(new ChartDataRow<Pair<Date, Date>, T>(ent.getKey(), map));
			}
		
		return (rows);
		}
	
	private <T> List<ChartDataRow<Pair<Number, Number>, T>> extractZRows(Class<T> valueType, Result r, T def)
		{
		final List<ChartDataRow<Pair<Number, Number>, T>> rows = new ArrayList<ChartDataRow<Pair<Number, Number>, T>>();
		final RowSet rs = getFirstFilledRowSet(r);
		
		final boolean hasValue = rs.getColumns().size() >= CHART_INTERVAL_COLUMNS;
		
		for (Map.Entry<String, RowSet> ent : r.getRowSets().entrySet())
			{
			if (ent.getValue().getRows().isEmpty())
				continue;
			
			if (!checkColumns(rs, ent.getValue(), hasValue ? CHART_INTERVAL_COLUMNS : CHART_MIN_COLUMNS))
				{
				frontendNotificationService.logError(ErrorKeys.DATA_TYPES_MISMATCH, ent.getValue().getQuery().getName());
				continue;
				}
			
			final Map<Pair<Number, Number>, T> map = new LinkedHashMap<Pair<Number, Number>, T>();
			for (ResultRow l : ent.getValue().getRows())
				{
				final Object start = l.getValues().get(CHART_DOMAIN_INDEX);
				final Object end = l.getValues().get(CHART_RANGE_INDEX);
				final Object value = hasValue ? l.getValues().get(CHART_INTERVAL_RANGE_INDEX) : def;
				if ((start != null) && (end != null))
					{
					try	{
						map.put(new Pair<Number, Number>((Number) start, (Number) end), valueType.cast(value));
						}
					catch (ClassCastException e)
						{
						frontendNotificationService.logError(ErrorKeys.RANGE_CONVERSION_ERROR, start.getClass().getName(), start, end.getClass().getName(), end, (value == null) ? "null" : value.getClass().getName(), value);
						break;
						}
					}
				}
			
			rows.add(new ChartDataRow<Pair<Number, Number>, T>(ent.getKey(), map));
			}
		
		return (rows);
		}
	
	private <T> List<ChartDataRow<Pair<Date, Date>, T>> convertToIntervalRows(List<ChartDataRow<Date, T>> rows, DataFormatter fmt)
		{
		final Calendar c = fmt.getCalendar();
		
		int field = Calendar.YEAR;
		int range = 0;
		
		// Inspect all values to find minimum date granularity (minutes, seconds, days, months, years)
		for (ChartDataRow<Date, T> row : rows)
			{
			for (Map.Entry<Date, T> ent : row.getValues().entrySet())
				{
				c.setTime(ent.getKey());
				
				if ((c.get(Calendar.MILLISECOND) != 0) || (c.get(Calendar.SECOND) != 0))
					return (null);
				
				if (c.get(Calendar.MINUTE) != 0)
					{
					if (field < Calendar.SECOND)
						{
						// create 30 second intervals
						field = Calendar.SECOND;
						range = 15;
						}
					}
				else if (c.get(Calendar.HOUR_OF_DAY) != 0)
					{
					if (field < Calendar.MINUTE)
						{
						// create 30 minute intervals
						field = Calendar.MINUTE;
						range = 15;
						}
					}
				else if (c.get(Calendar.DAY_OF_MONTH) != 1)
					{
					if (field < Calendar.HOUR_OF_DAY)
						{
						// create 12 hour intervals
						field = Calendar.HOUR_OF_DAY;
						range = 6;
						}
					}
				else if (c.get(Calendar.MONTH) != Calendar.JANUARY)
					{
					if (field < Calendar.DAY_OF_MONTH)
						{
						// create 14 day intervals
						field = Calendar.DAY_OF_MONTH;
						range = 7;
						}
					}
				else
					{
					if (field < Calendar.MONTH)
						{
						// create 6 month intervals
						field = Calendar.MONTH;
						range = 6;
						}
					}
				}
			}
		
		// Convert to intervals of found granularity
		final List<ChartDataRow<Pair<Date, Date>, T>> ret = new ArrayList<ChartDataRow<Pair<Date, Date>, T>>(rows.size());
		for (ChartDataRow<Date, T> row : rows)
			{
			final Map<Pair<Date, Date>, T> map = new LinkedHashMap<Pair<Date, Date>, T>();
			
			for (Map.Entry<Date, T> ent : row.getValues().entrySet())
				{
				c.setTime(ent.getKey());
				
				c.add(field, -range);
				final Date start = c.getTime();
				c.add(field, 2 * range);
				final Date end = c.getTime();
				
				map.put(new Pair<Date, Date>(start, end), ent.getValue());
				}
			
			ret.add(new ChartDataRow<Pair<Date, Date>, T>(row.getName(), map));
			}
		
		return (ret);
		}
	
	private boolean checkColumns(RowSet a, RowSet b, int n)
		{
		if ((a.getColumns().size() < n) || (b.getColumns().size() < n))
			return (false);
		
		for (int i = 0; i < n; i++)
			{
			if (a.getColumns().get(i).getType() != b.getColumns().get(i).getType())
				return (false);
			}
		
		return (true);
		}
	
	private RowSet getFirstFilledRowSet(Result r)
		{
		for (RowSet rs : r.getRowSets().values())
			{
			if (!rs.getRows().isEmpty())
				return (rs);
			}
		return (null);
		}
	
	@Override
	public String getImageContentType()
		{
		return (chartService.getImageContentType());
		}
	
	@Override
	public String getImageFileExtension()
		{
		return (chartService.getFileExtension());
		}
	
	@Override
	public void writeImageTo(Visualization obj, OutputStream stream) throws IOException
		{
		final ChartDefinition def = (ChartDefinition) obj.getDefinition();
		
		chartService.createImage(def, stream);
		}
	
	@Override
	public boolean supportsSourceText()
		{
		return (false);
		}
	
	@Override
	public String getSourceTextContentType()
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public String getSourceTextFileExtension()
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public void writeSourceTextTo(Visualization obj, OutputStream stream) throws IOException
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public boolean supportsSourceSVG()
		{
		return (false);
		}
	
	@Override
	public String getSourceSVG(Visualization obj)
		{
		throw new UnsupportedOperationException();
		}
	
	@Override
	public String getHtmlMap(Visualization obj)
		{
		final ChartDefinition def = (ChartDefinition) obj.getDefinition();
		
		try	{
			return (chartService.createHTMLMap(def));
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createHTMLMap for graph " + def.getName(), e);
			return ("");
			}
		}
	}
