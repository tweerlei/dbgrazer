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
package de.tweerlei.dbgrazer.extension.json;

import de.tweerlei.dbgrazer.extension.json.handler.PrettyPrintJSONHandler;
import de.tweerlei.dbgrazer.extension.json.parser.JSONParser;
import junit.framework.TestCase;

/**
 * Tests for XMLFormatter
 * 
 * @author Robert Wruck
 */
public class PrettyPrintJSONHandlerTest extends TestCase
	{
	/**
	 * Test format()
	 */
	public void testFormat()
		{
		assertEquals("", format((String) null));
		assertEquals("", format(""));
		assertEquals("{\n\t\"key\": false,\n\t\"key2\": {\n\t\t\"subkey\": [\n\t\t\t1,\n\t\t\t2,\n\t\t\t3\n\t\t],\n\t\t\"subkey2\": 3.1415\n\t}\n}", format("   { \"key\":false, \"key2\":{ \"subkey\":  [ 1, 2, 3 ], \"subkey2\" : 3.1415 }     } "));
		}
	
	private String format(String value)
		{
		final PrettyPrintJSONHandler h = new PrettyPrintJSONHandler();
		new JSONParser(h).parse(value);
		return (h.toString());
		}
	}
