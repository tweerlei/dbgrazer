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
package de.tweerlei.dbgrazer.plugins.ldap.impl;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Tests for LdapQueryParser
 * 
 * @author Robert Wruck
 */
public class LdapQueryParserTest extends TestCase
	{
	/**
	 * Test parse
	 */
	public void testParse()
		{
		failParse(null);
		parse("", "", "(objectClass=*)", "*");
		parse("SELECT *", "", "(objectClass=*)", "*");
		parse("SELECT *\nFROM o=test", "o=test", "(objectClass=*)", "*");
		parse("SELECT *\r\nFROM o=test\nWHERE (cn=sub)", "o=test", "(cn=sub)", "*");
		parse("  SELECT a , b , c \n FROM o=test \n WHERE (cn=sub) \n ", "o=test", "(cn=sub)", "a", "b", "c");
		parse("SELECT a , b , *\nFROM o=test\nWHERE (cn=sub)", "o=test", "(cn=sub)", "a", "b", "*");
		parse("SELECT a , b , *\nWHERE (cn=sub zero)", "", "(cn=sub zero)", "a", "b", "*");
		parse("SELECT *\nFROM \nWHERE ", "", "", "*");
		failParse("SELECT \nFROM \nWHERE ");
		}
	
	private void parse(String stmt, String base, String filter, String... attrs)
		{
		final LdapQueryParser p = new LdapQueryParser(stmt);
		assertEquals(base, p.getBaseDN());
		assertEquals(filter, p.getFilter());
		if (attrs.length == 0)
			assertNull(p.getAttributes());
		else
			assertEquals(Arrays.asList(attrs), Arrays.asList(p.getAttributes()));
		}
	
	private void failParse(String stmt)
		{
		try	{
			final LdapQueryParser p = new LdapQueryParser(stmt);
			fail(p.getBaseDN());
			}
		catch (RuntimeException e)
			{
			// expected
			}
		}
	}
