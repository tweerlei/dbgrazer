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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.tweerlei.spring.config.ConfigProvider;

/**
 * A ConfigProvider that queries other ConfigProviders
 * 
 * @author Robert Wruck
 */
public class CompositeConfigProvider implements ConfigProvider
	{
	private final List<ConfigProvider> providers;
	
	/**
	 * Constructor
	 * @param providers ConfigProviders
	 */
	public CompositeConfigProvider(ConfigProvider... providers)
		{
		this.providers = new ArrayList<ConfigProvider>();
		if (providers != null)
			{
			for (ConfigProvider p : providers)
				{
				if (p != null)
					this.providers.add(p);
				}
			}
		}
	
	public String get(String key)
		{
		for (ConfigProvider p : providers)
			{
			final String value = p.get(key);
			if (value != null)
				return (value);
			}
		return (null);
		}
	
	public Map<String, String> list()
		{
		final Map<String, String> ret = new HashMap<String, String>();
		for (final ListIterator<ConfigProvider> i = providers.listIterator(providers.size()); i.hasPrevious(); )
			ret.putAll(i.previous().list());
		return (ret);
		}
	}
