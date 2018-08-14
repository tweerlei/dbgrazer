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
package de.tweerlei.ermtools.tns;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import de.tweerlei.ermtools.tns.TNSNamesParser;
import junit.framework.TestCase;

/**
 * Tests for TNSNamesParser
 * 
 * @author Robert Wruck
 */
public class TNSNamesParserTest extends TestCase
	{
	/**
	 * Test parse empty file
	 * @throws IOException on error
	 */
	public void testParseEmpty() throws IOException
		{
		final StringReader sr = new StringReader("");
		
		final TNSNamesParser p = new TNSNamesParser();
		final Map<String, Object> map = p.parse(sr);
		
		assertTrue(map.isEmpty());
		}
	
	/**
	 * Test parse empty file with comments only
	 * @throws IOException on error
	 */
	public void testParseComments() throws IOException
		{
		final StringReader sr = new StringReader("# first line\r\n\r\n\r\n#another line");
		
		final TNSNamesParser p = new TNSNamesParser();
		final Map<String, Object> map = p.parse(sr);
		
		assertTrue(map.isEmpty());
		}
	
	/**
	 * Test parse single entry with comments
	 * @throws IOException on error
	 */
	public void testParseEntry() throws IOException
		{
		final StringReader sr = new StringReader("# first line\r\n\r\n"
				+"MYORADB = # this comment must be ignored\n"
				+" (DESCRIPTION = \n"
				+"   (ADDRESS = (PROTOCOL = TCP)(HOST = dbhost.example.com)(PORT = 1521)) \n"
				+"   (CONNECT_DATA = \n"
				+"   # this comment must be ignored\n"
				+"     (SERVER = DEDICATED) \n"
				+"     (SERVICE_NAME = MYORADB) \n"
				+"   ) \n"
				+" ) "
				);
		
		final TNSNamesParser p = new TNSNamesParser();
		final Map<String, Object> map = p.parse(sr);
		
		assertEquals(1, map.size());
		
		final Map<String, Object> connectData = new HashMap<String, Object>();
		connectData.put("SERVER", "DEDICATED");
		connectData.put("SERVICE_NAME", "MYORADB");
		
		final Map<String, Object> address = new HashMap<String, Object>();
		address.put("PROTOCOL", "TCP");
		address.put("HOST", "dbhost.example.com");
		address.put("PORT", "1521");
		
		final Map<String, Object> description = new HashMap<String, Object>();
		description.put("ADDRESS", address);
		description.put("CONNECT_DATA", connectData);
		
		final Map<String, Object> expected = new HashMap<String, Object>();
		expected.put("DESCRIPTION", description);
		
		assertEquals(expected, map.get("MYORADB"));
		}
	}
