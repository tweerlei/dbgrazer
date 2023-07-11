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
package de.tweerlei.spring.serializer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.tweerlei.spring.serializer.Serializer;

/**
 * Serializer for Collections.
 * Mainly used by the specialized implementations ListSerializer, SetSerializer and SortedSetSerializer.
 * @param <T> Element type
 * 
 * @author Robert Wruck
 */
public class CollectionSerializer<T> implements Serializer<Collection<T>>
	{
	private final Serializer<T> delegate;
	
	/**
	 * Constructor
	 * @param delegate Element Serializer
	 */
	public CollectionSerializer(Serializer<T> delegate)
		{
		this.delegate = delegate;
		}
	
	public Collection<T> decode(String value)
		{
		if (value == null)
			return (null);
		
		final String[] parts = value.split("[, ]+");
		final List<T> ret = new ArrayList<T>(parts.length);
		for (String s : parts)
			{
			final String t = s.trim();
			if (t.length() > 0)
				ret.add(delegate.decode(t));
			}
		return (ret);
		}
	
	public String encode(Collection<T> value)
		{
		if (value == null)
			return (null);
		
		final StringBuilder sb = new StringBuilder();
		for (T t : value)
			{
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(delegate.encode(t));
			}
		return (sb.toString());
		}
	
	@SuppressWarnings("unchecked")
	public Class<Collection<T>> getValueType()
		{
		return ((Class<Collection<T>>) (Class<?>) List.class);
		}
	}
