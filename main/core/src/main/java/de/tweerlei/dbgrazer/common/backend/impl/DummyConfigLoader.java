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
package de.tweerlei.dbgrazer.common.backend.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.backend.ConfigLoader;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;

/**
 * Dummy impl.
 * 
 * @author Robert Wruck
 */
@Service("dummyConfigLoader")
public class DummyConfigLoader implements ConfigLoader
	{
	@Override
	public Properties loadConfig()
		{
		return (new Properties());
		}
	
	@Override
	public void updateConfig(String user, Properties props) throws IOException
		{
		}
	
	@Override
	public List<HistoryEntry> getHistory(int limit) throws IOException
		{
		return (new ArrayList<HistoryEntry>());
		}
	}
