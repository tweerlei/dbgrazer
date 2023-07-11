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

import org.bson.types.ObjectId;

import de.tweerlei.dbgrazer.query.backend.ParamReplacer;
import de.tweerlei.dbgrazer.query.model.ParameterDef;

/**
 * Replace placeholder values of the form "?n?" with the nth parameter.
 * To enable passing literal question marks, "?0?" will be replaced with a single "?".
 * 
 * @author Robert Wruck
 */
public class MongoDBParamReplacer
	{
	private final ParamReplacer replacer;
	private final List<ParameterDef> paramDefs;
	private final List<Object> params;
	
	/**
	 * Constructor
	 * @param paramDefs Parameter definitions
	 * @param params Actual parameters
	 */
	public MongoDBParamReplacer(List<ParameterDef> paramDefs, List<Object> params)
		{
		this.replacer = new ParamReplacer(params);
		this.paramDefs = paramDefs;
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
			final String fullMatch = replacer.findMatch((String) value);
			if (fullMatch != null)
				return ((T) replaceSpecial(fullMatch));
			else
				return ((T) replacer.replaceAll((String) value));
			}
		else if (value instanceof Map)
			visit((Map<?, ?>) value);
		else if (value instanceof List)
			visit((List<?>) value);
		
		return (value);
		}
	
	private Object replaceSpecial(String match)
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
		
		final ParameterDef def = paramDefs.get(n - 1);
		final Object p = params.get(n - 1);
		
		switch (def.getType())
			{
			case ROWID:
				return (new ObjectId(p.toString()));
			case INTEGER:
			case FLOAT:
			case DATE:
				return (p);
			default:
				return (p.toString());
			}
		}
	}
