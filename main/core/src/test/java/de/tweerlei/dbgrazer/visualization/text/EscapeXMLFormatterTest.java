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

import de.tweerlei.dbgrazer.text.backend.EscapeXMLFormatter;
import de.tweerlei.dbgrazer.text.backend.TextFormatter;
import junit.framework.TestCase;

/**
 * Tests for EscapeXMLFormatter
 *
 * @author Robert Wruck
 */
public class EscapeXMLFormatterTest extends TestCase
	{
	/**
	 * Test format
	 */
	public void testFormat()
		{
		final TextFormatter fmt = new EscapeXMLFormatter();
		
		assertNull(fmt.format(null));
		assertEquals("", fmt.format(""));
		assertEquals(" Some \nText ", fmt.format(" Some \nText "));
		assertEquals(" Some \nText \n", fmt.format(" Some \nText \n"));
		
		assertEquals(" Some &lt;tagged&gt; Text ", fmt.format(" Some <tagged> Text "));
		assertEquals(" Some &amp; Text ", fmt.format(" Some & Text "));
		assertEquals(" Some &lt;font color=\"red\" size='-1'&gt; Text ", fmt.format(" Some <font color=\"red\" size='-1'> Text "));
		}
	}
