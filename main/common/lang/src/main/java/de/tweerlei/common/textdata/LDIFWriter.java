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
package de.tweerlei.common.textdata;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import de.tweerlei.common.codec.Base64OutputStream;

/**
 * Write records as LDIF (RFC 2849)
 * 
 * @author Robert Wruck
 */
public class LDIFWriter
	{
	private static final String EOL = "\n";
	
	private final Writer writer;
	
	/**
	 * Constructor
	 * @param writer Underlying Writer
	 */
	public LDIFWriter(Writer writer)
		{
		this.writer = writer;
		}
	
	/**
	 * Write a comment line
	 * @param comment Comment text
	 * @throws IOException on error
	 */
	public void writeComment(String comment) throws IOException
		{
		if (comment == null)
			return;
		
		final String[] lines = comment.split(EOL);
		for (int i = 0; i < lines.length; i++)
			{
			writer.write("# ");
			writer.write(lines[i]);
			writer.write(EOL);
			}
		}
	
	/**
	 * Write an entry consisting of multiple attributes.
	 * Attribute values can be of types:
	 * - byte[]: Simple attribute value to be encoded in Base64
	 * - Other array or collection: Multiple attribute values
	 * - Object: Simple attribute value
	 * @param dn Distinguished name
	 * @param values Attribute values
	 * @throws IOException on error
	 */
	public void write(String dn, Map values) throws IOException
		{
		writer.write("dn: ");
		writer.write(dn);
		writer.write(EOL);
		
		for (Iterator it = values.entrySet().iterator(); it.hasNext(); )
			{
			final Map.Entry ent = (Map.Entry) it.next();
			
			if (ent.getValue() == null)
				continue;
			else if (ent.getValue() instanceof byte[])
				writeSingleValue(ent.getKey().toString(), (byte[]) ent.getValue());
			else if (ent.getValue().getClass().isArray())
				{
				for (int i = 0, l = Array.getLength(ent.getValue()); i < l; i++)
					{
					final Object v = Array.get(ent.getValue(), i);
					if (v == null)
						continue;
					else if (v instanceof byte[])
						writeSingleValue(ent.getKey().toString(), (byte[]) v);
					else
						writeSingleValue(ent.getKey().toString(), String.valueOf(v));
					}
				}
			else if (ent.getValue() instanceof Collection)
				{
				final Collection c = (Collection) ent.getValue();
				
				for (Iterator i = c.iterator(); i.hasNext(); )
					{
					final Object v = i.next();
					if (v == null)
						continue;
					else if (v instanceof byte[])
						writeSingleValue(ent.getKey().toString(), (byte[]) v);
					else
						writeSingleValue(ent.getKey().toString(), String.valueOf(v));
					}
				}
			else
				writeSingleValue(ent.getKey().toString(), String.valueOf(ent.getValue()));
			}
		
		writer.write(EOL);
		}
	
	private void writeSingleValue(String name, String value) throws IOException
		{
		writer.write(name);
		writer.write(": ");
		writer.write(value);
		writer.write(EOL);
		}
	
	private void writeSingleValue(String name, byte[] value) throws IOException
		{
		writer.write(name);
		writer.write(":: ");
		
		final Base64OutputStream bos = new Base64OutputStream(writer);
		bos.write(value);
		bos.flush();
		
		writer.write(EOL);
		}
	
	/**
	 * Close the Writer
	 * @throws IOException on error
	 */
	public void close() throws IOException
		{
		writer.close();
		}
	}
