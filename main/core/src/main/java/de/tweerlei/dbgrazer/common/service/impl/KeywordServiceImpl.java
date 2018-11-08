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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.service.KeywordService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class KeywordServiceImpl implements KeywordService
	{
	// Names for objects:
	// Allow (unicode) letters, digits and certain special characters.
	// Disallow characters that may not occur in filenames: "*/:<>?\|
	// Disallow XML special characters: <>&"'
	// Disallow URL special characters: :/?&%#=
	// Disallow SVN special characters: @
	// Disallow SPLIT_DELIM
	private static final Pattern ILLEGAL_NAME_CHARS = Pattern.compile("[^-+._()$ \\p{javaLetterOrDigit}]+");
	
	// Names for parameters (and their values):
	// Allow (unicode) letters, digits and certain special characters.
	// Disallow XML special characters: <>&"'
	// Disallow SPLIT_DELIM
	// Disallow internal separator :
	// Disallow special marker *
	private static final Pattern ILLEGAL_PARAM_CHARS = Pattern.compile("[^-+._()@/ \\p{javaLetterOrDigit}]+");
	
	// Names for groups:
	// Allow (unicode) letters, digits and certain special characters.
	// Disallow XML special characters: <>&"'
	// Disallow SPLIT_DELIM
	// Disallow internal separator :
	private static final Pattern ILLEGAL_GROUP_CHARS = Pattern.compile("[^-*+._()@/ \\p{javaLetterOrDigit}]+");
	
	// Words to be replaced in raw SQL queries should be restricted to SQL names
	// (including the . and @ component operators)
	private static final Pattern ILLEGAL_WORD_CHARS = Pattern.compile("[^._@a-zA-Z0-9]+");
	
	private static final String SPLIT_DELIM = ", ";
	private static final Pattern SPLIT_PATTERN = Pattern.compile(",");
	
	private static final String WORD_DELIM = " ";
	private static final Pattern WORD_PATTERN = Pattern.compile("\\s+");
	
	@Override
	public String normalizeName(String name)
		{
		if (name == null)
			return ("");
		
		return (ILLEGAL_NAME_CHARS.matcher(name).replaceAll("").trim());
		}
	
	@Override
	public String normalizeParam(String name)
		{
		if (name == null)
			return ("");
		
		return (ILLEGAL_PARAM_CHARS.matcher(name).replaceAll("").trim());
		}
	
	@Override
	public String normalizeGroup(String name)
		{
		if (name == null)
			return ("");
		
		return (ILLEGAL_GROUP_CHARS.matcher(name).replaceAll("").trim());
		}
	
	@Override
	public String normalizeWord(String word)
		{
		if (word == null)
			return ("");
		
		return (ILLEGAL_WORD_CHARS.matcher(word).replaceAll("").trim());
		}
	
	@Override
	public String normalizePath(String path)
		{
		if (path == null)
			return ("");
		
		final String tmp = "/" + path.replace('\\', '/').trim() + "/";
		
		return (tmp
				// Remove illegal characters
				.replaceAll("[\u0000-\u001f?*:\"<>|\u007f-\u009f\u0250-\uffff]", "")
				// Remove . and ..
				.replaceAll("/\\.\\.?/", "/")
				// Compress multiple slashes
				.replaceAll("//+", "/")
				// Strip slashes from start and end
				.replaceAll("^/", "")
				.replaceAll("/$", "")
				);
		}
	
	@Override
	public List<String> extractValues(String value)
		{
		if (value == null)
			return (new ArrayList<String>(0));
		
		final String[] tokens = SPLIT_PATTERN.split(value, -1);
		final List<String> ret = new ArrayList<String>(tokens.length);
		for (String t : tokens)
			{
			final String s = t.trim();
			if (s.length() > 0)
				ret.add(s);
			}
		
		return (ret);
		}
	
	@Override
	public String combineValues(Iterable<String> values)
		{
		if (values == null)
			return ("");
		
		final StringBuilder sb = new StringBuilder();
		for (String t : values)
			{
			final String s = (t == null) ? "" : t.trim();
			if (s.length() > 0)
				{
				if (sb.length() > 0)
					sb.append(SPLIT_DELIM);
				sb.append(s);
				}
			}
		return (sb.toString());
		}
	
	@Override
	public List<String> extractWords(String value)
		{
		if (value == null)
			return (new ArrayList<String>(0));
		
		final String[] tokens = WORD_PATTERN.split(value, -1);
		final List<String> ret = new ArrayList<String>(tokens.length);
		for (String t : tokens)
			{
			final String s = t.trim();
			if (s.length() > 0)
				ret.add(s);
			}
		
		return (ret);
		}
	
	@Override
	public String combineWords(Iterable<String> values)
		{
		if (values == null)
			return ("");
		
		final StringBuilder sb = new StringBuilder();
		for (String t : values)
			{
			final String s = (t == null) ? "" : t.trim();
			if (s.length() > 0)
				{
				if (sb.length() > 0)
					sb.append(WORD_DELIM);
				sb.append(s);
				}
			}
		return (sb.toString());
		}
	}
