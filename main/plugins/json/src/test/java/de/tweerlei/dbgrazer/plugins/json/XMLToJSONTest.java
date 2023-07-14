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

import java.util.Collections;

import de.tweerlei.dbgrazer.extension.json.handler.SimpleJSONHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLConsumer;
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class XMLToJSONTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testAppend()
		{
		XMLConsumer c;
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("", c.appendDeclaration("DOCTYPE", "html").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("", c.appendProcessingInstruction("xml", Collections.singletonMap("version", "1.0")).finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("/* plain text */", c.appendComment(" plain text ").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":null}", c.startTag("tag", Collections.<String, String>emptyMap(), true).finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":null}", c.startTag("tag", Collections.<String, String>emptyMap(), false).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"attr\":42}}", c.startTag("tag", Collections.<String, String>singletonMap("attr", "42"), false).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":\"text content\"}", c.startTag("tag", Collections.<String, String>emptyMap(), false).appendText(" text content ").endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"attr\":42,\"text\":\"text content\"}}", c.startTag("tag", Collections.<String, String>singletonMap("attr", "42"), false).appendText(" text content ").endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"nested\":null}}", c.startTag("tag", Collections.<String, String>emptyMap(), false).startTag("nested", Collections.<String, String>emptyMap(), true).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"attr\":42,\"nested\":null}}", c.startTag("tag", Collections.<String, String>singletonMap("attr", "42"), false).startTag("nested", Collections.<String, String>emptyMap(), true).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"attr\":42,\"nested\":{\"nattr\":-0.3e7}}}", c.startTag("tag", Collections.<String, String>singletonMap("attr", "42"), false).startTag("nested", Collections.<String, String>singletonMap("nattr", "-0.3e7"), true).endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"attr\":42,\"text\":\"prefix\",\"nested\":{\"nattr\":43},\"text\":\"suffix\"}}", c.startTag("tag", Collections.<String, String>singletonMap("attr", "42"), false).appendText(" prefix ").startTag("nested", Collections.<String, String>singletonMap("nattr", "43"), true).appendText(" suffix ").endTag("tag").finish().toString());
		
		c = new XMLConsumer(new XMLToJSONHandler(new SimpleJSONHandler()));
		assertEquals("{\"tag\":{\"value1\":42,\"value2\":43,\"nested\":{\"value3\":44},\"value4\":45}}", c.startTag("tag", Collections.<String, String>emptyMap(), false).startTag("value1", Collections.<String, String>emptyMap(), false).appendText(" 42 ").endTag("value1").startTag("value2", Collections.<String, String>emptyMap(), false).appendText(" 43 ").endTag("value2").startTag("nested", Collections.<String, String>emptyMap(), false).startTag("value3", Collections.<String, String>emptyMap(), false).appendText(" 44 ").endTag("value3").endTag("nested").startTag("value4", Collections.<String, String>emptyMap(), false).appendText(" 45 ").endTag("value4").endTag("tag").finish().toString());
		}
	}
