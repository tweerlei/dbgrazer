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

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Populate maps
 * @param <K> Key type
 * @param <V> Value type
 * 
 * @author Robert Wruck
 */
public class MapBuilder<K, V>
	{
	private final Map<K, V> map;
	
	/**
	 * Constructor
	 * @param map Map to populate
	 */
	public MapBuilder(Map<K, V> map)
		{
		this.map = map;
		}
	
	/**
	 * Construct a MapBuilder that builds a HashMap
	 */
	public MapBuilder()
		{
		this(new HashMap<K, V>());
		}
	
	/**
	 * Populate the given Map
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param map Map to populate
	 * @return MapBuilder
	 */
	public static <K, V> MapBuilder<K, V> using(Map<K, V> map)
		{
		return (new MapBuilder<K, V>(map));
		}
	
	/**
	 * Populate a Map retaining insertion order
	 * @param <K> Key type
	 * @param <V> Value type
	 * @return MapBuilder
	 */
	public static <K, V> MapBuilder<K, V> ordered()
		{
		return (new MapBuilder<K, V>(new LinkedHashMap<K, V>()));
		}
	
	/**
	 * Populate a map that is sorted by key
	 * @param <K> Key type
	 * @param <V> Value type
	 * @return MapBuilder
	 */
	public static <K, V> MapBuilder<K, V> sorted()
		{
		return (new MapBuilder<K, V>(new TreeMap<K, V>()));
		}
	
	/**
	 * Populate a Map that is sorted by the given Comparator
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param comparator Comparator to sort keys
	 * @return MapBuilder
	 */
	public static <K, V> MapBuilder<K, V> sortedBy(Comparator<? super K> comparator)
		{
		return (new MapBuilder<K, V>(new TreeMap<K, V>(comparator)));
		}
	
	/**
	 * Populate a Map with Enum keys
	 * @param <K> Key type
	 * @param <V> Value type
	 * @param type Key type
	 * @return MapBuilder
	 */
	public static <K extends Enum<K>, V> MapBuilder<K, V> keyedBy(Class<K> keyType)
		{
		return (new MapBuilder<K, V>(new EnumMap<K, V>(keyType)));
		}
	
	/**
	 * Put a value
	 * @param key Key
	 * @param value Value
	 * @return this
	 */
	public MapBuilder<K, V> put(K key, V value)
		{
		map.put(key, value);
		return (this);
		}
	
	/**
	 * Put all values from a given Map
	 * @param values Map
	 * @return this
	 */
	public MapBuilder<K, V> putAll(Map<? extends K, ? extends V> values)
		{
		map.putAll(values);
		return (this);
		}
	
	/**
	 * Get the populated map
	 * @return Map
	 */
	public Map<K, V> build()
		{
		return (map);
		}
	
	/**
	 * Get an unmodifiable version of the populated map
	 * @return Map
	 */
	public Map<K, V> buildReadOnly()
		{
		return (Collections.unmodifiableMap(map));
		}
	}
