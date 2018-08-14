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
package de.tweerlei.dbgrazer.extension.xml.parser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parser for XML tokens.
 * Does not interpret attributes but extracts only whole tags.
 * Compresses consecutive whitespace to a single space character (except in attribute values, CDATA sections and comments).
 * 
 * @author Robert Wruck
 */
public class XMLParser
	{
	private static enum State
		{
		INITIAL,
		ENTITY_REF,
		START_TAG,
		DECLARATION,
		DECLARATION_STRING,
		TAG_NAME,
		TAG_CONTENT,
		ATTRIBUTE_NAME,
		ATTRIBUTE,
		ATTRIBUTE_VALUE,
		ATTRIBUTE_ENTITY_REF,
		END_TAG,
		CDATA_SECTION,
		COMMENT
		}
	
	private static enum TagType
		{
		PROCESSING_INSTRUCTION,
		OPEN,
		CLOSE,
		EMPTY
		}
	
	private final XMLConsumer consumer;
	private final StringBuilder token;
	private final Map<String, String> attrs;
	private final Map<String, String> roAttrs;
	private State state;
	private TagType tagType;
	private final StringBuilder tagName;
	private final StringBuilder attrValue;
	private final boolean compress;
	private boolean lws;
	private char attrEnd;
	
	/**
	 * Constructor
	 * @param handler Tag handler
	 */
	public XMLParser(XMLHandler handler)
		{
		this(handler, false);
		}
	
	/**
	 * Constructor
	 * @param handler Tag handler
	 * @param compress Compress white space
	 */
	public XMLParser(XMLHandler handler, boolean compress)
		{
		this.consumer = new XMLConsumer(handler);
		this.token = new StringBuilder();
		this.attrs = new LinkedHashMap<String, String>();
		this.roAttrs = Collections.unmodifiableMap(this.attrs);
		this.state = State.INITIAL;
		this.tagName = new StringBuilder();
		this.attrValue = new StringBuilder();
		this.compress = compress;
		this.lws = false;
		}
	
	private void consume(String tag, String content)
		{
		switch (state)
			{
			case INITIAL:
				consumer.appendText(decode(content));
				break;
			case CDATA_SECTION:
				consumer.appendCDATA(content);
				break;
			case COMMENT:
				consumer.appendComment(content);
				break;
			case DECLARATION:
				consumer.appendDeclaration(tag, content);
				break;
			case TAG_NAME:
			case TAG_CONTENT:
			case END_TAG:
				switch (tagType)
					{
					case PROCESSING_INSTRUCTION:
						consumer.appendProcessingInstruction(tag, roAttrs);
						break;
					case OPEN:
						consumer.startTag(tag, roAttrs, false);
						break;
					case CLOSE:
						consumer.endTag(tag);
						break;
					case EMPTY:
						consumer.startTag(tag, roAttrs, true);
						break;
					}
				break;
			default:
				break;
			}
		}
	
	private String decode(String s)
		{
		return (s.replace("&quot;", "\"").replace("&apos;", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&"));
		}
	
	private void append(char c)
		{
		token.append(c);
		}
	
	private void clearToken()
		{
		token.setLength(0);
		}
	
	private void appendName(char c)
		{
		tagName.append(c);
		}
	
	private void clearName()
		{
		tagName.setLength(0);
		}
	
	private void appendValue(char c)
		{
		attrValue.append(c);
		}
	
	private void clearValue()
		{
		attrValue.setLength(0);
		}
	
	private void clearAttrs()
		{
		attrs.clear();
		}
	
	private void addAttribute()
		{
		attrs.put(token.toString(), decode(attrValue.toString()));
		clearToken();
		clearValue();
		}
	
	private void flushToken()
		{
		if (tagName.length() > 0 || token.length() > 0)
			{
			consume(tagName.toString(), token.toString());
			clearToken();
			clearName();
			clearAttrs();
			}
		}
	
	private void whitespace(char c)
		{
		if (!compress)
			append(c);
		else if (!lws)
			{
			append(' ');
			lws = true;
			}
		}
	
	private boolean peek(String s, int offset, String expected)
		{
		if (s.length() < offset + expected.length())
			return (false);
		
		return (s.substring(offset, offset + expected.length()).equals(expected));
		}
	
	private boolean isNameStart(char c)
		{
		return (Character.isLetterOrDigit(c));
		}

	private boolean isName(char c)
		{
		return (Character.isLetterOrDigit(c) || (c == '_') || (c == '-') || (c == ':'));
		}
	
	private boolean isEntityName(char c)
		{
		return (Character.isLetterOrDigit(c) || (c == '#'));
		}

	/**
	 * Parse a string into tokens
	 * @param xml XML string
	 * @return this
	 */
	public XMLParser parse(String xml)
		{
		if (xml == null)
			return (this);
		
		state = State.INITIAL;
		
		final int n = xml.length();
		
		for (int i = 0; i < n; i++)
			{
			final char c = xml.charAt(i);
			switch (state)
				{
				case INITIAL:
					if (c == '<')
						{
						flushToken();
						tagType = TagType.OPEN;
						clearName();
						lws = false;
						state = State.START_TAG;
						}
					else if (c == '&')
						{
						append(c);
						lws = false;
						state = State.ENTITY_REF;
						}
					else if (Character.isWhitespace(c))
						whitespace(c);
					else
						{
						lws = false;
						append(c);
						}
					break;
				
				case ENTITY_REF:
					if (isEntityName(c))
						append(c);
					else if (c == ';')
						{
						append(c);
						lws = false;
						state = State.INITIAL;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case ATTRIBUTE_ENTITY_REF:
					if (isEntityName(c))
						appendValue(c);
					else if (c == ';')
						{
						appendValue(c);
						state = State.ATTRIBUTE_VALUE;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case START_TAG:
					if (c == '!')
						{
						append(c);
						if (peek(xml, i, "!--"))
							{
							clearToken();
							i += 2;
							state = State.COMMENT;
							}
						else if (peek(xml, i, "![CDATA["))
							{
							clearToken();
							i += 7;
							state = State.CDATA_SECTION;
							}
						else
							{
							clearToken();
							lws = false;
							state = State.DECLARATION;
							}
						}
					else if (c == '?')
						{
						tagType = TagType.PROCESSING_INSTRUCTION;
						state = State.TAG_NAME;
						}
					else if (c == '/')
						{
						tagType = TagType.CLOSE;
						state = State.TAG_NAME;
						}
					else if (isNameStart(c))
						{
						appendName(c);
						state = State.TAG_NAME;
						}
					else if (Character.isWhitespace(c))
						break;
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case DECLARATION:
					if (c == '>')
						{
						flushToken();
						lws = false;
						state = State.INITIAL;
						}
					else if (c == '"')
						{
						append(c);
						state = State.DECLARATION_STRING;
						}
					else if (isName(c))
						{
						lws = false;
						append(c);
						}
					else if (Character.isWhitespace(c))
						whitespace(c);
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case DECLARATION_STRING:
					append(c);
					if (c == '"')
						{
						lws = false;
						state = State.DECLARATION;
						}
					break;
				
				case TAG_NAME:
					if (isName(c))
						appendName(c);
					else if (c == '>')
						{
						flushToken();
						lws = false;
						state = State.INITIAL;
						}
					else if (c == '/')
						{
						if (tagType != TagType.OPEN)
							throw new IllegalStateException("Unexpected character '" + c + "'");
						tagType = TagType.EMPTY;
						state = State.END_TAG;
						}
					else if (c == '?')
						{
						if (tagType != TagType.PROCESSING_INSTRUCTION)
							throw new IllegalStateException("Unexpected character '" + c + "'");
						state = State.END_TAG;
						}
					else if (Character.isWhitespace(c))
						{
						clearToken();
						clearValue();
						state = State.TAG_CONTENT;
						}
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case TAG_CONTENT:
					if (isNameStart(c))
						{
						append(c);
						state = State.ATTRIBUTE_NAME;
						}
					else if (c == '>')
						{
						flushToken();
						lws = false;
						state = State.INITIAL;
						}
					else if (c == '/')
						{
						if (tagType != TagType.OPEN)
							throw new IllegalStateException("Unexpected character '" + c + "'");
						tagType = TagType.EMPTY;
						state = State.END_TAG;
						}
					else if (c == '?')
						{
						if (tagType != TagType.PROCESSING_INSTRUCTION)
							throw new IllegalStateException("Unexpected character '" + c + "'");
						state = State.END_TAG;
						}
					else if (Character.isWhitespace(c))
						break;
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case END_TAG:
					if (c == '>')
						{
						flushToken();
						lws = false;
						state = State.INITIAL;
						}
					else if (Character.isWhitespace(c))
						break;
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case ATTRIBUTE_NAME:
					if (isName(c))
						append(c);
					else if (c == '=')
						state = State.ATTRIBUTE;
					else if (Character.isWhitespace(c))
						break;
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case ATTRIBUTE:
					if ((c == '"') || (c == '\''))
						{
						attrEnd = c;
						state = State.ATTRIBUTE_VALUE;
						}
					else if (Character.isWhitespace(c))
						break;
					else
						throw new IllegalStateException("Unexpected character '" + c + "'");
					break;
				
				case ATTRIBUTE_VALUE:
					if (c == attrEnd)
						{
						addAttribute();
						lws = false;
						state = State.TAG_CONTENT;
						}
//					else if ((c == '<') || (c == '>'))
//						throw new IllegalStateException("Unexpected character '" + c + "'");
					else if (c == '&')
						{
						appendValue(c);
						state = State.ATTRIBUTE_ENTITY_REF;
						}
					else
						appendValue(c);
					break;
				
				case CDATA_SECTION:
					if (c == ']' && peek(xml, i, "]]>"))
						{
						flushToken();
						i += 2;
						lws = false;
						state = State.INITIAL;
						}
					else
						append(c);
					break;
				
				case COMMENT:
					if (c == '-' && peek(xml, i, "-->"))
						{
						flushToken();
						i += 2;
						lws = false;
						state = State.INITIAL;
						}
					else
						append(c);
					break;
				}
			}
		
		if (state != State.INITIAL)
			throw new IllegalStateException("Unterminated tag");
		
		flushToken();
		
		consumer.finish();
		
		return (this);
		}
	}
