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

import de.tweerlei.dbgrazer.extension.sql.handler.SQLPlusStatementSplitter;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLParser;
import junit.framework.TestCase;

/**
 * Tests for SQLStatementSplitter
 * 
 * @author Robert Wruck
 */
public class SQLStatementSplitterTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testFormatSQL()
		{
		testSQL("SELECT COUNT(*) FROM user.table t WHERE t.col = 2;"
				+" begin dbms_output.put_line('42');\n\nselect 1 into var from dual; end;"
				+"\n/\n"
				+" alter table testtbl rename to newtbl;",
				Arrays.asList(
				"SELECT COUNT(*) FROM user.table t WHERE t.col = 2",
				"begin dbms_output.put_line('42');\n\nselect 1 into var from dual; end;",
				"alter table testtbl rename to newtbl"
				));
		
		testSQL("SELECT COUNT(*) FROM user.table t WHERE t.col = 2;"
				+" create or replace package body testpkg as num constant number := 1; function func1(p1 in varchar2) return number; procedure proc2(p2 in out nocopy testtbl%rowtype); end testpkg;"
				+"\n/\n"
				+" alter table testtbl rename to newtbl;",
				Arrays.asList(
				"SELECT COUNT(*) FROM user.table t WHERE t.col = 2",
				"create or replace package body testpkg as num constant number := 1; function func1(p1 in varchar2) return number; procedure proc2(p2 in out nocopy testtbl%rowtype); end testpkg;",
				"alter table testtbl rename to newtbl"
				));
		
		testSQL("DELETE FROM user.table t WHERE t.col = 2;"
				+" update testtbl set col1 = val2 where not exists (select 1 from dual);"
				+" alter table testtbl rename to newtbl;",
				Arrays.asList(
				"DELETE FROM user.table t WHERE t.col = 2",
				"update testtbl set col1 = val2 where not exists (select 1 from dual)",
				"alter table testtbl rename to newtbl"
				));
		
		testSQL("CREATE OR REPLACE PACKAGE pkg AS\n\tCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\n/**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\nTYPE t3 IS TABLE OF NUMBER;\r\n\r\nEND pkg;\r\n",
				Arrays.asList(
				"CREATE OR REPLACE PACKAGE pkg AS\n\tCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\n/**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\nTYPE t3 IS TABLE OF NUMBER;\r\n\r\nEND pkg;"
				));
		
		testSQL("CREATE OR REPLACE PACKAGE pkg AS\n\tCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/*\n* test Non-JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\nEND pkg;\r\n",
				Arrays.asList(
				"CREATE OR REPLACE PACKAGE pkg AS\n\tCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/*\n* test Non-JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\nEND pkg;"
				));
		}
	
	private void testSQL(String value, List<String> expected)
		{
		final SQLPlusStatementSplitter h = new SQLPlusStatementSplitter();
		new SQLParser(h, true).parse(value);
		final List<String> actual = h.getStatements();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
		}
	
	/**
	 * Test format() in raw mode
	 */
	public void testFormatRaw()
		{
		testRaw("SELECT COUNT(*) FROM user.table t WHERE t.col = 2;"
				+" begin dbms_output.put_line('42');\n\nselect 1 into var from dual; end;"
				+"\n/\n"
				+" alter table testtbl rename to newtbl;",
				Arrays.asList(
				"SELECT COUNT(*) FROM user.table t WHERE t.col = 2",
				"begin dbms_output.put_line('42');\n\nselect 1 into var from dual; end;",
				"alter table testtbl rename to newtbl"
				));
		
		testRaw("SELECT COUNT(*) FROM user.table t WHERE t.col = 2;"
				+" create or replace package body testpkg as num constant number := 1; function func1(p1 in varchar2) return number; procedure proc2(p2 in out nocopy testtbl%rowtype); end testpkg;"
				+"\n/\n"
				+" alter table testtbl rename to newtbl;",
				Arrays.asList(
				"SELECT COUNT(*) FROM user.table t WHERE t.col = 2",
				"create or replace package body testpkg as num constant number := 1; function func1(p1 in varchar2) return number; procedure proc2(p2 in out nocopy testtbl%rowtype); end testpkg;",
				"alter table testtbl rename to newtbl"
				));
		
		testRaw("DELETE FROM user.table t WHERE t.col = 2;"
				+" update testtbl set col1 = val2 where not exists (select 1 from dual);"
				+" alter table testtbl rename to newtbl;",
				Arrays.asList(
				"DELETE FROM user.table t WHERE t.col = 2",
				"update testtbl set col1 = val2 where not exists (select 1 from dual)",
				"alter table testtbl rename to newtbl"
				));
		
		testRaw("CREATE OR REPLACE PACKAGE pkg AS\r\nCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\nEND pkg;\r\n",
				Arrays.asList(
				"CREATE OR REPLACE PACKAGE pkg AS\r\nCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/**\n* test JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\nEND pkg;"
				));
		
		testRaw("CREATE OR REPLACE PACKAGE pkg AS\r\nCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/*\n* test Non-JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\nEND pkg;\r\n",
				Arrays.asList(
				"CREATE OR REPLACE PACKAGE pkg AS\r\nCURSOR cur IS\r\nSELECT a, b, c\r\nFROM tbl\r\nWHERE 0=0\r\n\t-- only 10s\r\nAND a=10;\r\n\r\n/*\n* test Non-JavaDoc style comments\n   * @param p1 param 1 \n */\nPROCEDURE p2;\r\n\r\nEND pkg;"
				));
		}
	
	/**
	 * Test format() with SQL*Plus commands
	 */
	public void testSQLPlus()
		{
		testRaw("BEGIN NULL; END;\n/\n", Arrays.asList(
				"BEGIN NULL; END;"
				));
		
		testRaw("SET SERVEROUTPUT ON;\n\nDROP TABLE tbl;\n", Arrays.asList(
//				"SET SERVEROUTPUT ON;",
				"DROP TABLE tbl"
				));
		
		testRaw("WHENEVER SQLERROR EXIT 1\nSET SERVEROUTPUT ON SIZE UNLIMITED\nSPOOL log.txt\n\nBEGIN\nNULL;\nEND;\n/\n\nSPOOL OFF\n", Arrays.asList(
//				"WHENEVER SQLERROR EXIT 1",
//				"SET SERVEROUTPUT ON SIZE UNLIMITED",
//				"SPOOL log.txt",
				"BEGIN\nNULL;\nEND;"
//				"SPOOL OFF"
				));
		
		testRaw("CREATE PACKAGE testpkg AS\nn1 CONSTANT NUMBER := 1;\nEND;\n/\n", Arrays.asList(
				"CREATE PACKAGE testpkg AS\nn1 CONSTANT NUMBER := 1;\nEND;"
				));
		}
	
	private void testRaw(String value, List<String> expected)
		{
		final SQLPlusStatementSplitter h = new SQLPlusStatementSplitter(false);
		new SQLParser(h, true).parse(value);
		final List<String> actual = h.getStatements();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
		}
	}
