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
package de.tweerlei.dbgrazer.visualization.text;

import de.tweerlei.dbgrazer.text.backend.impl.HTMLFormatter;
import junit.framework.TestCase;

/**
 * Tests for HTMLFormatter
 * 
 * @author Robert Wruck
 */
public class HTMLFormatterTest extends TestCase
	{
	/**
	 * Test HTML content
	 */
	public void testFormat()
		{
		final HTMLFormatter fmt = new HTMLFormatter();
		
		assertEquals("<html><body></body></html>", fmt.format(null));
		
		assertEquals("<html><body></body></html>", fmt.format(""));
		
		assertEquals("<html><body><p>content</p></body></html>", fmt.format("<p>content</p>"));
		
		assertEquals("<!DOCTYPE>\n<HTML><BODY><p>content</p></BODY></HTML>", fmt.format("<!DOCTYPE>\n<HTML><BODY><p>content</p></BODY></HTML>"));
		assertEquals("<!DOCTYPE>\n<HTML VERSION=\"4.01\"><BODY><p>content</p></BODY></HTML>", fmt.format("<!DOCTYPE>\n<HTML VERSION=\"4.01\"><BODY><p>content</p></BODY></HTML>"));
		}
	}
