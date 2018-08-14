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
package de.tweerlei.common.util;

/**
 * \addtogroup misc Verschiedenes
 * @{
 */

/**
 * Hilfsfunktionen für Zahlen
 * 
 * @author Robert Wruck
 */
public final class MathUtils
	{
	/** Unerreichbarer Konstruktor */
	private MathUtils()
		{
		// s.o.
		}
	
	/**
	 * @return v, wenn l <= v <= h
	 *         l, wenn v < l
	 *         h, wenn v > h
	 */
	public static int clip(int v, int l, int h)
		{
		return ((v < l) ? l : ((v > h) ? h : v));
		}
	
	/**
	 * @return v, wenn l <= v <= h
	 *         l, wenn v < l
	 *         h, wenn v > h
	 */
	public static long clip(long v, long l, long h)
		{
		return ((v < l) ? l : ((v > h) ? h : v));
		}
	
	/**
	 * @return v, wenn l <= v <= h
	 *         l, wenn v < l
	 *         h, wenn v > h
	 */
	public static float clip(float v, float l, float h)
		{
		return ((v < l) ? l : ((v > h) ? h : v));
		}
	
	/**
	 * @return v, wenn l <= v <= h
	 *         l, wenn v < l
	 *         h, wenn v > h
	 */
	public static double clip(double v, double l, double h)
		{
		return ((v < l) ? l : ((v > h) ? h : v));
		}
	
	/**
	 * @return true, wenn |v1 - v2| < epsilon
	 */
	public static boolean floatEqual(float v1, float v2, float epsilon)
		{
		return (Math.abs(v1 - v2) < epsilon);
		}
	
	/**
	 * @return true, wenn |v1 - v2| < epsilon
	 */
	public static boolean floatEqual(double v1, double v2, double epsilon)
		{
		return (Math.abs(v1 - v2) < epsilon);
		}
	
	/**
	 * Calculate the checksum of a number in a given base
	 * @param number >= 0
	 * @param base > 1
	 * @return Checksum
	 */
	public static int checksum(int number, int base)
		{
		return ((number == 0) ? 0 : (number - 1) % (base - 1) + 1);
		}
	
	/**
	 * Mix two values with a given ratio
	 * @param a Ratio (0..1)
	 * @param l Left value
	 * @param r Right value
	 * @return a * l + (1 - a) * r
	 */
	public static double mix(double a, double l, double r)
		{
		return (a * l + (1.0 - a) * r);
		}
	}

/** @} */
