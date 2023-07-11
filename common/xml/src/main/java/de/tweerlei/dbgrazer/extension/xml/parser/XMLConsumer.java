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

import java.util.LinkedList;
import java.util.Map;

/**
 * Consume XML tokens, counting tag levels
 * 
 * @author Robert Wruck
 */
public class XMLConsumer
	{
	private final XMLHandler handler;
	private final LinkedList<String> tagStack;
	
	/**
	 * Constructor
	 * @param handler Tag handler
	 */
	public XMLConsumer(XMLHandler handler)
		{
		this.handler = handler;
		this.tagStack = new LinkedList<String>();
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param attrs Tag attributes
	 * @return this
	 */
	public XMLConsumer appendDeclaration(String tag, String attrs)
		{
		handler.handleDeclaration(tag, attrs, tagStack.size());
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param attrs Tag attributes
	 * @return this
	 */
	public XMLConsumer appendProcessingInstruction(String tag, Map<String, String> attrs)
		{
		handler.handleProcessingInstruction(tag, attrs, tagStack.size());
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param tagName Tag name
	 * @param attrs Tag attributes
	 * @param empty true for empty tags
	 * @return this
	 */
	public XMLConsumer startTag(String tagName, Map<String, String> attrs, boolean empty)
		{
		handler.startTag(tagName, attrs, empty, tagStack.size());
		if (!empty)
			tagStack.push(tagName);
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param tagName Tag name
	 * @return this
	 */
	public XMLConsumer endTag(String tagName)
		{
		if (tagStack.isEmpty())
			throw new IllegalStateException("Unmatched end tag");
		final String startTagName = tagStack.pop();
		if (!startTagName.equals(tagName))
			throw new IllegalStateException("End tag " + tagName + " for start tag " + startTagName);
		
		handler.endTag(tagName, tagStack.size());
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public XMLConsumer appendText(String text)
		{
		handler.handleText(text, tagStack.size());
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public XMLConsumer appendCDATA(String text)
		{
		handler.handleCDATA(text, tagStack.size());
		return (this);
		}
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public XMLConsumer appendComment(String text)
		{
		handler.handleComment(text, tagStack.size());
		return (this);
		}
	
	/**
	 * Finish processing
	 * @return this
	 */
	public XMLConsumer finish()
		{
		if (!tagStack.isEmpty())
			throw new IllegalStateException("Unmatched start tag " + tagStack.peek());
		return (this);
		}
	
	@Override
	public String toString()
		{
		return (handler.toString());
		}
	}
