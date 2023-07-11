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
package de.tweerlei.dbgrazer.extension.sql.handler;

import java.util.List;

/**
 * Consume SQL language tokens, counting brace levels
 * 
 * @author Robert Wruck
 */
public interface SQLPrinter
	{
	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public String printName(String t);

	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public String printIdentifier(String t);

	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public String printString(String t);

	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public String printNumber(String t);

	/**
	 * Append a token
	 * @param t Token
	 * @return this
	 */
	public String printOperator(String t);

	/**
	 * Handle a single comment
	 * @param lines Comment lines
	 * @return Comment lines
	 */
	public List<String> printComment(List<String> lines);

	/**
	 * Handle a single comment
	 * @param comment Comment text
	 * @return this
	 */
	public String printEOLComment(String comment);
	}
