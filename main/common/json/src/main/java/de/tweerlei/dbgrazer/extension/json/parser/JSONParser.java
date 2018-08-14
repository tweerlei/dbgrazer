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
package de.tweerlei.dbgrazer.extension.json.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Parser for JSON tokens.
 * 
 * @author Robert Wruck
 */
public class JSONParser
	{
	private static enum State
		{
		INITIAL,
		NAME,
		STRING,
		STRING_ESC,
		STRING_UNI,
		NUMBER,
		NUMBER_EXP,
		COMMENT_START,
		COMMENT,
		COMMENT_END
		}
	
	private static final Set<String> names;
	static
		{
		names = new HashSet<String>();
		names.add("true");
		names.add("false");
		names.add("null");
		}
	
	private final JSONConsumer consumer;
	private final StringBuilder token;
	private final StringBuilder space;
	private final StringBuilder unicode;
	private State state;
	
	/**
	 * Constructor
	 * @param handler Token handler
	 */
	public JSONParser(JSONHandler handler)
		{
		this.consumer = new JSONConsumer(handler);
		this.token = new StringBuilder();
		this.space = new StringBuilder();
		this.unicode = new StringBuilder();
		this.state = State.INITIAL;
		}
	
	private void append(char c)
		{
		token.append(c);
		}
	
	private void whitespace(char c)
		{
		space.append(c);
		}
	
	private void clearToken()
		{
		token.setLength(0);
		space.setLength(0);
		}
	
	private void flushToken()
		{
		if (space.length() > 0)
			{
			final String s = space.toString();
			consumer.appendSpace(s);
			}
		
		if ((state == State.STRING) || (token.length() > 0))
			{
			final String s = token.toString();
			switch (state)
				{
				case NAME:
					if (!names.contains(s))
						throw new IllegalStateException("Unknown name '" + s + "'");
					consumer.appendName(s);
					break;
				case STRING:
					consumer.appendString(s);
					break;
				case NUMBER:
					consumer.appendNumber(s);
					break;
				case COMMENT_END:
					consumer.appendComment(s);
					break;
				default:
					throw new RuntimeException("Unexpected flush");
				}
			}
		
		clearToken();
		}
	
	private boolean isNumberStart(char c)
		{
		return (Character.isDigit(c) || (c == '-'));
		}
	
	private boolean isNumber(char c)
		{
		return (Character.isDigit(c) || (c == '.'));
		}
	
	private boolean isNameStart(char c)
		{
		return ((c == 't') || (c == 'f') || (c == 'n'));
		}
	
	private boolean isName(char c)
		{
		return (Character.isLetter(c));
		}
	
	private boolean isHex(char c)
		{
		return (((c >= '0') && (c <= '9'))
				|| ((c >= 'A') && (c <= 'F'))
				|| ((c >= 'a') && (c <= 'f')));
		}
	
	/**
	 * Parse a string into tokens
	 * @param sql SQL string
	 * @return this
	 */
	public JSONParser parse(String sql)
		{
		if (sql == null)
			return (this);
		
		state = State.INITIAL;
		
		final int n = sql.length();
		
		for (int i = 0; i < n; i++)
			{
			final char c = sql.charAt(i);
			switch (state)
				{
				case INITIAL:
					if (isNumberStart(c))
						{
						flushToken();
						append(c);
						state = State.NUMBER;
						}
					else if (c == '{')
						{
						flushToken();
						consumer.startObject();
						}
					else if (c == '[')
						{
						flushToken();
						consumer.startArray();
						}
					else if (c == '}')
						{
						flushToken();
						consumer.endObject();
						}
					else if (c == ']')
						{
						flushToken();
						consumer.endArray();
						}
					else if (c == '"')
						{
						flushToken();
//						append(c);
						state = State.STRING;
						}
					else if (c == '/')
						{
						flushToken();
//						append(c);
						state = State.COMMENT_START;
						}
					else if (c == ':')
						{
						flushToken();
						consumer.appendKeySeparator();
						}
					else if (c == ',')
						{
						flushToken();
						consumer.appendValueSeparator();
						}
					else if (isNameStart(c))
						{
						append(c);
						state = State.NAME;
						}
					else if (Character.isWhitespace(c))
						whitespace(c);
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NAME:
					if (isName(c))
						append(c);
					else if (c == ',')
						{
						flushToken();
						consumer.appendValueSeparator();
						state = State.INITIAL;
						}
					else if (c == '}')
						{
						flushToken();
						consumer.endObject();
						state = State.INITIAL;
						}
					else if (c == ']')
						{
						flushToken();
						consumer.endArray();
						state = State.INITIAL;
						}
					else if (c == '/')
						{
						flushToken();
//						append(c);
						state = State.COMMENT_START;
						}
					else if (Character.isWhitespace(c))
						{
						flushToken();
						whitespace(c);
						state = State.INITIAL;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NUMBER:
					if (isNumber(c))
						append(c);
					else if ((c == 'e') || (c == 'E'))
						{
						append(c);
						state = State.NUMBER_EXP;
						}
					else if (c == ',')
						{
						flushToken();
						consumer.appendValueSeparator();
						state = State.INITIAL;
						}
					else if (c == '}')
						{
						flushToken();
						consumer.endObject();
						state = State.INITIAL;
						}
					else if (c == ']')
						{
						flushToken();
						consumer.endArray();
						state = State.INITIAL;
						}
					else if (c == '/')
						{
						flushToken();
//						append(c);
						state = State.COMMENT_START;
						}
					else if (Character.isWhitespace(c))
						{
						flushToken();
						whitespace(c);
						state = State.INITIAL;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NUMBER_EXP:
					if (isNumber(c))
						{
						append(c);
						state = State.NUMBER;
						}
					else if ((c == '-') || (c == '+'))
						{
						append(c);
						state = State.NUMBER;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case STRING:
					if (c == '\\')
						state = State.STRING_ESC;
					else if (c == '"')
						{
						flushToken();
						state = State.INITIAL;
						}
					else
						append(c);
					break;
				
				case STRING_ESC:
					if ((c == '"') || (c == '\\') || (c == '/'))
						{
						append(c);
						state = State.STRING;
						}
					else if (c == 'b')
						{
						append('\b');
						state = State.STRING;
						}
					else if (c == 'f')
						{
						append('\f');
						state = State.STRING;
						}
					else if (c == 'n')
						{
						append('\n');
						state = State.STRING;
						}
					else if (c == 'r')
						{
						append('\r');
						state = State.STRING;
						}
					else if (c == 't')
						{
						append('\t');
						state = State.STRING;
						}
					else if (c == 'u')
						{
						state = State.STRING_UNI;
						unicode.setLength(0);
						}
					break;
				
				case STRING_UNI:
					if (!isHex(c))
						throw new IllegalStateException("Unexpected character '" + c + "'");
					unicode.append(c);
					if (unicode.length() == 4)
						{
						append((char) Integer.parseInt(unicode.toString(), 16));
						state = State.STRING;
						}
					break;
				
				case COMMENT_START:
					if (c == '*')
						state = State.COMMENT;
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case COMMENT:
					if (c == '*')
						state = State.COMMENT_END;
					else
						append(c);
					break;
				
				case COMMENT_END:
					if (c == '/')
						{
						flushToken();
						state = State.INITIAL;
						}
					else if (c == '*')
						append('*');
					else
						{
						append('*');
						append(c);
						state = State.COMMENT;
						}
					break;
				}
			}
		
		if ((state == State.STRING) || (state == State.STRING_ESC) || (state == State.STRING_UNI))
			throw new IllegalStateException("Unterminated string literal");
		if ((state == State.COMMENT_START) || (state == State.COMMENT) || (state == State.COMMENT_END))
			throw new IllegalStateException("Unterminated comment");
		
		flushToken();
		
		consumer.finish();
		
		return (this);
		}
	}
