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
package de.tweerlei.dbgrazer.plugins.mongodb.impl;

import java.util.Arrays;

import org.bson.Document;

import junit.framework.TestCase;

/**
 * Tests for MongoDBParamReplacer
 * 
 * @author Robert Wruck
 */
public class MongoDBParamReplacerTest extends TestCase
	{
	public void testIt()
		{
		final Document doc = Document.parse("{ numVal: 42, strVal: 'Hello ?1? World', objVal: { nestedString: '?2?.*' } }");
		
		final MongoDBParamReplacer replacer = new MongoDBParamReplacer(Arrays.asList("new", "1234"));
		replacer.visit(doc);
		
		assertEquals("Hello new World", doc.get("strVal"));
		assertEquals("1234.*", ((Document) doc.get("objVal")).get("nestedString"));
		}
	}
