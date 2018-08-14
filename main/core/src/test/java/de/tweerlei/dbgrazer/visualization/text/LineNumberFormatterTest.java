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

import de.tweerlei.dbgrazer.text.backend.LineNumberFormatter;
import de.tweerlei.dbgrazer.text.backend.TextFormatter;
import junit.framework.TestCase;

/**
 * Tests for LineNumberFormatter
 *
 * @author Robert Wruck
 */
public class LineNumberFormatterTest extends TestCase
	{
	/**
	 * Test format
	 */
	public void testFormat()
		{
		final TextFormatter fmt = new LineNumberFormatter("%d:");
		
		assertNull(fmt.format(null));
		assertEquals("1:", fmt.format(""));
		assertEquals("1: Some \n2:Text ", fmt.format(" Some \nText "));
		assertEquals("1: Some \n2:Text \n3:", fmt.format(" Some \nText \n"));
		}
	}
