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
package de.tweerlei.ermtools.dialect.postgresql;

import de.tweerlei.ermtools.dialect.SQLStatementWrapper;

/**
 * Wrap plpgsql statements with DO
 * 
 * @author Robert Wruck
 */
public class PostgreSQLStatementWrapper implements SQLStatementWrapper
	{
	/** The instance */
	public static final SQLStatementWrapper INSTANCE = new PostgreSQLStatementWrapper();
	
	private PostgreSQLStatementWrapper()
		{
		}
	
	public String wrapStatement(String statement)
		{
		// TODO: Use a unique quote marker like $really_unique$ ?
		
		final int l = statement.length();
		if ((l >= 5) && statement.substring(0, 5).equalsIgnoreCase("BEGIN"))
			return ("DO $$ " + statement + " $$");
		if ((l >= 7) && statement.substring(0, 7).equalsIgnoreCase("DECLARE"))
			return ("DO $$ " + statement + " $$");
		
		return (statement);
		}
	}
