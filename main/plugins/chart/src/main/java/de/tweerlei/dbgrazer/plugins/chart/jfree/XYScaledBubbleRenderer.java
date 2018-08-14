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
package de.tweerlei.dbgrazer.plugins.chart.jfree;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;

/**
 * XYBubbleRenderer that scales the bubbles to a reasonable size
 * 
 * @author Robert Wruck
 */
public class XYScaledBubbleRenderer extends XYBubbleRenderer
	{
	private double zScale;
	
    /**
     * Constructs a new renderer.
     * @param scale The scale factor for z (bubble size) values
     */
    public XYScaledBubbleRenderer(double scale)
    	{
        super(SCALE_ON_BOTH_AXES);
        this.zScale = scale;
    	}
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset (an {@link XYZDataset} is expected).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass)
    	{
        // return straight away if the item is not visible
        if (!getItemVisible(series, item))
            return;

        PlotOrientation orientation = plot.getOrientation();

        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = Double.NaN;
        if (dataset instanceof XYZDataset)
        	{
            XYZDataset xyzData = (XYZDataset) dataset;
            z = xyzData.getZValue(series, item);
        	}
        if (!Double.isNaN(z))
        	{
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, domainAxisLocation);
            double transY = rangeAxis.valueToJava2D(y, dataArea, rangeAxisLocation);

            double transDomain = 0.0;
            double transRange = 0.0;
            double domMin, domMax, rangeMin, rangeMax;

            switch(getScaleType())
            	{
                case SCALE_ON_DOMAIN_AXIS:
                    domMin = domainAxis.valueToJava2D(domainAxis.getLowerBound(), dataArea, domainAxisLocation);
                    domMax = domainAxis.valueToJava2D(domainAxis.getUpperBound(), dataArea, domainAxisLocation);
                    transDomain = z / zScale * (domMax - domMin);
                    transRange = transDomain;
                    break;
                case SCALE_ON_RANGE_AXIS:
                    rangeMin = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, rangeAxisLocation);
                    rangeMax = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, rangeAxisLocation);
                    transRange = z / zScale * (rangeMax - rangeMin);
                    transDomain = transRange;
                    break;
                default:
	                domMin = domainAxis.valueToJava2D(domainAxis.getLowerBound(), dataArea, domainAxisLocation);
	                domMax = domainAxis.valueToJava2D(domainAxis.getUpperBound(), dataArea, domainAxisLocation);
	                rangeMin = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, rangeAxisLocation);
	                rangeMax = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, rangeAxisLocation);
                    transDomain = z / zScale * (domMax - domMin);
                    transRange = z / zScale * (rangeMax - rangeMin);
                    break;
            	}
            transDomain = Math.abs(transDomain);
            transRange = Math.abs(transRange);
            Ellipse2D circle = null;
            if (orientation == PlotOrientation.VERTICAL)
            	{
                circle = new Ellipse2D.Double(transX - transDomain / 2.0,
                        transY - transRange / 2.0, transDomain, transRange);
            	}
            else if (orientation == PlotOrientation.HORIZONTAL)
            	{
                circle = new Ellipse2D.Double(transY - transRange / 2.0,
                        transX - transDomain / 2.0, transRange, transDomain);
            	}
            g2.setPaint(getItemPaint(series, item));
            g2.fill(circle);
//            g2.setStroke(getItemOutlineStroke(series, item));
//            g2.setPaint(getItemOutlinePaint(series, item));
//            g2.draw(circle);

            if (isItemLabelVisible(series, item))
            	{
                if (orientation == PlotOrientation.VERTICAL)
                	{
                    drawItemLabel(g2, orientation, dataset, series, item,
                            transX, transY, false);
                	}
                else if (orientation == PlotOrientation.HORIZONTAL)
                	{
                    drawItemLabel(g2, orientation, dataset, series, item,
                            transY, transX, false);
                	}
            	}

            // add an entity if this info is being collected
            EntityCollection entities = null;
            if (info != null)
            	{
                entities = info.getOwner().getEntityCollection();
                if (entities != null && circle.intersects(dataArea))
                	{
                    addEntity(entities, circle, dataset, series, item,
                            circle.getCenterX(), circle.getCenterY());
                	}
            	}

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
                    rangeAxisIndex, transX, transY, orientation);
        	}
    	}
	}
