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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import de.tweerlei.common.io.StreamUtils;

/**
 * Hex codec
 * 
 * @author Robert Wruck
 */
public class HexCodec implements StringCodec
	{
	private final boolean lower;
	private final String spacer;
	private final int lineWidth;
	private final String eol;
	
	/**
	 * Constructor
	 */
	public HexCodec()
		{
		this(false, null, 0, null);
		}
	
	/**
	 * Constructor
	 * @param lc If true, output lowercase letters
	 * @param spc Spacer between bytes
	 * @param w Line width
	 * @param nl End of line marker
	 */
	public HexCodec(boolean lc, String spc, int w, String nl)
		{
		this.lower = lc;
		this.spacer = spc;
		this.lineWidth = w;
		this.eol = nl;
		}
	
	public byte[] decode(String code) throws IOException
		{
		final ByteArrayOutputStream os = new ByteArrayOutputStream(code.length() / 2);
		
		StreamUtils.copy(new HexInputStream(new StringReader(code)), os);
		
		return (os.toByteArray());
		}
	
	public String encode(byte[] data) throws IOException
		{
		return (encode(data, 0, data.length));
		}
	
	public String encode(byte[] data, int offset, int length) throws IOException
		{
		final StringWriter os = new StringWriter(data.length * 2);
		final HexOutputStream hs = new HexOutputStream(os, lower, spacer, lineWidth, eol);
		
		StreamUtils.copy(new ByteArrayInputStream(data, offset, length), hs);
		hs.close();
		
		return (os.toString());
		}
	}
