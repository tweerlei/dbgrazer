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
package de.tweerlei.ermtools.tns;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

/**
 * Write a TNSNAMES.ORA file
 * 
 * @author Robert Wruck
 */
public class TNSNamesWriter
	{
	private final Writer writer;
	private final boolean prettyPrint;
	
	/**
	 * Constructor
	 * @param w Writer
	 */
	public TNSNamesWriter(Writer w)
		{
		this(w, false);
		}
	
	/**
	 * Constructor
	 * @param w Writer
	 * @param prettyPrint Pretty print
	 */
	public TNSNamesWriter(Writer w, boolean prettyPrint)
		{
		this.writer = w;
		this.prettyPrint = prettyPrint;
		}
	
	/**
	 * Write a TNSNAMES.ORA file
	 * @param entries Entries
	 * @throws IOException on error
	 */
	public void write(Map<String, Object> entries) throws IOException
		{
		write(entries, 0);
		}
	
	/**
	 * Write a single entry
	 * @param name Entry name
	 * @param entry Entry description
	 * @throws IOException on error
	 */
	public void writeEntry(String name, Map<String, Object> entry) throws IOException
		{
		write(Collections.<String, Object>singletonMap(name, entry), 0);
		}
	
	/**
	 * Write a comment
	 * @param comment Comment text
	 * @throws IOException on error
	 */
	public void writeComment(String comment) throws IOException
		{
		for (String s : comment.split("\n"))
			{
			writer.write("# ");
			writer.write(s);
			writer.write("\n");
			}
		}
	
	private void write(Map<String, Object> entries, int level) throws IOException
		{
		for (Map.Entry<String, Object> ent : entries.entrySet())
			{
			if (level > 0)
				{
				writer.write("(");
				if (prettyPrint)
					indent(level);
				}
			writer.write(ent.getKey());
			writer.write(" = ");
			if (ent.getValue() instanceof Map)
				{
				@SuppressWarnings("unchecked")
				final Map<String, Object> valueMap = (Map<String, Object>) ent.getValue();
				write(valueMap, level + 1);
				}
			else
				{
				writer.write(ent.getValue().toString());
				}
			if (level > 0)
				{
				if (prettyPrint)
					indent(level - 1);
				writer.write(")");
				}
			else
				writer.write("\n");
			}
		}
	
	private void indent(int level) throws IOException
		{
		writer.write("\n");
		for (int i = 0; i < level; i++)
			writer.write("\t");
		}
	}
