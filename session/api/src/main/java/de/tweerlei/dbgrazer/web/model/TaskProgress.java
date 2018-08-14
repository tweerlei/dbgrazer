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

import de.tweerlei.dbgrazer.query.model.CancelableProgressMonitor;

/**
 * Progress of a single task
 * 
 * @author Robert Wruck
 */
public class TaskProgress implements CancelableProgressMonitor
	{
	private long todo;
	private long done;
	private boolean cancelled;
	private CancellationListener listener;
	
	@Override
	public boolean progress(int increment)
		{
		done += increment;
		return (!cancelled);
		}

	/**
	 * Get the todo
	 * @return the todo
	 */
	public long getTodo()
		{
		return todo;
		}

	/**
	 * Set the todo
	 * @param todo the todo to set
	 */
	public void setTodo(long todo)
		{
		this.todo = todo;
		}

	/**
	 * Get the done
	 * @return the done
	 */
	public long getDone()
		{
		return done;
		}

	/**
	 * Set the done
	 * @param done the done to set
	 */
	public void setDone(long done)
		{
		this.done = done;
		}
	
	@Override
	public boolean isCancelled()
		{
		return cancelled;
		}
	
	@Override
	public void cancel()
		{
		this.cancelled = true;
		if (listener != null)
			listener.cancelled();
		}
	
	@Override
	public void addListener(CancellationListener l)
		{
		if (this.listener != null)
			throw new IllegalStateException("Only one listener is supported");
		this.listener = l;
		}
	
	@Override
	public void removeListener(CancellationListener l)
		{
		if (this.listener == l)
			this.listener = null;
		}
	}
