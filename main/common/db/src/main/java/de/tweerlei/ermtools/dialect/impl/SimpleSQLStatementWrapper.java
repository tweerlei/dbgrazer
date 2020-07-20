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
package de.tweerlei.ermtools.dialect.impl;

import de.tweerlei.ermtools.dialect.SQLStatementWrapper;

/**
 * Wrap an SQL statement for execution by appending a fixed String
 * 
 * @author Robert Wruck
 */
public class SimpleSQLStatementWrapper implements SQLStatementWrapper
	{
	/** The common case to terminate with a semicolon */
	public static final SQLStatementWrapper SEMICOLON = new SimpleSQLStatementWrapper(";");
	/** The more pretty case to terminate with a semicolon and line break */
	public static final SQLStatementWrapper SEMICOLON_NL = new SimpleSQLStatementWrapper(";\n");
	
	private final String terminator;
	
	/**
	 * Constructor
	 * @param terminator Terminator to use
	 */
	public SimpleSQLStatementWrapper(String terminator)
		{
		this.terminator = terminator;
		}
	
	public String wrapStatement(String statement)
		{
		return (statement + terminator);
		}
	}
