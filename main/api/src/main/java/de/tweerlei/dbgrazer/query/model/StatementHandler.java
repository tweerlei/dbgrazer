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

/**
 * Handle query statements
 * 
 * @author Robert Wruck
 */
public interface StatementHandler
	{
	/**
	 * Called before any statements
	 */
	public void startStatements();
	
	/**
	 * Handle a statement
	 * @param stmt Statement text
	 */
	public void statement(String stmt);
	
	/**
	 * Handle a comment
	 * @param comment Comment text
	 */
	public void comment(String comment);
	
	/**
	 * Called after all statements
	 */
	public void endStatements();
	
	/**
	 * Signal an error during statement production
	 * @param e RuntimeException
	 */
	public void error(RuntimeException e);
	}
