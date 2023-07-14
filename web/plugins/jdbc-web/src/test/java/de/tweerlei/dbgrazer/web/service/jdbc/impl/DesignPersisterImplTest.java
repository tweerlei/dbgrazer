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
package de.tweerlei.dbgrazer.web.service.jdbc.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.SortedSet;
import java.util.TreeSet;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.web.service.jdbc.DesignPersister;
import junit.framework.TestCase;

/**
 * Tests for DesignPersisterImpl
 * 
 * @author Robert Wruck
 */
public class DesignPersisterImplTest extends TestCase
	{
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadEmpty() throws IOException
		{
		final DesignPersister cp = new DesignPersisterImpl();
		
		final SortedSet<QualifiedName> u = cp.readObject(new StringReader(""));
		
		assertTrue(u.isEmpty());
		}
	
	/**
	 * Test it
	 * @throws IOException on error
	 */
	public void testReadWrite() throws IOException
		{
		final DesignPersister cp = new DesignPersisterImpl();
		
		final SortedSet<QualifiedName> u = new TreeSet<QualifiedName>();
		
		final StringWriter sw = new StringWriter();
		cp.writeObject(sw, u);
		assertEquals("", sw.toString());
		
		u.add(new QualifiedName(null, null, "test1"));
		u.add(new QualifiedName("", null, "test2"));
		u.add(new QualifiedName(null, "", "test3"));
		u.add(new QualifiedName("a", null, "test4"));
		u.add(new QualifiedName(null, "b", "test5"));
		
		final StringWriter sw2 = new StringWriter();
		cp.writeObject(sw2, u);
		assertEquals("::test1\n"
					+"::test3\n"
					+":b:test5\n"
					+"::test2\n"
					+"a::test4\n", sw2.toString());
		
		final SortedSet<QualifiedName> u2 = cp.readObject(new StringReader(sw2.toString()));
		
		final SortedSet<QualifiedName> u3 = new TreeSet<QualifiedName>();
		
		u3.add(new QualifiedName("", "", "test1"));
		u3.add(new QualifiedName("", "", "test2"));
		u3.add(new QualifiedName("", "", "test3"));
		u3.add(new QualifiedName("a", "", "test4"));
		u3.add(new QualifiedName("", "b", "test5"));
		
		assertEquals(u2, u3);
		}
	}
