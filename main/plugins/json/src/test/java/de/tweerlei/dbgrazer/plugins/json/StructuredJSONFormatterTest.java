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
package de.tweerlei.dbgrazer.plugins.json;

import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class StructuredJSONFormatterTest extends TestCase
	{
	/**
	 * Test simple types
	 */
	public void testSimple()
		{
		final String result = new StructuredJSONFormatter().format("3.1415");
		assertEquals("3.1415", result);
		
		final String result2 = new StructuredJSONFormatter().format("\"Hello&Goodbye\"");
		assertEquals("Hello&amp;Goodbye", result2);
		
		final String result3 = new StructuredJSONFormatter().format("true");
		assertEquals("true", result3);
		
		final String result4 = new StructuredJSONFormatter().format("null");
		assertEquals("null", result4);
		}
	
	/**
	 * Test array
	 */
	public void testArray()
		{
		final String result = new StructuredJSONFormatter().format("[3.1415,\"Hello&Goodbye\",true,null]");
		assertEquals("<dl class=\"json-array\"><dt>0</dt><dd>3.1415</dd><dt>1</dt><dd>Hello&amp;Goodbye</dd><dt>2</dt><dd>true</dd><dt>3</dt><dd>null</dd></dl>", result);
		}
	
	/**
	 * Test array
	 */
	public void testObject()
		{
		final String result = new StructuredJSONFormatter().format("{\"p&i\":3.1415,\"greeting\":\"Hello&Goodbye\",\"boolean\":true,\"absent\":null}");
		assertEquals("<dl class=\"json-object\"><dt>p&amp;i</dt><dd>3.1415</dd><dt>greeting</dt><dd>Hello&amp;Goodbye</dd><dt>boolean</dt><dd>true</dd><dt>absent</dt><dd>null</dd></dl>", result);
		}
	
	/**
	 * Test array
	 */
	public void testNestedObject()
		{
		final String result = new StructuredJSONFormatter().format("[{\"p&i\":3.1415,\"greeting\":\"Hello&Goodbye\",\"boolean\":true,\"absent\":null}]");
		assertEquals("<dl class=\"json-array\"><dt>0</dt><dd><dl class=\"json-object\"><dt>p&amp;i</dt><dd>3.1415</dd><dt>greeting</dt><dd>Hello&amp;Goodbye</dd><dt>boolean</dt><dd>true</dd><dt>absent</dt><dd>null</dd></dl></dd></dl>", result);
		}
	
	/**
	 * Test array
	 */
	public void testNestedArray()
		{
		final String result = new StructuredJSONFormatter().format("{\"array\":[3.1415,\"Hello&Goodbye\",true,null]}");
		assertEquals("<dl class=\"json-object\"><dt>array</dt><dd><dl class=\"json-array\"><dt>0</dt><dd>3.1415</dd><dt>1</dt><dd>Hello&amp;Goodbye</dd><dt>2</dt><dd>true</dd><dt>3</dt><dd>null</dd></dl></dd></dl>", result);
		}
	}
