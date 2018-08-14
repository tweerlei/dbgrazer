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

import de.tweerlei.dbgrazer.query.model.DMLProgressMonitor;

/**
 * DMLProgressMonitor based on TaskProgress instances
 * 
 * @author Robert Wruck
 */
public class TaskDMLProgressMonitor implements DMLProgressMonitor
	{
	private final TaskProgress totalStatements;
	private final TaskProgress failedStatements;
	private final TaskProgress totalRows;
	private final TaskProgress committedRows;
	
	/**
	 * Constructor
	 * @param totalStatements TaskProgress
	 * @param failedStatements TaskProgress
	 * @param totalRows TaskProgress
	 * @param committedRows TaskProgress
	 */
	public TaskDMLProgressMonitor(TaskProgress totalStatements, TaskProgress failedStatements, TaskProgress totalRows, TaskProgress committedRows)
		{
		this.totalStatements = totalStatements;
		this.failedStatements = failedStatements;
		this.totalRows = totalRows;
		this.committedRows = committedRows;
		}
	
	/**
	 * Constructor
	 */
	public TaskDMLProgressMonitor()
		{
		this(new TaskProgress(), new TaskProgress(), new TaskProgress(), new TaskProgress());
		}
	
	@Override
	public TaskProgress getTotalStatements()
		{
		return totalStatements;
		}
	
	@Override
	public TaskProgress getFailedStatements()
		{
		return failedStatements;
		}
	
	@Override
	public TaskProgress getTotalRows()
		{
		return totalRows;
		}
	
	@Override
	public TaskProgress getCommittedRows()
		{
		return committedRows;
		}
	}
