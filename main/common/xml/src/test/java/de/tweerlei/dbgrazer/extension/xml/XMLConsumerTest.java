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
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class XMLConsumerTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testAppend()
		{
		XMLConsumer c;
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals(" plain text ", c.appendText(" plain text ").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<![CDATA[ plain text ]]>", c.appendCDATA(" plain text ").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<!-- plain text -->", c.appendComment(" plain text ").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<!DOCTYPE html>", c.appendDeclaration("DOCTYPE", " html").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<?xml version=\"1.0\"?>", c.appendProcessingInstruction("xml", Collections.singletonMap("version", "1.0")).finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<tag attr=\"1\"></tag>", c.startTag("tag", Collections.singletonMap("attr", "1"), false).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<tag attr=\"1\"/>", c.startTag("tag", Collections.singletonMap("attr", "1"), true).finish().toString());
		
		c = new XMLConsumer(new SimpleXMLHandler());
		assertEquals("<?xml version=\"1.0\"?>", c.appendProcessingInstruction("xml", Collections.singletonMap("version", "1.0")).finish().toString());
		
		try	{
			c = new XMLConsumer(new SimpleXMLHandler());
			c.startTag("tag", Collections.singletonMap("attr", "1"), false).finish();
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		
		try	{
			c = new XMLConsumer(new SimpleXMLHandler());
			c.startTag("tag", Collections.singletonMap("attr", "1"), false).endTag("end");
			}
		catch (IllegalStateException e)
			{
			// expected
			}
		}
	}
