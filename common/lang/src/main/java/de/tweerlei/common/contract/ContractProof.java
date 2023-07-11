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
package de.tweerlei.common.contract;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Utility methods to ensure contract compliance
 * 
 * @author Robert Wruck
 */
public class ContractProof
	{
	/** No instances */
	private ContractProof()
		{
		}
	
	/**
	 * Assert an invariant
	 * @param name Invariant description
	 * @param expr Expression
	 */
	public static void invariant(String name, boolean expr)
		{
		if (!expr)
			throw new InvariantViolationException(name);
		}
	
	/**
	 * Assert that a value is not null
	 * @param name Name of checked object
	 * @param value Checked object
	 */
	public static void notNull(String name, Object value)
		{
		if (value == null)
			throw new ParameterNullException(name);
		}
	
	/**
	 * Assert that a collection has a specific size
	 * @param name Checked list name
	 * @param l Checked collection
	 * @param min Minimum size
	 * @param max Maximum size
	 */
	public static void size(String name, Collection l, int min, int max)
		{
		if (l == null)
			throw new ParameterNullException(name);
		
		inRange(name, l.size(), min, max);
		}
	
	/**
	 * Assert that a collection has a specific size
	 * @param name Checked list name
	 * @param l Checked collection
	 * @param size Size
	 */
	public static void size(String name, Collection l, int size)
		{
		size(name, l, size, size);
		}
	
	/**
	 * Assert that a collection is not empty
	 * @param name Checked list name
	 * @param l Checked collection
	 */
	public static void notEmpty(String name, Collection l)
		{
		size(name, l, 1, Integer.MAX_VALUE);
		}
	
	/**
	 * Assert that an array has a specific size
	 * @param name Checked list name
	 * @param l Checked array
	 * @param min Minimum size
	 * @param max Maximum size
	 */
	public static void size(String name, Object l, int min, int max)
		{
		if (l == null)
			throw new ParameterNullException(name);
		
		inRange(name, Array.getLength(l), min, max);
		}
	
	/**
	 * Assert that an array has a specific size
	 * @param name Checked list name
	 * @param l Checked array
	 * @param size Size
	 */
	public static void size(String name, Object l, int size)
		{
		size(name, l, size, size);
		}
	
	/**
	 * Assert that a string is of given length
	 * @param name Checked string name
	 * @param value Checked string
	 * @param min Minimum length
	 * @param max Maximum length
	 */
	public static void length(String name, String value, int min, int max)
		{
		if (value == null)
			throw new ParameterNullException(name);
		
		inRange(name, value.length(), min, max);
		}
	
	/**
	 * Assert that a string is of given length
	 * @param name Checked string name
	 * @param value Checked string
	 * @param len Length
	 */
	public static void length(String name, String value, int len)
		{
		length(name, value, len, len);
		}
	
	/**
	 * Assert that a string is not empty
	 * @param name Checked list name
	 * @param value Checked string
	 */
	public static void notEmpty(String name, String value)
		{
		length(name, value, 1, Integer.MAX_VALUE);
		}
	
	/**
	 * Assert that a value is greater than another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param min Minimum value
	 */
	public static void greaterThan(String name, long value, long min)
		{
		if (value <= min)
			throw new ParameterValueException(name, "Not greater than " + min);
		}
	
	/**
	 * Assert that a value is greater than another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param min Minimum value
	 */
	public static void greaterThan(String name, Integer value, int min)
		{
		if (value == null)
			throw new ParameterNullException(name);
		
		greaterThan(name, value.intValue(), min);
		}
	
	/**
	 * Assert that a value is greater than or equal to another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param min Minimum value
	 */
	public static void greaterEqual(String name, long value, long min)
		{
		if (value < min)
			throw new ParameterValueException(name, "Less than " + min);
		}
	
	/**
	 * Assert that a value is greater than or equal to another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param min Minimum value
	 */
	public static void greaterEqual(String name, Integer value, int min)
		{
		if (value == null)
			throw new ParameterNullException(name);
		
		greaterEqual(name, value.intValue(), min);
		}
	
	/**
	 * Assert that a value is less than another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param max Maximum value
	 */
	public static void lessThan(String name, long value, long max)
		{
		if (value >= max)
			throw new ParameterValueException(name, "Not less than " + max);
		}
	
	/**
	 * Assert that a value is less than another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param max Maximum value
	 */
	public static void lessThan(String name, Integer value, int max)
		{
		if (value == null)
			throw new ParameterNullException(name);
		
		lessThan(name, value.intValue(), max);
		}
	
	/**
	 * Assert that a value is less than or equal to another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param max Maximum value
	 */
	public static void lessEqual(String name, long value, long max)
		{
		if (value > max)
			throw new ParameterValueException(name, "Greater than " + max);
		}
	
	/**
	 * Assert that a value is less than or equal to another
	 * @param name Checked value name
	 * @param value Checked value
	 * @param max Maximum value
	 */
	public static void lessEqual(String name, Integer value, int max)
		{
		if (value == null)
			throw new ParameterNullException(name);
		
		lessEqual(name, value.intValue(), max);
		}
	
	/**
	 * Assert that a value is in a given range
	 * @param name Checked value name
	 * @param value Checked value
	 * @param min Minimum value
	 * @param max Maximum value
	 */
	public static void inRange(String name, long value, long min, long max)
		{
		greaterEqual(name, value, min);
		lessEqual(name, value, max);
		}
	
	/**
	 * Assert that a value is in a given range
	 * @param name Checked value name
	 * @param value Checked value
	 * @param min Minimum value
	 * @param max Maximum value
	 */
	public static void inRange(String name, Integer value, int min, int max)
		{
		greaterEqual(name, value, min);
		lessEqual(name, value, max);
		}
	
	/**
	 * Assert that a value is greater or equal to zero
	 * @param name Checked value name
	 * @param value Checked value
	 */
	public static void positive(String name, long value)
		{
		greaterEqual(name, value, 0);
		}
	
	/**
	 * Assert that a value is greater or equal to zero
	 * @param name Checked value name
	 * @param value Checked value
	 */
	public static void positive(String name, Integer value)
		{
		greaterEqual(name, value, 0);
		}
	}
