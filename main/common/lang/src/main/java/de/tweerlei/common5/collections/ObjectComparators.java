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
package de.tweerlei.common5.collections;

import java.util.Comparator;

/**
 * Popular Object comparators, for use in e.g. TreeMap and TreeSet
 * 
 * @author Robert Wruck
 */
public final class ObjectComparators
	{
	private static final class NullsFirstComparator<T extends Comparable<T>> implements Comparator<T>
		{
		public int compare(T a, T b)
			{
			return (ObjectComparators.compareNullsFirst(a, b));
			}
		}
	
	private static final class NullsLastComparator<T extends Comparable<T>> implements Comparator<T>
		{
		public int compare(T a, T b)
			{
			return (ObjectComparators.compareNullsFirst(a, b));
			}
		}
	
	@SuppressWarnings("rawtypes")
	private static final Comparator<?> NULLS_FIRST = new NullsFirstComparator();
	@SuppressWarnings("rawtypes")
	private static final Comparator<?> NULLS_LAST = new NullsLastComparator();
	
	private ObjectComparators()
		{
		}
	
	/**
	 * Get a nulls first comparator
	 * @return Comparator
	 */
	public static <T extends Comparable<T>> Comparator<T> nullsFirst()
		{
		@SuppressWarnings("unchecked")
		final Comparator<T> ret = (Comparator<T>) NULLS_FIRST;
		return (ret);
		}
	
	/**
	 * Get a nulls last comparator
	 * @return Comparator
	 */
	public static <T extends Comparable<T>> Comparator<T> nullsLast()
		{
		@SuppressWarnings("unchecked")
		final Comparator<T> ret = (Comparator<T>) NULLS_LAST;
		return (ret);
		}
	
	/**
	 * Null-safe compare (nulls first)
	 * @param a An object
	 * @param b Another object
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
	 */
	public static <T extends Comparable<T>> int compareNullsFirst(T a, T b)
		{
		if (a == b)
			return (0);
		if (a == null)
			return (-1);
		if (b == null)
			return (1);
		
		return (a.compareTo(b));
		}
	
	/**
	 * Null-safe compare (nulls last)
	 * @param a An object
	 * @param b Another object
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
	 */
	public static <T extends Comparable<T>> int compareNullsLast(T a, T b)
		{
		if (a == b)
			return (0);
		if (a == null)
			return (1);
		if (b == null)
			return (-1);
		
		return (a.compareTo(b));
		}
	}
