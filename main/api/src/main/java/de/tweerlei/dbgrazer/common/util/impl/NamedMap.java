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
package de.tweerlei.dbgrazer.common.util.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Map that orders items according to Spring's Ordered interface or @Order annotation.
 * Elements that have the same order will be ordered by their name.
 * Invariant: get(key).getName().equals(key).
 * put() and putAll() will ignore keys and just add all values using their names as keys.
 * @param <T> Element type
 * 
 * @author Robert Wruck
 */
public class NamedMap<T extends Named> implements Map<String, T>
	{
	private final NamedSet<T> values;
	
	/**
	 * Constructor
	 */
	public NamedMap()
		{
		this.values = new NamedSet<T>();
		}
	
	/**
	 * Constructor
	 * @param values Values
	 */
	public NamedMap(Collection<? extends T> values)
		{
		this.values = new NamedSet<T>(values);
		}
	
	/**
	 * Constructor
	 * @param values NamedMap to copy
	 */
	public NamedMap(NamedMap<? extends T> values)
		{
		this.values = new NamedSet<T>(values.values);
		}
	
	@Override
	public void clear()
		{
		values.clear();
		}
	
	@Override
	public boolean containsKey(Object key)
		{
		return (get(key) != null);
		}
	
	@Override
	public boolean containsValue(Object value)
		{
		return (values.contains(value));
		}
	
	@Override
	public Set<Entry<String, T>> entrySet()
		{
		throw new UnsupportedOperationException("Not implemented");
		}
	
	@Override
	public T get(Object key)
		{
		for (T t : values)
			{
			if (t.getName().equals(key))
				return (t);
			}
		return (null);
		}
	
	@Override
	public boolean isEmpty()
		{
		return (values.isEmpty());
		}
	
	@Override
	public Set<String> keySet()
		{
		final Set<String> ret = new LinkedHashSet<String>();
		for (T t : values)
			ret.add(t.getName());
		
		return (Collections.unmodifiableSet(ret));
		}
	
	@Override
	public T put(String key, T value)
		{
		return (values.add(value) ? null : value);
		}
	
	@Override
	public void putAll(Map<? extends String, ? extends T> m)
		{
		values.addAll(m.values());
		}
	
	@Override
	public T remove(Object key)
		{
		for (Iterator<T> i = values.iterator(); i.hasNext(); )
			{
			final T t = i.next();
			if (t.getName().equals(key))
				{
				i.remove();
				return (t);
				}
			}
		return (null);
		}
	
	@Override
	public int size()
		{
		return (values.size());
		}
	
	@Override
	public Collection<T> values()
		{
		return (values);
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == null)
			return (false);
		if (o == this)
			return (true);
		if (!(o instanceof NamedMap))
			return (false);
		
		final NamedMap<?> m = (NamedMap<?>) o;
		return (values.equals(m.values));
		}
	
	@Override
	public int hashCode()
		{
		return (values.hashCode());
		}
	
	@Override
	public String toString()
		{
		return (values.toString());
		}
	}
