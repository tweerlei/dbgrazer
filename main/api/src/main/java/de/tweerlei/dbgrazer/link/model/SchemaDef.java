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
package de.tweerlei.dbgrazer.link.model;

import java.io.Serializable;

import de.tweerlei.common.util.StringUtils;

/**
 * Schema definition
 * 
 * @author Robert Wruck
 */
public class SchemaDef implements Comparable<SchemaDef>, Serializable
	{
	private final String name;
	private final String version;
	
	/**
	 * Constructor
	 * @param name Schema name
	 * @param version Schema version
	 */
	public SchemaDef(String name, String version)
		{
		this.name = StringUtils.notNull(name);
		this.version = StringUtils.notNull(version);
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
	 * Get the version
	 * @return the version
	 */
	public String getVersion()
		{
		return version;
		}

	/**
	 * Check whether the name is filled
	 * @return true if name filled
	 */
	private boolean hasName()
		{
		return (name.length() > 0);
		}
	
	/**
	 * Check whether the version is filled
	 * @return true if version filled
	 */
	private boolean hasVersion()
		{
		return (version.length() > 0);
		}
	
	/**
	 * Check whether this is a subschema name
	 * @return true for subschema name
	 */
	public boolean isSubschema()
		{
		return (hasName() && hasVersion());
		}
	
	/**
	 * Check whether this is a query set name
	 * @return true for query set name
	 */
	public boolean isQuerySet()
		{
		return (!hasName() && hasVersion());
		}
	
	/**
	 * Check whether this is a main schema name
	 * @return true for main schema name
	 */
	public boolean isMainSchema()
		{
		return (hasName() && !hasVersion());
		}
	
	/**
	 * Get a SchemaDef that has the same name but no version
	 * @return SchemaDef
	 */
	public SchemaDef getUnversionedSchema()
		{
		return (hasVersion() ? new SchemaDef(name, null): this);
		}
	
	@Override
	public int compareTo(SchemaDef other)
		{
		int i = name.compareTo(other.name);
		if (i == 0)
			i = version.compareTo(other.version);
		return (i);
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + version.hashCode();
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
		final SchemaDef other = (SchemaDef) obj;
		if (!name.equals(other.name))
			return false;
		if (!version.equals(other.version))
			return false;
		return true;
		}
	
	@Override
	public String toString()
		{
		if (hasName())
			{
			if (hasVersion())
				return (name + "/" + version);
			else
				return (name);
			}
		else
			return ("@" + version);
		}
	
	/**
	 * Create a SchemaDef from its toString representation
	 * @param s String
	 * @return SchemaDef
	 */
	public static SchemaDef valueOf(String s)
		{
		if (s == null)
			return (new SchemaDef(null, null));
		
		if (s.startsWith("@"))
			return (new SchemaDef(null, s.substring(1)));
		
		final int i = s.indexOf('/');
		if (i < 0)
			return (new SchemaDef(s, null));
		
		return (new SchemaDef(s.substring(0, i), s.substring(i + 1)));
		}
	}
