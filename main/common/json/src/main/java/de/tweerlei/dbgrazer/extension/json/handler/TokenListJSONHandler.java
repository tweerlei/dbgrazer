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

import java.util.ArrayList;
import java.util.List;

import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.json.printer.DefaultJSONPrinter;

/**
 * Concatenate XML tokens
 * 
 * @author Robert Wruck
 */
public class TokenListJSONHandler implements JSONHandler
	{
	private final List<String> tokens;
	private final JSONPrinter printer;
	
	/**
	 * Constructor
	 */
	public TokenListJSONHandler()
		{
		this(new DefaultJSONPrinter());
		}
	
	/**
	 * Constructor
	 * @param printer XMLPrinter
	 */
	public TokenListJSONHandler(JSONPrinter printer)
		{
		this.tokens = new ArrayList<String>();
		this.printer = printer;
		}
	
	@Override
	public void handleKey(String tag, int level)
		{
		tokens.add(printer.printKey(tag));
		}
	
	@Override
	public void handleString(String tag, int level)
		{
		tokens.add(printer.printString(tag));
		}
	
	@Override
	public void handleNumber(String tag, int level)
		{
		tokens.add(printer.printNumber(tag));
		}
	
	@Override
	public void handleName(String tag, int level)
		{
		tokens.add(printer.printName(tag));
		}
	
	@Override
	public void startObject(int level)
		{
		tokens.add(printer.printObjectStart());
		}
	
	@Override
	public void endObject(int level)
		{
		tokens.add(printer.printObjectEnd());
		}
	
	@Override
	public void handleKeySeparator(int level)
		{
		tokens.add(printer.printKeySeparator());
		}
	
	@Override
	public void handleValueSeparator(int level)
		{
		tokens.add(printer.printValueSeparator());
		}
	
	@Override
	public void startArray(int level)
		{
		tokens.add(printer.printArrayStart());
		}
	
	@Override
	public void endArray(int level)
		{
		tokens.add(printer.printArrayEnd());
		}
	
	@Override
	public void handleComment(String text, int level)
		{
		tokens.add(printer.printComment(text));
		}
	
	@Override
	public void handleSpace(String text, int level)
		{
		}
	
	/**
	 * Get the tokens
	 * @return Tokens
	 */
	public List<String> getTokens()
		{
		return (tokens);
		}
	}
