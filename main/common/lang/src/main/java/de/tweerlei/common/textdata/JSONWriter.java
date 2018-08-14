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
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import de.tweerlei.common.util.DateUtils;

/**
 * Serialize objects as JSON
 * 
 * @author Robert Wruck
 */
public class JSONWriter
	{
	// JSON literals
	private static final String JSON_NULL = "null";
	private static final String JSON_TRUE = "true";
	private static final String JSON_FALSE = "false";
	
	private static final String EOL = "\n";
	
	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	
	private static final int NO_ARRAY = 0;
	private static final int START_ARRAY = 1;
	private static final int IN_ARRAY = 2;
	
	private final Writer w;
	private int arrayMode;
	private boolean objectWritten;
	
	/**
	 * Constructor
	 * @param w Underlying writer
	 */
	public JSONWriter(Writer w)
		{
		this.w = w;
		this.arrayMode = NO_ARRAY;
		this.objectWritten = false;
		}
	
	/**
	 * Close the writer
	 * @throws IOException on error
	 */
	public void close() throws IOException
		{
		w.close();
		}
	
	/**
	 * Write a comment line
	 * @param comment Comment text
	 * @throws IOException on error
	 */
	public void writeComment(String comment) throws IOException
		{
		checkNoArrayMode();
		
		if (comment == null)
			return;
		
		if (objectWritten)
			{
			w.write(EOL);
			objectWritten = false;
			}
		
		final String[] lines = comment.split(EOL);
		for (int i = 0; i < lines.length; i++)
			{
			w.write("// ");
			w.write(lines[i]);
			w.write(EOL);
			}
		}
	
	/**
	 * Write an object
	 * @param obj The object
	 * @throws IOException on error
	 */
	public void write(Object obj) throws IOException
		{
		checkNoArrayMode();
		
		writeObject(obj);
		
		objectWritten = true;
//		w.write(EOL);
		}
	
	public void startArray() throws IOException
		{
		checkNoArrayMode();
		arrayMode = START_ARRAY;
		
		w.write("[");
		w.write(EOL);
		}
	
	public void appendArray(Object obj) throws IOException
		{
		checkArrayMode();
		
		if (arrayMode == START_ARRAY)
			arrayMode = IN_ARRAY;
		else
			{
			w.write(",");
			w.write(EOL);
			}
		
		writeObject(obj);
		}
	
	public void endArray() throws IOException
		{
		checkArrayMode();
		
		if (arrayMode == IN_ARRAY)
			w.write(EOL);
		arrayMode = NO_ARRAY;
		
		w.write("]");
		
		objectWritten = true;
//		w.write(EOL);
		}
	
	private void checkNoArrayMode() throws IOException
		{
		if (arrayMode != NO_ARRAY)
			throw new IOException("In array mode");
		}
	
	private void checkArrayMode() throws IOException
		{
		if (arrayMode == NO_ARRAY)
			throw new IOException("Not in array mode");
		}
	
	private void writeObject(Object obj) throws IOException
		{
		if (obj == null)
			w.write(JSON_NULL);
		else if (obj.getClass().isArray())
			writeArray(obj);
		else if (obj instanceof Collection)
			writeCollection((Collection) obj);
		else if (obj instanceof Map)
			writeMap((Map) obj);
		else
			writeSimple(obj);
		}
	
	private void writeArray(Object arr) throws IOException
		{
		w.write("[ ");
		final int n = Array.getLength(arr);
		for (int i = 0; i < n; i++)
			{
			if (i > 0)
				w.write(", ");
			writeObject(Array.get(arr, i));
			}
		w.write(" ]");
		}
	
	private void writeCollection(Collection c) throws IOException
		{
		w.write("[ ");
		boolean first = true;
		final Iterator i = c.iterator();
		while (i.hasNext())
			{
			if (first)
				first = false;
			else
				w.write(", ");
			writeObject(i.next());
			}
		w.write(" ]");
		}
	
	private void writeMap(Map m) throws IOException
		{
		w.write("{ ");
		boolean first = true;
		final Iterator i = m.entrySet().iterator();
		while (i.hasNext())
			{
			if (first)
				first = false;
			else
				w.write(", ");
			final Map.Entry ent = (Map.Entry) i.next();
			writeString(ent.getKey().toString());
			w.write(" : ");
			writeObject(ent.getValue());
			}
		w.write(" }");
		}
	
	private void writeSimple(Object obj) throws IOException
		{
		if (Boolean.TRUE.equals(obj))
			w.write(JSON_TRUE);
		else if (Boolean.FALSE.equals(obj))
			w.write(JSON_FALSE);
		else if (obj instanceof Number)
			writeNumber((Number) obj);
		else if (obj instanceof Date)
			writeDate((Date) obj);
		else
			writeString(obj.toString());
		}
	
	private void writeString(String s) throws IOException
		{
		w.write("\"");
		if (s != null)
			w.write(s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"").replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r").replaceAll("\\t", "\\\\t"));
		w.write("\"");
		}
	
	private void writeNumber(Number n) throws IOException
		{
		w.write(n.toString());
		}
	
	private void writeDate(Date d) throws IOException
		{
		writeString(DateUtils.asString(d, DateUtils.DATETIME_ISO8601, GMT));
		}
	}
