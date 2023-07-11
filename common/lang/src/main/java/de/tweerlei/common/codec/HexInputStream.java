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
package de.tweerlei.common.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * FilterInputStream that hex-decodes data
 * 
 * @author Robert Wruck
 */
public class HexInputStream extends InputStream
	{
	private static final byte[] hex_dec =
		{
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2, -1, -1, -2, -1, -1,	// 16
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 32
		-2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 48
		 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1,	// 64
		-1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 80
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 96
		-1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 112
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 128
		};
	
	private static final int fetch_size = 8;
	
	private final Reader in;
	private final char[] dpad;
	private int dpos;
	private int dlen;
	private final int[] dbuf;
	
	/**
	 * Constructor
	 * @param is Source InputStream
	 */
	public HexInputStream(Reader is)
		{
		in = is;
		dpad = new char[fetch_size];
		dpos = 0;
		dlen = 0;
		dbuf = new int[2];
		}
	
	public int available() throws IOException
		{
		return (0);
		}
	
	public void close() throws IOException
		{
		in.close();
		}
	
	public void mark(int readlimit)
		{
		}
	
	public boolean markSupported()
		{
		return (false);
		}

	public int read() throws IOException
		{
		return (decode());
		}

	public int read(byte[] b, int off, int l) throws IOException
		{
		int ret = 0;
		for (int i = 0; i < l; i++)
			{
			int c = read();
			if (c < 0)
				break;
			b[off + i] = (byte) c;
			ret++;
			}
		return (ret == 0 ? -1 : ret);
		}

	public int read(byte[] b) throws IOException
		{
		return (read(b, 0, b.length));
		}

	public void reset() throws IOException
		{
		}

	public long skip(long n) throws IOException
		{
		if (n < 0)
			return (0);
		for (long l = 0; l < n; l++)
			{
			if (!fetch())
				return (l);
			}
		return (n);
		}
	
	private int decode() throws IOException
		{
		if (!fetch())
			return (-1);
		
		return (dbuf[0] * 16 + dbuf[1]);
		}
	
	private boolean fetch() throws IOException
		{
		for (int i = 0; ; )
			{
			for (; dpos < dlen; dpos++)
				{
				if (dpad[dpos] > 127)
					throw new IOException("Invalid hex data");
				final int t = hex_dec[dpad[dpos]];
				if (t == -2)	// white space
					continue;
				else if (t < 0)
					throw new IOException("Invalid hex data");
				else
					dbuf[i] = t;
				if (++i == 2)
					{
					dpos++;
					return (true);
					}
				}
			
			dpos = 0;
			dlen = in.read(dpad);
			if (dlen < 0)
				return (false);
			}
		}
	}
