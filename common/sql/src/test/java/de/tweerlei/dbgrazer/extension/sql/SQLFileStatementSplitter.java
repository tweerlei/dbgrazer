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
package de.tweerlei.dbgrazer.extension.sql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import de.tweerlei.dbgrazer.extension.sql.handler.SQLPlusStatementSplitter;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLParser;

/**
 * Tests for SQLStatementSplitter
 * 
 * @author Robert Wruck
 */
public class SQLFileStatementSplitter
	{
	/**
	 * Entry point
	 * @param args Files to process
	 * @throws Exception on error
	 */
	public static void main(String[] args) throws Exception
		{
		for (String s : args)
			{
			for (String stmt : formatSQL(readFile(s)))
				{
				System.out.println(stmt);
				System.out.println("-------- cut here --------");
				}
			}
		}
	
	private static String readFile(String fileName) throws IOException
		{
		final StringBuffer sb = new StringBuffer();
		
		final InputStreamReader r = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
		try	{
			final char[] buffer = new char[65536];
			
			for (;;)
				{
				final int i = r.read(buffer);
				if (i < 0)
					break;
				sb.append(buffer, 0, i);
				}
			}
		finally
			{
			r.close();
			}
		
		return (sb.toString());
		}
	
	private static List<String> formatSQL(String value)
		{
		final SQLPlusStatementSplitter h = new SQLPlusStatementSplitter();
		new SQLParser(h, true).parse(value);
		return (h.getStatements());
		}
	}
