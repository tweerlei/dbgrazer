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

import java.util.Arrays;
import java.util.List;

import de.tweerlei.dbgrazer.extension.sql.handler.TokenListSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLParser;
import junit.framework.TestCase;

/**
 * Tests for SQLParser
 * 
 * @author Robert Wruck
 */
public class SQLParserTest extends TestCase
	{
	/**
	 * Test parse()
	 */
	public void testParse()
		{
		assertTrue(parse((String) null).isEmpty());
		assertTrue(parse("").isEmpty());
		assertTrue(parse(" \t").isEmpty());
		
		assertEquals(Arrays.asList("is"), parse("is"));
		assertEquals(Arrays.asList("=", "date", "'2001-01-01'"), parse("=date'2001-01-01'"));
		assertEquals(Arrays.asList("col", "=", "'test'"), parse("col = 'test'"));
		assertEquals(Arrays.asList("col", "=", "'test'"), parse("col='test'"));
		assertEquals(Arrays.asList("col", "=", "''", "+", "col2"), parse("col='' + col2"));
		assertEquals(Arrays.asList("col", "=", "''", "+", "col2"), parse("col=''+col2"));
		assertEquals(Arrays.asList("col", "=", "'te''st'"), parse("col='te''st'"));
		assertEquals(Arrays.asList("col", "=", "'test'"), parse("col='test'"));
		assertEquals(Arrays.asList("in", "(", "17", ",", "'test'", ",", "date", "'2001-01-01'", ",", "col2", ")"), parse("in (17, 'test', date'2001-01-01', col2)"));

		assertEquals(Arrays.asList("a", ".", "b", "=", "2"), parse("a.b = 2"));
		assertEquals(Arrays.asList("a", ".", "b", "=", "2"), parse("a . b = 2"));
		assertEquals(Arrays.asList("a", ".", "b", "=", "2"), parse("a. b = 2"));
		assertEquals(Arrays.asList("a", ".", "b", "=", "2"), parse("a .b = 2"));
		assertEquals(Arrays.asList("a", "(", "i", ")", ".", "b", "=", "2"), parse("a(i).b = 2"));
		assertEquals(Arrays.asList("a", "(", "i", ")", ".", "b", "=", "2"), parse("a(i) . b = 2"));
		assertEquals(Arrays.asList("a", "(", "i", ")", ".", "b", "=", "2"), parse("a(i). b = 2"));
		assertEquals(Arrays.asList("a", "(", "i", ")", ".", "b", "=", "2"), parse("a(i) .b = 2"));

		assertEquals(Arrays.asList("5", "*", "-", "1"), parse("5*-1"));
		
		assertEquals(Arrays.asList("between", "18", "and", "x", "+", "2"), parse("between 18 and x + 2"));
		assertEquals(Arrays.asList("between18andx", "+", "2"), parse("between18andx+2"));
		assertEquals(Arrays.asList("between", "18", "and", "x", "+", "2"), parse("between 18", "and x + 2"));
		
		assertEquals(Arrays.asList("col1", "ASC", ",", "col2", "DESC", ",", "col5", "+", "3"), parse(" col1 ASC, col2 DESC,col5 + 3 "));
		
		assertEquals(Arrays.asList("col1", "=", "7.5e3", "and", "col2", "=", ":var"), parse(" col1 = 7.5e3 and col2 = :var"));
		
		assertEquals(Arrays.asList("select", "\"column 1\"", "from", "\"table 1\""), parse("select \"column 1\" from \"table 1\""));
		assertEquals(Arrays.asList("select", "column", "from", "table", "where", "\"column 2\"", "=", ":var"), parse("select column from table where \"column 2\" = :var"));
		assertEquals(Arrays.asList("select", "column", "from", "table", "where", "column2", "=", ":\"var 1\""), parse("select column from table where column2 = : \"var 1\""));
		
		assertEquals(Arrays.asList("select", "fun1", "(", "a", ",", "b", ",", "c", ":", "(", "select", "3", "from", "dual", ")", ",", "d", ")", "from", "dual"), parse("select fun1(a, b, c: (select 3 from dual), d) from dual"));
		
		assertEquals(Arrays.asList("DO", "' this is verbatim ''text''; with a $1 placeholder '"), parse("DO $$ this is verbatim 'text'; with a $1 placeholder $$"));
		assertEquals(Arrays.asList("DO", "' this is verbatim ''text''; with a $1 placeholder '"), parse("DO $body$ this is verbatim 'text'; with a $1 placeholder $body$"));
		assertEquals(Arrays.asList("DO", "' this is verbatim ''text''; with a $head$ placeholder '"), parse("DO $body$ this is verbatim 'text'; with a $head$ placeholder $body$"));
		
		expectError("='test", "Error at line 1, column 7: Unterminated string literal");
		expectError("= 0; drop table users; --", "Error at line 1, column 4: Unexpected character ';'");
		expectError(") and 1=1", "Error at line 1, column 1: Unmatched closing brace");
		expectError("= 0 --", "Error at line 1, column 6: Unexpected character '-'");
		expectError("= ?", "Error at line 1, column 3: Unexpected character '?'");
		expectError("DO $$ test", "Error at line 1, column 11: Unterminated string literal");
		expectError("DO $body$ test $body $", "Error at line 1, column 23: Unterminated string literal");
		}
	
	/**
	 * Test parse()
	 */
	public void testParseExtended()
		{
		assertEquals(Arrays.asList("=", "?"), parseExt("= ?"));
		assertEquals(Arrays.asList("=", "0", ";", "drop", "table", "users", ";", "--"), parseExt("= 0; drop table users; --"));
		assertEquals(Arrays.asList("=", "0", ";", "drop", "table", "users", ";", "-- eol"), parseExt("= 0; drop table users; -- eol"));
		assertEquals(Arrays.asList("=", "0", ";", "drop", "/* comment */", "table", "users", ";"), parseExt("= 0; drop  /* comment */  table users;"));
		assertEquals(Arrays.asList("=", "0", ";", "drop", "/********", "comment", "********/", "table", "users", ";"), parseExt("= 0; drop\n/********\ncomment\n********/  table users;"));
		
		assertEquals(Arrays.asList("5", "*", "/* comment */", "-", "1"), parseExt("5*/* comment */-1"));
		assertEquals(Arrays.asList("5", ";", "-- comment"), parseExt("5;-- comment"));
		assertEquals(Arrays.asList("=", "0", ";", "drop", "table", "users", ";", "--", "commit", ";"), parseExt("= 0; drop table users;\n--\ncommit;"));
		}
	
	private List<String> parse(String... sql)
		{
		return (parse(false, sql));
		}
	
	private List<String> parseExt(String... sql)
		{
		return (parse(true, sql));
		}
	
	private List<String> parse(boolean b, String... sql)
		{
		final TokenListSQLHandler h = new TokenListSQLHandler();
		final SQLParser p = new SQLParser(h, b);
		
		for (String s : sql)
			p.parse(s);
		
		return (h.getTokens());
		}
	
	private void expectError(String sql, String error)
		{
		try	{
			parse(sql);
			fail();
			}
		catch (IllegalStateException e)
			{
			assertEquals(error, e.getMessage());
			}
		}
	}
