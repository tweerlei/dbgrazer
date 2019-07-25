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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.AbstractRecursiveQueryType;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.impl.WebDataFormatterImpl;
import de.tweerlei.dbgrazer.web.service.QuerySettingsManager;
import de.tweerlei.dbgrazer.web.session.UserSettings;
import de.tweerlei.dbgrazer.web.session.impl.UserSettingsImpl;
import junit.framework.TestCase;

/**
 * Tests for QuerySettingsManagerImpl
 * 
 * @author Robert Wruck
 */
public class QuerySettingsManagerImplTest extends TestCase
	{
	private static class TestQueryType extends AbstractRecursiveQueryType
		{
		/** The NAME */
		public static final String NAME = "VIEW";
		
		/**
		 * Constructor
		 */
		public TestQueryType()
			{
			super(NAME, null);
			}
		}
	
	/**
	 * Test getEffectiveParameters
	 */
	public void testGetEffectiveParameters()
		{
		final Query q = new QueryImpl("test", new SchemaDef("schema", "1"), "", "", new TestQueryType(), Arrays.<ParameterDef>asList(
				new ParameterDefImpl("p1", ColumnType.STRING, null),
				new ParameterDefImpl("p2", ColumnType.INTEGER, null)
				), Collections.<Integer, TargetDef>emptyMap(), Collections.<String, String>emptyMap());
		
		final UserSettings u = new UserSettingsImpl();
		final QuerySettingsManager qsm = new QuerySettingsManagerImpl(null, null, u, null);
		final Map<Integer, String> model = new HashMap<Integer, String>();
		List<String> params;
		
		// Only one parameter given, no history entries
		model.put(0, "test");
		
		params = qsm.getEffectiveParameters(q, model);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertNull(params.get(1));
		
		
		// Only one parameter given, use history entry for second parameter
		u.getParameterHistory().put("p1", "p1_value");
		u.getParameterHistory().put("p2", "1000");
		params = qsm.getEffectiveParameters(q, model);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertEquals("1000", params.get(1));
		
		
		// Second parameter given but empty: Don't use history entry
		model.put(1, "");
		params = qsm.getEffectiveParameters(q, model);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertEquals("", params.get(1));
		
		
		// Both parameters given: Don't use history entry
		model.put(1, "42");
		// Ignore additional parameter
		model.put(2, "43");
		
		params = qsm.getEffectiveParameters(q, model);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertEquals("42", params.get(1));
		}
	
	/**
	 * Test getAdditionalParameters
	 */
	public void testGetAdditionalParameters()
		{
		final Query q = new QueryImpl("test", new SchemaDef("schema", "1"), "", "", new TestQueryType(), Arrays.<ParameterDef>asList(
				new ParameterDefImpl("p1", ColumnType.STRING, null),
				new ParameterDefImpl("p2", ColumnType.INTEGER, null)
				), Collections.<Integer, TargetDef>emptyMap(), Collections.<String, String>emptyMap());
		
		final UserSettings u = new UserSettingsImpl();
		final QuerySettingsManager qsm = new QuerySettingsManagerImpl(null, null, u, null);
		final Map<Integer, String> model = new HashMap<Integer, String>();
		List<String> params;
		
		// Only one parameter given, no additional params
		model.put(0, "test");
		params = qsm.getAdditionalParameters(q, model);
		
		assertTrue(params.isEmpty());
		
		
		// Second parameter given, no additional params
		model.put(1, "42");
		params = qsm.getAdditionalParameters(q, model);
		
		assertTrue(params.isEmpty());
		
		
		// One additional parameter
		model.put(2, "43");
		
		params = qsm.getAdditionalParameters(q, model);
		
		assertEquals(1, params.size());
		assertEquals("43", params.get(0));
		}
	
	/**
	 * Test translateParameters
	 */
	public void testTranslateParameters()
		{
		final Query q = new QueryImpl("test", new SchemaDef("schema", "1"), "", "", new TestQueryType(), Arrays.<ParameterDef>asList(
				new ParameterDefImpl("p1", ColumnType.STRING, null),
				new ParameterDefImpl("p2", ColumnType.INTEGER, null)
				), Collections.<Integer, TargetDef>emptyMap(), Collections.<String, String>emptyMap());
		
		final UserSettings u = new UserSettingsImpl();
		final QuerySettingsManager qsm = new QuerySettingsManagerImpl(null, null, u, null);
		final DataFormatter fmt = new WebDataFormatterImpl("", "", "", "", "", Locale.GERMAN, TimeZone.getDefault(), 1000, 16);
		final List<String> model = new ArrayList<String>();
		List<Object> params;
		
		// Only one parameter given, no translation
		model.add("test");
		
		params = qsm.translateParameters(q, model, fmt);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertNull(params.get(1));
		
		
		// Translate second parameter to a Number
		model.add("1000");
		params = qsm.translateParameters(q, model, fmt);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertEquals(Long.valueOf(1000), params.get(1));
		
		
		// Translate second parameter to null
		model.set(1, "");
		params = qsm.translateParameters(q, model, fmt);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertNull(params.get(1));
		
		
		// Both parameters given: Don't use history entry
		model.set(1, "42");
		// Ignore additional parameter
		model.add("43");
		
		params = qsm.translateParameters(q, model, fmt);
		
		assertEquals(2, params.size());
		assertEquals("test", params.get(0));
		assertEquals(Long.valueOf(42), params.get(1));
		}
	}
