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
public interface WritableConfigAccessor extends ConfigAccessor
	{
	/**
	 * Set a value
	 * @param <T> Value type
	 * @param key Value key
	 * @param value Value to set
	 * @return Previous value as would have been returned by get()
	 */
	public <T> T put(ConfigKey<T> key, T value);
	
	/**
	 * Set a value
	 * @param <T> Value type
	 * @param key Value key
	 * @param value Value to set
	 * @return Previous value as would have been returned by getRaw()
	 */
	public <T> T putRaw(ConfigKey<T> key, T value);
	}
