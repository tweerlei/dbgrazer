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
package de.tweerlei.spring.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.spring.serializer.Serializer;
import de.tweerlei.spring.serializer.impl.EnumSerializer;
import de.tweerlei.spring.serializer.impl.ListSerializer;
import de.tweerlei.spring.serializer.impl.SetSerializer;
import de.tweerlei.spring.serializer.impl.SortedSetSerializer;
import de.tweerlei.spring.service.SerializerFactory;

/**
 * HashMap based impl. that detects available Serializers
 * 
 * @author Robert Wruck
 */
@Service("serializerFactory")
public class SerializerFactoryImpl implements SerializerFactory
	{
	private final Map<Class<?>, Serializer<?>> serializers;
	private final Map<Class<? extends Collection<?>>, Map<Class<?>, Serializer<?>>> collectionSerializers;
	
	/**
	 * Constructor
	 * @param impls All known Serializers
	 */
	@Autowired(required = false)
	public SerializerFactoryImpl(Set<Serializer<?>> impls)
		{
		this.serializers = new ConcurrentHashMap<Class<?>, Serializer<?>>();
		for (Serializer<?> s : impls)
			{
			Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "Adding " + s.getClass().getName()
					+ " for " + s.getValueType().getName());
			serializers.put(s.getValueType(), s);
			}
		collectionSerializers = new ConcurrentHashMap<Class<? extends Collection<?>>, Map<Class<?>, Serializer<?>>>();
		}
	
	/**
	 * Constructor used if no Serializer instances are available
	 */
	public SerializerFactoryImpl()
		{
		this(Collections.<Serializer<?>>emptySet());
		}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> Serializer<T> getSerializer(Class<T> clazz)
		{
		Serializer<T> ret = (Serializer<T>) serializers.get(clazz);
		if ((ret == null) && clazz.isEnum())
			{
			// Create EnumSerializers on demand
			Logger.getLogger(getClass().getCanonicalName()).log(Level.INFO, "Adding EnumSerializer for " + clazz.getName());
			ret = new EnumSerializer(clazz);
			serializers.put(clazz, ret);
			}
		return (ret);
		}
	
	@SuppressWarnings("unchecked")
	public <V, T extends Collection<V>> Serializer<T> getSerializer(Class<T> clazz, Class<V> elementClazz)
		{
		Map<Class<?>, Serializer<?>> known = collectionSerializers.get(clazz);
		if (known == null)
			{
			known = new ConcurrentHashMap<Class<?>, Serializer<?>>();
			collectionSerializers.put(clazz, known);
			}
		
		Serializer<T> ret = (Serializer<T>) known.get(elementClazz);
		if (ret == null)
			{
			final Serializer<V> s = getSerializer(elementClazz);
			if (s == null)
				return (null);
			
			if (List.class.isAssignableFrom(clazz))
				ret = (Serializer<T>) (Serializer<?>) new ListSerializer<V>(s);
			else if (SortedSet.class.isAssignableFrom(clazz))
				ret = (Serializer<T>) (Serializer<?>) new SortedSetSerializer<V>(s);
			else if (Set.class.isAssignableFrom(clazz))
				ret = (Serializer<T>) (Serializer<?>) new SetSerializer<V>(s);
			else
				return (null);
			
			known.put(elementClazz, ret);
			}
		return (ret);
		}
	
	public <T> T decode(Class<T> clazz, String value) throws IllegalArgumentException
		{
		final Serializer<T> s = getSerializer(clazz);
		if (s == null)
			throw new IllegalArgumentException("No serializer for " + clazz);
		
		return (s.decode(value));
		}
	
	public <V, T extends Collection<V>> T decode(Class<T> clazz, Class<V> elementClazz, String value) throws IllegalArgumentException
		{
		final Serializer<T> s = getSerializer(clazz, elementClazz);
		if (s == null)
			throw new IllegalArgumentException("No serializer for " + clazz + "<" + elementClazz + ">");
		
		return (s.decode(value));
		}
	
	public <T> String encode(Class<T> clazz, T value) throws IllegalArgumentException
		{
		final Serializer<T> s = getSerializer(clazz);
		if (s == null)
			throw new IllegalArgumentException("No serializer for " + clazz);
		
		return (s.encode(value));
		}
	
	public <V, T extends Collection<V>> String encode(Class<T> clazz, Class<V> elementClazz, T value) throws IllegalArgumentException
		{
		final Serializer<T> s = getSerializer(clazz, elementClazz);
		if (s == null)
			throw new IllegalArgumentException("No serializer for " + clazz + "<" + elementClazz + ">");
		
		return (s.encode(value));
		}
	}
