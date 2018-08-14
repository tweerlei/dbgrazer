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

import java.util.Map;

/**
 * Handle parsed XML tokens
 * 
 * @author Robert Wruck
 */
public interface XMLHandler
	{
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param attrs Tag attributes
	 * @param level Brace level
	 */
	public void handleDeclaration(String tag, String attrs, int level);
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param attrs Tag attributes
	 * @param level Brace level
	 */
	public void handleProcessingInstruction(String tag, Map<String, String> attrs, int level);
	
	/**
	 * Handle a single token
	 * @param tagName Tag name
	 * @param attrs Tag attributes
	 * @param empty true for empty tags
	 * @param level Brace level
	 */
	public void startTag(String tagName, Map<String, String> attrs, boolean empty, int level);
	
	/**
	 * Handle a single token
	 * @param tagName Tag name
	 * @param level Brace level
	 */
	public void endTag(String tagName, int level);
	
	/**
	 * Handle a single token
	 * @param text Token
	 * @param level Brace level
	 */
	public void handleText(String text, int level);
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @param level Brace level
	 */
	public void handleCDATA(String text, int level);
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @param level Brace level
	 */
	public void handleComment(String text, int level);
	}
