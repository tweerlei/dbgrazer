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
package de.tweerlei.dbgrazer.visualization.text;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import de.tweerlei.dbgrazer.text.backend.Hunk;
import de.tweerlei.dbgrazer.text.backend.impl.SimpleDiffCalculator;

/**
 * Tests for SimpleDiffCalculator
 * 
 * @author Robert Wruck
 */
public class SimpleDiffCalculatorTest extends TestCase
	{
	/** Test no lines */
	public void testEmpty()
		{
		final List<String> lhs = Arrays.asList();
		final List<String> rhs = Arrays.asList();
		
		final List<Hunk> diff = new SimpleDiffCalculator().diff(lhs, rhs);
		
		assertEquals(0, diff.size());
		}
	
	/** Test single lines */
	public void testSingle()
		{
		final List<String> lhs = Arrays.asList("");
		final List<String> rhs = Arrays.asList("");
		
		final List<Hunk> diff = new SimpleDiffCalculator().diff(lhs, rhs);
		
		assertEquals(0, diff.size());
		}
	
	/** Test add lines */
	public void testAdd()
		{
		final List<String> lhs = Arrays.asList("a", "b", "c");
		final List<String> rhs = Arrays.asList("a", "b", "bb", "c");
		
		final List<Hunk> diff = new SimpleDiffCalculator().diff(lhs, rhs);
		
		assertEquals(1, diff.size());
		assertEquals(new Hunk(2, 2, 2, 3), diff.get(0));
		}
	
	/** Test remove lines */
	public void testRemove()
		{
		final List<String> lhs = Arrays.asList("a", "b", "bb", "c");
		final List<String> rhs = Arrays.asList("a", "b", "c");
		
		final List<Hunk> diff = new SimpleDiffCalculator().diff(lhs, rhs);
		
		assertEquals(1, diff.size());
		assertEquals(new Hunk(2, 3, 2, 2), diff.get(0));
		}
	
	/** Test change lines */
	public void testChange()
		{
		final List<String> lhs = Arrays.asList("a", "b", "c");
		final List<String> rhs = Arrays.asList("a", "b", "d");
		
		final List<Hunk> diff = new SimpleDiffCalculator().diff(lhs, rhs);
		
		assertEquals(1, diff.size());
		assertEquals(new Hunk(2, 3, 2, 3), diff.get(0));
		}
	}
