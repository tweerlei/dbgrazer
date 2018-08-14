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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Table index description
 * 
 * @author Robert Wruck
 */
public class IndexDescription
	{
	private final String name;
	private final boolean unique;
	private final List<String> columns;
	
	/**
	 * Constructor
	 * @param name Index name
	 * @param unique Whether the index is unique
	 * @param columns Column names
	 */
	public IndexDescription(String name, boolean unique, Collection<String> columns)
		{
		this.name = name;
		this.unique = unique;
		this.columns = (columns == null) ? Collections.<String>emptyList() : Collections.unmodifiableList(new ArrayList<String>(columns));
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
	 * Get the unique
	 * @return the unique
	 */
	public boolean isUnique()
		{
		return unique;
		}

	/**
	 * Get the columns
	 * @return the columns
	 */
	public List<String> getColumns()
		{
		return columns;
		}
	
	/**
	 * Accept a visitor
	 * @param v Visitor
	 */
	public void accept(TableVisitor v)
		{
		v.visitIndex(this);
		}
	
	@Override
	public int hashCode()
		{
		return (columns.hashCode());
		}
	
	@Override
	public boolean equals(Object o)
		{
		if (o == null)
			return (false);
		if (o == this)
			return (true);
		if (!(o instanceof IndexDescription))
			return (false);
		final IndexDescription i = (IndexDescription) o;
		if (unique != i.unique)
			return (false);
		return (columns.equals(i.columns));
		}
	}
