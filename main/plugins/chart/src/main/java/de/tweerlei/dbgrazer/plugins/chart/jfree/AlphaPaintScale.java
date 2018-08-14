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

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.PaintScale;

/**
 * PaintScale that generates Paints by adding alpha values to given base paints,
 * one for positive values and another one for negative values.
 * 
 * @author Robert Wruck
 */
public class AlphaPaintScale implements PaintScale
	{
	private final double lowerBound;
	private final double upperBound;
	private final Color positive;
	private final Color negative;
	
	/**
	 * Constructor
	 * @param lowerBound Lower bound
	 * @param upperBound Upper bound
	 * @param positive Base paint for positive values
	 * @param negative Base paint for negative values
	 */
	public AlphaPaintScale(double lowerBound, double upperBound, Color positive, Color negative)
		{
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.positive = positive;
		this.negative = negative;
		}
	
	@Override
	public double getLowerBound()
		{
		return lowerBound;
		}
	
	@Override
	public double getUpperBound()
		{
		return upperBound;
		}
	
	@Override
	public Paint getPaint(double value)
		{
		final double n = (value < lowerBound) ? lowerBound : ((value > upperBound) ? upperBound : value);
		
		if (n < 0.0)
			return new Color(negative.getRed(), negative.getGreen(), negative.getBlue(), (int) (255.0 * n / lowerBound));
		else
			return new Color(positive.getRed(), positive.getGreen(), positive.getBlue(), (int) (255.0 * n / upperBound));
		}
	}
