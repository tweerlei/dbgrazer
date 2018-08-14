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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.spring.serializer.Serializer;
import de.tweerlei.spring.serializer.impl.EnumSerializer;
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
	
	public <T> T decode(Class<T> clazz, String value) throws IllegalArgumentException
		{
		final Serializer<T> s = getSerializer(clazz);
		if (s == null)
			throw new IllegalArgumentException("No serializer for " + clazz);
		
		return (s.decode(value));
		}
	
	public <T> String encode(Class<T> clazz, T value) throws IllegalArgumentException
		{
		final Serializer<T> s = getSerializer(clazz);
		if (s == null)
			throw new IllegalArgumentException("No serializer for " + clazz);
		
		return (s.encode(value));
		}
	}
