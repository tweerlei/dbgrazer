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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tweerlei.common.io.CopyStreamReader;
import de.tweerlei.common.io.CopyStreamWriter;
import de.tweerlei.common.io.NullStream;
import de.tweerlei.common.io.StreamReader;
import de.tweerlei.common.io.StreamUtils;
import de.tweerlei.common.io.StreamWriter;

/**
 * Hilfsfunktionen für Prozesse
 * 
 * @author Robert Wruck
 */
public class ProcessUtils
	{
	private static final class WriterThread extends Thread
		{
		private final StreamWriter w;
		private final OutputStream out;
		private IOException lastError;
		
		public WriterThread(StreamWriter w, OutputStream out)
			{
			this.w = w;
			this.out = out;
			}
		
		public IOException getLastError()
			{
			return (lastError);
			}
		
		public void run()
			{
			try	{
				w.write(out);
				}
			catch (IOException e)
				{
				lastError = e;
				}
			finally
				{
				StreamUtils.closeQuietly(out);
				}
			}
		}
	
	private static final class ReaderThread extends Thread
		{
		private final StreamReader r;
		private final InputStream in;
		private IOException lastError;
		
		public ReaderThread(InputStream in, StreamReader r)
			{
			this.r = r;
			this.in = in;
			}
		
		public IOException getLastError()
			{
			return (lastError);
			}
		
		public void run()
			{
			try	{
				r.read(in);
				}
			catch (IOException e)
				{
				lastError = e;
				}
			finally
				{
				StreamUtils.closeQuietly(in);
				}
			}
		}

	private ProcessUtils()
		{
		}
	
	/**
	 * Run an external program and handle I/O asynchronously
	 * @param cmdline Kommandozeile
	 * @param env Umgebungsvariablen (kann null sein)
	 * @param dir Startverzeichnis (kann null sein)
	 * @param stdin InputStream for STDIN
	 * @param stdout OutputStream for STDOUT
	 * @param stderr OutputStream for STDERR
	 * @param timeout Process runtime limit
	 * @return Return code
	 * @throws IOException on error
	 */
	public static int exec(String[] cmdline, String[] env, File dir, InputStream stdin, OutputStream stdout, OutputStream stderr, int timeout) throws IOException
		{
		return (exec(cmdline, env, dir,
				(stdin == null) ? null : new CopyStreamWriter(stdin),
				(stdout == null) ? null : new CopyStreamReader(stdout),
				(stderr == null) ? null : new CopyStreamReader(stderr),
				timeout));
		}
	
	/**
	 * Run an external program and handle I/O asynchronously
	 * @param cmdline Kommandozeile
	 * @param env Umgebungsvariablen (kann null sein)
	 * @param dir Startverzeichnis (kann null sein)
	 * @param stdin StreamWriter for STDIN
	 * @param stdout StreamReader for STDOUT
	 * @param stderr StreamReader for STDERR
	 * @param timeout Process runtime limit
	 * @return Return code
	 * @throws IOException on error
	 */
	public static int exec(String[] cmdline, String[] env, File dir, StreamWriter stdin, StreamReader stdout, StreamReader stderr, int timeout) throws IOException
		{
		final Process p = Runtime.getRuntime().exec(cmdline, env, dir);
		final OutputStream pin = p.getOutputStream();
		final InputStream pout = p.getInputStream();
		final InputStream perr = p.getErrorStream();
		
		final WriterThread inThread;
		if (stdin != null)
			{
			inThread = new WriterThread(stdin, pin);
			inThread.start();
			}
		else
			{
			// Usually, the input stream must be closed so the target process gets an EOF
			inThread = null;
			StreamUtils.closeQuietly(pin);
			}
		
		final ReaderThread outThread;
		if (stdout != null)
			outThread = new ReaderThread(pout, stdout);
		else
			outThread = new ReaderThread(pout, new CopyStreamReader(new NullStream()));
		outThread.start();
		
		final ReaderThread errThread;
		if (stderr != null)
			errThread = new ReaderThread(perr, stderr);
		else
			errThread = new ReaderThread(perr, new CopyStreamReader(new NullStream()));
		errThread.start();
		
		final InterrupterThread watcherThread;
		if (timeout > 0)
			{
			watcherThread = new InterrupterThread(Thread.currentThread(), timeout);
			watcherThread.start();
			}
		else
			watcherThread = null;
		
		try	{
			return (p.waitFor());
			}
		catch (InterruptedException e)
			{
			// Java Bug: waitFor() throws InterruptedException but leaves the current thread in interrupted state,
			// so clear it explicitly
			Thread.interrupted();
			
			p.destroy();
			
			if (inThread != null)
				inThread.interrupt();
			outThread.interrupt();
			errThread.interrupt();
			
			throw new IOException("Interrupted while waiting for child process");
			}
		finally
			{
			try	{
				if (watcherThread != null)
					{
					watcherThread.cancel();
					watcherThread.join();
					}
				
				if (inThread != null)
					inThread.join();
				outThread.join();
				errThread.join();
				}
			catch (InterruptedException e)
				{
				throw new IOException("Interrupted while waiting for child process data");
				}
			
			if ((inThread != null) && (inThread.getLastError() != null))
				throw inThread.getLastError();
			if (outThread.getLastError() != null)
				throw outThread.getLastError();
			if (errThread.getLastError() != null)
				throw errThread.getLastError();
			}
		}
	}
