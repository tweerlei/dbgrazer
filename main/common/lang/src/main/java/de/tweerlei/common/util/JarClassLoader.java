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
package de.tweerlei.common.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * ClassLoader that loads classes from JAR files
 * 
 * @author Robert Wruck
 */
public class JarClassLoader extends URLClassLoader
	{
	/**
	 * Constructor
	 * @param parent Parent classloader
	 */
	public JarClassLoader(ClassLoader parent)
		{
		super(new URL[] {}, parent);
		}
	
	/**
	 * Add a JAR file to the classpath
	 * @param path JAR file
	 * @return true if the file was successfully added
	 */
	public boolean addFile(File path)
		{
		if (!path.isFile())
			return (false);
		
		try	{
			addURL(new URL("jar:file:" + path.getAbsolutePath().replace('\\', '/') + "!/"));
			}
		catch (MalformedURLException e)
			{
			return (false);
			}
		
		return (true);
		}
	}
