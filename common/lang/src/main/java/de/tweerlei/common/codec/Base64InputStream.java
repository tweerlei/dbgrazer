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
 * FilterInputStream that base64-decodes data
 * 
 * @author Robert Wruck
 */
public class Base64InputStream extends InputStream
	{
	private static final byte[] b64_dec =
		{
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2, -1, -1, -2, -1, -1,	// 16
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,	// 32
		-2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,	// 48
		52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -3, -1, -1,	// 64
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,	// 80
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,	// 96
		-1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,	// 112
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,	// 128
		};
	
	private static final int fetch_size = 16;
	
	private final Reader in;
	private final char[] dpad;
	private int dpos;
	private int dlen;
	private final int[] dbuf;
	private final int[] pad;
	private int len;
	
	/**
	 * Constructor
	 * @param is Source InputStream
	 */
	public Base64InputStream(Reader is)
		{
		in = is;
		dpad = new char[fetch_size];
		dpos = 0;
		dlen = 0;
		dbuf = new int[4];
		pad = new int[3];
		len = 0;
		}
	
	public int available() throws IOException
		{
		return ((len < 0) ? 0 : len);
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
		if (len == 0)
			decode();
		if (len < 0)
			return (-1);
		
		len--;
		return (pad[len]);
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
		if (len < 0)
			return (0);
		if (n <= len)
			{
			len -= n;
			return (n);
			}
		len = 0;
		final long div = n / 3;
		final long rem = n % 3;
		for (long l = 0; l < div; l++)
			{
			if (!fetch())
				return (l * 3);
			}
		if (rem > 0)
			{
			if (!decode())
				return (div * 3);
			if (rem <= len)
				len -= rem;
			else
				len = -1;
			}
		return (n);
		}
	
	private boolean decode() throws IOException
		{
		if (!fetch())
			{
			len = -1;
			return (false);
			}
		
		if (dbuf[2] == -1)
			{
			pad[0] = (dbuf[0] << 2) | (dbuf[1] >> 4);
			len = 1;
			}
		else if (dbuf[3] == -1)
			{
			pad[1] = (dbuf[0] << 2) | (dbuf[1] >> 4);
			pad[0] = ((dbuf[1] << 4) | (dbuf[2] >> 2)) & 0xff;
			len = 2;
			}
		else
			{
			pad[2] = (dbuf[0] << 2) | (dbuf[1] >> 4);
			pad[1] = ((dbuf[1] << 4) | (dbuf[2] >> 2)) & 0xff;
			pad[0] = ((dbuf[2] << 6) | (dbuf[3])) & 0xff;
			len = 3;
			}
		
		return (true);
		}
	
	private boolean fetch() throws IOException
		{
		for (int i = 0; ; )
			{
			for (; dpos < dlen; dpos++)
				{
				if (dpad[dpos] > 127)
					throw new IOException("Invalid base64 data");
				final int t = b64_dec[dpad[dpos]];
				if (t == -3)	// padding
					{
					if (i < 2)
						throw new IOException("Invalid base64 data");
					dbuf[i] = -1;
					}
				else if (t == -2)	// white space
					continue;
				else if (t < 0)
					throw new IOException("Invalid base64 data");
				else
					dbuf[i] = t;
				if (++i == 4)
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
