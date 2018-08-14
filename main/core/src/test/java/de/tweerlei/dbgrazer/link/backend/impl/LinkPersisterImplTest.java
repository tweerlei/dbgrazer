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
package de.tweerlei.dbgrazer.link.backend.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.springframework.validation.Errors;

import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.link.backend.LinkPersister;
import de.tweerlei.dbgrazer.link.backend.impl.LinkPersisterImpl;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.model.impl.BaseLinkType;
import junit.framework.TestCase;

/**
 * Tests for LinkPersisterImpl
 * 
 * @author Robert Wruck
 */
public class LinkPersisterImplTest extends TestCase
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
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadWrite() throws IOException
		{
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.extractValues(null)).andReturn(Collections.<String>emptyList()).times(2);
		EasyMock.expect(kwMock.combineValues(Collections.<String>emptySet())).andReturn("");
		EasyMock.expect(kwMock.normalizeName("sch")).andReturn("sch");
		EasyMock.expect(kwMock.normalizeName("")).andReturn("").times(4);
		EasyMock.expect(kwMock.normalizeGroup("")).andReturn("");
		EasyMock.replay(kwMock);
		
		final LinkPersister cp = new LinkPersisterImpl(kwMock, Collections.<LinkType>singleton(new TestLinkType()));
		
		final LinkDef def = cp.readLink(new StringReader("driverClass = clazz\n"
					+"jdbcUrl = url\n"
					+"user = usr\n"
					+"password = pass\n"
					+"schema = sch\n"
					+"prop1 = value 1\n"
					), "test1");
		
		assertEquals("test1", def.getName());
		assertEquals("clazz", def.getDriver());
		assertEquals("url", def.getUrl());
		assertEquals("usr", def.getUsername());
		assertEquals("pass", def.getPassword());
		assertEquals(new SchemaDef("sch", null), def.getSchema());
		assertEquals("", def.getGroupName());
		assertEquals("test1", def.getSetName());
		assertEquals("", def.getDescription());
		
		assertEquals(1, def.getProperties().size());
		assertEquals("value 1", def.getProperties().getProperty("prop1"));
		
		final StringWriter sw = new StringWriter();
		// Properties with reserved names shall be suppressed
		def.getProperties().setProperty("driverClass", "bogus");
		cp.writeLink(sw, def);
		final LinkDef def2 = cp.readLink(new StringReader(sw.toString()), "name2");
		
		assertEquals("name2", def2.getName());
		assertEquals("clazz", def2.getDriver());
		assertEquals("url", def2.getUrl());
		assertEquals("usr", def2.getUsername());
		assertEquals("pass", def2.getPassword());
		assertEquals(new SchemaDef("sch", ""), def2.getSchema());
		assertEquals("", def2.getGroupName());
		assertEquals("test1", def2.getSetName());
		assertEquals("", def2.getDescription());
		
		assertEquals(1, def2.getProperties().size());
		assertEquals("value 1", def2.getProperties().getProperty("prop1"));
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadWrite2() throws IOException
		{
		final KeywordService kwMock = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kwMock.extractValues("set 1   ,  set2 ")).andReturn(Arrays.asList("set 1", "set2")).times(2);
		EasyMock.expect(kwMock.extractValues("set1, set 2")).andReturn(Arrays.asList("set1", "set 2"));
		EasyMock.expect(kwMock.combineValues(new HashSet<String>(Arrays.asList("set1", "set 2")))).andReturn("set1, set 2");
		EasyMock.expect(kwMock.normalizeName("set 1")).andReturn("set1");
		EasyMock.expect(kwMock.normalizeName("set2")).andReturn("set 2");
		EasyMock.expect(kwMock.normalizeName("sch")).andReturn("sch");
		EasyMock.expect(kwMock.normalizeName("")).andReturn("").times(4);
		EasyMock.expect(kwMock.normalizeGroup("")).andReturn("");
		EasyMock.replay(kwMock);
		
		final LinkPersister cp = new LinkPersisterImpl(kwMock, Collections.<LinkType>singleton(new TestLinkType()));
		
		final LinkDef def = cp.readLink(new StringReader("driverClass = clazz\n"
					+"jdbcUrl = url\n"
					+"user = usr\n"
					+"password = pass\n"
					+"schema = sch\n"
					+"set = setName\n"
					+"prop1 = value 1\n"
					+"querySets = set 1   ,  set2 \n"
					), "test1");
		
		assertEquals("test1", def.getName());
		assertEquals("clazz", def.getDriver());
		assertEquals("url", def.getUrl());
		assertEquals("usr", def.getUsername());
		assertEquals("pass", def.getPassword());
		assertEquals(new SchemaDef("sch", null), def.getSchema());
		assertEquals("", def.getGroupName());
		assertEquals("setName", def.getSetName());
		assertEquals("", def.getDescription());
		
		assertEquals(1, def.getProperties().size());
		assertEquals("value 1", def.getProperties().getProperty("prop1"));
		
		final StringWriter sw = new StringWriter();
		// Properties with reserved names shall be suppressed
		def.getProperties().setProperty("driverClass", "bogus");
		cp.writeLink(sw, def);
		final LinkDef def2 = cp.readLink(new StringReader(sw.toString()), "name2");
		
		assertEquals("name2", def2.getName());
		assertEquals("clazz", def2.getDriver());
		assertEquals("url", def2.getUrl());
		assertEquals("usr", def2.getUsername());
		assertEquals("pass", def2.getPassword());
		assertEquals(new SchemaDef("sch", ""), def2.getSchema());
		assertEquals("", def2.getGroupName());
		assertEquals("setName", def2.getSetName());
		assertEquals("", def2.getDescription());
		
		assertEquals(1, def2.getProperties().size());
		assertEquals("value 1", def2.getProperties().getProperty("prop1"));
		}
	}
