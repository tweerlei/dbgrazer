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
package de.tweerlei.dbgrazer.web.service.bookmark.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.web.service.bookmark.BookmarkPersister;

/**
 * Read user definitions from character streams
 * 
 * @author Robert Wruck
 */
@Service
public class BookmarkPersisterImpl implements BookmarkPersister
	{
	private static final char LINE_SEPARATOR = '\n';
	
	@Override
	public SortedSet<String> readObject(Reader reader) throws IOException
		{
		final SortedSet<String> ret = new TreeSet<String>();
		final BufferedReader br = new BufferedReader(reader);
		
		for (;;)
			{
			final String line = br.readLine();
			if (line == null)
				break;
			final String v = line.trim();
			if (!StringUtils.empty(v))
				ret.add(v);
			}
		
		return (ret);
		}
	
	@Override
	public void writeObject(Writer writer, SortedSet<String> bookmarks) throws IOException
		{
		final BufferedWriter w = new BufferedWriter(writer);
		
		for (String s : bookmarks)
			{
			w.write(s);
			w.write(LINE_SEPARATOR);
			}
		
		w.flush();
		}
	}
