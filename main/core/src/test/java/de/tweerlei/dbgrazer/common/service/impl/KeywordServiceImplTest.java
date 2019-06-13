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
package de.tweerlei.dbgrazer.common.service.impl;

import java.util.Arrays;

import junit.framework.TestCase;
import de.tweerlei.dbgrazer.common.service.KeywordService;

/**
 * Tests for KeywordServiceImpl
 * 
 * @author Robert Wruck
 */
public class KeywordServiceImplTest extends TestCase
	{
	/**
	 * Test normalizeName
	 */
	public void testNormalizeName()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.normalizeName(null));
		assertEquals("", svc.normalizeName(""));
		assertEquals("abcäD12-a (g)", svc.normalizeName(" abcäD12-a (g) "));
		assertEquals("a_bcäD12-a (g)", svc.normalizeName(" a_b/c$äD?\n1*2-a! (g) "));
		}
	
	/**
	 * Test normalizeParam
	 */
	public void testNormalizeParam()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.normalizeParam(null));
		assertEquals("", svc.normalizeParam(""));
		assertEquals("abcäD12-a (g)", svc.normalizeParam(" abcäD12-a (g) "));
		assertEquals("a_b/cäD12-a (g)", svc.normalizeParam(" a_b/c$äD?\n1*2-a! (g) "));
		}
	
	/**
	 * Test normalizeParam
	 */
	public void testNormalizeValue()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.normalizeValue(null));
		assertEquals("", svc.normalizeValue(""));
		assertEquals("abcäD12-a (g)", svc.normalizeValue(" abcäD12-a (g) "));
		assertEquals("a_b/c$äD?1*2-a! (g)", svc.normalizeValue(" a_b/c$äD?\n1*2-a! (g) "));
		}
	
	/**
	 * Test normalizeGroup
	 */
	public void testNormalizeGroup()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.normalizeGroup(null));
		assertEquals("", svc.normalizeGroup(""));
		assertEquals("abcäD12-a (g)", svc.normalizeGroup(" abcäD12-a (g) "));
		assertEquals("a_b/cäD1*2-a (g)", svc.normalizeGroup(" a_b/c$äD?\n1*2-a! (g) "));
		}
	
	/**
	 * Test normalizeWord
	 */
	public void testNormalizeWord()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.normalizeWord(null));
		assertEquals("", svc.normalizeWord(""));
		assertEquals("abc.D12_a", svc.normalizeWord(" abc.D12_a "));
		assertEquals("abc.D12_a", svc.normalizeWord(" a-bc$ä.D?\n12_a! "));
		}
	
	/**
	 * Test normalizePath
	 */
	public void testNormalizePath()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.normalizePath(null));
		assertEquals("", svc.normalizePath(""));
		assertEquals("", svc.normalizePath(" "));
		assertEquals("", svc.normalizePath("."));
		assertEquals("", svc.normalizePath(".."));
		assertEquals("", svc.normalizePath("*"));
		assertEquals("", svc.normalizePath("/"));
		assertEquals("a", svc.normalizePath("/a"));
		assertEquals("a", svc.normalizePath("a/"));
		assertEquals("a", svc.normalizePath("//a//"));
		assertEquals("a/b", svc.normalizePath("/a/b"));
		assertEquals("a/b", svc.normalizePath("/a/./b"));
		assertEquals("a/b", svc.normalizePath("/a/../b"));
		assertEquals("a/.../b", svc.normalizePath("/a/.../b"));
		assertEquals("a/.../b", svc.normalizePath("/a\\.../b/."));
		assertEquals("a/b/c", svc.normalizePath(" ../a\"//b/./c\\  "));
		}
	
	/**
	 * Test split
	 */
	public void testSplit()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals(Arrays.asList(), svc.extractValues(null));
		assertEquals(Arrays.asList(), svc.extractValues(""));
		assertEquals(Arrays.asList(), svc.extractValues("    "));
		assertEquals(Arrays.asList("1"), svc.extractValues(" 1 "));
		assertEquals(Arrays.asList(), svc.extractValues("  ,  "));
		assertEquals(Arrays.asList("1", "2"), svc.extractValues(" 1 , 2 "));
		assertEquals(Arrays.asList("1", "2"), svc.extractValues(" , 1 , 2 , "));
		}
	
	/**
	 * Test join
	 */
	public void testJoin()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.combineValues(null));
		assertEquals("", svc.combineValues(Arrays.<String>asList()));
		assertEquals("", svc.combineValues(Arrays.asList("")));
		assertEquals("", svc.combineValues(Arrays.asList("    ")));
		assertEquals("1", svc.combineValues(Arrays.asList(" 1 ")));
		assertEquals("1, 2", svc.combineValues(Arrays.asList(" 1  ", "2 ")));
		assertEquals("1, 2", svc.combineValues(Arrays.asList(" 1  ", null, "2 ")));
		}
	
	/**
	 * Test split
	 */
	public void testWordSplit()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals(Arrays.asList(), svc.extractWords(null));
		assertEquals(Arrays.asList(), svc.extractWords(""));
		assertEquals(Arrays.asList(), svc.extractWords("    "));
		assertEquals(Arrays.asList("1"), svc.extractWords(" 1 "));
		assertEquals(Arrays.asList("1", "2"), svc.extractWords(" 1  2 "));
		}
	
	/**
	 * Test join
	 */
	public void testWordJoin()
		{
		final KeywordService svc = new KeywordServiceImpl();
		
		assertEquals("", svc.combineWords(null));
		assertEquals("", svc.combineWords(Arrays.<String>asList()));
		assertEquals("", svc.combineWords(Arrays.asList("")));
		assertEquals("", svc.combineWords(Arrays.asList("    ")));
		assertEquals("1", svc.combineWords(Arrays.asList(" 1 ")));
		assertEquals("1 2", svc.combineWords(Arrays.asList(" 1  ", "2 ")));
		assertEquals("1 2", svc.combineWords(Arrays.asList(" 1  ", null, "2 ")));
		}
	}
