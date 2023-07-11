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

import java.util.Collection;

import de.tweerlei.spring.config.ConfigKey;
import de.tweerlei.spring.config.ConfigProvider;
import de.tweerlei.spring.config.ConfigProviderHolder;
import de.tweerlei.spring.service.SerializerFactory;

/**
 * ConfigAccessor backed by a ConfigProvider
 * 
 * @author Robert Wruck
 */
public class ConfigProviderAccessor extends AbstractConfigAccessor implements ConfigProviderHolder
	{
	private final ConfigProvider provider;
	private final SerializerFactory factory;
	
	/**
	 * Constructor
	 * @param provider ConfigProvider
	 * @param factory SerializerFactory
	 */
	public ConfigProviderAccessor(ConfigProvider provider, SerializerFactory factory)
		{
		this.provider = provider;
		this.factory = factory;
		}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T getRaw(ConfigKey<T> key)
		{
		final String value = provider.get(key.getKey());
		if (value == null)
			return (null);
		
		if (key.getElementType() != null)
			return ((T) factory.decode((Class<Collection>) key.getType(), key.getElementType(), value));
		
		final T ret = factory.decode(key.getType(), value);
		return (ret);
		}
	
	public ConfigProvider getConfigProvider()
		{
		return (provider);
		}
	}
