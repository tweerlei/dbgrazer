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
package de.tweerlei.common5.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find and replace substrings
 * 
 * @author Robert Wruck
 */
public abstract class FindReplace
	{
	private final Pattern pattern;
	
	/**
	 * Constructor
	 * @param pattern Pattern to match (null means no-op)
	 */
	public FindReplace(Pattern pattern)
		{
		this.pattern = pattern;
		}
	
	private String getMatch(Matcher m)
		{
		if (m.groupCount() > 0)
			return (m.group(1));
		
		return (m.group());
		}
	
	/**
	 * Find the first matching substring
	 * @param s String to match
	 * @return Matched substring or null if no match
	 */
	public String findFirst(String s)
		{
		if (s == null)
			return (null);
		if (pattern == null)
			return (null);
		
		final Matcher m = pattern.matcher(s);
		if (m.find())
			return (getMatch(m));
		return (null);
		}
	
	/**
	 * Find all matching substrings
	 * @param s String to match
	 * @return Matched substrings
	 */
	public List<String> findAll(String s)
		{
		if (s == null)
			return (Collections.emptyList());
		if (pattern == null)
			return (Collections.emptyList());
		
		final Matcher m = pattern.matcher(s);
		final List<String> ret = new LinkedList<String>();
		while (m.find())
			ret.add(getMatch(m));
		return (ret);
		}
	
	/**
	 * Replace the first occurrence of the search terms with their respective replacements
	 * @param s String
	 * @return Replaced String
	 */
	public String replaceFirst(String s)
		{
		if (s == null)
			return (null);
		if (pattern == null)
			return (s);
		
		final StringBuffer sb = new StringBuffer();
		final Matcher m = pattern.matcher(s);
		int i = 0;
		if (m.find())
			m.appendReplacement(sb, getReplacement(getMatch(m), i++));
		m.appendTail(sb);
		return (sb.toString());
		}
	
	/**
	 * Replace all occurrences of the search terms with their respective replacements
	 * @param s String
	 * @return Replaced String
	 */
	public String replaceAll(String s)
		{
		if (s == null)
			return (null);
		if (pattern == null)
			return (s);
		
		final StringBuffer sb = new StringBuffer();
		final Matcher m = pattern.matcher(s);
		int i = 0;
		while (m.find())
			m.appendReplacement(sb, getReplacement(getMatch(m), i++));
		m.appendTail(sb);
		return (sb.toString());
		}
	
	/**
	 * Get a replacement String for a matched substring
	 * @param match The matched substring
	 * @param index The match index (starting at 0)
	 * @return Replacement String
	 */
	protected abstract String getReplacement(String match, int index);
	}
