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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.easymock.EasyMock;

import de.tweerlei.dbgrazer.common.service.KeywordService;
import junit.framework.TestCase;

/**
 * Tests for ParamReplacer
 * 
 * @author Robert Wruck
 */
public class ParamReplacerTest extends TestCase
	{
	/**
	 * Test it
	 */
	public void testIt()
		{
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 34);
		cal.set(Calendar.SECOND, 56);
		cal.set(Calendar.MILLISECOND, 0);
		
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.normalizeWord("a string")).andReturn("astring").times(2);
		EasyMock.expect(kwMock.normalizeWord("42")).andReturn("42");
		EasyMock.replay(kwMock);
		
		final List<Object> params = Arrays.asList(new Object[] { "a string", 42, cal.getTime(), "another string", null });
		final JdbcParamReplacer rep = new JdbcParamReplacer(params, kwMock);
		
		final String stmt = rep.replaceAll("select * from ?1? where a = ?3? and b = ?2? and c = '?1?' and d = '?5?'");
		assertEquals("select * from astring where a = TIMESTAMP'2016-02-01 12:34:56' and b = 42 and c = 'astring' and d = ''", stmt);
		
		final List<Object> remaining = rep.getRemainingParams();
		assertEquals(1, remaining.size());
		assertEquals("another string", remaining.get(0));
		}
	}
