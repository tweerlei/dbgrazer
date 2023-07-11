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


/**
 * Handle parsed XML tokens
 * 
 * @author Robert Wruck
 */
public interface CreoleHandler
	{
	public void text(String text);
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void startBold();
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void endBold();
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void startItalic();
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void endItalic();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void heading(int level, String text);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void link(String url, String label);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void startParagraph();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void endParagraph();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void newLine();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void startUnorderedList();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void endUnorderedList();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void startOrderedList();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void endOrderedList();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void startListItem();
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void endListItem();
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @param level Brace level
	 */
	public void rule();
	
	/**
	 * Handle explicit whitespace
	 * @param text Whitespace
	 * @param level Brace level
	 */
	public void image(String url, String label);
	
	public void startTable();
	
	public void endTable();
	
	public void startTableRow();
	
	public void endTableRow();
	
	public void startTableHeading();
	
	public void endTableHeading();
	
	public void startTableCell();
	
	public void endTableCell();
	
	public void startCode();
	
	public void endCode();
	
	public void startCodeBlock();
	
	public void endCodeBlock();
	}
