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
package de.tweerlei.dbgrazer.web.service.bookmark.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.SortedSet;
import java.util.TreeSet;

import de.tweerlei.dbgrazer.web.service.bookmark.BookmarkPersister;
import junit.framework.TestCase;

/**
 * Tests for BookmarkPersisterImpl
 * 
 * @author Robert Wruck
 */
public class BookmarkPersisterImplTest extends TestCase
	{
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadEmpty() throws IOException
		{
		final BookmarkPersister cp = new BookmarkPersisterImpl();
		
		final SortedSet<String> u = cp.readObject(new StringReader(""));
		
		assertTrue(u.isEmpty());
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadWrite() throws IOException
		{
/*		final KeywordService kw = EasyMock.createMock(KeywordService.class);
		EasyMock.expect(kw.normalizeName("test1")).andReturn("test2");
		EasyMock.replay(kw);*/
		
		final BookmarkPersister cp = new BookmarkPersisterImpl();
		
		final SortedSet<String> u = new TreeSet<String>();
		
		final StringWriter sw = new StringWriter();
		cp.writeObject(sw, u);
		assertEquals("", sw.toString());
		
		u.add("q1");
		u.add("q2");
		u.add("q3");
		u.add("q4");
		
		final StringWriter sw3 = new StringWriter();
		cp.writeObject(sw3, u);
		assertEquals("q1\n"
					+"q2\n"
					+"q3\n"
					+"q4\n", sw3.toString());
		
		final SortedSet<String> u2 = cp.readObject(new StringReader(sw3.toString()));
		
		u.remove("test4");
		assertEquals(u, u2);
		}
	}
