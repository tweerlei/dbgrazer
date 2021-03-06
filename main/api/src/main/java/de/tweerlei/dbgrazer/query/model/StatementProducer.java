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
 * Produce statements and pass them to a StatementHandler
 * 
 * @author Robert Wruck
 */
public interface StatementProducer
	{
	/**
	 * Produce statements and pass them to a StatementHandler
	 * @param h StatementHandler
	 */
	public void produceStatements(StatementHandler h);
	
	/**
	 * Get the prepare statement that will be executed prior to all statements returned from the iterator
	 * @return Prepare statement or null
	 */
	public String getPrepareStatement();
	
	/**
	 * Get the cleanup statement that will be executed after all statements returned from the iterator and in case of an error
	 * @return Cleanup statement or null
	 */
	public String getCleanupStatement();
	}
