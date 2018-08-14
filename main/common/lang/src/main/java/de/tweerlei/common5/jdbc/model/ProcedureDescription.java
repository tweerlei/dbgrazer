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
 * Table description
 * 
 * @author Robert Wruck
 */
public class ProcedureDescription
	{
	/** Type name for functions returning results */
	public static final String FUNCTION = "FUNCTION";
	/** Type name for procedures not returning a result */
	public static final String PROCEDURE = "PROCEDURE";
	
	private final QualifiedName name;
	private final String comment;
	private final String type;
	private final List<ParameterDescription> params;
	private final List<ParameterDescription> results;
	
	/**
	 * Constructor
	 * @param catalog Catalog
	 * @param schema Schema
	 * @param name Name
	 * @param comment Procedure comment
	 * @param type Procedure type
	 * @param params Parameters
	 * @param results Results
	 */
	public ProcedureDescription(String catalog, String schema, String name, String comment, String type,
			Collection<ParameterDescription> params, Collection<ParameterDescription> results)
		{
		this.name = new QualifiedName(catalog, schema, name);
		this.comment = comment;
		this.type = type;
		this.params = (params == null) ? Collections.<ParameterDescription>emptyList() : Collections.unmodifiableList(new ArrayList<ParameterDescription>(params));
		this.results = (results == null) ? Collections.<ParameterDescription>emptyList() : Collections.unmodifiableList(new ArrayList<ParameterDescription>(results));
		}
	
	/**
	 * Get the name
	 * @return the name
	 */
	public QualifiedName getName()
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
	public String getType()
		{
		return type;
		}

	/**
	 * Get the params
	 * @return the params
	 */
	public List<ParameterDescription> getParams()
		{
		return params;
		}

	/**
	 * Get the results
	 * @return the results
	 */
	public List<ParameterDescription> getResults()
		{
		return results;
		}
	
	@Override
	public int hashCode()
		{
		return (name.hashCode());
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
		ProcedureDescription other = (ProcedureDescription) obj;
		if (!name.equals(other.name))
			return false;
		return true;
		}
	
	@Override
	public String toString()
		{
		return (name.toString());
		}
	}
