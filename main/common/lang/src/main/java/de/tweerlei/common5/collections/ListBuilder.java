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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Populate lists
 * @param <V> Value type
 * 
 * @author Robert Wruck
 */
public class ListBuilder<V>
	{
	private final List<V> list;
	
	/**
	 * Constructor
	 * @param list List to populate
	 */
	public ListBuilder(List<V> list)
		{
		this.list = list;
		}
	
	/**
	 * Construct a ListBuilder that builds an ArrayList
	 */
	public ListBuilder()
		{
		this(new ArrayList<V>());
		}
	
	/**
	 * Construct a ListBuilder that builds an ArrayList
	 * @param capacity Initial capacity
	 */
	public ListBuilder(int capacity)
		{
		this(new ArrayList<V>(capacity));
		}
	
	/**
	 * Factory method
	 * @param <V> Value type
	 * @param list List to populate
	 * @return ListBuilder
	 */
	public static <V> ListBuilder<V> using(List<V> list)
		{
		return (new ListBuilder<V>(list));
		}
	
	/**
	 * Populate a linked List
	 * @param <V> Value type
	 * @return MapBuilder
	 */
	public static <V> ListBuilder<V> linked()
		{
		return (new ListBuilder<V>(new LinkedList<V>()));
		}
	
	/**
	 * Add a value
	 * @param value Value
	 * @return this
	 */
	public ListBuilder<V> add(V value)
		{
		list.add(value);
		return (this);
		}
	
	/**
	 * Add all values from a given Collection
	 * @param values Collection
	 * @return this
	 */
	public ListBuilder<V> addAll(Collection<? extends V> values)
		{
		if (values != null)
			list.addAll(values);
		return (this);
		}
	
	/**
	 * Add all values from a given Iterable
	 * @param values Iterable
	 * @return this
	 */
	public ListBuilder<V> addAll(Iterable<? extends V> values)
		{
		if (values != null)
			{
			for (V v : values)
				list.add(v);
			}
		return (this);
		}
	
	/**
	 * Add all values from a given Enumeration
	 * @param values Enumeration
	 * @return this
	 */
	public ListBuilder<V> addAll(Enumeration<? extends V> values)
		{
		if (values != null)
			{
			while (values.hasMoreElements())
				list.add(values.nextElement());
			}
		return (this);
		}
	
	/**
	 * Add all values from a given Array
	 * @param values Array
	 * @return this
	 */
	public ListBuilder<V> addAll(V[] values)
		{
		if (values != null)
			{
			for (V v : values)
				list.add(v);
			}
		return (this);
		}
	
	/**
	 * Sort the list
	 * @return ListBuilder
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ListBuilder<V> sort()
		{
		Collections.sort((List) list);
		return (this);
		}
	
	/**
	 * Sort the list
	 * @param comparator Comparator
	 * @return ListBuilder
	 */
	public ListBuilder<V> sortBy(Comparator<? super V> comparator)
		{
		Collections.sort(list, comparator);
		return (this);
		}
	
	/**
	 * Get the populated list
	 * @return List
	 */
	public List<V> build()
		{
		return (list);
		}
	
	/**
	 * Get an unmodifiable version of the populated list
	 * @return List
	 */
	public List<V> buildReadOnly()
		{
		return (Collections.unmodifiableList(list));
		}
	}
