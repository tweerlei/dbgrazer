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

import de.tweerlei.spring.config.WritableConfigProvider;

/**
 * A ConfigProvider backed by a map. Channges to the map will be reflected by the ConfigProvider 
 * 
 * @author Robert Wruck
 */
public class MapBackedConfigProvider implements WritableConfigProvider
	{
	private final Map<String, String> entries;
	
	/**
	 * Constructor
	 */
	public MapBackedConfigProvider()
		{
		this(new HashMap<String, String>());
		}
	
	/**
	 * Constructor
	 * @param entries Entries
	 */
	public MapBackedConfigProvider(Map<String, String> entries)
		{
		this.entries = entries;
		}
	
	public String get(String key)
		{
		return (entries.get(key));
		}
	
	public Map<String, String> list()
		{
		return (entries);
		}
	
	public String put(String key, String value)
		{
		return (entries.put(key, value));
		}
	}
