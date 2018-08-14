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
package de.tweerlei.dbgrazer.web.session.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.security.model.User;
import de.tweerlei.dbgrazer.web.model.ErrorRecord;
import de.tweerlei.dbgrazer.web.model.PathInfo;
import de.tweerlei.dbgrazer.web.model.TaskProgress;
import de.tweerlei.dbgrazer.web.session.SchemaSettings;
import de.tweerlei.dbgrazer.web.session.UserSettings;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
@Scope("session")
public class UserSettingsImpl implements UserSettings, Serializable
	{
	private final ConcurrentMap<String, String> parameterHistory;
	// Settings per schema
	private final ConcurrentMap<String, SchemaSettings> ssettings;
	private final ConcurrentMap<String, TaskProgress> taskProgress;
	private final Collection<ErrorRecord> errors;
	
	private PathInfo sourceURL;
	private User principal;
	private boolean reload;
	private boolean editorActive;
	private boolean connEditor;
	private boolean userEditor;
	private boolean sql;
	private boolean dot;
	
	/**
	 * Constructor
	 */
	public UserSettingsImpl()
		{
		this.parameterHistory = new ConcurrentHashMap<String, String>();
		this.ssettings = new ConcurrentHashMap<String, SchemaSettings>();
		this.taskProgress = new ConcurrentHashMap<String, TaskProgress>();
		this.errors = new ConcurrentLinkedQueue<ErrorRecord>();
		}
	
	@Override
	public User getPrincipal()
		{
		return this.principal;
		}
	
	@Override
	public void setPrincipal(User principal)
		{
		this.principal = principal;
		}
	
	@Override
	public boolean isReloadEnabled()
		{
		return (reload);
		}
	
	@Override
	public void setReloadEnabled(boolean b)
		{
		reload = b;
		}

	@Override
	public boolean isEditorActive()
		{
		return (editorActive);
		}
	
	@Override
	public void setEditorActive(boolean b)
		{
		editorActive = b;
		}

	@Override
	public boolean isLinkEditorEnabled()
		{
		return (connEditor);
		}
	
	@Override
	public void setLinkEditorEnabled(boolean b)
		{
		connEditor = b;
		}

	@Override
	public boolean isUserEditorEnabled()
		{
		return (userEditor);
		}
	
	@Override
	public void setUserEditorEnabled(boolean b)
		{
		userEditor = b;
		}

	@Override
	public boolean isSqlDisplayEnabled()
		{
		return (sql);
		}
	
	@Override
	public void setSqlDisplayEnabled(boolean b)
		{
		sql = b;
		}
	
	@Override
	public boolean isDotDisplayEnabled()
		{
		return (dot);
		}
	
	@Override
	public void setDotDisplayEnabled(boolean b)
		{
		dot = b;
		}
	
	@Override
	public ConcurrentMap<String, String> getParameterHistory()
		{
		return (parameterHistory);
		}
	
	@Override
	public PathInfo getSourceURL()
		{
		return sourceURL;
		}
	
	@Override
	public void setSourceURL(PathInfo sourceURL)
		{
		this.sourceURL = sourceURL;
		}
	
	@Override
	public ConcurrentMap<String, SchemaSettings> getSchemaSettings()
		{
		return (ssettings);
		}
	
	@Override
	public ConcurrentMap<String, TaskProgress> getTaskProgress()
		{
		return (taskProgress);
		}
	
	@Override
	public Collection<ErrorRecord> getErrors()
		{
		return (errors);
		}
	
	@Override
	public Collection<ErrorRecord> getErrorsForDisplay()
		{
		return (getErrorsForDisplay(false));
		}
	
	@Override
	public Collection<ErrorRecord> getObjectErrorsForDisplay()
		{
		return (getErrorsForDisplay(true));
		}
	
	private Collection<ErrorRecord> getErrorsForDisplay(boolean withObject)
		{
		if (errors.isEmpty())
			return (Collections.emptyList());
		
		final Collection<ErrorRecord> ret = new ArrayList<ErrorRecord>(errors.size());
		
		for (Iterator<ErrorRecord> it = errors.iterator(); it.hasNext(); )
			{
			final ErrorRecord rec = it.next();
			if (withObject == (rec.getInfo() != null))
				{
				ret.add(rec);
				it.remove();
				}
			}
		
		return (ret);
		}
	}
