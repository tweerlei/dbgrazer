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
package de.tweerlei.dbgrazer.query.model;

import de.tweerlei.dbgrazer.common.util.Named;

/**
 * Result type
 * 
 * @author Robert Wruck
 */
public enum ResultType implements Named
	{
	/** Single value */
	SINGLE(false),
	/** Single row key/value */
	ROW(false),
	/** Two columns Key/value */
	KEYVALUE(false),
	/** Table */
	TABLE(false),
	/** Hierarchical */
	HIERARCHICAL(false),
	/** Multi level */
	MULTILEVEL(true),
	/** Visualization */
	VISUALIZATION(true),
	/** Content in subqueries */
	RECURSIVE(true);
	
	private final boolean view;
	
	private ResultType(boolean view)
		{
		this.view = view;
		}
	
	@Override
	public String getName()
		{
		return (name());
		}
	
	/**
	 * Check whether this QueryType is a view type
	 * @return true for views
	 */
	public boolean isView()
		{
		return (view);
		}
	}
