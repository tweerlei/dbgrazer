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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.model.TaskCompareProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskDMLProgressMonitor;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.service.TaskProgressService;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Monitor task progress
 *
 * @author Robert Wruck
 */
@Service
public class TaskProgressServiceImpl implements TaskProgressService
	{
	private final UserSettings userSettings;
	
	/**
	 * Constructor
	 * @param userSettings UserSettings
	 */
	@Autowired
	public TaskProgressServiceImpl(UserSettings userSettings)
		{
		this.userSettings = userSettings;
		}
	
	@Override
	public TaskProgress createTaskProgress(String task)
		{
		final TaskProgress p = new TaskProgress();
		
		if (userSettings.getTaskProgress().putIfAbsent(task, p) == null)
			return (p);
		else
			return (null);
		}
	
	@Override
	public TaskProgress getTaskProgress(String task)
		{
		return (userSettings.getTaskProgress().get(task));
		}
	
	@Override
	public TaskProgress removeTaskProgress(String task)
		{
		return (userSettings.getTaskProgress().remove(task));
		}
	
	@Override
	public SortedMap<String, TaskProgress> getProgress()
		{
		SortedMap<String, TaskProgress> ret = new TreeMap<String, TaskProgress>();
		for (Map.Entry<String, TaskProgress> ent : userSettings.getTaskProgress().entrySet())
			ret.put(ent.getKey(), ent.getValue());
		return (ret);
		}
	
	@Override
	public TaskDMLProgressMonitor createDMLProgressMonitor()
		{
		// Synchronize on TOTAL_STATEMENTS only
		final TaskProgress p = createTaskProgress(MessageKeys.TOTAL_STATEMENTS);
		if (p == null)
			return (null);
		
		createTaskProgress(MessageKeys.FAILED_STATEMENTS);
		createTaskProgress(MessageKeys.TOTAL_ROWS);
		createTaskProgress(MessageKeys.COMMITTED_ROWS);
		
		return (new TaskDMLProgressMonitor(p,
				getTaskProgress(MessageKeys.FAILED_STATEMENTS),
				getTaskProgress(MessageKeys.TOTAL_ROWS),
				getTaskProgress(MessageKeys.COMMITTED_ROWS)));
		}
	
	@Override
	public void removeDMLProgressMonitor()
		{
		removeTaskProgress(MessageKeys.COMMITTED_ROWS);
		removeTaskProgress(MessageKeys.TOTAL_ROWS);
		removeTaskProgress(MessageKeys.FAILED_STATEMENTS);
		
		// Synchronize on TOTAL_STATEMENTS only
		removeTaskProgress(MessageKeys.TOTAL_STATEMENTS);
		}
	
	@Override
	public TaskCompareProgressMonitor createCompareProgressMonitor()
		{
		// Synchronize on SOURCE_ROWS only
		final TaskProgress p = createTaskProgress(MessageKeys.SOURCE_ROWS);
		if (p == null)
			return (null);
		
		createTaskProgress(MessageKeys.DESTINATION_ROWS);
		createTaskProgress(MessageKeys.MATCHED);
		createTaskProgress(MessageKeys.INSERTED);
		createTaskProgress(MessageKeys.UPDATED);
		createTaskProgress(MessageKeys.DELETED);
		
		return (new TaskCompareProgressMonitor(
				getTaskProgress(MessageKeys.SOURCE_ROWS),
				getTaskProgress(MessageKeys.DESTINATION_ROWS),
				getTaskProgress(MessageKeys.MATCHED),
				getTaskProgress(MessageKeys.INSERTED),
				getTaskProgress(MessageKeys.UPDATED),
				getTaskProgress(MessageKeys.DELETED)
				));
		}
	
	@Override
	public void removeCompareProgressMonitor()
		{
		removeTaskProgress(MessageKeys.DESTINATION_ROWS);
		removeTaskProgress(MessageKeys.MATCHED);
		removeTaskProgress(MessageKeys.INSERTED);
		removeTaskProgress(MessageKeys.UPDATED);
		removeTaskProgress(MessageKeys.DELETED);
		
		// Synchronize on SOURCE_ROWS only
		removeTaskProgress(MessageKeys.SOURCE_ROWS);
		}
	}
