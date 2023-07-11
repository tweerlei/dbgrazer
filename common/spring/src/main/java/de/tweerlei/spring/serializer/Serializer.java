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
package de.tweerlei.spring.serializer;

/**
 * Standard methods for serializing objects to VARCHAR columns
 * @param <T> Value type
 * 
 * @author Robert Wruck
 */
public interface Serializer<T>
	{
	/**
	 * Decode a string into an object
	 * @param value String
	 * @return Object or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be parsed
	 */
	public T decode(String value) throws IllegalArgumentException;
	
	/**
	 * Encode an object into a string
	 * @param value Object
	 * @return String or null if the passed value was null
	 * @throws IllegalArgumentException if the String could not be encoded
	 */
	public String encode(T value) throws IllegalArgumentException;
	
	/**
	 * Get the value type supported by this Serializer
	 * @return Value type
	 */
	public Class<T> getValueType();
	}
