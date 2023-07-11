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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Populate sets
 * @param <V> Value type
 * 
 * @author Robert Wruck
 */
public class SetBuilder<V>
	{
	private final Set<V> set;
	
	/**
	 * Constructor
	 * @param set Set to populate
	 */
	public SetBuilder(Set<V> set)
		{
		this.set = set;
		}
	
	/**
	 * Construct a SetBuilder that builds a HashSet
	 */
	public SetBuilder()
		{
		this(new HashSet<V>());
		}
	
	/**
	 * Construct a SetBuilder that builds a HashSet
	 * @param capacity Initial capacity
	 */
	public SetBuilder(int capacity)
		{
		this(new HashSet<V>(capacity));
		}
	
	/**
	 * Factory method
	 * @param <V> Value type
	 * @param set Set to populate
	 * @return SetBuilder
	 */
	public static <V> SetBuilder<V> using(Set<V> set)
		{
		return (new SetBuilder<V>(set));
		}
	
	/**
	 * Populate a Set retaining insertion order
	 * @param <V> Value type
	 * @return MapBuilder
	 */
	public static <V> SetBuilder<V> ordered()
		{
		return (new SetBuilder<V>(new LinkedHashSet<V>()));
		}
	
	/**
	 * Populate a Set retaining insertion order
	 * @param <V> Value type
	 * @param capacity Initial capacity
	 * @return MapBuilder
	 */
	public static <V> SetBuilder<V> ordered(int capacity)
		{
		return (new SetBuilder<V>(new LinkedHashSet<V>(capacity)));
		}
	
	/**
	 * Populate a Set sorted by its elements
	 * @param <V> Value type
	 * @return SetBuilder
	 */
	public static <V> SetBuilder<V> sorted()
		{
		return (new SetBuilder<V>(new TreeSet<V>()));
		}
	
	/**
	 * Populate a Set sorted by the given Comparator
	 * @param <V> Value type
	 * @param comparator Comparator to sort entries
	 * @return SetBuilder
	 */
	public static <V> SetBuilder<V> sortedBy(Comparator<? super V> comparator)
		{
		return (new SetBuilder<V>(new TreeSet<V>(comparator)));
		}
	
	/**
	 * Factory method
	 * @param <V> Value type
	 * @param valueType Value type
	 * @return SetBuilder
	 */
	public static <V extends Enum<V>> SetBuilder<V> of(Class<V> valueType)
		{
		return (new SetBuilder<V>(EnumSet.noneOf(valueType)));
		}
	
	/**
	 * Add a value
	 * @param value Value
	 * @return this
	 */
	public SetBuilder<V> add(V value)
		{
		set.add(value);
		return (this);
		}
	
	/**
	 * Add all values from a given Collection
	 * @param values Collection
	 * @return this
	 */
	public SetBuilder<V> addAll(Collection<? extends V> values)
		{
		if (values != null)
			set.addAll(values);
		return (this);
		}
	
	/**
	 * Add all values from a given Iterable
	 * @param values Iterable
	 * @return this
	 */
	public SetBuilder<V> addAll(Iterable<? extends V> values)
		{
		if (values != null)
			{
			for (V v : values)
				set.add(v);
			}
		return (this);
		}
	
	/**
	 * Add all values from a given Enumeration
	 * @param values Enumeration
	 * @return this
	 */
	public SetBuilder<V> addAll(Enumeration<? extends V> values)
		{
		if (values != null)
			{
			while (values.hasMoreElements())
				set.add(values.nextElement());
			}
		return (this);
		}
	
	/**
	 * Add all values from a given Array
	 * @param values Array
	 * @return this
	 */
	public SetBuilder<V> addAll(V[] values)
		{
		if (values != null)
			{
			for (V v : values)
				set.add(v);
			}
		return (this);
		}
	
	/**
	 * Get the populated set
	 * @return Set
	 */
	public Set<V> build()
		{
		return (set);
		}
	
	/**
	 * Get an unmodifiable version of the populated set
	 * @return Set
	 */
	public Set<V> buildReadOnly()
		{
		return (Collections.unmodifiableSet(set));
		}
	}
