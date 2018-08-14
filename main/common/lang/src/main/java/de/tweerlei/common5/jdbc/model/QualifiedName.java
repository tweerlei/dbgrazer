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

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.common5.util.ObjectUtils;

/**
 * The qualified name of an SQL object, consisting of catalog name, schema name and object name
 * 
 * @author Robert Wruck
 */
public class QualifiedName implements Serializable, Comparable<QualifiedName>
	{
	private static final Pattern PAT_QNAME = Pattern.compile("\\[([^,]*),([^,]*),([^,]*)\\]");
	
	private final String catalogName;
	private final String schemaName;
	private final String objectName;
	
	/**
	 * Constructor
	 * @param catalogName Catalog name (may be null)
	 * @param schemaName Schema name (may be null)
	 * @param objectName Object name
	 */
	public QualifiedName(String catalogName, String schemaName, String objectName)
		{
		this.catalogName = catalogName;
		this.schemaName = schemaName;
		this.objectName = objectName;
		}

	/**
	 * Get the catalogName
	 * @return the catalogName
	 */
	public String getCatalogName()
		{
		return catalogName;
		}

	/**
	 * Get the schemaName
	 * @return the schemaName
	 */
	public String getSchemaName()
		{
		return schemaName;
		}

	/**
	 * Get the objectName
	 * @return the objectName
	 */
	public String getObjectName()
		{
		return objectName;
		}
	
	/**
	 * Check whether another QualifiedName refers to the same catalog (treating null catalog names as empty)
	 * @param o QualifiedName
	 * @return true if same
	 */
	public boolean hasSameCatalog(QualifiedName o)
		{
		if ((catalogName == null) || (catalogName.length() == 0))
			return ((o.catalogName == null) || (o.catalogName.length() == 0));
		
		return (catalogName.equals(o.catalogName));
		}
	
	/**
	 * Check whether another QualifiedName refers to the same schema (treating null catalog and schema names as empty)
	 * @param o QualifiedName
	 * @return true if same
	 */
	public boolean hasSameSchema(QualifiedName o)
		{
		if (!hasSameCatalog(o))
			return (false);
		
		if ((schemaName == null) || (schemaName.length() == 0))
			return ((o.schemaName == null) || (o.schemaName.length() == 0));
		
		return (schemaName.equals(o.schemaName));
		}
	
	/**
	 * Check whether another QualifiedName refers to the same object (treating null catalog and schema names as empty)
	 * @param o QualifiedName
	 * @return true if same
	 */
	public boolean hasSameName(QualifiedName o)
		{
		if (!hasSameSchema(o))
			return (false);
		
		if ((objectName == null) || (objectName.length() == 0))
			return ((o.objectName == null) || (o.objectName.length() == 0));
		
		return (objectName.equals(o.objectName));
		}
	
	/**
	 * Return a QualifiedName with all zero-length components set to NULL
	 * @return QualifiedName
	 */
	public QualifiedName normalize()
		{
		final boolean b1 = (catalogName != null) && (catalogName.length() == 0);
		final boolean b2 = (schemaName != null) && (schemaName.length() == 0);
		final boolean b3 = (objectName != null) && (objectName.length() == 0);
		
		if (b1 || b2 || b3)
			return (new QualifiedName(b1 ? null : catalogName, b2 ? null : schemaName, b3 ? null : objectName));
		
		return (this);
		}
	
	public int compareTo(QualifiedName o)
		{
		if (o == null)
			return (1);
		int i;
		i = StringComparators.compareTo(catalogName, o.catalogName);
		if (i != 0)
			return (i);
		i = StringComparators.compareTo(schemaName, o.schemaName);
		if (i != 0)
			return (i);
		i = StringComparators.compareTo(objectName, o.objectName);
		return (i);
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + ObjectUtils.hashCode(catalogName);
		result = prime * result + ObjectUtils.hashCode(schemaName);
		result = prime * result + ObjectUtils.hashCode(objectName);
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
		QualifiedName other = (QualifiedName) obj;
		return (ObjectUtils.equals(catalogName, other.catalogName)
				&& ObjectUtils.equals(schemaName, other.schemaName)
				&& ObjectUtils.equals(objectName, other.objectName));
		}
	
	@Override
	public String toString()
		{
		return ("[" + ((catalogName == null) ? "" : catalogName) + "," + ((schemaName == null) ? "" : schemaName) + "," + ((objectName == null) ? "" : objectName) + "]");
		}
	
	/**
	 * Parse a QualifiedName from its toString representation
	 * @param s String
	 * @return Normalized QualifiedName
	 */
	public static final QualifiedName valueOf(String s)
		{
		if (s == null)
			return (null);
		
		final Matcher m = PAT_QNAME.matcher(s);
		if (!m.matches())
			return (null);
		
		return (new QualifiedName(m.group(1), m.group(2), m.group(3)).normalize());
		}
	}
