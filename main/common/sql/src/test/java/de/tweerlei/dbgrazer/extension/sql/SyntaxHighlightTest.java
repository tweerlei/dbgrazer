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
import de.tweerlei.dbgrazer.extension.sql.printer.SyntaxHighlightSQLPrinter;
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class SyntaxHighlightTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testAppend()
		{
		SQLConsumer c;
		
		c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(false)));
		assertEquals("<span class=\"sql-word\">select</span> <span class=\"sql-word\">from</span>",
				c.appendName("select").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(false)));
		assertEquals("<span class=\"sql-word\">select</span>(a)<span class=\"sql-word\">from</span>",
				c.appendName("select").openBrace().appendName("a").closeBrace().appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(false)));
		assertEquals("<span class=\"sql-word\">select</span><span class=\"sql-comment\">/*comment &amp; co*/</span><span class=\"sql-word\">from</span>",
				c.appendName("select").appendComment("comment & co").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(false)));
		assertEquals("<span class=\"sql-word\">select</span><span class=\"sql-comment\">--comment &amp; co</span>\n<span class=\"sql-word\">from</span>",
				c.appendName("select").appendEOLComment("comment & co").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(true)));
		assertEquals("<span class=\"sql-word\">SELECT</span><span class=\"sql-comment\">/*comment &amp; co*/</span><span class=\"sql-word\">FROM</span>",
				c.appendName("select").appendComment("comment & co").appendName("from").finish().toString());
		
		c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(true)));
		assertEquals("<span class=\"sql-word\">SELECT</span><span class=\"sql-comment\">--comment &amp; co</span>\n<span class=\"sql-word\">FROM</span>",
				c.appendName("select").appendEOLComment("comment & co").appendName("from").finish().toString());
		
		try	{
			c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(false)));
			c.appendName("select").openBrace().appendName("a").appendName("from").finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		
		try	{
			c = new SQLConsumer(new SimpleSQLHandler(new SyntaxHighlightSQLPrinter(false)));
			c.appendName("select").openBrace().appendName("a").closeBrace().appendName("from").closeBrace();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		}
	}
