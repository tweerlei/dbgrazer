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
package de.tweerlei.spring.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import de.tweerlei.spring.service.ManifestEnumeratorService;
import de.tweerlei.spring.util.ManifestParser;

/**
 * Implementation that loads all manifests from an ApplicationContext's classpath
 * 
 * @author Robert Wruck
 */
public class ManifestEnumeratorServiceImpl implements ManifestEnumeratorService
	{
	private final ApplicationContext context;
	private final List<ManifestParser> libs;
	private final Logger logger;
	
	/**
	 * Constructor
	 * @param context Spring ApplicationContext
	 */
	@Autowired
	public ManifestEnumeratorServiceImpl(ApplicationContext context)
		{
		this.context = context;
		this.libs = new ArrayList<ManifestParser>();
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Detect manifests
	 */
	@PostConstruct
	public void init()
		{
		try	{
			for (Resource r : context.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + JarFile.MANIFEST_NAME))
				{
				if (r.isReadable())
					{
					final InputStream is = r.getInputStream();
					try	{
						final ManifestParser mp = new ManifestParser(is);
						if (!mp.getRevision().equals(ManifestParser.UNKNOWN_VALUE))
							libs.add(mp);
						}
					finally
						{
						is.close();
						}
					}
				}
			}
		catch (IOException e)
			{
			logger.log(Level.SEVERE, "getResources", e);
			}
		}
	
	public List<ManifestParser> getManifests()
		{
		return (Collections.unmodifiableList(libs));
		}
	}
