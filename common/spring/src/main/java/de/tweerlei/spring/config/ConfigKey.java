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
package de.tweerlei.spring.config;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Typed configuration key
 * @param <T> Value type
 * 
 * @author Robert Wruck
 */
public class ConfigKey<T>
	{
	private final Class<T> type;
	private final Class<?> elementType;
	private final String key;
	private final T defaultValue;
	
	/**
	 * Constructor
	 * @param key Key
	 * @param type Value type
	 * @param elementType Element type when type refers to a collection
	 * @param defaultValue Default value
	 */
	private ConfigKey(String key, Class<T> type, Class<?> elementType, T defaultValue)
		{
		this.key = key;
		this.type = type;
		this.elementType = elementType;
		this.defaultValue = defaultValue;
		}
	
	/**
	 * Get the type
	 * @return the type
	 */
	public Class<T> getType()
		{
		return type;
		}
	
	/**
	 * Get the element type
	 * @return the element type
	 */
	public Class<?> getElementType()
		{
		return elementType;
		}
	
	/**
	 * Get the key
	 * @return the key
	 */
	public String getKey()
		{
		return key;
		}
	
	/**
	 * Get the defaultValue
	 * @return the defaultValue
	 */
	public T getDefaultValue()
		{
		return defaultValue;
		}
	
	/**
	 * Create a ConfigKey instance
	 * @param <T> Value type
	 * @param key Key name
	 * @param type Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	public static <T> ConfigKey<T> create(String key, Class<T> type, T defaultValue)
		{
		return (new ConfigKey<T>(key, type, null, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance
	 * @param <T> Value type
	 * @param pkg Key package
	 * @param key Key name
	 * @param type Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	public static <T> ConfigKey<T> create(String pkg, String key, Class<T> type, T defaultValue)
		{
		return (create(pkg + "." + key, type, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance for a list of elements
	 * @param <T> Value type
	 * @param key Key name
	 * @param elementType Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	@SuppressWarnings("unchecked")
	public static <T> ConfigKey<List<T>> createList(String key, Class<T> elementType, List<T> defaultValue)
		{
		return (new ConfigKey<List<T>>(key, (Class<List<T>>) (Class<?>) List.class, elementType, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance for a list of elements
	 * @param <T> Value type
	 * @param pkg Key package
	 * @param key Key name
	 * @param type Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	public static <T> ConfigKey<List<T>> createList(String pkg, String key, Class<T> type, List<T> defaultValue)
		{
		return (createList(pkg + "." + key, type, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance for a set of elements
	 * @param <T> Value type
	 * @param key Key name
	 * @param elementType Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	@SuppressWarnings("unchecked")
	public static <T> ConfigKey<Set<T>> createSet(String key, Class<T> elementType, Set<T> defaultValue)
		{
		return (new ConfigKey<Set<T>>(key, (Class<Set<T>>) (Class<?>) Set.class, elementType, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance for a set of elements
	 * @param <T> Value type
	 * @param pkg Key package
	 * @param key Key name
	 * @param type Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	public static <T> ConfigKey<Set<T>> createSet(String pkg, String key, Class<T> type, Set<T> defaultValue)
		{
		return (createSet(pkg + "." + key, type, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance for a sorted set of elements
	 * @param <T> Value type
	 * @param key Key name
	 * @param elementType Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	@SuppressWarnings("unchecked")
	public static <T> ConfigKey<SortedSet<T>> createSortedSet(String key, Class<T> elementType, SortedSet<T> defaultValue)
		{
		return (new ConfigKey<SortedSet<T>>(key, (Class<SortedSet<T>>) (Class<?>) SortedSet.class, elementType, defaultValue));
		}
	
	/**
	 * Create a ConfigKey instance for a sorted set of elements
	 * @param <T> Value type
	 * @param pkg Key package
	 * @param key Key name
	 * @param type Value type
	 * @param defaultValue Default value
	 * @return The created ConfigKey
	 */
	public static <T> ConfigKey<SortedSet<T>> createSortedSet(String pkg, String key, Class<T> type, SortedSet<T> defaultValue)
		{
		return (createSortedSet(pkg + "." + key, type, defaultValue));
		}
	
	@Override
	public int hashCode()
		{
		return (key.hashCode());
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == this)
			return (true);
		if (o == null)
			return (false);
		if (!(o instanceof ConfigKey))
			return (false);
		return (key.equals(((ConfigKey<?>) o).getKey()));
		}
	
	@Override
	public String toString()
		{
		return (key);
		}
	}
