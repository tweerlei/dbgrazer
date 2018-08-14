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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService;
import de.tweerlei.dbgrazer.extension.jdbc.SQLGeneratorService.Style;
import de.tweerlei.dbgrazer.extension.jdbc.impl.SQLGeneratorServiceImpl;
import de.tweerlei.ermtools.dialect.impl.GenericDialect;
import junit.framework.TestCase;

/**
 * Tests for SQLGeneratorServiceImpl
 * 
 * @author Robert Wruck
 */
public class SQLGeneratorServiceImplTest extends TestCase
	{
	/**
	 * Test formatColumnName
	 */
	public void testFormatColumnName()
		{
		final SQLGeneratorService svc = new SQLGeneratorServiceImpl();
		
		assertEquals("", svc.formatColumnName(null));
		assertEquals("", svc.formatColumnName(""));
		assertEquals("Abc", svc.formatColumnName("abc"));
		assertEquals("Abc", svc.formatColumnName("ABC"));
		assertEquals("Abc_Def", svc.formatColumnName("ABC_def"));
		assertEquals("Abc2Def", svc.formatColumnName("ABC2def"));
		assertEquals("1Abc.Def", svc.formatColumnName("1ABC.def"));
		}
	
	/**
	 * Test generateSelectCount
	 */
	public void testGenerateSelectCount()
		{
		final SQLGeneratorService svc = new SQLGeneratorServiceImpl();
		
		assertEquals("SELECT COUNT(*)FROM schema.table", svc.generateSelectCount(new QualifiedName("", "schema", "table"), Style.SIMPLE, null, new GenericDialect()));
		
		assertEquals("SELECT COUNT(*)FROM schema.table", svc.generateSelectCount(new QualifiedName("", "schema", "table"), Style.SIMPLE, "", new GenericDialect()));
		
		assertEquals("SELECT COUNT(*)FROM schema.table WHERE col = 42", svc.generateSelectCount(new QualifiedName("", "schema", "table"), Style.SIMPLE, " col=42", new GenericDialect()));
		}
	
	/**
	 * Test generateDelete
	 */
	public void testGenerateDelete()
		{
		final SQLGeneratorService svc = new SQLGeneratorServiceImpl();
		
		assertEquals("DELETE FROM schema.table", svc.generateDelete(new QualifiedName("", "schema", "table"), Style.SIMPLE, null, new GenericDialect()));
		
		assertEquals("DELETE FROM schema.table", svc.generateDelete(new QualifiedName("", "schema", "table"), Style.SIMPLE, "", new GenericDialect()));
		
		assertEquals("DELETE FROM schema.table WHERE col = 42", svc.generateDelete(new QualifiedName("", "schema", "table"), Style.SIMPLE, " col=42", new GenericDialect()));
		}
	
	/**
	 * Test generateTruncate
	 */
	public void testGenerateTruncate()
		{
		final SQLGeneratorService svc = new SQLGeneratorServiceImpl();
		
		assertEquals("TRUNCATE TABLE schema.table", svc.generateTruncate(new QualifiedName("", "schema", "table"), Style.SIMPLE, new GenericDialect()));
		}
	}
