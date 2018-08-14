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
package de.tweerlei.dbgrazer.query.model.impl;

import java.util.ArrayList;
import java.util.List;

import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.query.model.StatementProducer;

/**
 * Iterate over statements produced by another thread
 * 
 * @author Robert Wruck
 */
public class StatementCollection implements StatementHandler, Iterable<String>, StatementProducer
	{
	private final List<String> queue;
	private final String prepare;
	private final String cleanup;
	private RuntimeException lastError;
	
	/**
	 * Constructor
	 * @param prepare Prepate statement
	 * @param cleanup Cleanup statement
	 */
	public StatementCollection(String prepare, String cleanup)
		{
		this.queue = new ArrayList<String>();
		this.prepare = prepare;
		this.cleanup = cleanup;
		}
	
	/*
	 * Producer side
	 */
	
	@Override
	public void startStatements()
		{
		}
	
	@Override
	public void statement(String stmt)
		{
		queue.add(stmt);
		}
	
	@Override
	public void comment(String comment)
		{
		// ignore comments
		}
	
	@Override
	public void endStatements()
		{
		}
	
	@Override
	public void error(RuntimeException e)
		{
		lastError = e;
		}
	
	/*
	 * Consumer side
	 */
	
	@Override
	public String getPrepareStatement()
		{
		if (lastError != null)
			throw lastError;
		
		return (prepare);
		}
	
	@Override
	public String getCleanupStatement()
		{
		if (lastError != null)
			throw lastError;
		
		return (cleanup);
		}
	
	@Override
	public StatementCollectionIterator iterator()
		{
		if (lastError != null)
			throw lastError;
		
		return (new StatementCollectionIterator(this, queue.iterator()));
		}
	
	@Override
	public int produceStatements(StatementHandler h)
		{
		if (lastError != null)
			throw lastError;
		
		int i = 0;
		
		for (String stmt : queue)
			{
			h.statement(stmt);
			i++;
			}
		
		return (i);
		}
	}
