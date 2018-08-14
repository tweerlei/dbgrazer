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
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Iterate over elements produced by another thread
 * @param <T> Element type
 * 
 * @author Robert Wruck
 */
public class AsyncQueue<T> implements Iterator<T>
	{
	private final BlockingQueue<T> queue;
	private final T nil;
	private boolean interrupted;
	private T current;
	
	/**
	 * Constructor
	 * @param nil End marker
	 */
	public AsyncQueue(T nil)
		{
		this.queue = new SynchronousQueue<T>();
		this.nil = nil;
		}
	
	/*
	 * Producer side
	 */
	
	/**
	 * Add an element
	 * @param elem Element
	 */
	public void add(T elem)
		{
		if (interrupted)
			return;
		
		try	{
			queue.put(elem);
			}
		catch (InterruptedException e)
			{
			done();
			}
		}
	
	/**
	 * Signal end of elements
	 */
	public void done()
		{
		interrupted = true;
		queue.offer(nil);	// release any waiting consumer
		}
	
	/*
	 * Consumer side
	 */
	
	private T fetch()
		{
		if (current == null)
			{
			if (interrupted)
				current = nil;
			else
				{
				try	{
					current = queue.take();
					}
				catch (InterruptedException e)
					{
					abort();
					current = nil;
					}
				}
			}
		return (current);
		}
	
	@Override
	public boolean hasNext()
		{
		return (fetch() != nil);
		}
	
	@Override
	public T next()
		{
		final T ret = fetch();
		if (ret == nil)
			throw new NoSuchElementException();
		current = null;
		return (ret);
		}
	
	@Override
	public void remove()
		{
		throw new UnsupportedOperationException();
		}
	
	/**
	 * Signal end of fetching; don't block the producer anymore
	 */
	public void abort()
		{
		interrupted = true;
		queue.poll();	// release any waiting producer
		}
	}
