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
package de.tweerlei.common.math;

/**
 * Rational number, expressed as numerator and denominator
 * 
 * @author Robert Wruck
 */
public class Rational extends Number implements Comparable
	{
	/** The rational number 0 */
	public static final Rational ZERO = new Rational(0, 1);
	/** The rational number 1 */
	public static final Rational ONE = new Rational(1, 1);
	
	private final long numerator;
	private final long denominator;
	
	/**
	 * Constructor
	 * @param numerator numerator
	 */
	public Rational(long numerator)
		{
		this(numerator, 1L);
		}
	
	/**
	 * Constructor
	 * @param numerator numerator
	 * @param denominator denominator
	 */
	public Rational(long numerator, long denominator)
		{
		this.numerator = numerator;
		this.denominator = denominator;
		}
	
	/**
	 * Get the numerator
	 * @return numerator
	 */
	public long getNumerator()
		{
		return numerator;
		}
	
	/**
	 * Get the denominator
	 * @return denominator
	 */
	public long getDenominator()
		{
		return denominator;
		}
	
	/**
	 * Return a normalized version
	 * @return Rational
	 */
	public Rational normalize()
		{
		final long gcd = greatestCommonDivisor(numerator, denominator);
		if (gcd > 1)
			{
			if (denominator < 0)
				return (new Rational(-numerator / gcd, -denominator / gcd));
			else
				return (new Rational(numerator / gcd, denominator / gcd));
			}
		
		if (denominator < 0)
			return (new Rational(-numerator, -denominator));
		
		return (this);
		}
	
	/**
	 * Return the reciprocal Rational
	 * @return Rational
	 */
	public Rational reciprocal()
		{
		return (new Rational(denominator, numerator));
		}
	
	/**
	 * Add another Rational
	 * @param r Rational to add
	 * @return Result
	 */
	public Rational add(Rational r)
		{
		final long lcm = leastCommonMultiple(denominator, r.denominator);
		if (lcm == 0)
			return (new Rational(0, 0));
		
		return (new Rational(lcm / denominator * numerator + lcm / r.denominator * r.numerator, lcm));
		}
	
	/**
	 * Subtract another Rational
	 * @param r Rational
	 * @return Result
	 */
	public Rational subtract(Rational r)
		{
		final long lcm = leastCommonMultiple(denominator, r.denominator);
		if (lcm == 0)
			return (new Rational(0, 0));
		
		return (new Rational(lcm / denominator * numerator - lcm / r.denominator * r.numerator, lcm));
		}
	
	/**
	 * Multiply by another Rational
	 * @param r Rational
	 * @return Result
	 */
	public Rational multiply(Rational r)
		{
		return (new Rational(numerator * r.numerator, denominator * r.denominator));
		}
	
	/**
	 * Divide by another Rational
	 * @param r Rational
	 * @return Result
	 */
	public Rational divide(Rational r)
		{
		return (new Rational(numerator * r.denominator, denominator * r.numerator));
		}
	
	/**
	 * Calculate the least common multiple of two numbers
	 * @param a A number
	 * @param b Another number
	 * @return LCM
	 */
	public static long leastCommonMultiple(long a, long b)
		{
		final long gcd = greatestCommonDivisor(a, b);
		if (gcd == 0)
			return (0);
		
		return ((a / gcd) * b);
		}
	
	/**
	 * Calculate the greatest common divisor of two numbers
	 * @param a A number
	 * @param b Another number
	 * @return GCD
	 */
	public static long greatestCommonDivisor(long a, long b)
		{
		return (gcd(Math.abs(a), Math.abs(b)));
		}
	
	private static long gcd(long a, long b)
		{
		if (a == 0)
			return (b);
		if (b == 0)
			return (a);
		if (a > b)
			return (gcd(b, a % b));
		if (a < b)
			return (gcd(a, b % a));
		return (a);
		}
	
	public int intValue()
		{
		return (int) (numerator / denominator);
		}

	public long longValue()
		{
		return (numerator / denominator);
		}

	public float floatValue()
		{
		return ((float) numerator / (float) denominator);
		}

	public double doubleValue()
		{
		return ((double) numerator / (double) denominator);
		}
	
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (denominator ^ (denominator >>> 32));
		result = prime * result + (int) (numerator ^ (numerator >>> 32));
		return result;
		}
	
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rational other = (Rational) obj;
		if (denominator != other.denominator)
			return false;
		if (numerator != other.numerator)
			return false;
		return true;
		}
	
	public int compareTo(Object o)
		{
		final Rational r = (Rational) o; // Expect a ClassCastException on improper usage
		final double d1 = doubleValue();
		final double d2 = r.doubleValue();
		if (d1 < d2)
			return (-1);
		if (d1 > d2)
			return (1);
		return (0);
		}
	
	/**
	 * Parse a Rational from its toString representation
	 * @param s String
	 * @return Parsed Rational
	 * @throws NumberFormatException if the string cannot be parsed
	 */
	public static Rational valueOf(String s) throws NumberFormatException
		{
		final String n;
		final String d;
		final int i = s.indexOf('/');
		if (i < 0)
			{
			n = s;
			d = "1";
			}
		else
			{
			n = s.substring(0, i);
			d = s.substring(i + 1);
			}
		
		return (new Rational(Long.parseLong(n), Long.parseLong(d)));
		}
	
	public String toString()
		{
		return (numerator + "/" + denominator);
		}
	}
