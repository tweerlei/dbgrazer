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
package de.tweerlei.dbgrazer.web.extension;

import de.tweerlei.dbgrazer.query.model.QueryGroup;

/**
 * Extension definition
 * 
 * @author Robert Wruck
 */
public class ExtensionGroup
	{
	private final String label;
	private final QueryGroup queries;
	
	/**
	 * Constructor
	 * @param label The label (message key)
	 * @param queries QueryGroup
	 */
	public ExtensionGroup(String label, QueryGroup queries)
		{
		this.label = label;
		this.queries = queries;
		}
	
	/**
	 * @return the label
	 */
	public String getLabel()
		{
		return label;
		}
	
	/**
	 * @return the queries
	 */
	public QueryGroup getQueries()
		{
		return queries;
		}
	}
