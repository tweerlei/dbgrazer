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
public class HexOutputStream extends OutputStream
	{
	private static final char hex_enc_u[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final char hex_enc_l[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	private final Writer out;
	private final StringBuffer epad;
	private final String spacer;
	private final int lineWidth;
	private final String eol;
	private final char[] hex_enc;
	private int len;
	
	/**
	 * Constructor
	 * @param os Target OutputStream
	 */
	public HexOutputStream(Writer os)
		{
		this(os, true, null, 0, null);
		}
	
	/**
	 * Constructor
	 * @param os Target OutputStream
	 * @param lowercase If true, output lowercase hex
	 * @param spc Spacer between octets
	 * @param w Line width
	 * @param nl End of line marker
	 */
	public HexOutputStream(Writer os, boolean lowercase, String spc, int w, String nl)
		{
		out = os;
		epad = new StringBuffer();
		spacer = spc;
		if (w <= 0)
			{
			lineWidth = 16;
			eol = null;
			}
		else
			{
			lineWidth = w;
			eol = nl;
			}
		hex_enc = lowercase ? hex_enc_l : hex_enc_u;
		len = 0;
		}
	
	public void close() throws IOException
		{
		flush();
		out.close();
		}
	
	public void flush() throws IOException
		{
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
		encode(c);
		}
	
	private void encode(int c) throws IOException
		{
		final int a = c & 0xff;
		
		synchronized (epad)
			{
			if ((spacer != null) && (epad.length() > 0))
				epad.append(spacer);
			
			epad.append(hex_enc[a >> 4]);
			epad.append(hex_enc[a & 0xf]);
			len++;
			
			if (len == lineWidth)
				{
				out.write(epad.toString());
				if (eol != null)
					out.write(eol);
				epad.setLength(0);
				len = 0;
				}
			}
		}
	}
