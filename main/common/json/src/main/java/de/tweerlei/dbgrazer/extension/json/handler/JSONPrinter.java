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
package de.tweerlei.dbgrazer.extension.json.handler;

/**
 * Print XML syntax elements
 * 
 * @author Robert Wruck
 */
public interface JSONPrinter
	{
	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public String printKey(String tag);

	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public String printString(String tag);

	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public String printNumber(String tag);

	/**
	 * Handle a single token
	 * @param tag Token
	 * @return this
	 */
	public String printName(String tag);

	/**
	 * Handle a single token
	 * @return this
	 */
	public String printObjectStart();

	/**
	 * Handle a single token
	 * @return this
	 */
	public String printObjectEnd();

	/**
	 * Handle a single token
	 * @return this
	 */
	public String printKeySeparator();

	/**
	 * Handle a single token
	 * @return this
	 */
	public String printValueSeparator();

	/**
	 * Handle a single token
	 * @return this
	 */
	public String printArrayStart();

	/**
	 * Handle a single token
	 * @return this
	 */
	public String printArrayEnd();

	/**
	 * Handle a single token
	 * @param text Text content
	 * @return this
	 */
	public String printComment(String text);
	}
