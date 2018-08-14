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
package de.tweerlei.common.util;

/**
 * Thread that interrupts another Thread after a given timeout unless interrupted itself
 * 
 * @author Robert Wruck
 */
public class InterrupterThread extends Thread
	{
	private final Thread thread;
	private final long timeout;
	private boolean cancelled;
	private boolean done;
	
	/**
	 * Constructor
	 * @param thread Thread
	 * @param t Timeout in millis
	 */
	public InterrupterThread(Thread thread, long t)
		{
		super("InterrupterThread for " + thread.getName());
		
		this.thread = thread;
		this.timeout = t;
		this.cancelled = false;
		this.done = false;
		}
	
	/**
	 * Prevent this InterrupterThread from interrupting its target thread.
	 * It still has to be joined afterwards.
	 */
	public synchronized void cancel()
		{
		if (!cancelled)
			{
			cancelled = true;
			if (!done)
				{
				// doInterrupt not called yet: interrupt our own sleep
				interrupt();
				}
			else
				{
				// doInterrupt already called: if the calling thread was the target, clear the interrupted status
				// so a subsequent join() will not throw an InterruptedException
				if (currentThread() == thread)
					interrupted();
				}
			}
		}
	
	private synchronized void doInterrupt()
		{
		if (!cancelled)
			{
			thread.interrupt();
			done = true;
			}
		}
	
	public void run()
		{
		try	{
			sleep(timeout);
			}
		catch (InterruptedException e)
			{
			// cancelled by caller
			return;
			}
		
		doInterrupt();
		}
	}
