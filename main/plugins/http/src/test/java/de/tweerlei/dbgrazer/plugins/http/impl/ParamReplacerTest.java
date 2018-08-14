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
package de.tweerlei.dbgrazer.plugins.http.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 34);
		cal.set(Calendar.SECOND, 56);
		cal.set(Calendar.MILLISECOND, 0);
		
		final List<Object> params = Arrays.asList(new Object[] { "a string", 42, cal.getTime(), "another string", null });
		final XMLParamReplacer rep = new XMLParamReplacer(params);
		
		final String stmt = rep.replaceAll("select * from ?1? where a = ?3? and b = ?2? and c = '?1?' and d = '?5?'");
		assertEquals("select * from a string where a = 2016-02-01T12:34:56Z and b = 42 and c = 'a string' and d = ''", stmt);
		}
	}
