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
package de.tweerlei.dbgrazer.query.backend;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

/**
 * Tests for ParamReplacer
 * 
 * @author Robert Wruck
 */
public class ParamReplacerTest extends TestCase
	{
	/**
	 * Test it
	 */
	public void testEmpty()
		{
		final ParamReplacer pr = new ParamReplacer(Collections.emptyList());
		
		assertEquals("", pr.replaceAll(""));
		
		assertEquals("test", pr.replaceAll("test"));
		
		try	{
			pr.replaceAll("?1?");
			fail();
			}
		catch (RuntimeException e)
			{
			// expected
			}
		}
	
	/**
	 * Test it
	 */
	public void testReplace()
		{
		final ParamReplacer pr = new ParamReplacer(Arrays.<Object>asList("Simple param", "\\path\\to\\dir\\"));
		
		assertEquals("", pr.replaceAll(""));
		
		assertEquals("test", pr.replaceAll("test"));
		
		assertEquals("Simple param", pr.replaceAll("?1?"));
		
		assertEquals("\\path\\to\\dir\\", pr.replaceAll("?2?"));
		
		assertEquals("\\path\\to\\dir\\ - Simple param - \\path\\to\\dir\\", pr.replaceAll("?2? - ?1? - ?2?"));
		}
	}
