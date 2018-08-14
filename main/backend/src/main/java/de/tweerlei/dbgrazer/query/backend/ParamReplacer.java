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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tweerlei.common5.util.FindReplace;

/**
 * Replace ?n? placeholders in the stmt with the n-th parameter
 * 
 * @author Robert Wruck
 */
public class ParamReplacer extends FindReplace
	{
	private static final Pattern PAT_PARAM = Pattern.compile("\\?(\\d)\\?");
	
	private final List<Object> params;
	
	/**
	 * Constructor
	 * @param params Actual parameters
	 */
	public ParamReplacer(List<Object> params)
		{
		super(PAT_PARAM);
		this.params = params;
		}
	
	/**
	 * Get the parameters
	 * @return Parameters
	 */
	public List<Object> getParams()
		{
		return (params);
		}
	
	@Override
	protected final String getReplacement(String match, int index)
		{
		final int n;
		try	{
			n = Integer.parseInt(match);
			}
		catch (NumberFormatException e)
			{
			throw new RuntimeException("Invalid parameter index: " + match);
			}
		
		if ((n < 1) || (n > params.size()))
			throw new RuntimeException("Undefined parameter index: " + match);
		
		return (getReplacement(n - 1));
		}
	
	/**
	 * Get the replacement value for a parameter
	 * @param index Parameter index (validated)
	 * @return Replacement String
	 */
	protected String getReplacement(int index)
		{
		final Object p = params.get(index);
		if (p == null)
			return ("");
		else
			return (Matcher.quoteReplacement(p.toString()));
		}
	}
