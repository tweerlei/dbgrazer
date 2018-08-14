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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Arrays;
import java.util.Collections;

import de.tweerlei.common.math.Rational;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;
import de.tweerlei.spring.service.jdk16.StringTransformerServiceImpl;
import junit.framework.TestCase;

/**
 * Tests for FrontendHelperServiceImpl
 *
 * @author Robert Wruck
 */
public class FrontendHelperServiceImplTest extends TestCase
	{
	/**
	 * Test method parsePath
	 */
	public void testParsePath()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertPathInfo(fhs.parsePath("", null), null, null, "");
		assertPathInfo(fhs.parsePath("test.html", null), null, null, "test.html");
		assertPathInfo(fhs.parsePath("/test.html", null), null, null, "test.html");
		assertPathInfo(fhs.parsePath("db/1/test.html", null), "db", "1", "test.html");
		assertPathInfo(fhs.parsePath("/db/1/test.html", null), "db", "1", "test.html");
		assertPathInfo(fhs.parsePath("db/test.html", null), null, null, "db/test.html");
		assertPathInfo(fhs.parsePath("db/1/ajax/test.html", null), "db", "1", "ajax/test.html");
		}
	
	private void assertPathInfo(PathInfo pi, String c, String s, String p)
		{
		assertEquals(c, pi.getCategory());
		assertEquals(s, pi.getSubcategory());
		assertEquals(p, pi.getPage());
		}
	
	/**
	 * Test method buildPath
	 */
	public void testBuildPath()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("", fhs.buildPath(null, null, null, null));
		assertEquals("test.html", fhs.buildPath(null, null, "test.html", null));
		assertEquals("test.html", fhs.buildPath("", null, "test.html", null));
		assertEquals("test.html", fhs.buildPath(null, "", "test.html", null));
		assertEquals("test.html", fhs.buildPath("", "", "test.html", null));
		assertEquals("test.html", fhs.buildPath("a", null, "test.html", null));
		assertEquals("test.html", fhs.buildPath(null, "b", "test.html", null));
		assertEquals("a/b/test.html", fhs.buildPath("a", "b", "test.html", null));
		assertEquals("a/b/c/test.html", fhs.buildPath("a", "b", "c/test.html", null));
		assertEquals("a/b/c/test.html", fhs.buildPath("a", "b", "c/test.html", ""));
		assertEquals("a/b/c/test.html?a=b", fhs.buildPath("a", "b", "c/test.html", "a=b"));
		}
	
	/**
	 * Test method paramEncode
	 */
	public void testParamEncode()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertNull(fhs.paramEncode(null, true));
		assertEquals("&amp;params[0]=", fhs.paramEncode("", true));
		assertEquals("&amp;params[0]=abc+def", fhs.paramEncode("abc def", true));
		assertEquals("&amp;params[0]=abc+def&amp;params[1]=ghi", fhs.paramEncode("abc def  ghi", true));
		assertEquals("&amp;params[0]=abc+def&amp;params[1]=ghi", fhs.paramEncode("abc def   ghi", true));
		assertEquals("&amp;params[0]=abc+def&amp;params[1]=ghi&amp;params[2]=M%C3%BCller", fhs.paramEncode("abc def   ghi  Müller", true));
		
		assertEquals("&params[0]=abc+def&params[1]=ghi&params[2]=M%C3%BCller", fhs.paramEncode("abc def   ghi  Müller", false));
		}
	
	/**
	 * Test method getLinkTitle
	 */
	public void testGetLinkTitle()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("\u2205", fhs.getLinkTitle(null));
		assertEquals("\u2205", fhs.getLinkTitle(""));
		assertEquals("abc", fhs.getLinkTitle("abc"));
		assertEquals("abc def", fhs.getLinkTitle("abc def"));
		assertEquals("def", fhs.getLinkTitle("abc  def"));
		assertEquals("ghi", fhs.getLinkTitle("abc  def  ghi"));
		assertEquals("ghi", fhs.getLinkTitle("abc    ghi"));
		assertEquals("ghi", fhs.getLinkTitle("    ghi"));
		assertEquals("\u2205", fhs.getLinkTitle("abc  def  "));
		}
	
	/**
	 * Test method getQueryTitle
	 */
	public void testGetQueryTitle()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("Müller", fhs.getQueryTitle("Müller", Collections.emptyList()));
		assertEquals("Müller: Meier , Lüdenscheid", fhs.getQueryTitle("Müller", Arrays.asList("Meier ", "Lüdenscheid")));
		}
	
	/**
	 * Test method getQueryLink
	 */
	public void testGetQueryParams()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("", fhs.getQueryParams(Collections.emptyList(), true));
		assertEquals("&amp;params[0]=Meier+&amp;params[1]=L%C3%BCdenscheid", fhs.getQueryParams(Arrays.asList("Meier ", "Lüdenscheid"), true));
		
		assertEquals("&params[0]=Meier+&params[1]=L%C3%BCdenscheid", fhs.getQueryParams(Arrays.asList("Meier ", "Lüdenscheid"), false));
		}
	
	/**
	 * Test method getQueryLink
	 */
	public void testGetQueryParamsMap()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("", fhs.getQueryParams(Collections.<Integer, Object>emptyMap(), true));
		assertEquals("&amp;params[2]=L%C3%BCdenscheid", fhs.getQueryParams(Collections.<Integer, Object>singletonMap(2, "Lüdenscheid"), true));
		}
	
	/**
	 * Test method getMenuRows
	 */
	public void testGetMenuRows()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		final Rational r = new Rational(4, 3);
		
		assertEquals(0, fhs.getMenuRows(0, r));
		assertEquals(1, fhs.getMenuRows(1, r));
		assertEquals(2, fhs.getMenuRows(2, r));
		assertEquals(3, fhs.getMenuRows(3, r));
		assertEquals(4, fhs.getMenuRows(4, r));
		assertEquals(5, fhs.getMenuRows(5, r));
		assertEquals(6, fhs.getMenuRows(6, r));
		assertEquals(7, fhs.getMenuRows(7, r));
		assertEquals(4, fhs.getMenuRows(8, r));
		assertEquals(15, fhs.getMenuRows(30, r));
		assertEquals(11, fhs.getMenuRows(31, r));
		}
	
	/**
	 * Test basename
	 */
	public void testBasename()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("", fhs.basename(null));
		assertEquals("", fhs.basename(""));
		assertEquals("test", fhs.basename("test"));
		assertEquals("", fhs.basename("/"));
		assertEquals("test", fhs.basename("/test"));
		assertEquals("test", fhs.basename("/foo/test"));
		assertEquals("", fhs.basename("/foo/"));
		}
	
	/**
	 * Test dirname
	 */
	public void testDirname()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("", fhs.dirname(null));
		assertEquals("", fhs.dirname(""));
		assertEquals("", fhs.dirname("test"));
		assertEquals("/", fhs.dirname("/"));
		assertEquals("/", fhs.dirname("/test"));
		assertEquals("/foo/", fhs.dirname("/foo/test"));
		assertEquals("/foo/", fhs.dirname("/foo/"));
		}
	
	/**
	 * Test filename
	 */
	public void testFilename()
		{
		final FrontendHelperService fhs = new FrontendHelperServiceImpl(new StringTransformerServiceImpl());
		
		assertEquals("", fhs.filename(null, null));
		assertEquals("", fhs.filename("", null));
		assertEquals("", fhs.filename(null, ""));
		assertEquals("", fhs.filename("", ""));
		assertEquals("file", fhs.filename(null, "file"));
		assertEquals("file", fhs.filename("", "file"));
		assertEquals("dir/", fhs.filename("dir", null));
		assertEquals("dir/", fhs.filename("dir", ""));
		assertEquals("/dir/", fhs.filename("/dir/", null));
		assertEquals("/dir/", fhs.filename("/dir/", ""));
		assertEquals("dir/file", fhs.filename("dir", "file"));
		assertEquals("/dir/file", fhs.filename("/dir/", "file"));
		}
	}
