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
package de.tweerlei.dbgrazer.extension.wiki.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Parser for JSON tokens.
 * 
 * @author Robert Wruck
 */
public class CreoleParser
	{
	private static enum State
		{
		INITIAL,
		TEXT,
		HEADING_LEVEL,
		HEADING,
		UL_LEVEL,
		OL_LEVEL,
		URL,
		LINK_URL,
		LINK_LABEL,
		IMAGE_URL,
		IMAGE_LABEL,
		TABLE,
		TABLE_CELL,
		IMAGE_CODE,
		IMAGE_CODEBLOCK,
		CODE,
		CODEBLOCK,
		CODEBLOCK_INITIAL
		}
	
	private static final Set<String> protocols;
	static
		{
		protocols = new HashSet<String>();
		protocols.add("http");
		protocols.add("https");
		protocols.add("mailto");
		}
	
	private final CreoleConsumer consumer;
	private final StringBuilder text;
	private final StringBuilder label;
	private final StringBuilder space;
	private State state;
	private char lastToken;
	private int lastTokenCount;
	private boolean listMode;
	
	/**
	 * Constructor
	 * @param handler Token handler
	 */
	public CreoleParser(CreoleHandler handler)
		{
		this.consumer = new CreoleConsumer(handler);
		this.text = new StringBuilder();
		this.label = new StringBuilder();
		this.space = new StringBuilder();
		this.state = State.INITIAL;
		this.lastTokenCount = 0;
		this.listMode = false;
		}
	
	private void append(char c)
		{
		text.append(c);
		}
	
	private void appendLabel(char c)
		{
		label.append(c);
		}
	
	private void appendSpace(char c)
		{
		space.append(c);
		}
	
	private void clearSpace()
		{
		space.setLength(0);
		}
	
	private void flushSpace()
		{
		text.append(space);
		clearSpace();
		}
	
	private void clearToken()
		{
		lastTokenCount = 0;
		}
	
	private void ignoreToken()
		{
		for (int i = 0; i < lastTokenCount; i++)
			text.append(lastToken);
		lastTokenCount = 0;
		}
	
	private int addToken(char token)
		{
		lastToken = token;
		lastTokenCount++;
		return (lastTokenCount);
		}
	
	private int checkToken(char token)
		{
		if ((lastTokenCount > 0) && (lastToken != token))
			ignoreToken();
		return (addToken(token));
		}
	
	private void flushToken()
		{
		switch (state)
			{
			case HEADING:
				consumer.heading(lastTokenCount, text.toString().trim());
				break;
			case URL:
				consumer.link(text.toString(), text.toString());
				break;
			case LINK_URL:
			case LINK_LABEL:
				consumer.link(text.toString(), label.toString());
				break;
			case IMAGE_URL:
			case IMAGE_LABEL:
				consumer.image(text.toString(), label.toString());
				break;
			case TEXT:
			case TABLE_CELL:
				consumer.append(text.toString());
				break;
			case UL_LEVEL:
				consumer.startUnorderedListItem(lastTokenCount);
				break;
			case OL_LEVEL:
				consumer.startOrderedListItem(lastTokenCount);
				break;
			case IMAGE_CODE:
				consumer.startCode();
				break;
			case IMAGE_CODEBLOCK:
				consumer.startCodeblock();
				break;
			case CODE:
				consumer.append(text.toString());
				consumer.endCode();
				break;
			case CODEBLOCK_INITIAL:
				consumer.append(text.toString());
				consumer.endCodeblock();
				break;
			default:
				ignoreToken();
				consumer.append(text.toString());
				break;
			}
		text.setLength(0);
		label.setLength(0);
		space.setLength(0);
		clearToken();
		}
	
	private boolean isToken(char c)
		{
		return ((c == '*') || (c == '/') || (c == '=') || (c == '[') || (c == '\\') || (c == '#') || (c == '-') || (c == '{') || (c == '|') || (c == '~'));
		}
	
	/**
	 * Parse a string into tokens
	 * @param sql SQL string
	 * @return this
	 */
	public CreoleParser parse(String sql)
		{
		if (sql == null)
			return (this);
		
		state = State.INITIAL;
		lastTokenCount = 0;
		
		final int n = sql.length();
		
		for (int i = 0; i < n; i++)
			{
			final char c = sql.charAt(i);
			if (c == '\r')
				continue;
			
			switch (state)
				{
				case INITIAL:
					if (c == '-')
						{
						if (checkToken(c) == 4)
							{
							clearSpace();
							consumer.rule();
							clearToken();
							}
						break;
						}
					else if (c == '{')
						{
						if (checkToken(c) == 2)
							{
							state = State.IMAGE_CODEBLOCK;
							}
						break;
						}
					else if (c == '|')
						{
						clearSpace();
						consumer.startTableRow();
						state = State.TABLE;
						break;
						}
					else if (c == '=')
						{
						clearSpace();
						addToken(c);
						state = State.HEADING_LEVEL;
						break;
						}
					else if (c == '*')
						{
						addToken(c);
						state = State.UL_LEVEL;
						break;
						}
					else if (c == '#')
						{
						addToken(c);
						state = State.OL_LEVEL;
						break;
						}
					else if (c == '\n')
						{
						listMode = false;
						clearSpace();
						consumer.newParagraph();
						break;
						}
					else if (c == '~')
						{
						flushSpace();
						ignoreToken();
						if (i + 1 < n)
							{
							final char d = sql.charAt(i + 1);
							if (isToken(d))
								{
								i++;
								append(d);
								}
							else
								append(c);
							}
						else
							append(c);
						state = State.TEXT;
						break;
						}
					else if (Character.isWhitespace(c))
						{
						if (lastTokenCount == 0)
							{
							appendSpace(c);
							break;
							}
						append(c);
						}
					flushSpace();
					ignoreToken();
					state = State.TEXT;
				case TABLE_CELL:
					if (c == '|')
						{
						flushToken();
						state = State.TABLE;
						break;
						}
				case TEXT:
					if (c == '*')
						{
						if (checkToken(c) == 2)
							{
							flushToken();
							consumer.toggleBold();
							}
						}
					else if (c == '/')
						{
						if (checkToken(c) == 2)
							{
							flushToken();
							consumer.toggleItalic();
							}
						}
					else if (c == '[')
						{
						if (checkToken(c) == 2)
							{
							flushToken();
							state = State.LINK_URL;
							}
						}
					else if (c == '{')
						{
						if (checkToken(c) == 2)
							{
							flushToken();
							state = State.IMAGE_CODE;
							}
						}
					else if (c == '\\')
						{
						if (checkToken(c) == 2)
							{
							flushToken();
							consumer.newLine();
							}
						}
					else if (c == '\n')
						{
						ignoreToken();
						flushToken();
						appendSpace(c);
						state = State.INITIAL;
						}
					else if (c == ':')
						{
						final String t = text.toString();
						for (String p : protocols)
							{
							if (t.endsWith(p))
								{
								text.setLength(text.length() - p.length());
								flushToken();
								text.append(p);
								state = State.URL;
								break;
								}
							}
						ignoreToken();
						append(c);
						}
					else if (c == '~')
						{
						ignoreToken();
						if (i + 1 < n)
							{
							final char d = sql.charAt(i + 1);
							if (isToken(d))
								{
								i++;
								append(d);
								}
							else
								append(c);
							}
						else
							append(c);
						break;
						}
					else
						{
						ignoreToken();
						append(c);
						}
					break;
				case HEADING_LEVEL:
					if (c == '=')
						{
						if (addToken(c) == 6)
							state = State.HEADING;
						}
					else
						{
						append(c);
						state = State.HEADING;
						}
					break;
				case HEADING:
					if (c == '=')
						break;
					else if (c == '\n')
						{
						flushToken();
						state = State.INITIAL;
						}
					else
						append(c);
					break;
				case UL_LEVEL:
					if (c == '*')
						{
						final int l = addToken(c);
						if (!listMode && l > 1)
							{
							flushSpace();
							clearToken();
							consumer.toggleBold();
							state = State.TEXT;
							}
						else if (l == 6)
							{
							flushToken();
							state = State.TEXT;
							listMode = true;
							}
						}
					else
						{
						flushToken();
						append(c);
						state = State.TEXT;
						listMode = true;
						}
					break;
				case OL_LEVEL:
					if (c == '#')
						{
						final int l = addToken(c);
						if (!listMode && l > 1)
							{
							flushSpace();
							ignoreToken();
							state = State.TEXT;
							}
						else if (l == 6)
							{
							flushToken();
							state = State.TEXT;
							listMode = true;
							}
						}
					else
						{
						flushToken();
						append(c);
						state = State.TEXT;
						listMode = true;
						}
					break;
				case URL:
					if (c == '\n')
						{
						flushToken();
						state = State.INITIAL;
						}
					else if (Character.isWhitespace(c))
						{
						flushToken();
						append(c);
						state = State.TEXT;
						}
					else
						append(c);
					break;
				case LINK_URL:
					if (c == '|')
						{
						state = State.LINK_LABEL;
						}
					else if (c == ']')
						{
						if ((i + 1 < n) && (sql.charAt(i + 1) == ']'))
							{
							i++;
							flushToken();
							state = State.TEXT;
							}
						else
							append(c);
						}
					else
						append(c);
					break;
				case LINK_LABEL:
					if (c == ']')
						{
						if ((i + 1 < n) && (sql.charAt(i + 1) == ']'))
							i++;
						flushToken();
						state = State.TEXT;
						}
					else
						appendLabel(c);
					break;
				case IMAGE_URL:
					if (c == '|')
						{
						state = State.IMAGE_LABEL;
						}
					else if (c == '}')
						{
						if ((i + 1 < n) && (sql.charAt(i + 1) == '}'))
							{
							i++;
							flushToken();
							state = State.TEXT;
							}
						else
							append(c);
						}
					else
						append(c);
					break;
				case IMAGE_LABEL:
					if (c == '}')
						{
						if ((i + 1 < n) && (sql.charAt(i + 1) == '}'))
							i++;
						flushToken();
						state = State.TEXT;
						}
					else
						appendLabel(c);
					break;
				case TABLE:
					if (c == '=')
						{
						consumer.startTableHeading();
						state = State.TABLE_CELL;
						}
					else
						{
						consumer.startTableCell();
						append(c);
						state = State.TABLE_CELL;
						}
					break;
				case IMAGE_CODE:
					if (c == '{')
						{
						flushToken();
						state = State.CODE;
						}
					else
						{
						clearToken();
						append(c);
						state = State.IMAGE_URL;
						}
					break;
				case IMAGE_CODEBLOCK:
					if (c == '{')
						{
						if (addToken(c) == 4)
							{
							flushSpace();
							clearToken();
							append(c);
							state = State.CODE;
							}
						}
					else if (lastTokenCount == 3)
						{
						if (c == '\n')
							{
							clearSpace();
							flushToken();
							append(c);
							state = State.CODEBLOCK;
							}
						else
							{
							flushSpace();
							clearToken();
							append(c);
							state = State.CODE;
							}
						}
					else
						{
						flushSpace();
						clearToken();
						append(c);
						state = State.IMAGE_URL;
						}
					break;
				case CODE:
					if (c == '}')
						{
						if (addToken(c) > 3)
							append(c);
						}
					else
						{
						if (lastTokenCount >= 3)
							{
							flushToken();
							state = State.TEXT;
							}
						append(c);
						}
					break;
				case CODEBLOCK:
					if (c == '\n')
						{
						append(c);
						state = State.CODEBLOCK_INITIAL;
						}
					else
						append(c);
					break;
				case CODEBLOCK_INITIAL:
					if (c == '}')
						{
						if (addToken(c) == 4)
							{
							ignoreToken();
							state = State.CODEBLOCK;
							}
						}
					else if (lastTokenCount == 3)
						{
						if (c == '\n')
							{
							flushToken();
							state = State.INITIAL;
							}
						else
							{
							ignoreToken();
							append(c);
							state = State.CODEBLOCK;
							}
						}
					else
						{
						append(c);
						state = State.CODEBLOCK;
						}
					break;
				}
			}
		
		flushToken();
		
		consumer.finish();
		
		return (this);
		}
	}
