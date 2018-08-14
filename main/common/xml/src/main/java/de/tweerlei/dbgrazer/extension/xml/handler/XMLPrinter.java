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
package de.tweerlei.dbgrazer.extension.xml.handler;

import java.util.Map;

/**
 * Print XML syntax elements
 * 
 * @author Robert Wruck
 */
public interface XMLPrinter
	{
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param attrs Tag attributes
	 * @return this
	 */
	public String printDeclaration(String tag, String attrs);

	/**
	 * Handle a single token
	 * @param tag Token
	 * @param attrs Tag attributes
	 * @return this
	 */
	public String printProcessingInstruction(String tag, Map<String, String> attrs);

	/**
	 * Handle a single token
	 * @param tagName Tag name
	 * @param attrs Tag attributes
	 * @param empty true for empty tags
	 * @return this
	 */
	public String printStartTag(String tagName, Map<String, String> attrs, boolean empty);

	/**
	 * Handle a single token
	 * @param tagName Tag name
	 * @return this
	 */
	public String printEndTag(String tagName);

	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public String printText(String text);

	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public String printCDATA(String text);

	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public String printComment(String text);
	}
