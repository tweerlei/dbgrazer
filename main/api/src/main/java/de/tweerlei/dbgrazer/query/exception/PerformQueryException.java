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
package de.tweerlei.dbgrazer.query.exception;

/**
 * Checked exception wrapping any database error
 * 
 * @author Robert Wruck
 */
public class PerformQueryException extends Exception
	{
	private final String queryName;
	
	/**
	 * Constructor
	 * @param queryName Failed query
	 * @param cause Causing error
	 */
	public PerformQueryException(String queryName, RuntimeException cause)
		{
		super(cause);
		
		this.queryName = queryName;
		}
	
	/**
	 * Get the queryName
	 * @return the queryName
	 */
	public String getQueryName()
		{
		return queryName;
		}
	
	@Override
	public RuntimeException getCause()
		{
		return ((RuntimeException) super.getCause());
		}
	}
