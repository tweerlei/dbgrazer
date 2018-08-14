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
 * Base64 codec
 * 
 * @author Robert Wruck
 */
public class Base64Codec implements StringCodec
	{
	private final int lineWidth;
	private final String eol;
	
	/**
	 * Constructor
	 */
	public Base64Codec()
		{
		this(0, null);
		}
	
	/**
	 * Constructor
	 * @param w Line width
	 * @param nl End of line marker
	 */
	public Base64Codec(int w, String nl)
		{
		this.lineWidth = w;
		this.eol = nl;
		}
	
	public byte[] decode(String code) throws IOException
		{
		final ByteArrayOutputStream os = new ByteArrayOutputStream(code.length() * 3 / 4);
		
		StreamUtils.copy(new Base64InputStream(new StringReader(code)), os);
		
		return (os.toByteArray());
		}
	
	public String encode(byte[] data) throws IOException
		{
		return (encode(data, 0, data.length));
		}
	
	public String encode(byte[] data, int offset, int length) throws IOException
		{
		final StringWriter os = new StringWriter(data.length * 4 / 3 + 3);
		final Base64OutputStream hs = new Base64OutputStream(os, lineWidth, eol);
		
		StreamUtils.copy(new ByteArrayInputStream(data, offset, length), hs);
		hs.close();
		
		return (os.toString());
		}
	}
