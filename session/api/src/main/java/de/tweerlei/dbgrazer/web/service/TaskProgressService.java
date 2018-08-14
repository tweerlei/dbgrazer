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
package de.tweerlei.dbgrazer.web.service;

import java.util.SortedMap;

import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskDMLProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskProgress;

/**
 * Monitor task progress
 *
 * @author Robert Wruck
 */
public interface TaskProgressService
	{
	/**
	 * Set the TaskProgress
	 * @param task Task name
	 * @return New TaskProgress or null if there is already a TaskProgress with the same task name set
	 */
	public TaskProgress createTaskProgress(String task);
	
	/**
	 * Get the current TaskProgress for a task
	 * @param task Task name
	 * @return TaskProgress or null
	 */
	public TaskProgress getTaskProgress(String task);
	
	/**
	 * Remove the TaskProgress for a task
	 * @param task Task name
	 * @return TaskProgress or null
	 */
	public TaskProgress removeTaskProgress(String task);
	
	/**
	 * Get all progress entries, sorted by task
	 * @return Map: Task name -> Progress value
	 */
	public SortedMap<String, TaskProgress> getProgress();
	
	/**
	 * Convenience method to create a DMLProgressMonitor based on TaskProgress instances
	 * @return DMLProgressMonitor or null if another DMLProgressMonitor is already active
	 */
	public TaskDMLProgressMonitor createDMLProgressMonitor();
	
	/**
	 * Convenience method to remove all TaskProgress instances used by a DMLProgressMonitor
	 */
	public void removeDMLProgressMonitor();
	
	/**
	 * Convenience method to create a CompareProgressMonitor based on TaskProgress instances
	 * @return CompareProgressMonitor or null if another CompareProgressMonitor is already active
	 */
	public TaskCompareProgressMonitor createCompareProgressMonitor();
	
	/**
	 * Convenience method to remove all TaskProgress instances used by a CompareProgressMonitor
	 */
	public void removeCompareProgressMonitor();
	}
