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
package de.tweerlei.dbgrazer.query.backend.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.validation.Errors;

import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.model.impl.BaseLinkType;
import de.tweerlei.dbgrazer.query.backend.QueryPersister;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.ResultMapMode;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.AbstractTableQueryType;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ParameterTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;

/**
 * Tests for LinkPersisterImpl
 * 
 * @author Robert Wruck
 */
public class QueryPersisterImplTest extends TestCase
	{
	private static class TestLinkType extends BaseLinkType
		{
		/** The NAME */
		public static final String NAME = "MULTIPLE";
		
		/**
		 * Constructor
		 */
		public TestLinkType()
			{
			super(NAME);
			}
		
		@Override
		public boolean isCustomQuerySupported()
			{
			return (false);
			}
		
		@Override
		public void validate(LinkDef conn, Errors errors)
			{
			}
		}
	
	private static class TestQueryType extends AbstractTableQueryType
		{
		/** The NAME */
		public static final String NAME = "MULTIPLE";
		
		/**
		 * Constructor
		 */
		public TestQueryType()
			{
			super(NAME, new TestLinkType(), ResultMapMode.SINGLE);
			}
		}
	
	/**
	 * Test it
	 */
	public void testInvalid()
		{
		final QueryPersister cp = new QueryPersisterImpl(null, Collections.<QueryType>emptySet());
		
		try	{
			cp.readQuery(new StringReader(""), "test1", new SchemaDef(null, null));
			fail();
			}
		catch (IOException e)
			{
			// expected
			}
		
		try	{
			cp.readQuery(new StringReader("Type: test\n"
					+"Group: *\n"
					+"Parameters: \n"
					+"Links: a,b,c\n"
					+"Views: d,e,f\n"
					+"\n"
					+"SELECT 42\n"
					), "test1", new SchemaDef(null, null));
			fail();
			}
		catch (IOException e)
			{
			// expected
			}
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadQuery() throws IOException
		{
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.extractValues("x:INTEGER, y:FLOAT, z:STRING:v")).andReturn(Arrays.asList("x:INTEGER", "y:FLOAT", "z:STRING:v"));
		EasyMock.expect(kwMock.extractValues("1:a,5:*b,3:c")).andReturn(Arrays.asList("1:a", "5:*b", "3:c"));
		EasyMock.expect(kwMock.extractValues("d, e:42, f")).andReturn(Arrays.asList("d", "e:42", "f"));
		EasyMock.expect(kwMock.extractValues("key1 : value1, key2 : value2")).andReturn(Arrays.asList("key1 : value1", "key2 : value2"));
		EasyMock.expect(kwMock.normalizeName("")).andReturn("");
		EasyMock.replay(kwMock);
		
		final QueryPersister cp = new QueryPersisterImpl(kwMock, Collections.<QueryType>singleton(new TestQueryType()));
		
		final Query def = cp.readQuery(new StringReader("Type: MULTIPLE\n"
				+"Group: *\n"
				+"Parameters: x:INTEGER, y:FLOAT, z:STRING:v \n"
				+"Links: 1:a,5:*b,3:c\n"
				+"Views: d, e:42, f \n"
				+"Attributes: key1 : value1, key2 : value2 \n"
				+"\n"
				+"SELECT 42\n"
				), "test1", new SchemaDef(null, null));
		
		assertEquals("test1", def.getName());
		assertEquals("*", def.getGroupName());
		assertTrue(def.getType() instanceof TestQueryType);
		assertEquals("SELECT 42", def.getStatement());
		
		assertEquals(3, def.getParameters().size());
		assertEquals("x", def.getParameters().get(0).getName());
		assertEquals(ColumnType.INTEGER, def.getParameters().get(0).getType());
		assertNull(def.getParameters().get(0).getValueQuery());
		assertEquals("y", def.getParameters().get(1).getName());
		assertEquals(ColumnType.FLOAT, def.getParameters().get(1).getType());
		assertNull(def.getParameters().get(1).getValueQuery());
		assertEquals("z", def.getParameters().get(2).getName());
		assertEquals(ColumnType.STRING, def.getParameters().get(2).getType());
		assertEquals("v", def.getParameters().get(2).getValueQuery());
		
		assertEquals(0, def.getSubQueries().size());
		
		assertEquals(3, def.getTargetQueries().size());
		assertFalse(def.getTargetQueries().get(1).isParameter());
		assertEquals("a", def.getTargetQueries().get(1).getQueryName());
		assertTrue(def.getTargetQueries().get(5).isParameter());
		assertEquals("b", def.getTargetQueries().get(5).getParameterName());
		assertFalse(def.getTargetQueries().get(3).isParameter());
		assertEquals("c", def.getTargetQueries().get(3).getQueryName());
		
		assertEquals(2, def.getAttributes().size());
		assertEquals("value1", def.getAttributes().get("key1"));
		assertEquals("value2", def.getAttributes().get("key2"));
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadView() throws IOException
		{
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.extractValues("x:INTEGER, y:FLOAT, z:STRING:v")).andReturn(Arrays.asList("x:INTEGER", "y:FLOAT", "z:STRING:v"));
		EasyMock.expect(kwMock.extractValues("1:a,5:b,3:c")).andReturn(Arrays.asList("1:a", "5:b", "3:c"));
		EasyMock.expect(kwMock.extractValues("d, e:42, f")).andReturn(Arrays.asList("d", "e:42", "f"));
		EasyMock.expect(kwMock.extractValues("key1 : value1, key2 : value2")).andReturn(Arrays.asList("key1 : value1", "key2 : value2"));
		EasyMock.expect(kwMock.normalizeName("")).andReturn("");
		EasyMock.replay(kwMock);
		
		final QueryPersister cp = new QueryPersisterImpl(kwMock, Collections.<QueryType>singleton(new ViewQueryType()));
		
		final Query def3 = cp.readQuery(new StringReader("Type: VIEW\n"
				+"Group: *\n"
				+"Parameters: x:INTEGER, y:FLOAT, z:STRING:v \n"
				+"Links: 1:a,5:b,3:c\n"
				+"Views: d, e:42, f \n"
				+"Attributes: key1 : value1, key2 : value2 \n"
				+"\n"
				+"SELECT 42\n"
				), "test1", new SchemaDef(null, null));
		
		assertEquals("test1", def3.getName());
		assertEquals("*", def3.getGroupName());
		assertTrue(def3.getType() instanceof ViewQueryType);
		assertEquals("", def3.getStatement());
		
		assertEquals(3, def3.getParameters().size());
		assertEquals("x", def3.getParameters().get(0).getName());
		assertEquals(ColumnType.INTEGER, def3.getParameters().get(0).getType());
		assertNull(def3.getParameters().get(0).getValueQuery());
		assertEquals("y", def3.getParameters().get(1).getName());
		assertEquals(ColumnType.FLOAT, def3.getParameters().get(1).getType());
		assertNull(def3.getParameters().get(1).getValueQuery());
		assertEquals("z", def3.getParameters().get(2).getName());
		assertEquals(ColumnType.STRING, def3.getParameters().get(2).getType());
		assertEquals("v", def3.getParameters().get(2).getValueQuery());
		
		assertEquals(3, def3.getSubQueries().size());
		assertEquals("d", def3.getSubQueries().get(0).getName());
		assertEquals(0, def3.getSubQueries().get(0).getParameterValues().size());
		assertEquals("e", def3.getSubQueries().get(1).getName());
		assertEquals(1, def3.getSubQueries().get(1).getParameterValues().size());
		assertEquals("42", def3.getSubQueries().get(1).getParameterValues().get(0));
		assertEquals("f", def3.getSubQueries().get(2).getName());
		assertEquals(0, def3.getSubQueries().get(2).getParameterValues().size());
		
		assertEquals(0, def3.getTargetQueries().size());
		
		assertEquals(2, def3.getAttributes().size());
		assertEquals("value1", def3.getAttributes().get("key1"));
		assertEquals("value2", def3.getAttributes().get("key2"));
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testWriteQuery() throws IOException
		{
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.normalizeName("group")).andReturn("grp");
		EasyMock.expect(kwMock.normalizeParam("p1")).andReturn("p_1");
		EasyMock.expect(kwMock.normalizeParam("p2")).andReturn("p_2");
		EasyMock.expect(kwMock.normalizeName("q7")).andReturn("q_7");
		EasyMock.expect(kwMock.normalizeName("q8")).andReturn("q_8");
		EasyMock.expect(kwMock.normalizeName("p9")).andReturn("p_9");
		EasyMock.expect(kwMock.normalizeParam("key1")).andReturn("key_1");
		EasyMock.expect(kwMock.normalizeValue("value1")).andReturn("value_1");
		EasyMock.expect(kwMock.normalizeParam("key2")).andReturn("key_2");
		EasyMock.expect(kwMock.normalizeValue("value2")).andReturn("value_2");
		EasyMock.expect(kwMock.combineValues(Arrays.asList("key_1:value_1", "key_2:value_2"))).andReturn("key_1:value_1, key_2:value_2");
		EasyMock.expect(kwMock.combineValues(Arrays.asList("p_1:INTEGER", "p_2:FLOAT:q_7"))).andReturn("p_1:INTEGER, p_2:FLOAT:q_7");
		EasyMock.expect(kwMock.combineValues(Arrays.asList("2:q_8", "4:*p_9"))).andReturn("2:q_8, 4:*p_9");
		EasyMock.replay(kwMock);
		
		final QueryPersister cp = new QueryPersisterImpl(kwMock, Collections.<QueryType>emptySet());
		
		final Map<Integer, TargetDef> targets = new HashMap<Integer, TargetDef>();
		targets.put(2, new QueryTargetImpl("q8"));
		targets.put(4, new ParameterTargetImpl("p9"));
		final List<ParameterDef> params = new ArrayList<ParameterDef>();
		params.add(new ParameterDefImpl("p1", ColumnType.INTEGER, ""));
		params.add(new ParameterDefImpl("p2", ColumnType.FLOAT, "q7"));
		final Map<String, String> attributes = new LinkedHashMap<String, String>();
		attributes.put("key1", "value1");
		attributes.put("key2", "value2");
		final Query def = new QueryImpl("qn", new SchemaDef(null, null), "group", "SELECT 42\nFROM DUAL", new TestQueryType(), params, targets, attributes);
		
		final StringWriter sw = new StringWriter();
		// Properties with reserved names shall be suppressed
		cp.writeQuery(sw, def);
		
		assertEquals("Type: MULTIPLE\n"
				+"Group: grp\n"
				+"Attributes: key_1:value_1, key_2:value_2\n"
				+"Parameters: p_1:INTEGER, p_2:FLOAT:q_7\n"
				+"Links: 2:q_8, 4:*p_9\n"
				+"\n"
				+"SELECT 42\n"
				+"FROM DUAL\n", sw.toString());
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testWriteView() throws IOException
		{
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.normalizeName("group")).andReturn("grp");
		EasyMock.expect(kwMock.normalizeParam("p1")).andReturn("p_1");
		EasyMock.expect(kwMock.normalizeParam("p2")).andReturn("p_2");
		EasyMock.expect(kwMock.normalizeName("q7")).andReturn("q_7");
		EasyMock.expect(kwMock.normalizeName("q8")).andReturn("q_8");
		EasyMock.expect(kwMock.normalizeName("q9")).andReturn("q_9");
		EasyMock.expect(kwMock.normalizeParam("key1")).andReturn("key_1");
		EasyMock.expect(kwMock.normalizeValue("value1")).andReturn("value_1");
		EasyMock.expect(kwMock.normalizeParam("key2")).andReturn("key_2");
		EasyMock.expect(kwMock.normalizeValue("value2")).andReturn("value_2");
		EasyMock.expect(kwMock.normalizeValue("p9")).andReturn("p_9");
		EasyMock.expect(kwMock.combineValues(Arrays.asList("key_1:value_1", "key_2:value_2"))).andReturn("key_1:value_1, key_2:value_2");
		EasyMock.expect(kwMock.combineValues(Arrays.asList("p_1:INTEGER", "p_2:FLOAT:q_7"))).andReturn("p_1:INTEGER, p_2:FLOAT:q_7");
		EasyMock.expect(kwMock.combineValues(Arrays.asList("q_8", "q_9:p_9"))).andReturn("q_8, q_9:p_9");
		EasyMock.replay(kwMock);
		
		final QueryPersister cp = new QueryPersisterImpl(kwMock, Collections.<QueryType>emptySet());
		
		final List<SubQueryDef> links = new ArrayList<SubQueryDef>();
		links.add(new SubQueryDefImpl("q8", null));
		links.add(new SubQueryDefImpl("q9", Collections.singletonList("p9")));
		final List<ParameterDef> params = new ArrayList<ParameterDef>();
		params.add(new ParameterDefImpl("p1", ColumnType.INTEGER, ""));
		params.add(new ParameterDefImpl("p2", ColumnType.FLOAT, "q7"));
		final Map<String, String> attributes = new LinkedHashMap<String, String>();
		attributes.put("key1", "value1");
		attributes.put("key2", "value2");
		final Query def = new ViewImpl("qn", new SchemaDef(null, null), "group", new ViewQueryType(), params, links, attributes);
		
		final StringWriter sw = new StringWriter();
		// Properties with reserved names shall be suppressed
		cp.writeQuery(sw, def);
		
		assertEquals("Type: VIEW\n"
				+"Group: grp\n"
				+"Attributes: key_1:value_1, key_2:value_2\n"
				+"Parameters: p_1:INTEGER, p_2:FLOAT:q_7\n"
				+"Views: q_8, q_9:p_9\n", sw.toString());
		}
	}
