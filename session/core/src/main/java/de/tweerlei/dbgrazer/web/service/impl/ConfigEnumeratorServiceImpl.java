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
package de.tweerlei.dbgrazer.web.service.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.service.ConfigEnumeratorService;
import de.tweerlei.spring.config.ConfigKey;

/**
 * Implementation that loads all "ConfigKeys" classes from the classpath
 * 
 * @author Robert Wruck
 */
@Service
public class ConfigEnumeratorServiceImpl implements ConfigEnumeratorService
	{
	private static final String CONFIG_CLASSES = "de/tweerlei/dbgrazer/**/ConfigKeys.class";
	
	private final ApplicationContext context;
	private final List<Class<?>> classes;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param context Spring ApplicationContext
	 */
	@Autowired
	public ConfigEnumeratorServiceImpl(ApplicationContext context)
		{
		this.context = context;
		this.classes = new ArrayList<Class<?>>();
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Detect classes
	 */
	@PostConstruct
	public void init()
		{
		final MetadataReaderFactory factory = new SimpleMetadataReaderFactory();
		
		try	{
			for (Resource r : context.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + CONFIG_CLASSES))
				{
				if (r.isReadable())
					{
					final String className = factory.getMetadataReader(r).getClassMetadata().getClassName();
					logger.log(Level.INFO, className);
					try	{
						final Class<?> c = Class.forName(className);
						classes.add(c);
						}
					catch (ClassNotFoundException e)
						{
						logger.log(Level.WARNING, "Class.forName", e);
						}
					}
				}
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "getResources", e);
			}
		}
	
	@Override
	public List<ConfigKey<?>> getConfigKeys()
		{
		final List<ConfigKey<?>> ret = new ArrayList<ConfigKey<?>>();
		
		for (Class<?> s : classes)
			addConfigKeys(ret, s);
		
		return (ret);
		}
	
	private void addConfigKeys(List<ConfigKey<?>> ret, Class<?> type)
		{
		for (Field f : type.getDeclaredFields())
			{
			if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && (f.getType() == ConfigKey.class))
				{
				try	{
					final ConfigKey<?> k = (ConfigKey<?>) f.get(null);
					ret.add(k);
					}
				catch (IllegalArgumentException e)
					{
					// ignore
					}
				catch (IllegalAccessException e)
					{
					// ignore
					}
				}
			}
		}
	}
