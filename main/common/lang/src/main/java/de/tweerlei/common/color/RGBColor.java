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
package de.tweerlei.common.color;

import java.awt.Color;

import de.tweerlei.common.util.MathUtils;

/**
 * A color represented as Red, Green and Blue intensity
 * 
 * @author Robert Wruck
 */
public class RGBColor
	{
	/** Color intensity for the red component, according to ITU-R BT.601 */
	public static final double RED_WEIGHT = 0.299;
	/** Color intensity for the green component, according to ITU-R BT.601 */
	public static final double GREEN_WEIGHT = 0.587;
	/** Color intensity for the blue component, according to ITU-R BT.601 */
	public static final double BLUE_WEIGHT = 0.114;
	
	private static final RGBColor BLACK = new RGBColor(0.0, 0.0, 0.0);
	private static final RGBColor WHITE = new RGBColor(1.0, 1.0, 1.0);
	
	private double red;
	private double green;
	private double blue;
	
	/**
	 * Default constructor.
	 * Initializes the color to black (0, 0, 0)
	 */
	public RGBColor()
		{
		}
	
	/**
	 * Constructor
	 * @param c Color object
	 */
	public RGBColor(Color c)
		{
		setColor(c);
		}
	
	/**
	 * Constructor
	 * @param red Red (0..1)
	 * @param green Green (0..1)
	 * @param blue Blue (0..1)
	 */
	public RGBColor(double red, double green, double blue)
		{
		setRGB(red, green, blue);
		}
	
	/**
	 * Set all components from a Color object
	 * @param c Color object
	 * @return this
	 */
	public RGBColor setColor(Color c)
		{
		return (setRGB(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0));
		}
	
	/**
	 * Set all components
	 * @param red Red (0..1)
	 * @param green Green (0..1)
	 * @param blue Blue (0..1)
	 * @return this
	 */
	public RGBColor setRGB(double red, double green, double blue)
		{
		setRed(red);
		setGreen(green);
		setBlue(blue);
		
		return (this);
		}
	
	/**
	 * Get this color's lightness (gray value)
	 * @return Lightness
	 */
	public double getLightness()
		{
		return (RED_WEIGHT * red + GREEN_WEIGHT * green + BLUE_WEIGHT * blue);
		}
	
	/**
	 * Get a Color object for this RGBColor
	 * @return Color
	 */
	public Color toColor()
		{
		return (new Color((int) Math.round(getRed() * 255.0), (int) Math.round(getGreen() * 255.0), (int) Math.round(getBlue() * 255.0)));
		}
	
	/**
	 * Brighten this color, same as mix(white, factor)
	 * @param factor Factor (0..1) where 0 means no change and 1 means brighten to white
	 * @return this
	 */
	public RGBColor brighten(double factor)
		{
		return (mix(WHITE, factor));
		}
	
	/**
	 * Darken this color, same as mix(black, factor)
	 * @param factor Factor (0..1) where 0 means no change and 1 means darken to black
	 * @return this
	 */
	public RGBColor darken(double factor)
		{
		return (mix(BLACK, factor));
		}
	
	/**
	 * Mix this color with another
	 * @param other Other color
	 * @param factor Factor (0..1) where 0 means to use this color only and 1 means use the other color only
	 * @return this
	 */
	public RGBColor mix(RGBColor other, double factor)
		{
		final double f = MathUtils.clip(factor, 0.0, 1.0);
		red = MathUtils.mix(f, other.red, red);
		green = MathUtils.mix(f, other.green, green);
		blue = MathUtils.mix(f, other.blue, blue);
		return (this);
		}
	
	/**
	 * Get the red (0..1)
	 * @return the red
	 */
	public double getRed()
		{
		return red;
		}

	/**
	 * Set the red (0..1)
	 * @param red the red to set
	 */
	public void setRed(double red)
		{
		this.red = MathUtils.clip(red, 0.0, 1.0);
		}

	/**
	 * Get the green (0..1)
	 * @return the green
	 */
	public double getGreen()
		{
		return green;
		}

	/**
	 * Set the green (0..1)
	 * @param green the green to set
	 */
	public void setGreen(double green)
		{
		this.green = MathUtils.clip(green, 0.0, 1.0);
		}

	/**
	 * Get the blue (0..1)
	 * @return the blue
	 */
	public double getBlue()
		{
		return blue;
		}

	/**
	 * Set the blue (0..1)
	 * @param blue the blue to set
	 */
	public void setBlue(double blue)
		{
		this.blue = MathUtils.clip(blue, 0.0, 1.0);
		}
	
	public String toString()
		{
		return ("RGB(" + red + ", " + green + ", " + blue + ")");
		}
	}
