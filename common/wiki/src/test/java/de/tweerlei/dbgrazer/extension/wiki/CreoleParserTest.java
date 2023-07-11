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
package de.tweerlei.dbgrazer.extension.wiki;

import de.tweerlei.dbgrazer.extension.wiki.handler.SimpleCreoleHandler;
import de.tweerlei.dbgrazer.extension.wiki.parser.CreoleParser;
import junit.framework.TestCase;

/**
 * Tests for XMLParser
 * 
 * @author Robert Wruck
 */
public class CreoleParserTest extends TestCase
	{
	/**
	 * Test parse()
	 */
	public void testParse()
		{
		assertEquals("", parse(null));
		assertEquals("<p></p>\n", parse(""));
		
		assertEquals("<p>Hello, World!</p>\n", parse("Hello, World!"));
		assertEquals("<h1>Headline</h1>\n<p>Hello, World!</p>\n", parse("= Headline =\nHello, World!"));
		assertEquals("<h1>Headline</h1>\n<hr/>\n<p>Hello,<br/>World!</p>\n", parse("= Headline =\n----\nHello,\\\\World!"));
		assertEquals("<p>= Headline =\n----\nHello,\\\\World!</p>\n", parse("~= Headline =\n~----\nHello,~\\\\World!"));
		assertEquals("<p>10 * 20 + 30 / 3 = 210</p>\n", parse("10 * 20 + 30 / 3 = 210"));
		
		assertEquals("<p>Bold and italics should <em>be\n" + 
				"able</em> to cross lines.</p>\n" + 
				"<p>But, should <em>not be...</em></p>\n" + 
				"<p>...able<em> to cross paragraphs.</em></p>\n",
				parse("Bold and italics should //be\n" + 
				"able// to cross lines.\n" + 
				"\n" + 
				"But, should //not be...\n" + 
				"\n" + 
				"...able// to cross paragraphs."));
		
		assertEquals("<p>Go to <a href=\"http://www.example.org/index.html\">http://www.example.org/index.html</a> for more</p>\n", parse("Go to http://www.example.org/index.html for more"));
		
		assertEquals("<p>Go to <a href=\"http://www.example.org/index.html\">My site</a> for more</p>\n", parse("Go to [[http://www.example.org/index.html|My site]] for more"));
		
		assertEquals("<p>Go to <img src=\"http://www.example.org/index.html\" alt=\"My site\"/> for more</p>\n", parse("Go to {{http://www.example.org/index.html|My site}} for more"));
		
		assertEquals("<p>Here are some points:</p>\n" + 
				"<ul>\n" +
				"<li> first</li>\n" +
				"<li> second<ol>\n" +
				"<li> subitem <strong>1</strong></li>\n" +
				"<li> subitem <em>2</em></li>\n" +
				"</ol>\n" +
				"</li>\n" +
				"<li> third</li>\n" +
				"</ul>\n" +
				"<p>That's it.</p>\n",
				parse("Here are some points:\n" + 
				"* first\n" + 
				"* second\n" + 
				"## subitem **1**\n" + 
				"## subitem //2//\n" + 
				"* third\n\n" + 
				"That's it."));
		
		assertEquals("<p><strong>Here</strong> are some points:</p>\n" + 
				"<ul>\n" +
				"<li> first\n" +
				"  second</li>\n" +
				"<li> third</li>\n" +
				"</ul>\n" +
				"<p>##That's it.</p>\n",
				parse("**Here** are some points:\n" + 
				"* first\n" + 
				"  second\n" + 
				"* third\n\n" + 
				"##That's it."));
		
		assertEquals("<p>Here is a table:</p>\n" + 
				"<table>\n" +
				"<tr>\n" +
				"<th>Key</th>\n" +
				"<th>Value</th>\n" +
				"</tr>\n" +
				"<tr>\n" +
				"<td>ID</td>\n" +
				"<td>42</td>\n" +
				"</tr>\n" +
				"<tr>\n" +
				"<td>Name</td>\n" +
				"<td>Arthur <strong>Dent</strong></td>\n" +
				"</tr>\n" +
				"</table>\n" +
				"<p>That's it.</p>\n",
				parse("Here is a table:\n" + 
				"|=Key|=Value\n" + 
				"|ID|42\n" + 
				"|Name|Arthur **Dent**\n" + 
				"\n" + 
				"That's it."));
		
		assertEquals("<p>Use the <code>{source}</code>, luke!</p>\n<pre>\n  {{{\n  10 print hello\n  20 goto 10\n  }}}\n</pre>\n", parse("Use the {{{{source}}}}, luke!\n{{{\n  {{{\n  10 print hello\n  20 goto 10\n  }}}\n}}}"));
		}
	
	private String parse(String xml)
		{
		final SimpleCreoleHandler h = new SimpleCreoleHandler();
		final CreoleParser p = new CreoleParser(h);
		
		p.parse(xml);
		
		return (h.toString());
		}
	}
