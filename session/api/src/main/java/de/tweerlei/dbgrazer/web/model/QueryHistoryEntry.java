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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Query history entry
 * 
 * @author Robert Wruck
 */
public class QueryHistoryEntry implements Serializable
	{
	private final String queryName;
	private final List<String> params;
	
	/**
	 * Constructor
	 * @param queryName Query name
	 * @param params Parameters
	 */
	public QueryHistoryEntry(String queryName, List<String> params)
		{
		this.queryName = queryName;
		this.params = Collections.unmodifiableList(new ArrayList<String>(params));
		}

	/**
	 * Get the queryName
	 * @return the queryName
	 */
	public String getQueryName()
		{
		return queryName;
		}
	
	/**
	 * Get the params
	 * @return the params
	 */
	public List<String> getParams()
		{
		return params;
		}
	
	@Override
	public int hashCode()
		{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((queryName == null) ? 0 : queryName.hashCode());
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
		QueryHistoryEntry other = (QueryHistoryEntry) obj;
		if (params == null)
			{
			if (other.params != null)
				return false;
			}
		else if (!params.equals(other.params))
			return false;
		if (queryName == null)
			{
			if (other.queryName != null)
				return false;
			}
		else if (!queryName.equals(other.queryName))
			return false;
		return true;
		}
	}
