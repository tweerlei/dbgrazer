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

import de.tweerlei.dbgrazer.extension.xml.handler.PrettyPrintXMLHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLParser;
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class PrettyPrintXMLHandlerTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testFormat()
		{
		assertEquals("", format((String) null));
		assertEquals("", format(""));
		
		assertEquals("<?xml version=\"1.0\"?>\n<!-- comment -->\n<html>\n\t<head>\n\t\t<title>Title</title>\n\t\t<style>Style</style>\n\t</head>\n</html>", format("<?xml version=\"1.0\"?>\n<!-- comment -->\n<html>\n<head>\n\t<title>Title</title><style>Style</style></head>\n</html>"));
		
		assertEquals("<root>\n\t<child1>text</child1>\n\t<child2>text</child2>\n</root>", format("<root>\n\t<child1>text</child1>\n\t<child2>text</child2>\n</root>"));
		
		assertEquals("<root>\n\t<child1>text</child1>\n\t<!-- comment -->\n\t<child2>text</child2>\n</root>", format("<root>\n\t<child1>text</child1>\n\t<!-- comment -->\n\t<child2>text</child2>\n</root>"));
		}
	
	private String format(String value)
		{
		final PrettyPrintXMLHandler h = new PrettyPrintXMLHandler();
		new XMLParser(h).parse(value);
		return (h.toString());
		}
	}
