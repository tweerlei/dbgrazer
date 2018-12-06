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
package de.tweerlei.dbgrazer.plugins.ldap.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse SQL-like query strings
 * 
 * @author Robert Wruck
 */
public class LdapQueryParser
	{
	private static final Pattern PAT_QUERY = Pattern.compile("\\s*(\\S+)\\s+(.*)", Pattern.DOTALL);
	private static final String ALL_ATTRIBUTES = "*";
	private static final String DEFAULT_BASE = "";
	private static final String DEFAULT_FILTER = "(objectClass=*)";
	
	private final String baseDN;
	private final String filter;
	private final String[] attributes;
	
	/**
	 * Constructor
	 * @param statement Statement to parse
	 */
	public LdapQueryParser(String statement)
		{
		String tmp_base = DEFAULT_BASE;
		String tmp_filter = DEFAULT_FILTER;
		String[] tmp_attrs = null;
		
		for (String line : statement.split("\\n"))
			{
			if (line.trim().length() == 0)
				continue;
			
			final Matcher m = PAT_QUERY.matcher(line);
			if (!m.matches())
				throw new RuntimeException("Invalid query");
			final String key = m.group(1).toUpperCase();
			final String value = m.group(2);
			
			if (key.equals("SELECT"))
				{
				boolean all = false;
				final String[] attrs = value.split(",");
				for (int i = 0; i < attrs.length; i++)
					{
					attrs[i] = attrs[i].trim();
					if ((attrs[i].length() == 0) || ALL_ATTRIBUTES.equals(attrs[i]))
						all = true;
					}
				if (!all)
					tmp_attrs = attrs;
				}
			else if (key.equals("FROM"))
				{
				tmp_base = value.trim();
				}
			else if (key.equals("WHERE"))
				{
				tmp_filter = value.trim();
				}
			else
				throw new RuntimeException("Invalid query");
			}
		
		attributes = tmp_attrs;
		baseDN = tmp_base;
		filter = tmp_filter;
		}
	
	/**
	 * Get the baseDN
	 * @return the baseDN
	 */
	public String getBaseDN()
		{
		return baseDN;
		}
	
	/**
	 * Get the filter
	 * @return the filter
	 */
	public String getFilter()
		{
		return filter;
		}
	
	/**
	 * Get the attributes
	 * @return the attributes
	 */
	public String[] getAttributes()
		{
		return attributes;
		}
	}
