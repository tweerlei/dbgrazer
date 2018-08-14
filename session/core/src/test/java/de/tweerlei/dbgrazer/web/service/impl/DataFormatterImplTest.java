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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.impl.WebDataFormatterImpl;

/**
 * Tests for DataFormatterImpl
 * 
 * @author Robert Wruck
 */
public class DataFormatterImplTest extends TestCase
	{
	/**
	 * Test parse
	 */
	public void testParseInteger()
		{
		final DataFormatter fmt = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.GERMAN, TimeZone.getTimeZone("Europe/Berlin"), 0, 0);
		
		assertNull(fmt.parse(ColumnType.INTEGER, null));
		assertNull(fmt.parse(ColumnType.INTEGER, ""));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.INTEGER, "1"));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.INTEGER, " 1\n"));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.INTEGER, " 1\nsome text"));
		assertNull(fmt.parse(ColumnType.INTEGER, " some text\n1"));
		assertEquals(Double.valueOf(1.1), fmt.parse(ColumnType.INTEGER, "1,1"));
		}
	
	/**
	 * Test parse
	 */
	public void testParseFloat()
		{
		final DataFormatter fmt = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.GERMAN, TimeZone.getTimeZone("Europe/Berlin"), 0, 0);
		
		assertNull(fmt.parse(ColumnType.FLOAT, null));
		assertNull(fmt.parse(ColumnType.FLOAT, ""));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.FLOAT, "1"));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.FLOAT, " 1\n"));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.FLOAT, " 1\nsome text"));
		assertNull(fmt.parse(ColumnType.FLOAT, " some text\n1"));
		assertEquals(Double.valueOf(1.1), fmt.parse(ColumnType.FLOAT, "1,1"));
		assertEquals(Long.valueOf(1), fmt.parse(ColumnType.FLOAT, "1.1"));
		}
	
	/**
	 * Test parse
	 */
	public void testParseBoolean()
		{
		final DataFormatter fmt = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.GERMAN, TimeZone.getTimeZone("Europe/Berlin"), 0, 0);
		
		assertNull(fmt.parse(ColumnType.BOOLEAN, null));
		assertEquals(Boolean.FALSE, fmt.parse(ColumnType.BOOLEAN, ""));
		assertEquals(Boolean.TRUE, fmt.parse(ColumnType.BOOLEAN, "1"));
		assertEquals(Boolean.TRUE, fmt.parse(ColumnType.BOOLEAN, "tRuE"));
		assertEquals(Boolean.TRUE, fmt.parse(ColumnType.BOOLEAN, "y"));
		assertEquals(Boolean.TRUE, fmt.parse(ColumnType.BOOLEAN, "Yes"));
		assertEquals(Boolean.FALSE, fmt.parse(ColumnType.BOOLEAN, "yo"));
		assertEquals(Boolean.FALSE, fmt.parse(ColumnType.BOOLEAN, "2"));
		}
	
	/**
	 * Test parse
	 */
	public void testParseString()
		{
		final DataFormatter fmt = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.GERMAN, TimeZone.getTimeZone("Europe/Berlin"), 0, 0);
		
		assertNull(fmt.parse(ColumnType.STRING, null));
		assertEquals("", fmt.parse(ColumnType.STRING, ""));
		assertEquals(" \t", fmt.parse(ColumnType.STRING, " \t"));
		}
	
	/**
	 * Test parse
	 */
	public void testParsePattern()
		{
		final DataFormatter fmt = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.GERMAN, TimeZone.getTimeZone("Europe/Berlin"), 0, 0);
		
		assertNull(fmt.parse(ColumnType.PATTERN, null));
		assertEquals("%%", fmt.parse(ColumnType.PATTERN, ""));
		assertEquals("% \t%", fmt.parse(ColumnType.PATTERN, " \t"));
		}
	
	/**
	 * Test parse
	 */
	public void testParseDate()
		{
		final DataFormatter fmt = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.GERMAN, TimeZone.getTimeZone("Europe/Berlin"), 0, 0);
		
		assertNull(fmt.parse(ColumnType.DATE, null));
		assertNull(fmt.parse(ColumnType.DATE, ""));
		assertEquals(new Date(1400439360000L), fmt.parse(ColumnType.DATE, "2014 Mai 18 20:56:00"));
		assertNull(fmt.parse(ColumnType.DATE, "2014 May 18 20:56:00"));
		
		// Test respection of locale
		final DataFormatter fmt2 = new WebDataFormatterImpl("0", "0", "yyyy MMM dd HH:mm:ss", "0", "0", Locale.US, TimeZone.getTimeZone("Europe/London"), 0, 0);
		assertEquals(new Date(1400439360000L), fmt2.parse(ColumnType.DATE, "2014 May 18 19:56:00"));
		assertNull(fmt2.parse(ColumnType.DATE, "2014 Mai 18 19:56:00"));
		assertEquals(Double.valueOf(1.1), fmt2.parse(ColumnType.FLOAT, "1.1"));
		assertEquals(Long.valueOf(1), fmt2.parse(ColumnType.FLOAT, "1,1"));
		}
	}
