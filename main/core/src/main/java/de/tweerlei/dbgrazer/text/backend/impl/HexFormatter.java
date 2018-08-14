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
package de.tweerlei.dbgrazer.text.backend.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.text.backend.BaseTextFormatter;

/**
 * Format Strings by encoding as ISO-8859-15 (single bytes) and rendering the result as hex bytes + ASCII dump
 * 
 * @author Robert Wruck
 */
@Service
public class HexFormatter extends BaseTextFormatter
	{
	private static final String CHARSET = "ISO-8859-15";
	private static final int WIDTH = 32;
	private static final char HEX_CHARS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final char SPACE = ' ';
	private static final char EOL = '\n';
	private static final char CTRL = '.';
	
	/**
	 * Constructor
	 */
	public HexFormatter()
		{
		super("Hex");
		}
	
	@Override
	public String format(String value)
		{
		if (value == null)
			return (value);
		
		try	{
			final byte[] bytes = value.getBytes(CHARSET);
			final StringBuilder sb = new StringBuilder();
			
			final int lastLine = bytes.length % WIDTH;
			final int length = bytes.length - lastLine;
			
			// Write full lines
			for (int i = 0; i < length; i += WIDTH)
				writeLine(sb, bytes, i, WIDTH);
			
			if (lastLine > 0)
				writeLine(sb, bytes, length, lastLine);
			
			return (sb.toString());
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	private void writeLine(StringBuilder sb, byte[] bytes, int offset, int length)
		{
		// Write hex bytes
		for (int j = 0; j < length; j++)
			{
			final byte b = bytes[offset + j];
			sb.append(HEX_CHARS[(b >> 4) & 0x0f]);
			sb.append(HEX_CHARS[b & 0x0f]);
			sb.append(SPACE);
			}
		for (int j = length; j < WIDTH; j++)
			{
			sb.append(SPACE);
			sb.append(SPACE);
			sb.append(SPACE);
			}
		sb.append(SPACE);
		// Write ASCII chars
		for (int j = 0; j < length; j++)
			{
			final byte b = bytes[offset + j];
			if (b >= 0x20 && b < 0x7f)
				sb.append((char) b);
			else
				sb.append(CTRL);
			}
		for (int j = length; j < WIDTH; j++)
			sb.append(SPACE);
		sb.append(EOL);
		}
	
	@Override
	public boolean isXMLEncoded()
		{
		return (true);
		}
	}
