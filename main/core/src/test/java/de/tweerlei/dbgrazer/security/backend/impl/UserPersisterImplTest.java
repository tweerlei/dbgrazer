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
package de.tweerlei.dbgrazer.security.backend.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.security.backend.UserPersister;
import de.tweerlei.dbgrazer.security.model.Authority;
import de.tweerlei.dbgrazer.security.model.User;

/**
 * Tests for UserPersisterImpl
 * 
 * @author Robert Wruck
 */
public class UserPersisterImplTest extends TestCase
	{
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadEmpty() throws IOException
		{
		final KeywordService kw = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kw.extractValues(null)).andReturn(Collections.<String>emptyList()).times(2);
		EasyMock.replay(kw);
		
		final UserPersister cp = new UserPersisterImpl(kw);
		
		final User u = cp.readUser(new StringReader(""), "test1");
		
		assertEquals("test1", u.getLogin());
		assertEquals("test1", u.getName());
		assertEquals("", u.getPassword());
		assertTrue(u.getGrantedAuthorities().isEmpty());
		assertTrue(u.getAttributes().isEmpty());
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadWrite() throws IOException
		{
		final KeywordService kw = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kw.normalizeName("test1")).andReturn("test2");
		EasyMock.expect(kw.normalizeParam("prop1")).andReturn("prop2");
		EasyMock.expect(kw.normalizeParam("value 1")).andReturn("value 2");
		EasyMock.expect(kw.extractValues("login , edit")).andReturn(Arrays.asList("login", "edit"));
		EasyMock.expect(kw.extractValues("edit, login")).andReturn(Arrays.asList("login", "edit"));
		EasyMock.expect(kw.extractValues(null)).andReturn(Collections.<String>emptyList()).times(2);
		EasyMock.expect(kw.combineValues(Collections.<String>emptyList())).andReturn("");
		EasyMock.expect(kw.combineValues(new HashSet<String>(Arrays.asList("edit", "login")))).andReturn("edit, login");
		EasyMock.replay(kw);
		
		final UserPersister cp = new UserPersisterImpl(kw);
		
		final User def = cp.readUser(new StringReader("password = pass\n"
					+"authorities = login , edit\n"
					+"prop1 = value 1\n"
					), "test1");
		
		assertEquals("test1", def.getLogin());
		assertEquals("test1", def.getName());
		assertEquals("pass", def.getPassword());
		assertTrue(def.getGrantedAuthorities().contains(Authority.ROLE_LOGIN));
		assertTrue(def.getGrantedAuthorities().contains(Authority.ROLE_EDIT));
		assertFalse(def.getGrantedAuthorities().contains(Authority.ROLE_BROWSE));
		assertFalse(def.getGrantedAuthorities().contains(Authority.ROLE_RELOAD));
		assertEquals(1, def.getAttributes().size());
		assertEquals("value 1", def.getAttributes().get("prop1"));
		
		final StringWriter sw = new StringWriter();
		cp.writeUser(sw, def);
		final User def2 = cp.readUser(new StringReader(sw.toString()), "name2");
		
		assertEquals("name2", def2.getLogin());
		assertEquals("test2", def2.getName());
		assertEquals("pass", def2.getPassword());
		assertTrue(def2.getGrantedAuthorities().contains(Authority.ROLE_LOGIN));
		assertTrue(def2.getGrantedAuthorities().contains(Authority.ROLE_EDIT));
		assertFalse(def2.getGrantedAuthorities().contains(Authority.ROLE_BROWSE));
		assertFalse(def2.getGrantedAuthorities().contains(Authority.ROLE_RELOAD));
		assertEquals(1, def2.getAttributes().size());
		assertEquals("value 2", def2.getAttributes().get("prop2"));
		}
	}
