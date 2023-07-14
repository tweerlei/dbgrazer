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

import org.springframework.validation.BindException;

import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.impl.LinkDefImpl;
import junit.framework.TestCase;

/**
 * Tests for JdbcLinkType
 * 
 * @author Robert Wruck
 */
public class JdbcLinkTypeTest extends TestCase
	{
	/**
	 * Test it
	 */
	public void testInvalid()
		{
		final JdbcLinkType cp = new JdbcLinkType();
		
		final LinkDef def5 = new LinkDefImpl(cp, "test", "desc", "", "", "", "", false, "", "", "", "", "", null, "", "", null);
		final BindException errors5 = new BindException(def5, "def5");
		cp.validate(def5, errors5);
		assertTrue(errors5.hasErrors());
		assertEquals(4, errors5.getErrorCount());
		
		final LinkDef def4 = new LinkDefImpl(cp, "test", "desc", "", "", "", "", false, "", "", "", "", "", null, "schema", "", null);
		final BindException errors4 = new BindException(def4, "def4");
		cp.validate(def4, errors4);
		assertTrue(errors4.hasErrors());
		assertEquals(3, errors4.getErrorCount());
		
		final LinkDef def3 = new LinkDefImpl(cp, "test", "desc", "", "", "user", "", false, "", "", "", "", "", null, "schema", "", null);
		final BindException errors3 = new BindException(def3, "def3");
		cp.validate(def3, errors3);
		assertTrue(errors3.hasErrors());
		assertEquals(3, errors3.getErrorCount());
		
		final LinkDef def2 = new LinkDefImpl(cp, "test", "desc", "", "url", "user", "", false, "", "", "", "", "", null, "schema", "", null);
		final BindException errors2 = new BindException(def2, "def2");
		cp.validate(def2, errors2);
		assertTrue(errors2.hasErrors());
		assertEquals(2, errors2.getErrorCount());
		
		final LinkDef def1 = new LinkDefImpl(cp, "test", "desc", "drvr", "url", "user", "", false, "", "", "", "", "", null, "schema", "", null);
		final BindException errors1 = new BindException(def1, "def1");
		cp.validate(def1, errors1);
		assertTrue(errors1.hasErrors());
		assertEquals(1, errors1.getErrorCount());
		
		final LinkDef def0 = new LinkDefImpl(cp, "test", "", "java.lang.Object", "url", "user", "", false, "", "", "", "", "", null, "schema", "", null);
		final BindException errors0 = new BindException(def0, "def0");
		cp.validate(def0, errors0);
		assertFalse(errors0.hasErrors());
		}
	}
