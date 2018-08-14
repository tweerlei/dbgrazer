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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods for generic collections
 * 
 * @author Robert Wruck
 */
public final class CollectionUtils
	{
	private CollectionUtils()
		{
		}
	
	/**
	 * Check whether a Collection is null or empty
	 * @param c Collection
	 * @return true if null or empty
	 */
	public static boolean empty(Collection<?> c)
		{
		return ((c == null) || c.isEmpty());
		}
	
	/**
	 * Check whether a Map is null or empty
	 * @param c Map
	 * @return true if null or empty
	 */
	public static boolean empty(Map<?, ?> c)
		{
		return ((c == null) || c.isEmpty());
		}
	
	/**
	 * Create a set that contains the arguments
	 * @param <T> Argument type
	 * @param a Elements
	 * @return LinkedHashSet
	 */
	public static <T> Set<T> set(T... a)
		{
		final Set<T> ret = new LinkedHashSet<T>(a.length);
		
		for (T t : a)
			ret.add(t);
		
		return (ret);
		}
	
	/**
	 * Create a list that contains the arguments
	 * @param <T> Argument type
	 * @param a Elements
	 * @return ArrayList
	 */
	public static <T> List<T> list(T... a)
		{
		final List<T> ret = new ArrayList<T>(a.length);
		
		for (T t : a)
			ret.add(t);
		
		return (ret);
		}
	
	/**
	 * Create a set that contains all elements from a given Iterable
	 * @param <T> Argument type
	 * @param a Elements
	 * @return Set
	 */
	public static <T> Set<T> set(Iterable<T> a)
		{
		final Set<T> ret = new LinkedHashSet<T>();
		
		if (a != null)
			{
			for (T t : a)
				ret.add(t);
			}
		
		return (ret);
		}
	
	/**
	 * Create a list that contains all elements from a given Iterable
	 * @param <T> Argument type
	 * @param a Elements
	 * @return ArrayList
	 */
	public static <T> List<T> list(Iterable<T> a)
		{
		final List<T> ret = new LinkedList<T>();
		
		if (a != null)
			{
			for (T t : a)
				ret.add(t);
			}
		
		return (ret);
		}
	
	/**
	 * Merge values from multiple Iterables into a Set
	 * @param <T> Argument type
	 * @param a Iterables
	 * @return Union set
	 */
	public static <T> Set<T> union(Iterable<T>... a)
		{
		final Set<T> ret = new LinkedHashSet<T>();
		
		for (Iterable<T> i : a)
			{
			for (T t : i)
				ret.add(t);
			}
		
		return (ret);
		}
	
	/**
	 * Merge values from two Iterables into a List
	 * @param <T> Argument type
	 * @param a Iterable
	 * @param b Iterable
	 * @return Concatenated list
	 */
	public static <T> List<T> concat(Iterable<T> a, Iterable<T> b)
		{
		final List<T> ret = new LinkedList<T>();
		
		if (a != null)
			{
			for (T t : a)
				ret.add(t);
			}
		if (b != null)
			{
			for (T t : b)
				ret.add(t);
			}
		
		return (ret);
		}
	
	/**
	 * Create a sorted list from a given collection
	 * @param <T> Element type
	 * @param l List to sort
	 * @return Sorted list
	 */
	public static <T extends Comparable<? super T>> List<T> sort(Collection<T> l)
		{
		if (l == null)
			return (null);
		
		final List<T> ret = new ArrayList<T>(l);
		
		Collections.sort(ret);
		
		return (ret);
		}
	
	/**
	 * Create a sorted list from a given collection
	 * @param <T> Element type
	 * @param l List to sort
	 * @param c Comparator
	 * @return Sorted list
	 */
	public static <T> List<T> sort(Collection<T> l, Comparator<? super T> c)
		{
		if (l == null)
			return (null);
		
		final List<T> ret = new ArrayList<T>(l);
		
		Collections.sort(ret, c);
		
		return (ret);
		}
	
	/**
	 * Create an Iterable for an Iterable (handles null values)
	 * @param <V> value type
	 * @param i Iterable
	 * @return Iterable
	 */
	public static <V> Iterable<V> iterate(final Iterable<V> i)
		{
		if (i == null)
			return (Collections.emptyList());
		
		return (i);
		}
	
	/**
	 * Create an Iterable for an Iterator. You can only iterate this once.
	 * @param <V> value type
	 * @param i Iterator
	 * @return Iterable
	 */
	public static <V> Iterable<V> iterate(final Iterator<V> i)
		{
		if (i == null)
			return (Collections.emptyList());
		
		return (new Iterable<V>()
			{
			public Iterator<V> iterator()
				{
				return (i);
				}
			});
		}
	
	/**
	 * Create an Iterable for an Enumeration
	 * @param <V> value type
	 * @param i Iterator
	 * @return Iterable
	 */
	public static <V> Iterable<V> iterate(final Enumeration<V> i)
		{
		if (i == null)
			return (Collections.emptyList());
		
		return (new Iterable<V>()
			{
			public Iterator<V> iterator()
				{
				return (new EnumIterator<V>(i));
				}
			});
		}
	
	/**
	 * Create an Iterable for an Array
	 * @param <V> value type
	 * @param i Iterator
	 * @return Iterable
	 */
	public static <V> Iterable<V> iterate(V[] i)
		{
		if (i == null)
			return (Collections.emptyList());
		
		return (Arrays.asList(i));
		}
	}
