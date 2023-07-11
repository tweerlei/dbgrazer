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
package de.tweerlei.ermtools.dialect;

/**
 * SQL data type description
 * 
 * @author Robert Wruck
 */
public class SQLDataType
	{
	private final String name;
	private final boolean hasLength;
	private final boolean hasDecimals;
	
	/**
	 * Constructor
	 * @param name Type name for usage in CREATE TABLE
	 * @param hasLength Whether to specify the length
	 * @param hasDecimals Whether to specify the decimals
	 */
	public SQLDataType(String name, boolean hasLength, boolean hasDecimals)
		{
		this.name = name;
		this.hasLength = hasLength;
		this.hasDecimals = hasDecimals;
		}

	/**
	 * Get the name
	 * @return the name
	 */
	public String getName()
		{
		return name;
		}

	/**
	 * Get the hasLength
	 * @return the hasLength
	 */
	public boolean hasLength()
		{
		return hasLength;
		}

	/**
	 * Get the hasDecimals
	 * @return the hasDecimals
	 */
	public boolean hasDecimals()
		{
		return hasDecimals;
		}
	}
