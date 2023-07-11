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
 * Handle parsed SQL tokens
 * 
 * @author Robert Wruck
 */
public interface SQLHandler
	{
	/**
	 * Handle a single token
	 * @param token Token
	 * @param level Brace level
	 */
	public void handleName(String token, int level);
	
	/**
	 * Handle a single token
	 * @param token Token
	 * @param level Brace level
	 */
	public void handleString(String token, int level);
	
	/**
	 * Handle a single token
	 * @param token Token
	 * @param level Brace level
	 */
	public void handleNumber(String token, int level);
	
	/**
	 * Handle a single token
	 * @param token Token
	 * @param level Brace level
	 */
	public void handleOperator(String token, int level);
	
	/**
	 * Handle a single comment
	 * @param token Token
	 * @param level Brace level
	 */
	public void handleComment(String token, int level);
	
	/**
	 * Handle a single comment
	 * @param token Token
	 * @param level Brace level
	 */
	public void handleEOLComment(String token, int level);
	
	/**
	 * Handle explicit whitespace
	 * @param token Whitespace
	 * @param level Brace level
	 */
	public void handleSpace(String token, int level);
	
	/**
	 * Handle end of input. Brace level is 0 by definition.
	 */
	public void finish();
	}
