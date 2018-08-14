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
import java.io.OutputStream;
import java.io.Writer;

/**
 * FilterOutputStream that base64-encodes data
 * 
 * @author Robert Wruck
 */
public class Base64OutputStream extends OutputStream
	{
	private static final char b64_enc[] =
		{
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
		'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
		'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
		};
	
	private static final char b64_padding = '=';
	
	private final Writer out;
	private final byte[] pad;
	private final StringBuffer epad;
	private final int lineWidth;
	private final String eol;
	private int len;
	
	/**
	 * Constructor
	 * @param os Target OutputStream
	 */
	public Base64OutputStream(Writer os)
		{
		this(os, 0, null);
		}
	
	/**
	 * Constructor
	 * @param os Target OutputStream
	 * @param w Line width
	 * @param nl End of line marker
	 */
	public Base64OutputStream(Writer os, int w, String nl)
		{
		out = os;
		pad = new byte[3];
		epad = new StringBuffer();
		if (w <= 0)
			{
			lineWidth = 72;
			eol = null;
			}
		else
			{
			lineWidth = w;
			eol = nl;
			}
		len = 0;
		}
	
	public void close() throws IOException
		{
		flush();
		out.close();
		}
	
	public void flush() throws IOException
		{
		if (len > 0)
			encode();
		if (epad.length() > 0)
			{
			out.write(epad.toString());
			if (eol != null)
				out.write(eol);
			}
		out.flush();
		}
	
	public void write(byte[] b, int off, int l) throws IOException
		{
		for (int i = 0; i < l; i++)
			write(b[off + i]);
		}
	
	public void write(byte[] b) throws IOException
		{
		write(b, 0, b.length);
		}
	
	public void write(int c) throws IOException
		{
		pad[len++] = (byte) c;
		if (len == 3)
			encode();
		}
	
	private void encode() throws IOException
		{
		final int a = pad[0] & 0xff;
		final int b = pad[1] & 0xff;
		final int c = pad[2] & 0xff;
		
		synchronized (epad)
			{
			switch (len)
				{
				case 1:
					epad.append(b64_enc[(a >> 2) & 0x3f]);
					epad.append(b64_enc[(a << 4) & 0x30]);
					epad.append(b64_padding);
					epad.append(b64_padding);
					break;
				
				case 2:
					epad.append(b64_enc[(a >> 2) & 0x3f]);
					epad.append(b64_enc[(a << 4) & 0x30 | (b >> 4) & 0x0f]);
					epad.append(b64_enc[(b << 2) & 0x3c]);
					epad.append(b64_padding);
					break;
				
				case 3:
					epad.append(b64_enc[(a >> 2) & 0x3f]);
					epad.append(b64_enc[(a << 4) & 0x30 | (b >> 4) & 0x0f]);
					epad.append(b64_enc[(b << 2) & 0x3c | (c >> 6) & 0x03]);
					epad.append(b64_enc[c & 0x3f]);
					break;
				}
			
			while (epad.length() >= lineWidth)
				{
				out.write(epad.substring(0, lineWidth));
				if (eol != null)
					out.write(eol);
				epad.delete(0, lineWidth);
				}
			}
		
		len = 0;
		}
	}
