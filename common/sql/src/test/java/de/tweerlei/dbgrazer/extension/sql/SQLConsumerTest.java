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

import de.tweerlei.dbgrazer.extension.sql.handler.SimpleSQLHandler;
import de.tweerlei.dbgrazer.extension.sql.parser.SQLConsumer;
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class SQLConsumerTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testAppend()
		{
		SQLConsumer c;
		
		c = new SQLConsumer(new SimpleSQLHandler());
		assertEquals("select from", c.appendName("select").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler());
		assertEquals("select a from", c.appendName("select").appendName("a").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler());
		assertEquals("select(a)from", c.appendName("select").openBrace().appendName("a").closeBrace().appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler());
		assertEquals("select/*comment*/from", c.appendName("select").appendComment("comment").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler());
		assertEquals("select--comment\nfrom", c.appendName("select").appendEOLComment("comment").appendName("from").finish().toString());
		
		try	{
			c = new SQLConsumer(new SimpleSQLHandler());
			c.appendName("select").openBrace().appendName("a").appendName("from").finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		
		try	{
			c = new SQLConsumer(new SimpleSQLHandler());
			c.appendName("select").openBrace().appendName("a").closeBrace().appendName("from").closeBrace();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		}
	}
