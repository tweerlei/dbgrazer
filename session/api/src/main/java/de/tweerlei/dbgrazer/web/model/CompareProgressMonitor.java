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

import de.tweerlei.common.util.ProgressMonitor;

/**
 * Monitor comparison progress
 * 
 * @author Robert Wruck
 */
public interface CompareProgressMonitor
	{
	/**
	 * Get the number of rows from the source table
	 * @return Number of rows
	 */
	public ProgressMonitor getSourceRows();
	
	/**
	 * Get the number of rows from the destination table
	 * @return Number of rows
	 */
	public ProgressMonitor getDestinationRows();
	
	/**
	 * Get the number of matched rows
	 * @return Number of rows
	 */
	public ProgressMonitor getMatchedRows();
	
	/**
	 * Get the number of inserted rows
	 * @return Number of rows
	 */
	public ProgressMonitor getInsertedRows();
	
	/**
	 * Get the number of updated rows
	 * @return Number of rows
	 */
	public ProgressMonitor getUpdatedRows();
	
	/**
	 * Get the number of deleted rows
	 * @return Number of rows
	 */
	public ProgressMonitor getDeletedRows();
	}
