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
package de.tweerlei.dbgrazer.security.backend.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.file.StringPersister;

/**
 * Read plain strings from character streams
 * 
 * @author Robert Wruck
 */
@Service
public class StringPersisterImpl implements StringPersister
	{
	private static final String LINE_SEPARATOR = "\n";
	
	@Override
	public String readObject(Reader reader) throws IOException
		{
		final StringBuilder sb = new StringBuilder();
		final BufferedReader br = new BufferedReader(reader);
		
		for (;;)
			{
			final String line = br.readLine();
			if (line == null)
				break;
			sb.append(line).append(LINE_SEPARATOR);
			}
		
		return (sb.toString());
		}
	
	@Override
	public void writeObject(Writer writer, String text) throws IOException
		{
		final String txt = text
				.replace("\r\n", LINE_SEPARATOR)	// Windows-style
				.replace("\r", LINE_SEPARATOR);	// Mac style
		
		writer.write(txt);
		}
	}
