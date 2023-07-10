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
package de.tweerlei.ermtools.dialect.mysql;

import de.tweerlei.ermtools.dialect.SQLDialect;
import junit.framework.TestCase;

/**
 * Tests for MSSQLDialect
 * 
 * @author Robert Wruck
 */
public class MySQLDialectTest extends TestCase
	{
	public void testQuoteIdentifier()
		{
		final SQLDialect d = new MySQLDialect();
		
		assertEquals("", d.quoteIdentifier(""));
		assertEquals("hello", d.quoteIdentifier("hello"));
		assertEquals("HELLO", d.quoteIdentifier("HELLO"));
		assertEquals("Hello", d.quoteIdentifier("Hello"));
		assertEquals("Hello_World2", d.quoteIdentifier("Hello_World2"));
		assertEquals("`Hello, World!`", d.quoteIdentifier("Hello, World!"));
		}
	}
