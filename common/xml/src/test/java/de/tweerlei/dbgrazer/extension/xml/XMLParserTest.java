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

import java.util.Arrays;
import java.util.List;

import de.tweerlei.dbgrazer.extension.xml.handler.TokenListXMLHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLParser;
import junit.framework.TestCase;

/**
 * Tests for XMLParser
 * 
 * @author Robert Wruck
 */
public class XMLParserTest extends TestCase
	{
	/**
	 * Test parse()
	 */
	public void testParse()
		{
		assertTrue(parse((String) null).isEmpty());
		assertTrue(parse("").isEmpty());
		
		assertEquals(Arrays.asList(" "), parse(" \t"));
		assertEquals(Arrays.asList("is"), parse("is"));
		assertEquals(Arrays.asList(" some text &amp; some more text "), parse("   some text &amp;  some more text "));
		assertEquals(Arrays.asList(" prefix ", "<tag>", " text ", "</tag>", " suffix "), parse("  prefix\n<tag>\ttext\t</tag>\nsuffix   "));
		assertEquals(Arrays.asList(" prefix ", "<tag>", " outer", "<inner/>", " outer2 ", "</tag>", " suffix "), parse("  prefix\n<tag>\touter<inner/> outer2\t</tag>\nsuffix   "));
		assertEquals(Arrays.asList(" prefix ", "<tag>", " outer", "<inner/>", " outer2 ", "<tag2>", " text2 ", "</tag2>", " ", "</tag>", " suffix "), parse("  prefix\n<tag>\touter<inner/> outer2  <tag2> text2 </tag2>\t</tag>\nsuffix   "));
		assertEquals(Arrays.asList(" some text ", "<![CDATA[  &amp; ]]>", " some more text "), parse("   some text <![CDATA[  &amp; ]]>  some more text "));
		assertEquals(Arrays.asList("prefix ", "<start attr=\"value\">", "</start>", " suffix"), parse("prefix  <start \t attr='value'></start>  suffix"));
		
		expectError("test &amp", "Unterminated tag");
		expectError("test <tag", "Unterminated tag");
		expectError("test <tag> test ", "Unmatched start tag tag");
		expectError("test </tag> test ", "Unmatched end tag");
		}
	
	private List<String> parse(String... xml)
		{
		final TokenListXMLHandler h = new TokenListXMLHandler();
		final XMLParser p = new XMLParser(h, true);
		
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
