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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.config.ConfigKey;

/**
 * ConfigAccessor that caches results from a delegate ConfigAccessor
 * 
 * @author Robert Wruck
 */
public class CachedConfigAccessor extends AbstractConfigAccessor
	{
	private static final Object NO_VALUE = new Object();
	
	private final ConfigAccessor accessor;
	private final Map<ConfigKey<?>, Object> cache;
	
	/**
	 * Constructor
	 * @param accessor Delegate ConfigAccessor
	 */
	public CachedConfigAccessor(ConfigAccessor accessor)
		{
		this.accessor = accessor;
		this.cache = new ConcurrentHashMap<ConfigKey<?>, Object>();
		}
	
	public <T> T getRaw(ConfigKey<T> key)
		{
		Object o = cache.get(key);
		if (o == NO_VALUE)
			return (null);
		if (o == null)
			{
			o = accessor.getRaw(key);
			if (o == null)
				cache.put(key, NO_VALUE);
			else
				cache.put(key, o);
			}
		return (key.getType().cast(o));
		}
	
	/**
	 * Flush the cache
	 */
	public void flush()
		{
		cache.clear();
		}
	}
