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

import de.tweerlei.common.util.ProgressMonitor;

/**
 * Monitor progress of DML operations
 * 
 * @author Robert Wruck
 */
public interface DMLProgressMonitor
	{
	/**
	 * Get the total number of executed statements
	 * @return Number of statements
	 */
	public ProgressMonitor getTotalStatements();
	
	/**
	 * Get the number of executed statements that failed
	 * @return Number of statements
	 */
	public ProgressMonitor getFailedStatements();
	
	/**
	 * Get the total number of rows affected by successfully executed statements
	 * @return Number of rows
	 */
	public ProgressMonitor getTotalRows();
	
	/**
	 * Get the number of affected rows that have already been committed
	 * @return Number of rows
	 */
	public ProgressMonitor getCommittedRows();
	}
