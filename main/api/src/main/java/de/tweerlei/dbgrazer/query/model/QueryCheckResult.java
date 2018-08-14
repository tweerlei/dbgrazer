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

import java.io.Serializable;

/**
 * Query check result
 * 
 * @author Robert Wruck
 */
public class QueryCheckResult implements Serializable
	{
	private final String errorKey;
	private final Object param;
	
	/**
	 * Constructor
	 * @param errorKey Error key
	 * @param param Message parameter
	 */
	public QueryCheckResult(String errorKey, Object param)
		{
		this.errorKey = errorKey;
		this.param = param;
		}
	
	/**
	 * Get the errorKey
	 * @return the errorKey
	 */
	public String getErrorKey()
		{
		return errorKey;
		}
	
	/**
	 * Get the param
	 * @return the param
	 */
	public Object getParam()
		{
		return param;
		}
	}
