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
package de.tweerlei.dbgrazer.plugins.mongodb.impl;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace placeholder values of the form "?n?" with the nth parameter.
 * To enable passing literal question marks, a "??" sequence at the beginning of
 * a string will be replaced by a single "?".
 * 
 * @author Robert Wruck
 */
public class MongoDBParamReplacer
	{
	private static final Pattern PAT_PARAM = Pattern.compile("\\?(\\d)\\?");
	
	private final List<Object> params;
	
	/**
	 * Constructor
	 * @param params Actual parameters
	 */
	public MongoDBParamReplacer(List<Object> params)
		{
		this.params = params;
		}
	
	/**
	 * Recursively process a map
	 * @param <T> Value type
	 * @param obj Map
	 */
	public <T> void visit(Map<?, T> obj)
		{
		for (Map.Entry<?, T> ent : obj.entrySet())
			{
			final T value = ent.getValue();
			final T replaceWith = replace(value);
			if (replaceWith != value)
				ent.setValue(replaceWith);
			}
		}
	
	/**
	 * Recursively process a list
	 * @param <T> Value type
	 * @param arr List
	 */
	public <T> void visit(List<T> arr)
		{
		for (ListIterator<T> it = arr.listIterator(); it.hasNext(); )
			{
			final T value = it.next();
			final T replaceWith = replace(value);
			if (replaceWith != value)
				it.set(replaceWith);
			}
		}
	
	@SuppressWarnings("unchecked")
	private <T> T replace(T value)
		{
		if (value instanceof String)
			{
			final String stringValue = (String) value;
			final Matcher m = PAT_PARAM.matcher(stringValue);
			if (m.matches())
				{
				final int n;
				try	{
					n = Integer.parseInt(m.group(1));
					}
				catch (NumberFormatException e)
					{
					throw new RuntimeException("Invalid parameter index: " + m.group(1));
					}
				
				if ((n < 1) || (n > params.size()))
					throw new RuntimeException("Undefined parameter index: " + m.group(1));
				
				return ((T) params.get(n - 1));
				}
			else if (stringValue.startsWith("??"))
				{
				return ((T) stringValue.substring(1));
				}
			}
		else if (value instanceof Map)
			visit((Map<?, ?>) value);
		else if (value instanceof List)
			visit((List<?>) value);
		
		return (value);
		}
	}
