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

/**
 * Typed configuration key
 * @param <T> Value type
 * 
 * @author Robert Wruck
 */
public class ConfigKey<T>
	{
	private final Class<T> type;
	private final String key;
	private final T defaultValue;
	
	/**
	 * Constructor
	 * @param key Key
	 * @param type Value type
	 * @param defaultValue Default value
	 */
	private ConfigKey(String key, Class<T> type, T defaultValue)
		{
		this.key = key;
		this.type = type;
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
		return (new ConfigKey<T>(key, type, defaultValue));
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
		return (new ConfigKey<T>(pkg + "." + key, type, defaultValue));
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
