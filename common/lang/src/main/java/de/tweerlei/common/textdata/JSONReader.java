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

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Deserialize objects from JSON
 * 
 * @author Robert Wruck
 */
public class JSONReader
	{
	// JSON literals
	private static final String JSON_NULL = "null";
	private static final String JSON_TRUE = "true";
	private static final String JSON_FALSE = "false";
	
	private static final class Token
		{
		private final String name;
		
		public Token(String name)
			{
			this.name = name;
			}
		
		public String toString()
			{
			return (name);
			}
		}
	
	private static final Token ARRAY = new Token("'['");
	private static final Token MAP = new Token("'{'");
	private static final Token ARRAY_SEP = new Token("','");
	private static final Token MAP_SEP = new Token("':'");
	private static final Token ARRAY_END = new Token("']'");
	private static final Token MAP_END = new Token("'}'");
	
	private final Reader r;
	private int lastChar;
	
	/**
	 * Constructor
	 * @param r Underlying reader
	 */
	public JSONReader(Reader r)
		{
		this.r = r;
		this.lastChar = -1;
		}
	
	/**
	 * Close the reader
	 * @throws IOException on error
	 */
	public void close() throws IOException
		{
		r.close();
		}
	
	/**
	 * Read a Java Object
	 * @return The Java Object
	 * @throws IOException on error
	 */
	public Object read() throws IOException
		{
		final Object token = readToken();
		if (token == ARRAY)
			return (readList());
		else if (token == MAP)
			return (readMap());
		else if (token instanceof Token)
			throw new IOException("Unexpected token " + token);
		else
			return (token);
		}
	
	private List readList() throws IOException
		{
		final List ret = new ArrayList();
		
		for (;;)
			{
			Object token = readToken();
			if (token == ARRAY_END)
				break;
			if (!ret.isEmpty())
				{
				if (token != ARRAY_SEP)
					throw new IOException("Expected ',' but got " + token);
				token = readToken();
				}
			
			if (token == ARRAY)
				token = readList();
			else if (token == MAP)
				token = readMap();
			else if (token instanceof Token)
				throw new IOException("Unexpected token " + token);
			
			ret.add(token);
			}
		
		return (ret);
		}
	
	private Map readMap() throws IOException
		{
		final Map ret = new LinkedHashMap();
		
		for (;;)
			{
			Object token = readToken();
			if (token == MAP_END)
				break;
			if (!ret.isEmpty())
				{
				if (token != ARRAY_SEP)
					throw new IOException("Expected ',' but got " + token);
				token = readToken();
				}
			
			if (!(token instanceof String))
				throw new IOException("Expected a String but got " + token);
			
			Object token2 = readToken();
			if (token2 != MAP_SEP)
				throw new IOException("Expected ':' but got " + token2);
			
			token2 = readToken();
			if (token2 == ARRAY)
				token2 = readList();
			else if (token2 == MAP)
				token2 = readMap();
			else if (token2 instanceof Token)
				throw new IOException("Unexpected token " + token2);
			
			ret.put(token, token2);
			}
		
		return (ret);
		}
	
	private Object readToken() throws IOException
		{
		for (;;)
			{
			final int ch = readChar();
			if (ch < 0)
				throw new EOFException();
			switch (ch)
				{
				case '[':
					return (ARRAY);
				case '{':
					return (MAP);
				case ',':
					return (ARRAY_SEP);
				case ':':
					return (MAP_SEP);
				case ']':
					return (ARRAY_END);
				case '}':
					return (MAP_END);
				case ' ':
				case '\r':
				case '\n':
				case '\t':
					break;
				case '\"':
					return (readString());
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '-':
					return (readNumber((char) ch));
				case 't':
					{
					final String word = readWord((char) ch, 3);
					if (word.equals(JSON_TRUE))
						return (Boolean.TRUE);
					throw new IOException("Unknown literal '" + word + "'");
					}
				case 'f':
					{
					final String word = readWord((char) ch, 4);
					if (word.equals(JSON_FALSE))
						return (Boolean.FALSE);
					throw new IOException("Unknown literal '" + word + "'");
					}
				case 'n':
					{
					final String word = readWord((char) ch, 3);
					if (word.equals(JSON_NULL))
						return (null);
					throw new IOException("Unknown literal '" + word + "'");
					}
				default:
					throw new IOException("Unexpected character '" + ((char) ch) + "'");
				}
			}
		}
	
	private String readString() throws IOException
		{
		final StringBuffer sb = new StringBuffer();
		boolean quote = false;
		int unicodePos = 0;
		int unicodeChar = 0;
		
		for (;;)
			{
			final int ch = readChar();
			if (ch < 0)
				throw new EOFException();
			if (unicodePos > 0)
				{
				switch (ch)
					{
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						unicodeChar = (unicodeChar << 4) | (ch - '0');
						break;
					case 'A':
					case 'B':
					case 'C':
					case 'D':
					case 'E':
					case 'F':
						unicodeChar = (unicodeChar << 4) | (ch - '7');
						break;
					case 'a':
					case 'b':
					case 'c':
					case 'd':
					case 'e':
					case 'f':
						unicodeChar = (unicodeChar << 4) | (ch - 'W');
						break;
					default:
						throw new IOException("Undefined hex digit '" + ((char) ch) + "'");
					}
				unicodePos--;
				if (unicodePos == 0)
					sb.append(unicodeChar);
				}
			else if (quote)
				{
				switch (ch)
					{
					case '\"':
					case '\\':
					case '/':
						sb.append((char) ch);
						break;
					case 'b':
						sb.append('\b');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'u':
						unicodePos = 4;
						unicodeChar = 0;
						break;
					default:
						throw new IOException("Undefined escape sequence '" + ((char) ch) + "'");
					}
				quote = false;
				}
			else
				{
				switch (ch)
					{
					case '\"':
						return (sb.toString());
					case '\\':
						quote = true;
						break;
					default:
						if (Character.isISOControl((char) ch))
							throw new IOException("Illegal string character '" + ch + "'");
						sb.append((char) ch);
						break;
					}
				}
			}
		}
	
	private Number readNumber(char first) throws IOException
		{
		final String word = readNumberString(first);
		
		try	{
			return (new Integer(Integer.parseInt(word)));
			}
		catch (NumberFormatException e)
			{
			// continue
			}
		
		try	{
			return (new Long(Long.parseLong(word)));
			}
		catch (NumberFormatException e)
			{
			// continue
			}
		
		try	{
			return (new Double(Double.parseDouble(word)));
			}
		catch (NumberFormatException e)
			{
			// continue
			}
		
		throw new IOException("Invalid number '" + word + "'");
		}
	
	private String readNumberString(char first) throws IOException
		{
		final StringBuffer sb = new StringBuffer();
		sb.append(first);
		
		for (;;)
			{
			final int ch = readChar();
			if (ch < 0)
				return (sb.toString());
			switch (ch)
				{
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '.':
				case '+':
				case '-':
				case 'e':
				case 'E':
					sb.append((char) ch);
					break;
				default:
					pushBack(ch);
					return (sb.toString());
				}
			}
		}
	
	private String readWord(char first, int n) throws IOException
		{
		final StringBuffer sb = new StringBuffer();
		sb.append(first);
		for (int i = 0; i < n; i++)
			{
			final int ch = readChar();
			if (ch < 0)
				throw new EOFException();
			sb.append((char) ch);
			}
		return (sb.toString());
		}
	
	private int readChar() throws IOException
		{
		if (lastChar < 0)
			return (r.read());
		else
			{
			final int ch = lastChar;
			lastChar = -1;
			return (ch);
			}
		}
	
	private void pushBack(int ch)
		{
		lastChar = ch;
		}
	}
