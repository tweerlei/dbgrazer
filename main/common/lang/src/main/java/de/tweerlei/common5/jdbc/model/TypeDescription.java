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
package de.tweerlei.common5.jdbc.model;

/**
 * Data type description
 * 
 * @author Robert Wruck
 */
public class TypeDescription
	{
	private final String name;
	private final int type;
	private final int length;
	private final int decimals;
	
	/**
	 * Constructor
	 * @param name Type name
	 * @param type Column type
	 * @param length Length
	 * @param decimals Decimal digits
	 */
	public TypeDescription(String name, int type, int length, int decimals)
		{
		this.name = name;
		this.type = type;
		this.length = length;
		this.decimals = decimals;
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
	 * Get the type
	 * @return the type
	 */
	public int getType()
		{
		return type;
		}

	/**
	 * Get the length
	 * @return the length
	 */
	public int getLength()
		{
		return length;
		}

	/**
	 * Get the decimals
	 * @return the decimals
	 */
	public int getDecimals()
		{
		return decimals;
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
		result = prime * result + decimals;
		result = prime * result + length;
		result = prime * result + type;
		return result;
		}

	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeDescription other = (TypeDescription) obj;
		if (name == null)
			{
			if (other.name != null)
				return false;
			}
		else if (!name.equals(other.name))
			return false;
		if (decimals != other.decimals)
			return false;
		if (length != other.length)
			return false;
		if (type != other.type)
			return false;
		return true;
		}
	}
