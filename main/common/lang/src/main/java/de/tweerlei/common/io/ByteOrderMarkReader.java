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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * FilterWriter that discards an initial BOM
 * 
 * @author Robert Wruck
 */
public class ByteOrderMarkReader extends FilterReader
	{
	private static final char BOM = '\ufeff';
	
	private boolean bomRead;
	
	/**
	 * Constructor
	 * @param in Underlying Reader
	 */
	public ByteOrderMarkReader(Reader in)
		{
		super(in);
		bomRead = false;
		}
	
	public int read() throws IOException
		{
		int ret = super.read();
		if (!bomRead)
			{
			bomRead = true;
			if (ret == BOM)
				ret = super.read();
			}
		return (ret);
		}

	public int read(char[] cbuf, int off, int len) throws IOException
		{
		if (!bomRead && (len > 0))
			{
			bomRead = true;
			final int ch = super.read();
			if (ch < 0)
				return (ch);
			if (ch != BOM)
				{
				cbuf[0] = (char) ch;
				final int ret = super.read(cbuf, 1, len - 1);
				if (ret < 0)
					return (1);
				else
					return (ret + 1);
				}
			}
		return (super.read(cbuf, off, len));
		}

	public long skip(long n) throws IOException
		{
		if (!bomRead && (n > 0))
			{
			bomRead = true;
			final int ch = super.read();
			if (ch < 0)
				return (0);
			if (ch != BOM)
				return (super.skip(n - 1) + 1);
			}
		return (super.skip(n));
		}
	}
