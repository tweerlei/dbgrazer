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
		EOL_COMMENT
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
	private final StringBuilder token;
	private final StringBuilder space;
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
		this.token = new StringBuilder();
		this.space = new StringBuilder();
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
	
	private void append(char c)
		{
		token.append(c);
		}
	
	private String currentToken()
		{
		return (token.toString());
		}
	
	private void clearToken()
		{
		token.setLength(0);
		}
	
	private void clearSpace()
		{
		space.setLength(0);
		}
	
	private void whitespace(char c)
		{
		space.append(c);
		}
	
	private void flushToken()
		{
		if (space.length() > 0)
			{
			final String s = space.toString();
			consumer.appendSpace(s);
			clearSpace();
			}
		
		if ((token.length() > 0) || (state == State.STRING) || (state == State.COMMENT_END) || (state == State.EOL_COMMENT))
			{
			final String s = token.toString();
			switch (state)
				{
				case NAME:
				case QUOTED_NAME:
					consumer.appendName(s);
					break;
				case STRING:
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
			clearToken();
			}
		}
	
	private boolean isNumber(char c)
		{
		return (Character.isDigit(c) || (c == '.'));
		}

	private boolean isNameStart(char c)
		{
		return (Character.isLetter(c) || (c == '_') || (c == '$') || (c == '#')
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
							append(c);
							state = State.OPERATOR;
							}
						else if ((c == '.') && (i + 1 < n) && (sql.charAt(i + 1) == '.'))
							{
							append(c);
							state = State.OPERATOR;
							}
						else
							{
							append(c);
							state = State.NUMBER;
							}
						}
					else if (isNameStart(c))
						{
						append(c);
						state = State.NAME;
						}
					else if (c == ':')
						{
						append(c);
						state = State.PARAMETER;
						}
					else if (isOperator(c))
						{
						append(c);
						state = State.OPERATOR;
						}
					else if (c == '\'')
						{
//						append(c);
						state = State.STRING;
						}
					else if (c == '"')
						{
						append(c);
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
						whitespace(c);
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case NAME:
					if (isName(c))
						append(c);
					else if (isOperator(c))
						{
						flushToken();
						append(c);
						state = State.OPERATOR;
						}
					else if (c == '\'')
						{
						flushToken();
//						append(c);
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
						whitespace(c);
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
							append(c);
							state = State.OPERATOR;
							}
						else
							append(c);
						}
					else if ((c == 'e') || (c == 'E'))
						{
						append(c);
						state = State.NUMBER_EXP;
						}
					else if (isOperator(c))
						{
						flushToken();
						append(c);
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
					if (c == '\'')
						{
						// detect embedded single quotes
						if ((i + 1 >= n) || (sql.charAt(i + 1) != '\''))
							{
//							append(c);
							flushToken();
							state = State.INITIAL;
							}
						else
							{
							append(c);
							i++;
							}
						}
					else
						append(c);
					break;
				
				case QUOTED_NAME:
					append(c);
					if (c == '"')
						{
						flushToken();
						state = State.INITIAL;
						}
					break;
				
				case PARAMETER:
					if (isNameStart(c))
						{
						append(c);
						state = State.NAME;
						}
					else if (Character.isDigit(c))
						{
						append(c);
						state = State.NAME;
						}
					else if (c == '"')
						{
						append(c);
						state = State.QUOTED_NAME;
						}
					else if ((c == '='))
						{
						// special case to distinguish the assignment operator from a host variable declaration
						state = State.OPERATOR;
						append(c);
						flushToken();
						}
					else if (Character.isWhitespace(c))
						whitespace(c);
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case OPERATOR:
					if (isOperator(c))
						{
						final String tmp = currentToken() + c;
						// check for known compound operators
						if (tmp.equals(".*"))
							{
							flushToken();
							append(c);
							flushToken();
							state = State.INITIAL;
							break;
							}
						else if (operators.contains(tmp))
							{
							append(c);
							flushToken();
							state = State.INITIAL;
							break;
							}
						else if (tmp.equals("--"))
							{
							if (!extended)
								throw new IllegalStateException("Unexpected character '" + c + "'");
							
							state = State.EOL_COMMENT;
							clearToken();
//							flushToken();
							break;
							}
						else if (tmp.equals("/*"))
							{
							if (!extended)
								throw new IllegalStateException("Unexpected character '" + c + "'");
							
							state = State.COMMENT;
							clearToken();
//							flushToken();
							break;
							}
						else if ((c == '-') || (c == '/'))
							{
							flushToken();
							append(c);
							break;
							}
						}
					
					if (isNumber(c))
						{
						flushToken();
						append(c);
						state = State.NUMBER;
						}
					else if (isNameStart(c))
						{
						flushToken();
						append(c);
						state = State.NAME;
						}
					else if (c == ':')
						{
						append(c);
						state = State.PARAMETER;
						}
					else if (c == '\'')
						{
						flushToken();
//						append(c);
						state = State.STRING;
						}
					else if (c == '"')
						{
						flushToken();
						append(c);
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
						whitespace(c);
						state = State.INITIAL;
						}
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
				
				case EOL_COMMENT:
					if ((c == '\r') || (c == '\n'))
						{
						flushToken();
						whitespace(c);
						state = State.INITIAL;
						}
					else
						append(c);
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
		
		if (state == State.STRING)
			throw new IllegalStateException("Unterminated string literal");
		if (state == State.QUOTED_NAME)
			throw new IllegalStateException("Unterminated name");
		if ((state == State.COMMENT) || (state == State.COMMENT_END))
			throw new IllegalStateException("Unterminated comment");
		
		flushToken();
		
		consumer.finish();
		}
	}
