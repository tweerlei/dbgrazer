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
package de.tweerlei.dbgrazer.web.model;

import java.io.Serializable;

/**
 * UserObject key
 * @param <T> Value type
 * 
 * @author Robert Wruck
 */
public class UserObjectKey<T extends UserObject> implements Serializable
	{
	private final Class<T> type;
	private final boolean persistent;
	
	/**
	 * Constructor
	 * @param type Value type
	 * @param persistent Whether the value should persist across user logout
	 */
	private UserObjectKey(Class<T> type, boolean persistent)
		{
		this.type = type;
		this.persistent = persistent;
		}
	
	/**
	 * Get the type
	 * @return the type
	 */
	public Class<T> getType()
		{
		return type;
		}
	
	/**
	 * Get whether the value should persist across user logout
	 * @return Whether the value is persistent
	 */
	public boolean isPersistent()
		{
		return (persistent);
		}
	
	/**
	 * Create a ConfigKey instance
	 * @param <T> Value type
	 * @param type Value type
	 * @param persistent Whether the value should persist across logins
	 * @return The created UserObjectKey
	 */
	public static <T extends UserObject> UserObjectKey<T> create(Class<T> type, boolean persistent)
		{
		return (new UserObjectKey<T>(type, persistent));
		}
	
	@Override
	public int hashCode()
		{
		return (type.hashCode());
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == this)
			return (true);
		if (o == null)
			return (false);
		if (!(o instanceof UserObjectKey))
			return (false);
		return (type.equals(((UserObjectKey<?>) o).getType()));
		}
	
	@Override
	public String toString()
		{
		return (type.getSimpleName());
		}
	}
