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

import de.tweerlei.dbgrazer.extension.json.handler.SimpleJSONHandler;
import de.tweerlei.dbgrazer.extension.json.parser.JSONConsumer;
import de.tweerlei.dbgrazer.extension.json.printer.SyntaxHighlightJSONPrinter;
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
		JSONConsumer c;
		
		c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
		assertEquals("<span class=\"json-value\">null</span>", c.appendName("null").finish().toString());
		
		c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
		assertEquals("<span class=\"json-value\">\" plain text \"</span>", c.appendString(" plain text ").finish().toString());
		
		c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
		assertEquals("<span class=\"json-value\">42</span>", c.appendNumber("42").finish().toString());
		
		c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
		assertEquals("<span class=\"json-comment\">/* plain text */</span>", c.appendComment(" plain text ").finish().toString());
		
		c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
		assertEquals("{<span class=\"json-key\">\"key\"</span>:<span class=\"json-value\">42</span>}", c.startObject().appendString("key").appendKeySeparator().appendNumber("42").endObject().finish().toString());
		
		c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
		assertEquals("{<span class=\"json-key\">\"key2\"</span>:[<span class=\"json-value\">42</span>,<span class=\"json-value\">43</span>,<span class=\"json-value\">null</span>]}", c.startObject().appendString("key2").appendKeySeparator().startArray().appendNumber("42").appendValueSeparator().appendNumber("43").appendValueSeparator().appendName("null").endArray().endObject().finish().toString());
		
		try	{
			c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
			c.appendValueSeparator().finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		
		try	{
			c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
			c.endObject().finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		
		try	{
			c = new JSONConsumer(new SimpleJSONHandler(new SyntaxHighlightJSONPrinter()));
			c.endArray().finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		}
	}
