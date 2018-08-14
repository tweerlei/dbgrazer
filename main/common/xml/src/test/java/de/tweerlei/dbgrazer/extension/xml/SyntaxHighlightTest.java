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
package de.tweerlei.dbgrazer.extension.xml;

import java.util.Collections;

import de.tweerlei.dbgrazer.extension.xml.handler.SimpleXMLHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLConsumer;
import de.tweerlei.dbgrazer.extension.xml.printer.SyntaxHighlightXMLPrinter;
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
		XMLConsumer c;
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals(" plain text ", c.appendText(" plain text ").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("&lt;![CDATA[ plain text ]]&gt;", c.appendCDATA(" plain text ").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("<span class=\"xml-comment\">&lt;!-- plain text --&gt;</span>", c.appendComment(" plain text ").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("&lt;!<span class=\"xml-tag\">DOCTYPE</span> html&gt;", c.appendDeclaration("DOCTYPE", " html").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("&lt;?<span class=\"xml-tag\">xml</span> <span class=\"xml-attr\">version</span>=<span class=\"xml-value\">\"1.0\"</span>?&gt;", c.appendProcessingInstruction("xml", Collections.singletonMap("version", "1.0")).finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("&lt;<span class=\"xml-tag\">tag</span> <span class=\"xml-attr\">attr</span>=<span class=\"xml-value\">\"1\"</span>&gt;&lt;/<span class=\"xml-tag\">tag</span>&gt;", c.startTag("tag", Collections.singletonMap("attr", "1"), false).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("&lt;<span class=\"xml-tag\">tag</span> <span class=\"xml-attr\">attr</span>=<span class=\"xml-value\">\"1\"</span>/&gt;", c.startTag("tag", Collections.singletonMap("attr", "1"), true).finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
		assertEquals("&lt;?<span class=\"xml-tag\">xml</span> <span class=\"xml-attr\">version</span>=<span class=\"xml-value\">\"1.0\"</span>?&gt;", c.appendProcessingInstruction("xml", Collections.singletonMap("version", "1.0")).finish().toString());
		
		try	{
			c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
			c.startTag("tag", Collections.singletonMap("attr", "1"), false).finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		
		try	{
			c = new XMLConsumer(new SimpleXMLHandler(new SyntaxHighlightXMLPrinter()));
			c.startTag("tag", Collections.singletonMap("attr", "1"), false).endTag("end");
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		}
	}
