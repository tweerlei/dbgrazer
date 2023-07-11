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
 * Serializer for Lists.
 * You have to create a separate instance for each element type.
 * @param <T> Element type
 * 
 * @author Robert Wruck
 */
public class ListSerializer<T> implements Serializer<List<T>>
	{
	private final CollectionSerializer<T> delegate;
	
	/**
	 * Constructor
	 * @param delegate Element Serializer
	 */
	public ListSerializer(Serializer<T> delegate)
		{
		this.delegate = new CollectionSerializer<T>(delegate);
		}
	
	public List<T> decode(String value)
		{
		final Collection<T> tmp = delegate.decode(value);
		if (tmp == null)
			return (null);
		
		return (new ArrayList<T>(tmp));
		}
	
	public String encode(List<T> value)
		{
		return (delegate.encode(value));
		}
	
	@SuppressWarnings("unchecked")
	public Class<List<T>> getValueType()
		{
		return ((Class<List<T>>) (Class<?>) List.class);
		}
	}
