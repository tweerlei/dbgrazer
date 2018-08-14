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
 * Provides for type-safe access to configuration values
 * 
 * @author Robert Wruck
 */
public interface ConfigAccessor
	{
	/**
	 * Get a value
	 * @param <T> Value type
	 * @param key Value key
	 * @return Value (or the key's default value if not set)
	 */
	public <T> T get(ConfigKey<T> key);
	
	/**
	 * Get a value
	 * @param <T> Value type
	 * @param key Value key
	 * @return Value or null
	 */
	public <T> T getRaw(ConfigKey<T> key);
	}
