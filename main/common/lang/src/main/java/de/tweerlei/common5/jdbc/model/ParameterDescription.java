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
 * Procedure parameter description
 * 
 * @author Robert Wruck
 */
public class ParameterDescription
	{
	/**
	 * Parameter direction
	 */
	public static enum Direction
		{
		/** Input parameter */
		IN,
		/** Output parameter */
		OUT,
		/** Input/output parameter */
		INOUT
		}
	
	private final Direction dir;
	private final String name;
	private final String comment;
	private final TypeDescription type;
	private final boolean nullable;
	
	/**
	 * Constructor
	 * @param dir Direction
	 * @param name Column name
	 * @param comment Column comment
	 * @param type Column type
	 * @param typeName Column type name
	 * @param length Length
	 * @param decimals Decimal digits
	 * @param nullable Nullability
	 */
	public ParameterDescription(Direction dir, String name, String comment, int type, String typeName, int length, int decimals, boolean nullable)
		{
		this.dir = dir;
		this.name = name;
		this.comment = comment;
		this.type = new TypeDescription(typeName, type, length, decimals);
		this.nullable = nullable;
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
	 * Get the comment
	 * @return the comment
	 */
	public String getComment()
		{
		return comment;
		}

	/**
	 * Get the type
	 * @return the type
	 */
	public TypeDescription getType()
		{
		return type;
		}

	/**
	 * Get the direction
	 * @return the direction
	 */
	public Direction getDirection()
		{
		return dir;
		}

	/**
	 * Get the nullable
	 * @return the nullable
	 */
	public boolean isNullable()
		{
		return nullable;
		}

	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + (nullable ? 1231 : 1237);
		result = prime * result
				+ ((type == null) ? 0 : type.hashCode());
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
		ParameterDescription other = (ParameterDescription) obj;
		if (dir != other.dir)
			return false;
		if (nullable != other.nullable)
			return false;
		if (type == null)
			{
			if (other.type != null)
				return false;
			}
		else if (!type.equals(other.type))
			return false;
		return true;
		}
	}
