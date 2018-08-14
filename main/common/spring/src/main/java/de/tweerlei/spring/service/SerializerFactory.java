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
	 * Decode a string into an object
	 * @param <T> Type
	 * @param clazz Type instance
	 * @param value String
	 * @return Object or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be parsed
	 */
	public <T> T decode(Class<T> clazz, String value) throws IllegalArgumentException;
	
	/**
	 * Encode an object into a string
	 * @param <T> Type
	 * @param clazz Type instance
	 * @param value Object
	 * @return String or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be encoded
	 */
	public <T> String encode(Class<T> clazz, T value) throws IllegalArgumentException;
	}
