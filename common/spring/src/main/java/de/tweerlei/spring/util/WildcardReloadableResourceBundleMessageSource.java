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
package de.tweerlei.spring.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * ReloadableResourceBundleMessageSource that supports classpath*: prefixes
 * 
 * @author Robert Wruck
 */
public class WildcardReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource
	{
	private static final String PROPERTIES_SUFFIX = ".properties";
	
	private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	
	@Override
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder)
		{
		if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX))
			{
			final Properties properties = new Properties();
			long lastModified = -1;
			
			for (String resolvedFilename : resolveResourceNames(filename))
				{
				final PropertiesHolder holder = super.refreshProperties(resolvedFilename, propHolder);
				properties.putAll(holder.getProperties());
				if (lastModified < holder.getFileTimestamp())
					lastModified = holder.getFileTimestamp();
				}
			
		    return new PropertiesHolder(properties, lastModified);
			}
		
		return super.refreshProperties(filename, propHolder);
		}
	
	private String[] resolveResourceNames(String filename)
		{
		try	{
			final Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
			final String[] filenames = new String[resources.length];
			
			for (int i = 0; i < resources.length; i++)
				filenames[i] = resources[i].getURI().toString().replace(PROPERTIES_SUFFIX, "");
			
			return (filenames);
			}
		catch (IOException e)
			{
			return (new String[0]);
			}
		}
	}
