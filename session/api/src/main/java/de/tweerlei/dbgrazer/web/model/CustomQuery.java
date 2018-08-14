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

import java.util.HashMap;
import java.util.Map;

/**
 * A named query string
 * 
 * @author Robert Wruck
 */
public class CustomQuery extends UserObject
	{
	private String type;
	private String query;
	private final Map<String, String> attributes;
	
	/**
	 * Constructor
	 */
	public CustomQuery()
		{
		this.attributes = new HashMap<String, String>();
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
	 * Set the type
	 * @param type the type to set
	 */
	public void setType(String type)
		{
		this.type = type;
		}
	
	/**
	 * Get the query
	 * @return the query
	 */
	public String getQuery()
		{
		return query;
		}
	
	/**
	 * Set the query
	 * @param query the query to set
	 */
	public void setQuery(String query)
		{
		this.query = query;
		}
	
	/**
	 * Get the attributes
	 * @return Attributes
	 */
	public Map<String, String> getAttributes()
		{
		return (attributes);
		}
	}
