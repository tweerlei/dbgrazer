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

import java.util.Iterator;

import de.tweerlei.dbgrazer.query.model.StatementIterator;

/**
 * Iterate over statements produced by another thread
 * 
 * @author Robert Wruck
 */
public class StatementCollectionIterator implements StatementIterator
	{
	private final StatementCollection collection;
	private final Iterator<String> it;
	
	/**
	 * Constructor
	 * @param collection StatementCollection
	 * @param it Iterator
	 */
	public StatementCollectionIterator(StatementCollection collection, Iterator<String> it)
		{
		this.collection = collection;
		this.it = it;
		}
	
	@Override
	public String getPrepareStatement()
		{
		return (collection.getPrepareStatement());
		}
	
	@Override
	public String getCleanupStatement()
		{
		return (collection.getCleanupStatement());
		}
	
	@Override
	public void abort()
		{
		}
	
	@Override
	public boolean hasNext()
		{
		return (it.hasNext());
		}
	
	@Override
	public String next()
		{
		return (it.next());
		}
	
	@Override
	public void remove()
		{
		it.remove();
		}
	}
