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
package de.tweerlei.ermtools.tns;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parse TNSNAMES.ORA
 * 
 * @author Robert Wruck
 */
public class TNSNamesParser
	{
	private static enum State
		{
		TOP,
		KEY_START,
		KEY,
		KEY_END,
		VALUE_START,
		VALUE,
		VALUE_END,
		VALUE_NEXT
		}
	
	/**
	 * Constructor
	 */
	public TNSNamesParser()
		{
		}
	
	/**
	 * Parse data from a Reader
	 * @param r Reader
	 * @return Parsed map
	 * @throws IOException on error
	 */
	public Map<String, Object> parse(Reader r) throws IOException
		{
		final Map<String, Object> map = new TreeMap<String, Object>();
		parse(r, map, true);
		return (map);
		}
	
	private void parse(Reader r, Map<String, Object> map, boolean toplevel) throws IOException
		{
		State state = toplevel ? State.TOP : State.KEY_START;
		StringBuilder key = null;
		StringBuilder value = null;
		Map<String, Object> valueMap = null;
		
		for (;;)
			{
			final int c = r.read();
			if (c < 0)
				{
				if (state != State.TOP)
					throw new IllegalStateException("Unexpected end of file");
				return;
				}
			
			final char ch = (char) c;
			
			switch (state)
				{
				case TOP:
				case KEY_START:
					if (ch == '#')
						skipComment(r);
					else if (isName(ch))
						{
						key = new StringBuilder();
						key.append(ch);
						state = State.KEY;
						}
					else if (!Character.isWhitespace(ch))
						throw new IllegalStateException("Unexpected character " + ch);
					break;
				
				case KEY:
					if (ch == '=')
						state = State.VALUE_START;
					else if (isName(ch))
						key.append(ch);
					else if (Character.isWhitespace(ch))
						state = State.KEY_END;
					else
						throw new IllegalStateException("Unexpected character " + ch);
					break;
				
				case KEY_END:
					if (ch == '#')
						skipComment(r);
					else if (ch == '=')
						state = State.VALUE_START;
					else if (!Character.isWhitespace(ch))
						throw new IllegalStateException("Unexpected character " + ch);
					break;
				
				case VALUE_START:
					if (ch == '#')
						skipComment(r);
					else if (ch == '(')
						{
						valueMap = new HashMap<String, Object>(1);
						parse(r, valueMap, false);
						if (toplevel)
							{
							map.put(key.toString().toUpperCase(), valueMap);
							key = null;
							state = State.TOP;
							}
						else
							state = State.VALUE_NEXT;
						}
					else if (isName(ch))
						{
						value = new StringBuilder();
						value.append(ch);
						state = State.VALUE;
						}
					else if (!Character.isWhitespace(ch))
						throw new IllegalStateException("Unexpected character " + ch);
					break;
				
				case VALUE_NEXT:
					if (ch == '#')
						skipComment(r);
					else if (ch == '(')
						parse(r, valueMap, false);
					else if (c == ')')
						{
						map.put(key.toString().toUpperCase(), valueMap);
						return;
						}
					else if (!Character.isWhitespace(ch))
						throw new IllegalStateException("Unexpected character " + ch);
					break;
				
				case VALUE:
					if (isName(ch))
						value.append(ch);
					else if (c == ')')
						{
						map.put(key.toString().toUpperCase(), value.toString());
						return;
						}
					else if (Character.isWhitespace(c))
						state = State.VALUE_END;
					else
						throw new IllegalStateException("Unexpected character " + c);
					break;
				
				case VALUE_END:
					if (c == ')')
						{
						map.put(key.toString().toUpperCase(), value.toString());
						return;
						}
					else if (!Character.isWhitespace(c))
						throw new IllegalStateException("Unexpected character " + c);
					break;
				}
			}
		}
	
	private boolean isName(char ch)
		{
		return ((ch != '=') && (ch != '(') && (ch != ')') && !Character.isWhitespace(ch));
		}
	
	private void skipComment(Reader r) throws IOException
		{
		for (;;)
			{
			final int c = r.read();
			if (c < 0)
				break;
			if (c == '\n')
				break;
			}
		}
	}
