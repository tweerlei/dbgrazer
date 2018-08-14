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

/**
 * CompareProgressMonitor that uses a TaskProgress for each aspect
 * 
 * @author Robert Wruck
 */
public class TaskCompareProgressMonitor implements CompareProgressMonitor
	{
	private final TaskProgress sourceRows;
	private final TaskProgress destinationRows;
	private final TaskProgress matchedRows;
	private final TaskProgress insertedRows;
	private final TaskProgress updatedRows;
	private final TaskProgress deletedRows;
	
	/**
	 * Constrcutr
	 * @param sourceRows TaskProgress
	 * @param destinationRows TaskProgress
	 * @param matchedRows TaskProgress
	 * @param insertedRows TaskProgress
	 * @param updatedRows TaskProgress
	 * @param deletedRows TaskProgress
	 */
	public TaskCompareProgressMonitor(TaskProgress sourceRows, TaskProgress destinationRows, TaskProgress matchedRows, TaskProgress insertedRows, TaskProgress updatedRows, TaskProgress deletedRows)
		{
		this.sourceRows = sourceRows;
		this.destinationRows = destinationRows;
		this.matchedRows = matchedRows;
		this.insertedRows = insertedRows;
		this.updatedRows = updatedRows;
		this.deletedRows = deletedRows;
		}
	
	/**
	 * Constructor
	 */
	public TaskCompareProgressMonitor()
		{
		this(new TaskProgress(), new TaskProgress(), new TaskProgress(), new TaskProgress(), new TaskProgress(), new TaskProgress());
		}
	
	@Override
	public TaskProgress getSourceRows()
		{
		return sourceRows;
		}
	
	@Override
	public TaskProgress getDestinationRows()
		{
		return destinationRows;
		}
	
	@Override
	public TaskProgress getMatchedRows()
		{
		return matchedRows;
		}
	
	@Override
	public TaskProgress getInsertedRows()
		{
		return insertedRows;
		}
	
	@Override
	public TaskProgress getUpdatedRows()
		{
		return updatedRows;
		}
	
	@Override
	public TaskProgress getDeletedRows()
		{
		return deletedRows;
		}
	}
