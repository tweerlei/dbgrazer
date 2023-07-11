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

import de.tweerlei.spring.serializer.Serializer;

/**
 * Serializer for Enums.
 * You have to create a separate instance for each enum type.
 * @param <T> Enum type
 * 
 * @author Robert Wruck
 */
public class EnumSerializer<T extends Enum<T>> implements Serializer<T>
	{
	private final Class<T> type;
	
	/**
	 * Constructor
	 * @param type Enum type
	 */
	public EnumSerializer(Class<T> type)
		{
		this.type = type;
		}
	
	public T decode(String value)
		{
		if (value == null)
			return (null);
		
		try	{
			return (Enum.valueOf(type, value));
			}
		catch (NumberFormatException e)
			{
			throw new IllegalArgumentException(e);
			}
		}
	
	public String encode(T value)
		{
		if (value == null)
			return (null);
		
		return (value.name());
		}
	
	public Class<T> getValueType()
		{
		return (type);
		}
	}
