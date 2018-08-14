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

import de.tweerlei.common.util.MathUtils;

/**
 * A color represented as Hue, Saturation and Lightness
 * 
 * @author Robert Wruck
 */
public class HSLColor
	{
	private static final double YELLOW_WEIGHT = RGBColor.RED_WEIGHT + RGBColor.GREEN_WEIGHT;
	private static final double CYAN_WEIGHT = RGBColor.GREEN_WEIGHT + RGBColor.BLUE_WEIGHT;
	private static final double MAGENTA_WEIGHT = RGBColor.BLUE_WEIGHT + RGBColor.RED_WEIGHT;
	
	/** 30 degrees, in radians */
	private static final double DEG_30 = Math.PI / 6.0;
	/** 60 degrees, in radians */
	private static final double DEG_60 = Math.PI / 3.0;
	/** cosine of 30 degrees ~ 0.866 */
	private static final double COS_30 = Math.cos(DEG_30);
	/** sine of 30 degrees = 0.5 */
	private static final double SIN_30 = Math.sin(DEG_30);
	/** Limit below which to consider a color value as 0 */
	private static final double EPSILON = 1.0 / 256.0;
	
	private double hue;
	private double saturation;
	private double lightness;
	
	/**
	 * Default constructor.
	 * Initializes the color to black (0, 0, 0)
	 */
	public HSLColor()
		{
		}
	
	/**
	 * Constructor
	 * @param hue Hue (0..1)
	 * @param saturation Saturation (0..1)
	 * @param lightness Lightness (0..1)
	 */
	public HSLColor(double hue, double saturation, double lightness)
		{
		setHSL(hue, saturation, lightness);
		}
	
	/**
	 * Constructor
	 * @param c RGBColor
	 */
	public HSLColor(RGBColor c)
		{
		setRGBColor(c);
		}
	
	/**
	 * Set all values
	 * @param hue Hue (0..1)
	 * @param saturation Saturation (0..1)
	 * @param lightness Lightness (0..1)
	 * @return this
	 */
	public HSLColor setHSL(double hue, double saturation, double lightness)
		{
		setHue(hue);
		setSaturation(saturation);
		setLightness(lightness);
		
		return (this);
		}
	
	/**
	 * Set from an RGBColor
	 * @param c RGBColor
	 * @return this
	 */
	public HSLColor setRGBColor(RGBColor c)
		{
		final double r = c.getRed();
		final double g = c.getGreen();
		final double b = c.getBlue();
		
		// The lightness is given by the RGB luminance
		final double l = c.getLightness();
		
		// Get the color vector inside the color hexagon that is defined by the three base vectors:
		// R = (cos 0, sin 0); G = (cos 120, sin 120); B = (cos 240, sin 240)
		final double x = 1.0 * r - SIN_30 * g - SIN_30 * b;
		final double y = 0.0 * r + COS_30 * g - COS_30 * b;
		
		if ((x > -EPSILON) && (x < EPSILON) && (y > -EPSILON) && (y < EPSILON))
			{
			// Gray - trivial
			setHue(0.0);
			setSaturation(0.0);
			setLightness(l);
			return (this);
			}
		
		// The hue is the angle of the color vector
		final double h0 = Math.atan2(y, x);
		final double h = (h0 < 0) ? h0 + 2.0 * Math.PI : h0;
		
		// The color section (0 = red, 1 = yellow, 2 = green, ..., 5 = magenta)
		final double v = Math.floor(h / DEG_60);
		// In Java, floor(h / x) + h % x != h
		final double w = h - v * DEG_60;	// h % DEG_60;
		// The ratio of the starting color to the ending color of the section
		final double a = w / DEG_60;
		
		// The absolute saturation that needs to be normalized
		final double sabs = Math.sqrt(x * x + y * y);
		// The maximum saturation for the hue
		final double smax = 1.0 + COS_30 - Math.cos(w - DEG_30);
		
		// The lightness where the maximum saturation is reached
		final double lmax;
		switch ((int) v)
			{
			case 0:
				lmax = MathUtils.mix(a, YELLOW_WEIGHT, RGBColor.RED_WEIGHT);
				break;
			case 1:
				lmax = MathUtils.mix(a, RGBColor.GREEN_WEIGHT, YELLOW_WEIGHT);
				break;
			case 2:
				lmax = MathUtils.mix(a, CYAN_WEIGHT, RGBColor.GREEN_WEIGHT);
				break;
			case 3:
				lmax = MathUtils.mix(a, RGBColor.BLUE_WEIGHT, CYAN_WEIGHT);
				break;
			case 4:
				lmax = MathUtils.mix(a, MAGENTA_WEIGHT, RGBColor.BLUE_WEIGHT);
				break;
			default:
				lmax = MathUtils.mix(a, RGBColor.RED_WEIGHT, MAGENTA_WEIGHT);
				break;
			}
		
		// The maximum saturation for the hue and lightness
		final double sl = (l > lmax) ? ((1.0 - l) / (1.0 - lmax)) : (l / lmax);
		
		final double s = sabs / (sl * smax);
		
		setHue(h / (2.0 * Math.PI));
		setLightness(l);
		setSaturation(s);
		
		return (this);
		}
	
	/**
	 * Get the RGB color
	 * @return RGBColor object
	 */
	public RGBColor toRGBColor()
		{
		final double h = getHue() * 6.0;
		final double v = Math.floor(h);
		// In Java, floor(h / x) + h % x != h
		final double w = h - v;	// h % 1.0;
		
		final RGBColor ret;
		switch ((int) v)
			{
			case 0:
				ret = new RGBColor(1.0, w, 0.0);
				break;
			case 1:
				ret = new RGBColor(1.0 - w, 1.0, 0.0);
				break;
			case 2:
				ret = new RGBColor(0.0, 1.0, w);
				break;
			case 3:
				ret = new RGBColor(0.0, 1.0 - w, 1.0);
				break;
			case 4:
				ret = new RGBColor(w, 0.0, 1.0);
				break;
			default:
				ret = new RGBColor(1.0, 0.0, 1.0 - w);
				break;
			}
		
		// Adjust lightness
		final double l = getLightness();
		final double lret = ret.getLightness();
		
		if (l > lret)
			{
			// brighten
			final double f = (l - lret) / (1.0 - lret);
			
			ret.brighten(f);
			}
		else
			{
			// darken
			final double f = l / lret;
			
			ret.darken(1.0 - f);
			}
		
		// Adjust saturation
		final double s = getSaturation();
		ret.mix(new RGBColor(l, l, l), 1.0 - s);
		
		return (ret);
		}
	
	/**
	 * Get the hue (0..1)
	 * @return the hue
	 */
	public double getHue()
		{
		return hue;
		}

	/**
	 * Set the hue (0..1)
	 * @param hue the hue to set
	 */
	public void setHue(double hue)
		{
		final double h = MathUtils.clip(hue, 0.0, 1.0);
		if (h == 1.0)
			this.hue = 0.0;
		else
			this.hue = h;
		}

	/**
	 * Get the saturation (0..1)
	 * @return the saturation
	 */
	public double getSaturation()
		{
		return saturation;
		}

	/**
	 * Set the saturation (0..1)
	 * @param saturation the saturation to set
	 */
	public void setSaturation(double saturation)
		{
		this.saturation = MathUtils.clip(saturation, 0.0, 1.0);
		}

	/**
	 * Set the lightness (0..1)
	 * @return the lightness
	 */
	public double getLightness()
		{
		return lightness;
		}

	/**
	 * Set the lightness (0..1)
	 * @param lightness the lightness to set
	 */
	public void setLightness(double lightness)
		{
		this.lightness = MathUtils.clip(lightness, 0.0, 1.0);
		}
	
	public String toString()
		{
		return ("HSL(" + hue + ", " + saturation + ", " + lightness + ")");
		}
	}
