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
package de.tweerlei.spring.config.impl;

import java.util.HashMap;
import java.util.Map;

import de.tweerlei.spring.config.ConfigKey;

/**
 * Holds ConfigKeys and associated values
 * 
 * @author Robert Wruck
 */
public class ConfigMap extends AbstractWritableConfigAccessor
	{
	private final Map<String, Object> map;
	
	/**
	 * Constructor
	 */
	public ConfigMap()
		{
		this.map = new HashMap<String, Object>();
		}
	
	public <T> T getRaw(ConfigKey<T> key)
		{
		final Object value = map.get(key.getKey());
		
		return (key.getType().cast(value));
		}
	
	public <T> T putRaw(ConfigKey<T> key, T value)
		{
		final Object prev = map.put(key.getKey(), value);
		
		return (key.getType().cast(prev));
		}
	}
