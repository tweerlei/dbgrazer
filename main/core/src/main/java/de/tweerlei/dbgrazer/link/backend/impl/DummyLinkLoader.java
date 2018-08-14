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
package de.tweerlei.dbgrazer.link.backend.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.link.backend.LinkLoader;
import de.tweerlei.dbgrazer.link.model.LinkDef;

/**
 * LinkLoader that returns a single link
 * 
 * @author Robert Wruck
 */
@Service("dummyLinkLoader")
public class DummyLinkLoader implements LinkLoader
	{
	@Override
	public SortedMap<String, LinkDef> loadLinks()
		{
		final SortedMap<String, LinkDef> ret = new TreeMap<String, LinkDef>();
/*		ret.put("ImageDB", new LinkDefImpl(
				"JDBC",
				"ImageDB",
				"router (ImageDB)",
				"com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost/imagedb?characterEncoding=UTF-8&amp;useUnicode=true",
				"imagedb",
				"citti",
				false,
				"",
				"",
				new Properties(),
				"ImageDB",
				"",
				null
				));*/
		return (ret);
		}

	@Override
	public void createLink(String user, String name, LinkDef conn) throws IOException
		{
		throw new IOException("Not implemented");
		}

	@Override
	public void updateLink(String user, String name, String newName, LinkDef conn) throws IOException
		{
		throw new IOException("Not implemented");
		}

	@Override
	public void removeLink(String user, String name) throws IOException
		{
		throw new IOException("Not implemented");
		}
	
	@Override
	public List<HistoryEntry> getHistory(String name, int limit) throws IOException
		{
		return (new ArrayList<HistoryEntry>());
		}
	}
