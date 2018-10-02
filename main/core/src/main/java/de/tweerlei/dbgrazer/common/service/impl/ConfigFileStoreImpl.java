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
package de.tweerlei.dbgrazer.common.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import de.tweerlei.common.io.FileUtils;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.backend.impl.ConfigKeys;
import de.tweerlei.dbgrazer.common.service.ConfigFileStore;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class ConfigFileStoreImpl implements ConfigFileStore
	{
	private static final String CONFIG_DIR = ".dbgrazer";
	private static final String CHARSET = "UTF-8";
	
	private final File configPath;
	
	/**
	 * Constructor
	 */
	public ConfigFileStoreImpl()
		{
		final String configPathProperty = System.getProperty(ConfigKeys.CONFIG_PATH.getKey());
		if (configPathProperty != null)
			this.configPath = new File(configPathProperty);
		else
			this.configPath = new File(System.getProperty("user.home"), CONFIG_DIR);
		}
	
	@Override
	public File getFileLocation(String path)
		{
		final File resolved;
		
		if (StringUtils.empty(path))
			resolved = configPath;
		else
			{
			final File file = new File(path);
			resolved = FileUtils.resolveFile(file, configPath);
			}
		
		return (resolved);
		}
	
	@Override
	public String getFileEncoding()
		{
		return (CHARSET);
		}
	}
