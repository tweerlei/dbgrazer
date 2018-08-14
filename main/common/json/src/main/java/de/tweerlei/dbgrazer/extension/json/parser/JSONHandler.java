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


/**
 * Handle parsed XML tokens
 * 
 * @author Robert Wruck
 */
public interface JSONHandler
	{
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void handleKey(String tag, int level);
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void handleString(String tag, int level);
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void handleNumber(String tag, int level);
	
	/**
	 * Handle a single token
	 * @param tag Token
	 * @param level Brace level
	 */
	public void handleName(String tag, int level);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void startObject(int level);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void endObject(int level);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void handleKeySeparator(int level);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void handleValueSeparator(int level);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void startArray(int level);
	
	/**
	 * Handle a single token
	 * @param level Brace level
	 */
	public void endArray(int level);
	
	/**
	 * Handle a single token
	 * @param text Text content
	 * @param level Brace level
	 */
	public void handleComment(String text, int level);
	
	/**
	 * Handle explicit whitespace
	 * @param text Whitespace
	 * @param level Brace level
	 */
	public void handleSpace(String text, int level);
	}
