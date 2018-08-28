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
package de.tweerlei.dbgrazer.web.session;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.web.model.ErrorRecord;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.model.TaskProgress;

/**
 * User settings for the current session
 * 
 * @author Robert Wruck
 */
public interface UserSettings
	{
	/**
	 * Get the principal, null if not logged in
	 * @return Principal
	 */
	public User getPrincipal();
	
	/**
	 * Set the principal
	 * @param principal User
	 */
	public void setPrincipal(User principal);
	
	/**
	 * Check whether configuration reloading is enabled
	 * @return Reload enabled
	 */
	public boolean isReloadEnabled();
	
	/**
	 * Set configuration reloading enabled
	 * @param b Reload enabled
	 */
	public void setReloadEnabled(boolean b);
	
	/**
	 * Check whether the editor is active
	 * @return Editor active
	 */
	public boolean isEditorActive();
	
	/**
	 * Set editor active
	 * @param b Editor active
	 */
	public void setEditorActive(boolean b);
	
	/**
	 * Check whether the editor is enabled
	 * @return Editor enabled
	 */
	public boolean isLinkEditorEnabled();
	
	/**
	 * Set editor enabled
	 * @param b Editor enabled
	 */
	public void setLinkEditorEnabled(boolean b);
	
	/**
	 * Check whether the editor is enabled
	 * @return Editor enabled
	 */
	public boolean isUserEditorEnabled();
	
	/**
	 * Set editor enabled
	 * @param b Editor enabled
	 */
	public void setUserEditorEnabled(boolean b);
	
	/**
	 * Check whether the editor is enabled
	 * @return Editor enabled
	 */
	public boolean isConfigEditorEnabled();
	
	/**
	 * Set editor enabled
	 * @param b Editor enabled
	 */
	public void setConfigEditorEnabled(boolean b);
	
	/**
	 * Check whether the SQL display is enabled
	 * @return SQL enabled
	 */
	public boolean isSqlDisplayEnabled();
	
	/**
	 * Set SQL enabled
	 * @param b SQL enabled
	 */
	public void setSqlDisplayEnabled(boolean b);
	
	/**
	 * Check whether the DOT display is enabled
	 * @return DOT enabled
	 */
	public boolean isDotDisplayEnabled();
	
	/**
	 * Set DOT enabled
	 * @param b DOT enabled
	 */
	public void setDotDisplayEnabled(boolean b);
	
	/**
	 * Get recently used parameter values
	 * @return Map: name -> value
	 */
	public ConcurrentMap<String, String> getParameterHistory();
	
	/**
	 * Get the source URL
	 * @return Source URL
	 */
	public PathInfo getSourceURL();
	
	/**
	 * Set the source URL
	 * @param sourceURL Source URL
	 */
	public void setSourceURL(PathInfo sourceURL);
	
	/**
	 * Get the SchemaSettings
	 * @return Map: Schema name -> SchemaSettings
	 */
	public ConcurrentMap<String, SchemaSettings> getSchemaSettings();
	
	/**
	 * Get the progress of all tasks
	 * @return Map: Task name -> TaskProgress
	 */
	public ConcurrentMap<String, TaskProgress> getTaskProgress();
	
	/**
	 * Get the logged errors
	 * @return ErrorRecords
	 */
	public Collection<ErrorRecord> getErrors();
	
	/**
	 * Get AND RESET the logged errors
	 * @return ErrorRecords
	 */
	public Collection<ErrorRecord> getErrorsForDisplay();
	
	/**
	 * Get AND RESET the logged object errors
	 * @return ErrorRecords
	 */
	public Collection<ErrorRecord> getObjectErrorsForDisplay();
	}
