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

/**
 * Consume SQL language tokens, counting brace levels
 * 
 * @author Robert Wruck
 */
public class SQLConsumer
	{
	private final SQLHandler handler;
	private int braceLevel;
	
	/**
	 * Constructor
	 * @param handler Token handler
	 */
	public SQLConsumer(SQLHandler handler)
		{
		this.handler = handler;
		this.braceLevel = 0;
		}
	
	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public SQLConsumer appendName(String t)
		{
		handler.handleName(t, braceLevel);
		return (this);
		}
	
	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public SQLConsumer appendString(String t)
		{
		handler.handleString(t, braceLevel);
		return (this);
		}
	
	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public SQLConsumer appendNumber(String t)
		{
		handler.handleNumber(t, braceLevel);
		return (this);
		}
	
	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public SQLConsumer appendOperator(String t)
		{
		handler.handleOperator(t, braceLevel);
		return (this);
		}
	
	/**
	 * Handle a single comment
	 * @param comment Comment text
	 * @return this
	 */
	public SQLConsumer appendComment(String comment)
		{
		handler.handleComment(comment, braceLevel);
		return (this);
		}
	
	/**
	 * Handle a single comment
	 * @param comment Comment text
	 * @return this
	 */
	public SQLConsumer appendEOLComment(String comment)
		{
		handler.handleEOLComment(comment, braceLevel);
		return (this);
		}
	
	/**
	 * Handle explicit whitespace
	 * @param text Whitespace
	 * @return this
	 */
	public SQLConsumer appendSpace(String text)
		{
		handler.handleSpace(text, braceLevel);
		return (this);
		}
	
	/**
	 * Append an opening brace
	 * @return this
	 */
	public SQLConsumer openBrace()
		{
		braceLevel++;
		return (appendOperator("("));
		}
	
	/**
	 * Append a closing brace
	 * @return this
	 */
	public SQLConsumer closeBrace()
		{
		braceLevel--;
		
		if (braceLevel < 0)
			throw new IllegalStateException("Unmatched closing brace");
		
		return (appendOperator(")"));
		}
	
	/**
	 * Finish processing
	 * @return this
	 */
	public SQLConsumer finish()
		{
		if (braceLevel > 0)
			throw new IllegalStateException("Unmatched opening brace");
		handler.finish();
		return (this);
		}
	
	@Override
	public String toString()
		{
		return (handler.toString());
		}
	}
