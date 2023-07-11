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
package de.tweerlei.spring.service;

import java.util.Collection;

import de.tweerlei.spring.serializer.Serializer;

/**
 * Factory for Serializers
 * 
 * @author Robert Wruck
 */
public interface SerializerFactory
	{
	/**
	 * Get a serializer for a specific class
	 * @param <T> Type
	 * @param clazz Type instance
	 * @return Serializer or null if no matching Serializer was found
	 */
	public <T> Serializer<T> getSerializer(Class<T> clazz);
	
	/**
	 * Get a serializer for a specific container and element type
	 * @param <T> Collection type
	 * @param <V> Element type
	 * @param clazz Type instance
	 * @param elementClazz Element type instance
	 * @return Serializer or null if no matching Serializer was found
	 */
	public <V, T extends Collection<V>> Serializer<T> getSerializer(Class<T> clazz, Class<V> elementClazz);
	
	/**
	 * Decode a string into an object
	 * @param <T> Type
	 * @param clazz Type instance
	 * @param value String
	 * @return Object or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be parsed
	 */
	public <T> T decode(Class<T> clazz, String value) throws IllegalArgumentException;
	
	/**
	 * Decode a string into an object
	 * @param <T> Type
	 * @param <V> Element type
	 * @param clazz Type instance
	 * @param elementClazz Element type instance
	 * @param value String
	 * @return Object or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be parsed
	 */
	public <V, T extends Collection<V>> T decode(Class<T> clazz, Class<V> elementClazz, String value) throws IllegalArgumentException;
	
	/**
	 * Encode an object into a string
	 * @param <T> Type
	 * @param clazz Type instance
	 * @param value Object
	 * @return String or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be encoded
	 */
	public <T> String encode(Class<T> clazz, T value) throws IllegalArgumentException;
	
	/**
	 * Encode an object into a string
	 * @param <T> Type
	 * @param <V> Element type
	 * @param clazz Type instance
	 * @param elementClazz Element type instance
	 * @param value Object
	 * @return String or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be encoded
	 */
	public <V, T extends Collection<V>> String encode(Class<T> clazz, Class<V> elementClazz, T value) throws IllegalArgumentException;
	}
