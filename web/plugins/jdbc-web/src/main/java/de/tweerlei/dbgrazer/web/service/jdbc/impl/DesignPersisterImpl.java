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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignPersister;

/**
 * Read user definitions from character streams
 * 
 * @author Robert Wruck
 */
@Service
public class DesignPersisterImpl implements DesignPersister
	{
	private static final String SEPARATOR = ":";
	private static final String LINE_SEPARATOR = "\n";
	
	@Override
	public SortedSet<QualifiedName> readObject(Reader reader) throws IOException
		{
		final SortedSet<QualifiedName> ret = new TreeSet<QualifiedName>();
		final BufferedReader br = new BufferedReader(reader);
		
		for (;;)
			{
			final String line = br.readLine();
			if (line == null)
				break;
			final String[] parts = line.split(SEPARATOR);
			if (parts.length == 3)
				ret.add(new QualifiedName(parts[0], parts[1], parts[2]));
			}
		
		return (ret);
		}
	
	@Override
	public void writeObject(Writer writer, SortedSet<QualifiedName> tables) throws IOException
		{
		final BufferedWriter w = new BufferedWriter(writer);
		
		for (QualifiedName qn : tables)
			{
			w.write(StringUtils.notNull(qn.getCatalogName()));
			w.write(SEPARATOR);
			w.write(StringUtils.notNull(qn.getSchemaName()));
			w.write(SEPARATOR);
			w.write(StringUtils.notNull(qn.getObjectName()));
			w.write(LINE_SEPARATOR);
			}
		
		w.flush();
		}
	}
