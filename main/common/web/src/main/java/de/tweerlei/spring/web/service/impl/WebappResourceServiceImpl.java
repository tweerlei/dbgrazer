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
package de.tweerlei.spring.web.service.impl;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarFile;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.spring.util.ManifestParser;
import de.tweerlei.spring.web.service.WebappResourceService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service("webappResourceService")
public class WebappResourceServiceImpl implements WebappResourceService
	{
	private final ServletContext context;
	private ManifestParser parser;
	
	/**
	 * Constructor
	 * @param context ServletContext
	 */
	@Autowired
	public WebappResourceServiceImpl(ServletContext context)
		{
		this.context = context;
		}
	
	public InputStream getResourceAsStream(String path)
		{
		return (context.getResourceAsStream(path));
		}
	
	public URL getResource(String path) throws MalformedURLException
		{
		return (context.getResource(path));
		}
	
	@SuppressWarnings("unchecked")
	public Set<String> getResourcePaths(String path)
		{
		return (context.getResourcePaths(path));
		}
	
	public ManifestParser getManifestParser()
		{
		if (parser == null)
			{
			final InputStream is = getResourceAsStream("/" + JarFile.MANIFEST_NAME);
			try	{
				parser = new ManifestParser(is);
				}
			finally
				{
				StreamUtils.closeQuietly(is);
				}
			}
		return (parser);
		}
	}
