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
package de.tweerlei.dbgrazer.plugins.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartTheme;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.TableOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.Pair;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.plugins.chart.jfree.AlphaPaintScale;
import de.tweerlei.dbgrazer.plugins.chart.jfree.DefaultValueCategoryDataset;
import de.tweerlei.dbgrazer.plugins.chart.jfree.XYScaledBlockRenderer;
import de.tweerlei.dbgrazer.plugins.chart.jfree.XYScaledBubbleRenderer;
import de.tweerlei.dbgrazer.plugins.chart.types.AreaChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.BarChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.BlockChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.DifferenceChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.DualChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.MinMaxChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.PieChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.PointChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.StackedAreaChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.StackedBarChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.SteppedAreaChartType;
import de.tweerlei.dbgrazer.plugins.chart.types.SteppedLineChartType;
import de.tweerlei.dbgrazer.visualization.model.ChartDataRow;
import de.tweerlei.dbgrazer.visualization.model.ChartDefinition;
import de.tweerlei.dbgrazer.visualization.service.ChartBuilder;
import de.tweerlei.dbgrazer.visualization.service.ChartScaling;
import de.tweerlei.dbgrazer.visualization.service.ChartStyle;
import de.tweerlei.dbgrazer.visualization.service.ChartType;

/**
 * Build JFreeCharts
 * 
 * @author Robert Wruck
 */
@Service("chartBuilder")
public class ChartBuilderImpl implements ChartBuilder
	{
	// Helper interface for createXYChart
	private static interface IntervalTableXYDataset extends TableXYDataset, IntervalXYDataset
		{
		}
	
	private static final class DefaultValueCategoryTableXYDataset extends CategoryTableXYDataset implements IntervalTableXYDataset
		{
		public DefaultValueCategoryTableXYDataset()
			{
			}
		}
	
	private static final class DefaultValueTimeTableXYDataset extends TimeTableXYDataset implements IntervalTableXYDataset
		{
		public DefaultValueTimeTableXYDataset()
			{
			}
		}
	
	private static final class CustomPieURLGenerator implements PieURLGenerator
		{
		private final ChartStyle style;
		private final String rowLink;
		
		public CustomPieURLGenerator(ChartStyle style, String rowLink)
			{
			this.style = style;
			this.rowLink = rowLink;
			}
		
		@Override
		public String generateURL(PieDataset dataset, @SuppressWarnings("rawtypes") Comparable key, int pieIndex)
			{
			return (style.getURL(rowLink, key.toString()));
			}
		}
	
	private static final class CustomCategoryURLGenerator implements CategoryURLGenerator
		{
		private final ChartStyle style;
		private final String rowLink;
		
		public CustomCategoryURLGenerator(ChartStyle style, String rowLink)
			{
			this.style = style;
			this.rowLink = rowLink;
			}
		
		@Override
		public String generateURL(CategoryDataset dataset, int series, int category)
			{
			@SuppressWarnings("rawtypes") 
	        final Comparable key = dataset.getColumnKey(category);
			return (style.getURL(rowLink, key.toString()));
			}
		}
	
	private static final class CustomXYURLGenerator implements XYURLGenerator
		{
		private final ChartStyle style;
		private final String rowLink;
		
		public CustomXYURLGenerator(ChartStyle style, String rowLink)
			{
			this.style = style;
			this.rowLink = rowLink;
			}
		
		@Override
		public String generateURL(XYDataset dataset, int series, int item)
			{
			@SuppressWarnings("rawtypes") 
			final Comparable key = dataset.getSeriesKey(series);
			return (style.getURL(rowLink, key.toString()));
			}
		}
	
	private final StandardChartTheme defaultTheme;
	private final Map<String, ChartType> types;
	private final Map<String, ChartScaling> scalings;
	
	/**
	 * Constructor
	 * @param types ChartTypes
	 * @param scalings ChartScalings
	 */
	@Autowired
	public ChartBuilderImpl(Set<ChartType> types, Set<ChartScaling> scalings)
		{
		this.types = Collections.unmodifiableMap(new NamedMap<ChartType>(types));
		this.scalings = Collections.unmodifiableMap(new NamedMap<ChartScaling>(scalings));
		
		this.defaultTheme = (StandardChartTheme) StandardChartTheme.createJFreeTheme();
		
		// Don't use the fancy 3D-look of recent JFreeChart versions
		this.defaultTheme.setBarPainter(new StandardBarPainter());
		this.defaultTheme.setXYBarPainter(new StandardXYBarPainter());
		this.defaultTheme.setShadowVisible(false);
		
		final Logger logger = Logger.getLogger(getClass().getCanonicalName());
		logger.log(Level.INFO, "Chart types: " + this.types);
		logger.log(Level.INFO, "Chart scalings: " + this.scalings);
		}
	
	@Override
	public ChartDefinition buildCategoryChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink)
		{
		if ((type instanceof PieChartType) && (rows.size() > 1))
			return buildMultiPieChart(name, rows, type, scaling, style, title, subtitle, rowLink);
		else if (type instanceof PieChartType)
			return buildPieChart(name, rows, type, scaling, style, title, subtitle, rowLink);
		else if ((type instanceof DualChartType) && (rows.size() > 1))
			return buildDualAxisChart(name, rows, type, scaling, style, title, subtitle, discrete, xLabel, rowLink);
		else if ((type instanceof MinMaxChartType) && (rows.size() > 1))
			return buildMinMaxChart(name, rows, type, scaling, style, title, subtitle, yLabel, discrete, xLabel, rowLink);
		else
			return buildDefaultCategoryChart(name, rows, type, scaling, style, title, subtitle, yLabel, discrete, xLabel, rowLink);
		}
	
	private ChartDefinition buildPieChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String rowLink)
		{
		final DefaultPieDataset cd = new DefaultPieDataset();
		if (true)
			{
			// Use first row only
			final ChartDataRow<String, Number> row = rows.get(0);
			for (Map.Entry<String, Number> ent : row.getValues().entrySet())
				cd.setValue(ent.getKey(), ent.getValue());
			}
		
		final JFreeChart c = createPieChart(cd, title, subtitle);
		final PiePlot plot = (PiePlot) c.getPlot();
		
		@SuppressWarnings("unchecked")
		final List<Comparable<?>> keys = cd.getKeys();
		applyPieStyle(c, plot, keys, style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	private ChartDefinition buildMultiPieChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String rowLink)
		{
		final DefaultCategoryDataset cd = new DefaultCategoryDataset();
		for (ChartDataRow<String, Number> row : rows)
			{
			for (Map.Entry<String, Number> ent : row.getValues().entrySet())
				cd.addValue(ent.getValue(), row.getName(), ent.getKey());
			}
		
		final JFreeChart c = createCategoryChart(cd, type, title, subtitle, null, null);
		final MultiplePiePlot mplot = (MultiplePiePlot) c.getPlot();
		final PiePlot plot = (PiePlot) mplot.getPieChart().getPlot();
		
		@SuppressWarnings("unchecked")
		final List<Comparable<?>> keys = cd.getColumnKeys();
		applyPieStyle(c, plot, keys, style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	private ChartDefinition buildDualAxisChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, boolean discrete, String xLabel, String rowLink)
		{
		final DefaultCategoryDataset cd = new DefaultValueCategoryDataset(0);
		if (true)
			{
			// Use first row for bars, add remaining rows as lines to secondary dataset below
			final ChartDataRow<String, Number> row = rows.get(0);
			for (Map.Entry<String, Number> ent : row.getValues().entrySet())
				cd.addValue(ent.getValue(), row.getName(), ent.getKey());
			}
		
		final JFreeChart c = createCategoryChart(cd, type, title, subtitle, rows.get(0).getName(), xLabel);
		final CategoryPlot plot = (CategoryPlot) c.getPlot();
		final Locale locale = style.getLocale();
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), discrete, locale);
		
		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		
		// Adjust margin between bars of the same category
		renderer.setItemMargin(0.1);
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(StandardCategoryToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING, NumberFormat.getInstance(locale)));
        
		// Add remaining rows to secondary dataset
		final CategoryItemRenderer renderer2;
		if (rows.size() > 1)
			{
			final DefaultCategoryDataset cd2 = new DefaultValueCategoryDataset(0);
			for (ChartDataRow<String, Number> row : rows)
				{
				if (row == rows.get(0))
					continue;
				for (Map.Entry<String, Number> ent : row.getValues().entrySet())
					cd2.addValue(ent.getValue(), row.getName(), ent.getKey());
				}
			
			final NumberAxis axis = new NumberAxis(rows.get(1).getName());
			configureValueAxis(axis, discrete, locale);
			
			renderer2 = new DefaultCategoryItemRenderer();
			renderer2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(StandardCategoryToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING, NumberFormat.getInstance(locale)));
	        
			if (rowLink != null)
				renderer2.setBaseItemURLGenerator(new CustomCategoryURLGenerator(style, rowLink));
			
			// Add dataset, axis and renderer, link dataset to axis, render bars first so the lines are better visible
			plot.setDataset(1, cd2);
			plot.setRangeAxis(1, axis);
			plot.mapDatasetToRangeAxis(1, 1);
			plot.setRenderer(1, renderer2);
			plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
			}
		else
			renderer2 = null;
		
		applyCategoryStyle(c, renderer, rows.size(), style, rowLink);
		
		if (renderer2 != null)
			{
			// Secondary bar colors
			for (int i = 0, n = rows.size(); i < n; i++)
				{
				renderer2.setSeriesPaint(i, style.getSecondaryRowPaint(i));
				renderer2.setSeriesStroke(i, style.getSecondaryRowStroke(i));
				}
			}
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	private ChartDefinition buildMinMaxChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink)
		{
		final DefaultCategoryDataset cd = new DefaultValueCategoryDataset(0);
		for (ChartDataRow<String, Number> row : rows)
			{
			for (Map.Entry<String, Number> ent : row.getValues().entrySet())
				cd.addValue(ent.getValue(), row.getName(), ent.getKey());
			}
		
		final JFreeChart c = createCategoryChart(cd, type, title, subtitle, yLabel, xLabel);
		final CategoryPlot plot = (CategoryPlot) c.getPlot();
		final Locale locale = style.getLocale();
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), discrete, locale);
		
		final MinMaxCategoryRenderer renderer = new MinMaxCategoryRenderer();
		plot.setRenderer(renderer);
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(StandardCategoryToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING, NumberFormat.getInstance(locale)));
        
		applyCategoryStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	private ChartDefinition buildDefaultCategoryChart(String name, List<ChartDataRow<String, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink)
		{
		final DefaultCategoryDataset cd = new DefaultValueCategoryDataset(0);
		for (ChartDataRow<String, Number> row : rows)
			{
			for (Map.Entry<String, Number> ent : row.getValues().entrySet())
				cd.addValue(ent.getValue(), row.getName(), ent.getKey());
			}
		
		final JFreeChart c = createCategoryChart(cd, type, title, subtitle, yLabel, xLabel);
		final CategoryPlot plot = (CategoryPlot) c.getPlot();
		final Locale locale = style.getLocale();
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), discrete, locale);
		
		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		
		// Adjust margin between bars of the same category
		renderer.setItemMargin(0.1);
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(StandardCategoryToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING, NumberFormat.getInstance(locale)));
        
		applyCategoryStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	@Override
	public ChartDefinition buildNumberChart(String name, List<ChartDataRow<Number, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean yDiscrete, String xLabel, boolean xDiscrete, String rowLink)
		{
		final DefaultValueCategoryTableXYDataset cd = new DefaultValueCategoryTableXYDataset();
		for (ChartDataRow<Number, Number> row : rows)
			{
			for (Map.Entry<Number, Number> ent : row.getValues().entrySet())
				cd.add(ent.getKey(), ent.getValue(), row.getName(), false);
			}
		
		final JFreeChart c = createXYChart(cd, type, style, title, subtitle, yLabel, xLabel);
		final XYPlot plot = (XYPlot) c.getPlot();
		final Locale locale = style.getLocale();
		
		if (scaling.isDomainAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setDomainAxis(createLogarithmicAxis(plot.getDomainAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getDomainAxis(), xDiscrete, locale);
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), yDiscrete, locale);
		
		final XYItemRenderer renderer = plot.getRenderer();
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
				NumberFormat.getInstance(locale),
				NumberFormat.getInstance(locale)
				));
		
		applyXYStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	@Override
	public ChartDefinition buildTimeChart(String name, List<ChartDataRow<Date, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink)
		{
		final DefaultValueTimeTableXYDataset cd = new DefaultValueTimeTableXYDataset();
		for (ChartDataRow<Date, Number> row : rows)
			{
			for (Map.Entry<Date, Number> ent : row.getValues().entrySet())
				cd.add(new FixedMillisecond(ent.getKey()), ent.getValue(), row.getName(), false);
			}
		
		final JFreeChart c = createXYChart(cd, type, style, title, subtitle, yLabel, xLabel);
		final XYPlot plot = (XYPlot) c.getPlot();
		final Locale locale = style.getLocale();
		final TimeZone timezone = style.getTimeZone();
		
		// Use a date axis
		plot.setDomainAxis(new DateAxis(plot.getDomainAxis().getLabel(), timezone, locale));
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), discrete, locale);
		
		final XYItemRenderer renderer = plot.getRenderer();
		
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		df.setTimeZone(timezone);
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
				df,
				NumberFormat.getInstance(locale)
				));
		
		applyXYStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	@Override
	public ChartDefinition buildTimerangeChart(String name, List<ChartDataRow<Pair<Date, Date>, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink)
		{
		final DefaultValueTimeTableXYDataset cd = new DefaultValueTimeTableXYDataset();
		for (ChartDataRow<Pair<Date, Date>, Number> row : rows)
			{
			for (Map.Entry<Pair<Date, Date>, Number> ent : row.getValues().entrySet())
				cd.add(new SimpleTimePeriod(ent.getKey().getFirst(), ent.getKey().getSecond()), ent.getValue(), row.getName(), false);
			}
		
		final JFreeChart c = createXYChart(cd, type, style, title, subtitle, yLabel, xLabel);
		final XYPlot plot = (XYPlot) c.getPlot();
		final Locale locale = style.getLocale();
		final TimeZone timezone = style.getTimeZone();
		
		// Use a date axis
		plot.setDomainAxis(new DateAxis(plot.getDomainAxis().getLabel(), timezone, locale));
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), discrete, locale);
		
		final XYItemRenderer renderer = plot.getRenderer();
		
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		df.setTimeZone(timezone);
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
				df,
				NumberFormat.getInstance(locale)
				));
		
		applyXYStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	@Override
	public ChartDefinition buildTimeSeriesChart(String name, List<ChartDataRow<Pair<Date, Date>, String>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean discrete, String xLabel, String rowLink)
		{
		final Map<String, TaskSeries> seriesMap = new TreeMap<String, TaskSeries>();
		
		for (ChartDataRow<Pair<Date, Date>, String> row : rows)
			{
			if (!row.getValues().isEmpty())
				{
				final Map<String, List<Task>> subtaskMap = new TreeMap<String, List<Task>>();
				
				// Create groups of subtasks by value
				for (Map.Entry<Pair<Date, Date>, String> ent : row.getValues().entrySet())
					{
					final String desc = (ent.getValue() == null) ? title : ent.getValue();
					List<Task> l = subtaskMap.get(desc);
					if (l == null)
						{
						l = new ArrayList<Task>();
						subtaskMap.put(desc, l);
						}
					l.add(new Task(desc, ent.getKey().getFirst(), ent.getKey().getSecond()));
					}
				
				// Create tasks for subtasks and assign them to series
				for (Map.Entry<String, List<Task>> ent : subtaskMap.entrySet())
					{
					Date start = null;
					Date end = null;
					for (Task t : ent.getValue())
						{
						if (start == null || start.after(t.getDuration().getStart()))
							start = t.getDuration().getStart();
						if (end == null || end.before(t.getDuration().getEnd()))
							end = t.getDuration().getEnd();
						}
					final Task task = new Task(row.getName(), start, end);
					for (Task t : ent.getValue())
						task.addSubtask(t);
					
					TaskSeries s = seriesMap.get(ent.getKey());
					if (s == null)
						{
						s = new TaskSeries(ent.getKey());
						seriesMap.put(ent.getKey(), s);
						}
					s.add(task);
					}
				}
			}
		
		final TaskSeriesCollection cd = new TaskSeriesCollection();
		for (TaskSeries s : seriesMap.values())
			cd.add(s);
		
		final JFreeChart c = createGanttChart(cd, title, subtitle, xLabel);
		final CategoryPlot plot = (CategoryPlot) c.getPlot();
		final Locale locale = style.getLocale();
		final TimeZone timezone = style.getTimeZone();
		
		// Use a date axis
		plot.setRangeAxis(new DateAxis(plot.getRangeAxis().getLabel(), timezone, locale));
		
		final GanttRenderer renderer = (GanttRenderer) plot.getRenderer();
		
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		df.setTimeZone(timezone);
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator("{1}: {3} - {4}", df));
        
		applyCategoryStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	@Override
	public ChartDefinition buildZChart(String name, List<ChartDataRow<Pair<Number, Number>, Number>> rows, ChartType type, ChartScaling scaling,
			ChartStyle style, String title, String subtitle, String yLabel, boolean yDiscrete, String xLabel, boolean xDiscrete, String rowLink)
		{
		final DefaultXYZDataset cd = new DefaultXYZDataset();
		for (ChartDataRow<Pair<Number, Number>, Number> row : rows)
			{
			final double[] xvalues = new double[row.getValues().size()];
			final double[] yvalues = new double[row.getValues().size()];
			final double[] zvalues = new double[row.getValues().size()];
			
			int i = 0;
			for (Map.Entry<Pair<Number, Number>, Number> ent : row.getValues().entrySet())
				{
				xvalues[i] = doubleValueOf(ent.getKey().getFirst());
				yvalues[i] = doubleValueOf(ent.getKey().getSecond());
				zvalues[i] = doubleValueOf(ent.getValue());
				i++;
				}
			
			cd.addSeries(row.getName(), new double[][] { xvalues, yvalues, zvalues });
			}
		
		final JFreeChart c = createXYZChart(cd, type, style, title, subtitle, yLabel, xLabel);
		final XYPlot plot = (XYPlot) c.getPlot();
		final Locale locale = style.getLocale();
		
		if (scaling.isDomainAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setDomainAxis(createLogarithmicAxis(plot.getDomainAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getDomainAxis(), xDiscrete, locale);
		
		if (scaling.isRangeAxisScaled())
			{
			// Use a logarithmically scaled axis
			plot.setRangeAxis(createLogarithmicAxis(plot.getRangeAxis().getLabel(), locale));
			}
		configureValueAxis(plot.getRangeAxis(), yDiscrete, locale);
		
		final XYItemRenderer renderer = plot.getRenderer();
		
		// Use a tooltip generator that respects the locale
		renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator(
				"{0}: ({1}, {2}) = {3}",
				NumberFormat.getInstance(locale),
				NumberFormat.getInstance(locale),
				NumberFormat.getInstance(locale)
				));
		
		applyXYStyle(c, renderer, rows.size(), style, rowLink);
		
		return (new ChartDefinition(name, c, type, scaling));
		}
	
	private double doubleValueOf(Number n)
		{
		if (n == null)
			return (Double.NaN);
		return (n.doubleValue());
		}
	
	private JFreeChart createPieChart(PieDataset cd, String title, String subtitle)
		{
		// Pass false for tooltips and urls, since ChartFactory doesn't accept a locale and creates useless default instances
		final JFreeChart c = ChartFactory.createPieChart(title, cd, true, false, false);
		
		if (!StringUtils.empty(subtitle))
			c.addSubtitle(new TextTitle(subtitle));
		
		return (c);
		}
	
	private JFreeChart createGanttChart(IntervalCategoryDataset cd, String title, String subtitle, String xLabel)
		{
		// Pass false for tooltips and urls, since ChartFactory doesn't accept a locale and creates useless default instances
		final JFreeChart c = ChartFactory.createGanttChart(title, null, xLabel, cd, true, false, false);
		
		if (!StringUtils.empty(subtitle))
			c.addSubtitle(new TextTitle(subtitle));
		
		return (c);
		}
	
	private JFreeChart createCategoryChart(CategoryDataset cd, ChartType type, String title, String subtitle, String yLabel, String xLabel)
		{
		final JFreeChart c;
		
		// Pass false for tooltips and urls, since ChartFactory doesn't accept a locale and creates useless default instances
		if (type instanceof PieChartType)
			c = ChartFactory.createMultiplePieChart(title, cd, TableOrder.BY_ROW, true, false, false);
		else if (type instanceof StackedBarChartType)
			c = ChartFactory.createStackedBarChart(title, xLabel, yLabel, cd, PlotOrientation.HORIZONTAL, true, false, false);
		else
			c = ChartFactory.createBarChart(title, xLabel, yLabel, cd, PlotOrientation.HORIZONTAL, true, false, false);
		
		if (!StringUtils.empty(subtitle))
			c.addSubtitle(new TextTitle(subtitle));
		
		return (c);
		}
	
	private JFreeChart createXYChart(IntervalTableXYDataset cd, ChartType type, ChartStyle style, String title, String subtitle, String yLabel, String xLabel)
		{
		final JFreeChart c;
		// Pass false for tooltips and urls, since ChartFactory doesn't accept a locale and creates useless default instances
		if (type instanceof AreaChartType)
			c = ChartFactory.createXYAreaChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
		else if (type instanceof StackedAreaChartType)
			c = ChartFactory.createStackedXYAreaChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
		else if (type instanceof BarChartType)
			c = ChartFactory.createXYBarChart(title, xLabel, false, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
		else if (type instanceof StackedBarChartType)
			{
			c = ChartFactory.createXYBarChart(title, xLabel, false, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
			// There's no createStackedXYBarChart method, so we have to override the renderer here
			((XYPlot) c.getPlot()).setRenderer(new StackedXYBarRenderer());
			}
		else if (type instanceof PointChartType)
			c = ChartFactory.createScatterPlot(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
		else if (type instanceof SteppedLineChartType)
			c = ChartFactory.createXYStepChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
		else if (type instanceof SteppedAreaChartType)
			{
			c = ChartFactory.createXYStepAreaChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
			// ChartFactory creates the renderer with AREA_AND_SHAPES
			((XYStepAreaRenderer) ((XYPlot) c.getPlot()).getRenderer()).setShapesVisible(false);
			}
		else if (type instanceof DifferenceChartType)
			{
			c = ChartFactory.createXYLineChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
			// There's no createXYDifferenceChart method, so we have to override the renderer here
			((XYPlot) c.getPlot()).setRenderer(new XYDifferenceRenderer(style.getPositivePaint(), style.getNegativePaint(), false));
			}
		else
			c = ChartFactory.createXYLineChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
		
		if (!StringUtils.empty(subtitle))
			c.addSubtitle(new TextTitle(subtitle));
		
		return (c);
		}
	
	private JFreeChart createXYZChart(XYZDataset cd, ChartType type, ChartStyle style, String title, String subtitle, String yLabel, String xLabel)
		{
		final JFreeChart c;
		// Pass false for tooltips and urls, since ChartFactory doesn't accept a locale and creates useless default instances
		if (type instanceof BlockChartType)
			{
			c = ChartFactory.createBubbleChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
			// There's no createXYBlockChart method, so we have to override the renderer here
			final XYScaledBlockRenderer renderer = new XYScaledBlockRenderer();
			// Scale the blocks for an ideal distribution (sqrt(N) * sqrt(N) blocks)
			final double nblocks = Math.sqrt(cd.getItemCount(0));
			final Range xrange = DatasetUtilities.findDomainBounds(cd);
			if (xrange != null)
				renderer.setBlockWidth(xrange.getLength() / nblocks);
			final Range yrange = DatasetUtilities.findRangeBounds(cd);
			if (yrange != null)
				renderer.setBlockHeight(yrange.getLength() / nblocks);
			// Use a PaintScale appropriate for the Z value range
			final Range range = DatasetUtilities.findZBounds(cd);
			if (range == null)
				renderer.setPaintScale(new AlphaPaintScale(0.0, 1.0, (Color) style.getRowPaint(0), (Color) style.getRowPaint(1)));
			else if (range.getLowerBound() == 0.0)
				renderer.setPaintScale(new AlphaPaintScale(range.getLowerBound(), range.getUpperBound(), (Color) style.getRowPaint(0), (Color) style.getRowPaint(1)));
			else if (range.getUpperBound() == 0.0)
				renderer.setPaintScale(new AlphaPaintScale(range.getLowerBound(), range.getUpperBound(), (Color) style.getRowPaint(0), (Color) style.getRowPaint(1)));
			else
				{
				final double maximum = Math.max(-range.getLowerBound(), range.getUpperBound());
				renderer.setPaintScale(new AlphaPaintScale(-maximum, maximum, (Color) style.getRowPaint(0), (Color) style.getRowPaint(1)));
				}
			((XYPlot) c.getPlot()).setRenderer(renderer);
			// Add a paint scale legend
			final NumberAxis axis = new NumberAxis();
			configureValueAxis(axis, false, style.getLocale());
			final PaintScaleLegend legend = new PaintScaleLegend(renderer.getPaintScale(), axis);
			legend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			legend.setPosition(RectangleEdge.RIGHT);
			c.addSubtitle(legend);
			}
		else
			{
			c = ChartFactory.createBubbleChart(title, xLabel, yLabel, cd, PlotOrientation.VERTICAL, true, false, false);
			// ChartFactory creates the renderer with SCALE_ON_RANGE_AXIS
			final Range range = DatasetUtilities.findZBounds(cd);
			final XYScaledBubbleRenderer renderer;
			if (range == null)
				renderer = new XYScaledBubbleRenderer(1.0);
			else
				{
				final double maximum = Math.max(-range.getLowerBound(), range.getUpperBound());
				// Scale the largest bubble to 1/8 of the chart area size
				renderer = new XYScaledBubbleRenderer(8.0 * maximum);
				}
			((XYPlot) c.getPlot()).setRenderer(renderer);
			}
		
		if (!StringUtils.empty(subtitle))
			c.addSubtitle(new TextTitle(subtitle));
		
		return (c);
		}
	
	private LogarithmicAxis createLogarithmicAxis(String label, Locale locale)
		{
		final LogarithmicAxis axis = new LogarithmicAxis(label);
		axis.setStrictValuesFlag(false);
		// Fix number formatting for horizontal LogarithmicAxis
		axis.setTickUnit(new NumberTickUnit(1.0, NumberFormat.getInstance(locale)), false, false);
		// Fix number formatting for vertical LogarithmicAxis
		axis.setNumberFormatOverride(NumberFormat.getInstance(locale));
		return (axis);
		}
	
	private void configureValueAxis(ValueAxis axis, boolean discrete, Locale locale)
		{
		if (discrete)
			{
			// Use integer intervals for the range axis
			axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits(locale));
			}
		else
			axis.setStandardTickUnits(NumberAxis.createStandardTickUnits(locale));
		}
	
	private void applyPieStyle(JFreeChart c, PiePlot plot, List<Comparable<?>> columns, ChartStyle style, String rowLink)
		{
		// Use a label generator that shows values and respects the locale
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}", style.getLocale()));
		
		// Use a tooltip generator that respects the locale
		plot.setToolTipGenerator(new StandardPieToolTipGenerator(StandardPieToolTipGenerator.DEFAULT_TOOLTIP_FORMAT, style.getLocale()));
		
		if (rowLink != null)
			plot.setURLGenerator(new CustomPieURLGenerator(style, rowLink));
		
		// Apply format after replacing the axes but before overriding colors
		applyChartStyle(c, style);
		
		// Not set by the theme
		plot.setLabelBackgroundPaint(style.getBackgroundPaint());
		plot.setLabelPaint(style.getForegroundPaint());
		plot.setLabelShadowPaint(null);
		plot.setLabelOutlinePaint(null);
		plot.setShadowPaint(null);
		
		// Bar colors
		int i = 0;
		for (Comparable<?> key : columns)
			{
			plot.setSectionPaint(key, style.getRowPaint(i));
			i++;
			}
		}
	
	private void applyCategoryStyle(JFreeChart c, CategoryItemRenderer renderer, int n, ChartStyle style, String rowLink)
		{
		if (rowLink != null)
			renderer.setBaseItemURLGenerator(new CustomCategoryURLGenerator(style, rowLink));
		
		// Apply format after replacing the axes but before overriding colors
		applyChartStyle(c, style);
		
		// Bar colors
		for (int i = 0; i < n; i++)
			{
			renderer.setSeriesPaint(i, style.getRowPaint(i));
			renderer.setSeriesStroke(i, style.getRowStroke(i));
			}
		}
	
	private void applyXYStyle(JFreeChart c, XYItemRenderer renderer, int n, ChartStyle style, String rowLink)
		{
		if (rowLink != null)
			renderer.setURLGenerator(new CustomXYURLGenerator(style, rowLink));
		
		// Apply format before overriding colors
		applyChartStyle(c, style);
		
		// Series coloring
		for (int i = 0; i < n; i++)
			{
			renderer.setSeriesPaint(i, style.getRowPaint(i));
			renderer.setSeriesStroke(i, style.getRowStroke(i));
			}
		}
	
	private void applyChartStyle(JFreeChart c, ChartStyle style)
		{
		// Some ChartFactory methods set this, some don't
		c.getPlot().setForegroundAlpha(0.5f);
		
		getTheme(style).apply(c);
		}
	
	private ChartTheme getTheme(ChartStyle style)
		{
		try	{
			final StandardChartTheme theme = (StandardChartTheme) defaultTheme.clone();
			
			final Font oldExtraLargeFont = theme.getExtraLargeFont();
			final Font oldLargeFont = theme.getLargeFont();
			final Font oldRegularFont = theme.getRegularFont();
			final Font oldSmallFont = theme.getSmallFont();
			final String newFontName = style.getFontFamily();
			
			final Font extraLargeFont = new Font(newFontName, oldExtraLargeFont.getStyle(), oldExtraLargeFont.getSize());
			final Font largeFont = new Font(newFontName, oldLargeFont.getStyle(), oldLargeFont.getSize());
			final Font regularFont = new Font(newFontName, oldRegularFont.getStyle(), oldRegularFont.getSize());
			final Font smallFont = new Font(newFontName, oldSmallFont.getStyle(), oldSmallFont.getSize());
			
			theme.setExtraLargeFont(extraLargeFont);
			theme.setLargeFont(largeFont);
			theme.setRegularFont(regularFont);
			theme.setSmallFont(smallFont);
			
			final Paint bg = style.getBackgroundPaint();
			theme.setChartBackgroundPaint(bg);
			theme.setPlotBackgroundPaint(bg);
			theme.setLegendBackgroundPaint(bg);
			
			final Paint gr = style.getGridPaint();
			theme.setDomainGridlinePaint(gr);
			theme.setRangeGridlinePaint(gr);
			theme.setLabelLinkPaint(gr);
			
			final Paint fg = style.getForegroundPaint();
			theme.setAxisLabelPaint(fg);
			theme.setItemLabelPaint(fg);
			theme.setTitlePaint(fg);
			theme.setSubtitlePaint(fg);
			theme.setLegendItemPaint(fg);
			theme.setTickLabelPaint(fg);
			
			return (theme);
			}
		catch (CloneNotSupportedException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public ChartType getChartType(String name)
		{
		return (types.get(name));
		}
	
	@Override
	public Collection<ChartType> getChartTypes()
		{
		return (types.values());
		}
	
	@Override
	public ChartScaling getChartScaling(String name)
		{
		return (scalings.get(name));
		}
	
	@Override
	public Collection<ChartScaling> getChartScalings()
		{
		return (scalings.values());
		}
	}
