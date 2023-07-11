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
package de.tweerlei.spring.web.service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import de.tweerlei.spring.util.ManifestParser;

/**
 * Service for accessing webapp resources
 * 
 * @author Robert Wruck
 */
public interface WebappResourceService
	{
	/**
	 * Get a resource as stream
	 * @param path Path to resource, must start with "/" 
	 * @return InputStream or null
	 */
	public InputStream getResourceAsStream(String path);
	
	/**
	 * Get a URL for a resource
	 * @param path Path to resource, must start with "/"
	 * @return URL or null
	 * @throws MalformedURLException if the path is invalid
	 */
	public URL getResource(String path) throws MalformedURLException;
	
	/**
	 * Get resource paths matching the given path prefix
	 * @param path Path prefix for resources to find, must start with "/"
	 * @return Set of resource paths
	 */
	public Set<String> getResourcePaths(String path);
	
	/**
	 * Convenience method to read META-INF/MANIFEST.MF
	 * @return ManifestParser
	 */
	public ManifestParser getManifestParser();
	}
