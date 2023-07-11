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
import de.tweerlei.dbgrazer.extension.wiki.parser.CreoleConsumer;
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class CreoleConsumerTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testAppend()
		{
		CreoleConsumer c;
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<h1>Headline</h1>\n", c.heading(1, "Headline").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<h1>Headline</h1>\n<p>Hello, <strong>World!</strong></p>\n", c.heading(1, "Headline").append("Hello, ").toggleBold().append("World!").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>The <strong>quick <em>brown fox<strong> jumps over<em> the lazy dog.</em></strong></em></strong></p>\n", c.append("The ").toggleBold().append("quick ").toggleItalic().append("brown fox").toggleBold().append(" jumps over").toggleItalic().append(" the lazy dog.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>The <strong>quick </strong>brown fox<em> jumps over</em> the lazy dog.</p>\n", c.append("The ").toggleBold().append("quick ").toggleBold().append("brown fox").toggleItalic().append(" jumps over").toggleItalic().append(" the lazy dog.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>Click <strong><a href=\"http://go.to\">here</a></strong> for more.</p>\n", c.append("Click ").toggleBold().link("http://go.to", "here").toggleBold().append(" for more.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>Click <em><img src=\"http://go.to\" alt=\"here\"/></em> for more.</p>\n", c.append("Click ").toggleItalic().image("http://go.to", "here").toggleItalic().append(" for more.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>The quick brown fox<br/> jumps over</p>\n<p> the lazy dog.</p>\n", c.append("The quick brown fox").newLine().append(" jumps over").newParagraph().append(" the lazy dog.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>The quick brown fox<br/> jumps over</p>\n<hr/>\n<p> the lazy dog.</p>\n", c.append("The quick brown fox").newLine().append(" jumps over").rule().append(" the lazy dog.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>Here are some points:</p>\n" + 
				"<ol>\n" + 
				"<li>first</li>\n" + 
				"<li>second<ul>\n" + 
				"<li>subitem <strong>1</strong></li>\n" + 
				"<li>subitem <em>2</em></li>\n" + 
				"</ul>\n" + 
				"</li>\n" + 
				"<li>third</li>\n" + 
				"</ol>\n" + 
				"<p>That's it.</p>\n",
				c.append("Here are some points:")
				.startOrderedListItem(1).append("first")
				.startOrderedListItem(1).append("second")
				.startUnorderedListItem(2).append("subitem ").toggleBold().append("1")
				.startUnorderedListItem(2).append("subitem ").toggleItalic().append("2")
				.startOrderedListItem(1).append("third")
				.newParagraph().append("That's it.").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>Here's a table:</p>\n" + 
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
				"<hr/>\n" + 
				"<p>Table legend</p>\n",
				c.append("Here's a table:")
				.startTableRow().startTableHeading().append("Key").startTableHeading().append("Value")
				.startTableRow().startTableCell().append("ID").startTableCell().append("42")
				.startTableRow().startTableCell().append("Name").startTableCell().append("Arthur ").toggleBold().append("Dent")
				.rule()
				.append("Table legend").finish().toString());
		
		c = new CreoleConsumer(new SimpleCreoleHandler());
		assertEquals("<p>The <code>quick brown fox</code> jumps over</p>\n<pre> the lazy dog.</pre>\n", c.append("The ").startCode().append("quick brown fox").endCode().append(" jumps over").startCodeblock().append(" the lazy dog.").finish().toString());
		}
	}
