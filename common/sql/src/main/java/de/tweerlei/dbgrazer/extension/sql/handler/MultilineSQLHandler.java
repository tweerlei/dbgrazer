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

import java.util.HashSet;
import java.util.Set;

import de.tweerlei.dbgrazer.extension.sql.printer.DefaultSQLPrinter;

/**
 * Concatenate SQL tokens, enforcing spaces where necessary
 * and inserting line breaks before certain keywords
 * 
 * @author Robert Wruck
 */
public class MultilineSQLHandler extends SimpleSQLHandler
	{
	private static final Set<String> NEWLINE_TOKENS;
	static
		{
		NEWLINE_TOKENS = new HashSet<String>();
		NEWLINE_TOKENS.add("WITH");
		NEWLINE_TOKENS.add("SELECT");
		NEWLINE_TOKENS.add("FROM");
		NEWLINE_TOKENS.add("INNER");
		NEWLINE_TOKENS.add("LEFT");
		NEWLINE_TOKENS.add("RIGHT");
		NEWLINE_TOKENS.add("CROSS");
		NEWLINE_TOKENS.add("FULL");
		NEWLINE_TOKENS.add("JOIN");
		NEWLINE_TOKENS.add("WHERE");
		NEWLINE_TOKENS.add("ORDER");
		NEWLINE_TOKENS.add("GROUP");
		NEWLINE_TOKENS.add("HAVING");
		}
	
	/**
	 * Constructor
	 */
	public MultilineSQLHandler()
		{
		this(new DefaultSQLPrinter(), new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param pr SQLPrinter
	 */
	public MultilineSQLHandler(SQLPrinter pr)
		{
		this(pr, new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param a Appendable to receive formatted output
	 */
	public MultilineSQLHandler(Appendable a)
		{
		this(new DefaultSQLPrinter(), a);
		}
	
	/**
	 * Constructor
	 * @param pr SQLPrinter
	 * @param a Appendable to receive formatted output
	 */
	public MultilineSQLHandler(SQLPrinter pr, Appendable a)
		{
		super(pr, a);
		}
	
	@Override
	public void handleName(String token, int level)
		{
		if (NEWLINE_TOKENS.contains(token.toUpperCase()))
			handleSpace("\n", level);
		super.handleName(token, level);
		}
	}
