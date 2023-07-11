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
package de.tweerlei.common.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * FilterWriter that writes an initial BOM on demand
 * 
 * @author Robert Wruck
 */
public class ByteOrderMarkWriter extends FilterWriter
	{
	private static final char BOM = '\ufeff';
	
	private boolean bomWritten;
	
	/**
	 * Constructor
	 * @param out Underlying Writer
	 */
	public ByteOrderMarkWriter(Writer out)
		{
		super(out);
		bomWritten = false;
		}
	
	public void write(char[] cbuf, int off, int len) throws IOException
		{
		writeBOM();
		super.write(cbuf, off, len);
		}
	
	public void write(int c) throws IOException
		{
		writeBOM();
		super.write(c);
		}
	
	public void write(String str, int off, int len) throws IOException
		{
		writeBOM();
		super.write(str, off, len);
		}
	
	private void writeBOM() throws IOException
		{
		if (!bomWritten)
			{
			super.write(BOM);
			bomWritten = true;
			}
		}
	}
