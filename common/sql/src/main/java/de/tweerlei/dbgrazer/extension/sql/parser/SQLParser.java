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
package de.tweerlei.dbgrazer.extension.sql.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Parser for SQL language tokens
 * 
 * @author Robert Wruck
 */
public class SQLParser
	{
	private static final class TokenBuffer
		{
		private final StringBuilder sb;
		
		public TokenBuffer()
			{
			this.sb = new StringBuilder();
			}
		
		public void append(char c)
			{
			sb.append(c);
			}
		
		public void append(String s)
			{
			sb.append(s);
			}
		
		public void clear()
			{
			sb.setLength(0);
			}
		
		@Override
		public String toString()
			{
			return (sb.toString());
			}
		}
	
	private static enum State
		{
		INITIAL,
		NAME,
		QUOTED_NAME,
		PARAMETER,
		STRING,
		NUMBER,
		NUMBER_EXP,
		OPERATOR,
		COMMENT,
		COMMENT_END,
		EOL_COMMENT,
		QUOTED_STRING_START,
		QUOTED_STRING,
		QUOTED_STRING_END
		}
	
	private static final Set<String> operators;
	static
		{
		operators = new HashSet<String>();
		operators.add("+");
		operators.add("-");
		operators.add("*");
		operators.add("/");
		operators.add("<");
		operators.add(">");
		operators.add("=");
		operators.add(",");
		operators.add(".");
		operators.add("@");
		operators.add("%");
		operators.add(";");
		operators.add("<=");
		operators.add(">=");
		operators.add("<>");
		operators.add("!=");
		operators.add("~=");
		operators.add("^=");
		operators.add(":=");
		operators.add("=>");
		operators.add("||");
		operators.add("**");
		operators.add("<<");
		operators.add(">>");
		operators.add("..");
		}
	
	private final SQLConsumer consumer;
	private final boolean extended;
	private final TokenBuffer token;
	private final TokenBuffer space;
	private final TokenBuffer startLabel;
	private final TokenBuffer endLabel;
	private State state;
	private int row;
	private int column;
	
	/**
	 * Constructor
	 * @param handler Token handler
	 */
	public SQLParser(SQLHandler handler)
		{
		this(handler, false);
		}
	
	/**
	 * Constructor
	 * @param handler Token handler
	 * @param extended Whether to allow multiple statements and comments
	 */
	public SQLParser(SQLHandler handler, boolean extended)
		{
		this.consumer = new SQLConsumer(handler);
		this.extended = extended;
		this.token = new TokenBuffer();
		this.space = new TokenBuffer();
		this.startLabel = new TokenBuffer();
		this.endLabel = new TokenBuffer();
		this.state = State.INITIAL;
		}
	
	/**
	 * Get the error row
	 * @return Error row
	 */
	public int getRow()
		{
		return (row);
		}
	
	/**
	 * Get the error column
	 * @return Error column
	 */
	public int getColumn()
		{
		return (column);
		}
	
	private void flushToken()
		{
		final String sp = space.toString();
		if (sp.length() > 0)
			consumer.appendSpace(sp);
		space.clear();
		
		final String s = token.toString();
		if ((s.length() > 0) || (state == State.STRING) || (state == State.COMMENT_END) || (state == State.EOL_COMMENT))
			{
			switch (state)
				{
				case NAME:
				case QUOTED_NAME:
					consumer.appendName(s);
					break;
				case STRING:
				case QUOTED_STRING_END:
					consumer.appendString(s);
					break;
				case NUMBER:
					consumer.appendNumber(s);
					break;
				case OPERATOR:
					consumer.appendOperator(s);
					break;
				case COMMENT_END:
					consumer.appendComment(s);
					break;
				case EOL_COMMENT:
					consumer.appendEOLComment(s);
					break;
				default:
					throw new RuntimeException("Unexpected flush");
				}
			}
		token.clear();
		}
	
	private boolean isNumber(char c)
		{
		return (Character.isDigit(c) || (c == '.'));
		}

	private boolean isNameStart(char c)
		{
		return (Character.isLetter(c) || (c == '_') //|| (c == '$') || (c == '#')
				|| (extended && (c == '?')));
		}
	
	private boolean isName(char c)
		{
		return (Character.isLetterOrDigit(c) || (c == '_') || (c == '$') || (c == '#')
				|| (extended && (c == '?')));
		}

	private boolean isOperator(char c)
		{
		return ((c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == '>') || (c == '<') || (c == '=') || (c == ':') || (c == '!') || (c == ',') || (c == '.') || (c == '@') || (c == '%') || (c == '|') || (c == '~') || (c == '^')
				|| (extended && (c == ';')));
		}
	
	/**
	 * Parse a string into tokens
	 * @param sql SQL string
	 * @return this
	 */
	public SQLParser parse(String sql)
		{
		try	{
			doParse(sql);
			return (this);
			}
		catch (IllegalStateException e)
			{
			throw new IllegalStateException("Error at line " + (row + 1) + ", column " + (column + 1) + ": " + e.getMessage(), e);
			}
		}
	
	private void doParse(String sql)
		{
		state = State.INITIAL;
		row = 0;
		column = 0;
		
		if (sql == null)
			return;
		
		final int n = sql.length();
		
		for (int i = 0; i < n; i++)
			{
			final char c = sql.charAt(i);
			switch (state)
				{
				case INITIAL:
					if (isNumber(c))
						{
						// distinguish number literals from "." and ".." operators
						if ((c == '.') && (i + 1 < n) && !isNumber(sql.charAt(i + 1)))
							{
							token.append(c);
							state = State.OPERATOR;
							}
						else if ((c == '.') && (i + 1 < n) && (sql.charAt(i + 1) == '.'))
							{
							token.append(c);
							state = State.OPERATOR;
							}
						else
							{
							token.append(c);
							state = State.NUMBER;
							}
						}
					else if (isNameStart(c))
						{
						token.append(c);
						state = State.NAME;
						}
					else if (c == ':')
						{
						token.append(c);
						state = State.PARAMETER;
						}
					else if (c == '$')
						{
						startLabel.append(c);
						state = State.QUOTED_STRING_START;
						}
					else if (isOperator(c))
						{
						token.append(c);
						state = State.OPERATOR;
						}
					else if (c == '\'')
						{
//						token.append(c);
						state = State.STRING;
						}
					else if (c == '"')
						{
						token.append(c);
						state = State.QUOTED_NAME;
						}
					else if (c == '(')
						{
						flushToken();
						consumer.openBrace();
						state = State.INITIAL;
						}
					else if (c == ')')
						{
						flushToken();
						consumer.closeBrace();
						state = State.INITIAL;
						}
					else if (Character.isWhitespace(c))
						space.append(c);
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NAME:
					if (isName(c))
						token.append(c);
					else if (isOperator(c))
						{
						flushToken();
						token.append(c);
						state = State.OPERATOR;
						}
					else if (c == '\'')
						{
						flushToken();
//						token.append(c);
						state = State.STRING;
						}
					else if (c == '(')
						{
						flushToken();
						consumer.openBrace();
						state = State.INITIAL;
						}
					else if (c == ')')
						{
						flushToken();
						consumer.closeBrace();
						state = State.NAME;
						}
					else if (Character.isWhitespace(c))
						{
						flushToken();
						space.append(c);
						state = State.INITIAL;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NUMBER:
					if (isNumber(c))
						{
						// detect .. operator following a number
						if ((c == '.') && (i + 1 < n) && (sql.charAt(i + 1) == '.'))
							{
							flushToken();
							token.append(c);
							state = State.OPERATOR;
							}
						else
							token.append(c);
						}
					else if ((c == 'e') || (c == 'E'))
						{
						token.append(c);
						state = State.NUMBER_EXP;
						}
					else if (isOperator(c))
						{
						flushToken();
						token.append(c);
						state = State.OPERATOR;
						}
					else if (c == '(')
						{
						flushToken();
						consumer.openBrace();
						state = State.INITIAL;
						}
					else if (c == ')')
						{
						flushToken();
						consumer.closeBrace();
						state = State.NAME;
						}
					else if (Character.isWhitespace(c))
						{
						flushToken();
						space.append(c);
						state = State.INITIAL;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NUMBER_EXP:
					if (isNumber(c))
						{
						token.append(c);
						state = State.NUMBER;
						}
					else if ((c == '-') || (c == '+'))
						{
						token.append(c);
						state = State.NUMBER;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case STRING:
					if (c == '\'')
						{
						// detect embedded single quotes
						if ((i + 1 >= n) || (sql.charAt(i + 1) != '\''))
							{
//							token.append(c);
							flushToken();
							state = State.INITIAL;
							}
						else
							{
							token.append(c);
							i++;
							}
						}
					else
						token.append(c);
					break;
				
				case QUOTED_NAME:
					token.append(c);
					if (c == '"')
						{
						flushToken();
						state = State.INITIAL;
						}
					break;
				
				case PARAMETER:
					if (isNameStart(c))
						{
						token.append(c);
						state = State.NAME;
						}
					else if (Character.isDigit(c))
						{
						token.append(c);
						state = State.NAME;
						}
					else if (c == '"')
						{
						token.append(c);
						state = State.QUOTED_NAME;
						}
					else if (c == '=')
						{
						// special case to distinguish the assignment operator from a host variable declaration
						state = State.OPERATOR;
						token.append(c);
						flushToken();
						}
					else if (c == '(')
						{
						flushToken();
						consumer.openBrace();
						state = State.INITIAL;
						}
					else if (Character.isWhitespace(c))
						space.append(c);
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case OPERATOR:
					if (isOperator(c))
						{
						final String tmp = token.toString() + c;
						// check for known compound operators
						if (tmp.equals(".*"))
							{
							flushToken();
							token.append(c);
							flushToken();
							state = State.INITIAL;
							break;
							}
						else if (operators.contains(tmp))
							{
							token.append(c);
							flushToken();
							state = State.INITIAL;
							break;
							}
						else if (tmp.equals("--"))
							{
							if (!extended)
								throw new IllegalStateException("Unexpected character '" + c + "'");
							
							state = State.EOL_COMMENT;
							token.clear();
//							flushToken();
							break;
							}
						else if (tmp.equals("/*"))
							{
							if (!extended)
								throw new IllegalStateException("Unexpected character '" + c + "'");
							
							state = State.COMMENT;
							token.clear();
//							flushToken();
							break;
							}
						else if ((c == '-') || (c == '/'))
							{
							flushToken();
							token.append(c);
							break;
							}
						}
					
					if (isNumber(c))
						{
						flushToken();
						token.append(c);
						state = State.NUMBER;
						}
					else if (isNameStart(c))
						{
						flushToken();
						token.append(c);
						state = State.NAME;
						}
					else if (c == ':')
						{
						token.append(c);
						state = State.PARAMETER;
						}
					else if (c == '\'')
						{
						flushToken();
//						token.append(c);
						state = State.STRING;
						}
					else if (c == '"')
						{
						flushToken();
						token.append(c);
						state = State.QUOTED_NAME;
						}
					else if (c == '(')
						{
						flushToken();
						consumer.openBrace();
						state = State.INITIAL;
						}
					else if (c == ')')
						{
						flushToken();
						consumer.closeBrace();
						state = State.NAME;
						}
					else if (Character.isWhitespace(c))
						{
						flushToken();
						space.append(c);
						state = State.INITIAL;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case COMMENT:
					if (c == '*')
						state = State.COMMENT_END;
					else
						token.append(c);
					break;
				
				case COMMENT_END:
					if (c == '/')
						{
						flushToken();
						state = State.INITIAL;
						}
					else if (c == '*')
						token.append('*');
					else
						{
						token.append('*');
						token.append(c);
						state = State.COMMENT;
						}
					break;
				
				case EOL_COMMENT:
					if ((c == '\r') || (c == '\n'))
						{
						flushToken();
						space.append(c);
						state = State.INITIAL;
						}
					else
						token.append(c);
					break;
				
				case QUOTED_STRING_START:
					if (c == '$')
						state = State.QUOTED_STRING;
					else if (isNameStart(c))
						startLabel.append(c);
					else
						throw new IllegalStateException("Malformed quote delimiter '" + startLabel.toString() + "'");
					break;
				
				case QUOTED_STRING:
					if (c == '$')
						{
						endLabel.append(c);
						state = State.QUOTED_STRING_END;
						}
					else
						token.append(c);
					break;
				
				case QUOTED_STRING_END:
					if (c == '$')
						{
						if (endLabel.toString().equals(startLabel.toString()))
							{
							flushToken();
							startLabel.clear();
							endLabel.clear();
							state = State.INITIAL;
							}
						else
							{
							token.append(endLabel.toString());
							endLabel.clear();
							endLabel.append(c);
							}
						}
					else if (isNameStart(c))
						endLabel.append(c);
					else
						{
						endLabel.append(c);
						token.append(endLabel.toString());
						endLabel.clear();
						state = State.QUOTED_STRING;
						}
					break;
				}
			
			if (c == '\n')
				{
				row++;
				column = 0;
				}
			else
				column++;
			}
		
		if ((state == State.STRING) || (state == State.QUOTED_STRING_START) || (state == State.QUOTED_STRING) || (state == State.QUOTED_STRING_END))
			throw new IllegalStateException("Unterminated string literal");
		if (state == State.QUOTED_NAME)
			throw new IllegalStateException("Unterminated name");
		if ((state == State.COMMENT) || (state == State.COMMENT_END))
			throw new IllegalStateException("Unterminated comment");
		
		flushToken();
		
		consumer.finish();
		}
	}
