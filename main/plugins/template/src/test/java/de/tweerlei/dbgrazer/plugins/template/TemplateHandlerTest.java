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
package de.tweerlei.dbgrazer.plugins.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.tweerlei.dbgrazer.query.model.ColumnDef;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ResultRow;
import de.tweerlei.dbgrazer.query.model.RowHandler;
import de.tweerlei.dbgrazer.query.model.impl.ColumnDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.DefaultResultRow;
import junit.framework.TestCase;

/**
 * Tests for TemplateHandler
 * 
 * @author Robert Wruck
 */
public class TemplateHandlerTest extends TestCase
	{
	/**
	 * Test the it
	 */
	public void testIt()
		{
		final List<ColumnDef> columns = new ArrayList<ColumnDef>();
		columns.add(new ColumnDefImpl("string1", ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl("string2", ColumnType.STRING, null, null, null, null));
		columns.add(new ColumnDefImpl("int1", ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl("int2", ColumnType.INTEGER, null, null, null, null));
		columns.add(new ColumnDefImpl("date1", ColumnType.DATE, null, null, null, null));
		
		final ResultRow row = new DefaultResultRow("hello", "world", 42, 23, new Date(1560370000000L));
		
		final RowHandler handler = new TemplateHandler("c1!STRING!${values[0]}_${values[1]} c2!INTEGER!${values['int1']+values[3]} c3!!constant c4!!${fn.format('%1$tY-%1$tm-%1$td',values[4])}");
		
		handler.startRows(columns);
		assertEquals(4, columns.size());
		assertEquals(ColumnType.STRING, columns.get(0).getType());
		assertEquals(ColumnType.INTEGER, columns.get(1).getType());
		assertEquals(ColumnType.STRING, columns.get(2).getType());
		assertEquals(ColumnType.STRING, columns.get(3).getType());
		assertEquals("c1", columns.get(0).getName());
		assertEquals("c2", columns.get(1).getName());
		assertEquals("c3", columns.get(2).getName());
		assertEquals("c4", columns.get(3).getName());
		
		handler.handleRow(row);
		
		assertEquals(4, row.getValues().size());
		assertEquals("hello_world", row.getValues().get(0));
		assertEquals(65L, row.getValues().get(1));
		assertEquals("constant", row.getValues().get(2));
		assertEquals("2019-06-12", row.getValues().get(3));
		}
	}
