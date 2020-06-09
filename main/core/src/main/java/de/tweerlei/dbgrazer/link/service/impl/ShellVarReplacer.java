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
package de.tweerlei.dbgrazer.link.service.impl;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tweerlei.common5.util.FindReplace;

/**
 * Replace shell-style variables like ${VARIABLE}.
 * Escaping is possible via ${$}.
 * Variables that are not defined in the passed Map will be replaced by the empty String.
 * 
 * @author Robert Wruck <wruck@tweerlei.de>
 */
public class ShellVarReplacer extends FindReplace
	{
	private static final Pattern PAT_SHELL = Pattern.compile("\\$\\{([^}]*)\\}");
	
	private final Map<String, String> vars;
	
	/**
	 * Constructor
	 * @param vars Map: Variable name -> value
	 */
	public ShellVarReplacer(Map<String, String> vars)
		{
		super(PAT_SHELL);
		this.vars = vars;
		}
	
	@Override
	protected String getReplacement(String match, int index)
		{
		if (match.equals("$"))
			return ("\\$");
		final String value = vars.get(match);
		if (value == null)
			return ("");
		return (Matcher.quoteReplacement(value));
		}
	}
