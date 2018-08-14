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
package de.tweerlei.dbgrazer.extension.json;

import java.util.Arrays;
import java.util.List;

import de.tweerlei.dbgrazer.extension.json.handler.TokenListJSONHandler;
import de.tweerlei.dbgrazer.extension.json.parser.JSONParser;
import junit.framework.TestCase;

/**
 * Tests for XMLParser
 * 
 * @author Robert Wruck
 */
public class JSONParserTest extends TestCase
	{
	/**
	 * Test parse()
	 */
	public void testParse()
		{
		assertTrue(parse((String) null).isEmpty());
		assertTrue(parse("").isEmpty());
		assertTrue(parse(" \t").isEmpty());
		
		assertEquals(Arrays.asList("true"), parse("true"));
		assertEquals(Arrays.asList("false"), parse("false"));
		assertEquals(Arrays.asList("null"), parse("null"));
		expectError("nill", "Unknown name 'nill'");
		expectError("truer", "Unknown name 'truer'");
		expectError("qull", "Unexpected character 'q'");
		
		assertEquals(Arrays.asList("42"), parse("42"));
		assertEquals(Arrays.asList("\"test\""), parse("\"test\""));
		expectError("'test'", "Unexpected character '''");
		assertEquals(Arrays.asList("{", "}"), parse("{}"));
		assertEquals(Arrays.asList("[", "]"), parse("[]"));
		
		expectError(" true false ", "Separator expected");
		expectError(" true, false ", "Unexpected separator");
		expectError(" [ true,, false ", "Value expected");
		
		assertEquals(Arrays.asList("[", "true", ",", "false", ",", "null", ",", "-3.14e+7", ",", "\" a \\\"quoted\\\" text\"", "]"), parse(" [  true, false, null, -3.14e+7, \" a \\\"quoted\\\" text\" ] "));
		assertEquals(Arrays.asList("\"line\\nbreak\""), parse(" \"line\\nbreak\" "));
		assertEquals(Arrays.asList("/* header comment */", "{", "\"key\"", ":", "[", "1", ",", "2", ",", "3", "]", ",", "\"key2\"", ":", "[", "true", ",", "false", "]", ",", "\"key3\"", ":", "{", "\"subkey\"", ":", "\"subvalue\"", "}", "}"),
				parse(" /* header comment */ { \"key\" : [ 1, 2, 3 ], \"key2\": [ true, false ], \"key3\":{\"subkey\":\"subvalue\"}} "));
		
		expectError(" \"text", "Unterminated string literal");
		expectError(" \"text\\", "Unterminated string literal");
		expectError(" \"text\\u", "Unterminated string literal");
		expectError(" \"text\\u002", "Unterminated string literal");
		expectError(" /*text", "Unterminated comment");
		
		expectError(" { \"key\" ", "Unmatched opening brace");
		expectError(" { \"key\" ] ", "Unmatched closing bracket");
		
		parse("{\"EMLogEntryVO\":{\"employeeId\":0,\"dmlTimestamp\":\"2017-03-15 17:12:29\",\"emTableId\":49353,\"emColumnId\":211576,\"primaryKey\":\"20226137,6641\",\"emLogTypeId\":2,\"applExecutionId\":7579338,\"columnData\":{\"entry\":{\"int\":211576,\"null\":\"\"}}}}");
		
		parse("{\"EMLogEntryVO\":{\"employeeId\":230251, \"dmlTimestamp\":\"2017-05-04 17:14:27\", \"emTableId\":2774, \"primaryKey\":\"305529863,14\", \"emLogTypeId\":1, \"applExecutionId\":7706037, \"logSource\":55, \"columnData\":{\"entry\":[]}}}");
		}
	
	private List<String> parse(String... xml)
		{
		final TokenListJSONHandler h = new TokenListJSONHandler();
		final JSONParser p = new JSONParser(h);
		
		for (String s : xml)
			p.parse(s);
		
		return (h.getTokens());
		}
	
	private void expectError(String xml, String error)
		{
		try	{
			parse(xml);
			fail();
			}
		catch (IllegalStateException e)
			{
			assertEquals(error, e.getMessage());
			}
		}
	}
