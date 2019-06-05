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
package de.tweerlei.dbgrazer.plugins.kafka.impl;

import junit.framework.TestCase;

/**
 * Tests for KafkaQueryParser
 * 
 * @author Robert Wruck
 */
public class KafkaQueryParserTest extends TestCase
	{
	/**
	 * Test 0 arguments
	 */
	public void testParse0()
		{
		final KafkaQueryParser p = new KafkaQueryParser("");
		assertEquals("", p.getTopic());
		assertEquals(0, p.getPartition().intValue());
		assertEquals(null, p.getStartOffset());
		assertEquals(null, p.getEndOffset());
		assertEquals(null, p.getKey());
		}
	
	/**
	 * Test Topic only
	 */
	public void testParse1()
		{
		final KafkaQueryParser p = new KafkaQueryParser("Topic");
		assertEquals("Topic", p.getTopic());
		assertEquals(0, p.getPartition().intValue());
		assertEquals(null, p.getStartOffset());
		assertEquals(null, p.getEndOffset());
		assertEquals(null, p.getKey());
		}
	
	/**
	 * Test Topic + Offset
	 */
	public void testParse2a()
		{
		final KafkaQueryParser p = new KafkaQueryParser("Topic:42");
		assertEquals("Topic", p.getTopic());
		assertEquals(0, p.getPartition().intValue());
		assertEquals(42L, p.getStartOffset().longValue());
		assertEquals(null, p.getEndOffset());
		assertEquals(null, p.getKey());
		}
	
	/**
	 * Test Topic + Offsets
	 */
	public void testParse2b()
		{
		final KafkaQueryParser p = new KafkaQueryParser("Topic:42-43");
		assertEquals("Topic", p.getTopic());
		assertEquals(0, p.getPartition().intValue());
		assertEquals(42L, p.getStartOffset().longValue());
		assertEquals(43L, p.getEndOffset().longValue());
		assertEquals(null, p.getKey());
		}
	
	/**
	 * Test Topic + Partition
	 */
	public void testParse3()
		{
		final KafkaQueryParser p = new KafkaQueryParser("Topic::7");
		assertEquals("Topic", p.getTopic());
		assertEquals(7, p.getPartition().intValue());
		assertEquals(null, p.getStartOffset());
		assertEquals(null, p.getEndOffset());
		assertEquals(null, p.getKey());
		}
	
	/**
	 * Test Topic + Key
	 */
	public void testParse4()
		{
		final KafkaQueryParser p = new KafkaQueryParser("Topic:::Key");
		assertEquals("Topic", p.getTopic());
		assertEquals(null, p.getPartition());
		assertEquals(null, p.getStartOffset());
		assertEquals(null, p.getEndOffset());
		assertEquals("Key", p.getKey());
		}
	
	/**
	 * Test Topic, Offsets, Partition + Offset
	 */
	public void testParseFull()
		{
		final KafkaQueryParser p = new KafkaQueryParser("Topic:42-43:7:Key");
		assertEquals("Topic", p.getTopic());
		assertEquals(7, p.getPartition().intValue());
		assertEquals(42L, p.getStartOffset().longValue());
		assertEquals(43L, p.getEndOffset().longValue());
		assertEquals("Key", p.getKey());
		}
	}
