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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tweerlei.dbgrazer.extension.sql.parser.SQLHandler;
import de.tweerlei.dbgrazer.extension.sql.printer.DefaultSQLPrinter;

/**
 * Count SQL tokens
 * 
 * @author Robert Wruck
 */
public class TokenListSQLHandler implements SQLHandler
	{
	private final SQLPrinter printer;
	private final List<String> tokens;
	
	/**
	 * Constructor
	 */
	public TokenListSQLHandler()
		{
		this(new DefaultSQLPrinter());
		}
	
	/**
	 * Constructor
	 * @param printer SQLPrinter
	 */
	public TokenListSQLHandler(SQLPrinter printer)
		{
		this.printer = printer;
		this.tokens = new ArrayList<String>();
		}
	
	@Override
	public void handleName(String token, int level)
		{
		tokens.add(printer.printName(token));
		}
	
	@Override
	public void handleString(String token, int level)
		{
		tokens.add(printer.printString(token));
		}
	
	@Override
	public void handleNumber(String token, int level)
		{
		tokens.add(printer.printNumber(token));
		}
	
	@Override
	public void handleOperator(String token, int level)
		{
		tokens.add(printer.printOperator(token));
		}
	
	@Override
	public void handleComment(String token, int level)
		{
		tokens.addAll(printer.printComment(Arrays.asList(token.split("\n", -1))));
		}
	
	@Override
	public void handleEOLComment(String token, int level)
		{
		tokens.add(printer.printEOLComment(token));
		}
	
	@Override
	public void handleSpace(String token, int level)
		{
		}
	
	@Override
	public void finish()
		{
		}
	
	/**
	 * Get the tokens
	 * @return the tokens
	 */
	public List<String> getTokens()
		{
		return tokens;
		}
	}
