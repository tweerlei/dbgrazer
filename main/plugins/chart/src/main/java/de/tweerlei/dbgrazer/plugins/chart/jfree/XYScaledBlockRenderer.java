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
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

/**
 * XYBlockRenderer that scales the blocks to a reasonable size
 * 
 * @author Robert Wruck
 */
public class XYScaledBlockRenderer extends XYBlockRenderer
	{
    /**
     * Creates a new <code>XYScaledBlockRenderer</code> instance with default
     * attributes.
     */
    public XYScaledBlockRenderer()
    	{
    	}
    
    /**
     * Draws the block representing the specified item.
     *
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area.
     * @param info  the plot rendering info.
     * @param plot  the plot.
     * @param domainAxis  the x-axis.
     * @param rangeAxis  the y-axis.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairState  the crosshair state.
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass)
    	{
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double z = 0.0;
        if (dataset instanceof XYZDataset)
            z = ((XYZDataset) dataset).getZValue(series, item);
        
        Paint p = getPaintScale().getPaint(z);
        double xx0 = domainAxis.valueToJava2D(x - getBlockWidth() / 2.0, dataArea, plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y - getBlockHeight() / 2.0, dataArea, plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + getBlockWidth() / 2.0, dataArea, plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + getBlockHeight() / 2.0, dataArea, plot.getRangeAxisEdge());
        Rectangle2D block;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.equals(PlotOrientation.HORIZONTAL))
        	{
            block = new Rectangle2D.Double(Math.min(yy0, yy1),
                    Math.min(xx0, xx1), Math.abs(yy1 - yy0),
                    Math.abs(xx0 - xx1));
        	}
        else
        	{
            block = new Rectangle2D.Double(Math.min(xx0, xx1),
                    Math.min(yy0, yy1), Math.abs(xx1 - xx0),
                    Math.abs(yy1 - yy0));
        	}
        g2.setPaint(p);
        g2.fill(block);
//        g2.setStroke(getItemOutlineStroke(series, item));
//        g2.setPaint(getItemOutlinePaint(series, item));
//        g2.draw(block);

        EntityCollection entities = state.getEntityCollection();
        if (entities != null)
            addEntity(entities, block, dataset, series, item, 0.0, 0.0);
    	}
	}
