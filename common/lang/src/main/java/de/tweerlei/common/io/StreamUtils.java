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
package de.tweerlei.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import de.tweerlei.common.util.ProgressMonitor;

/**
 * Hilfsklassen für Streams
 * 
 * @author Robert Wruck
 */
public final class StreamUtils
	{
	private static final int BUFFERSIZE = 65536;
	
	/** Unerreichbarer Konstruktor */
	private StreamUtils()
		{
		}
	
	/**
	 * Read data from an InputStream (InputStream.read is not guaranteed to read all available data)
	 * @param in InputStream
	 * @param data Destination buffer
	 * @param offset Starting offset into destination buffer
	 * @param length Number of bytes to read
	 * @return Number of bytes read, will be less than length ONLY if EOF was reached
	 * @throws IOException on errors
	 */
	public static int read(InputStream in, byte[] data, int offset, int length) throws IOException
		{
		int off = offset;
		int len = length;
		int total = 0;
		
		while (total < length)
			{
			final int r = in.read(data, off, len);
			if (r < 0)
				break;
			total += r;
			off += r;
			len -= r;
			}
		
		return (total);
		}
	
	/**
	 * Read data from an InputStream (shortcut for read(in, data, 0, data.length))
	 * @param in InputStream
	 * @param data Destination buffer
	 * @return Number of bytes read, will be less than length ONLY if EOF was reached
	 * @throws IOException on errors
	 */
	public static int read(InputStream in, byte[] data) throws IOException
		{
		return (read(in, data, 0, data.length));
		}
	
	/**
	 * Read data from a Reader (Reader.read is not guaranteed to read all available data)
	 * @param in InputStream
	 * @param data Destination buffer
	 * @param offset Starting offset into destination buffer
	 * @param length Number of bytes to read
	 * @return Number of bytes read, will be less than length ONLY if EOF was reached
	 * @throws IOException on errors
	 */
	public static int read(Reader in, char[] data, int offset, int length) throws IOException
		{
		int off = offset;
		int len = length;
		int total = 0;
		
		while (total < length)
			{
			final int r = in.read(data, off, len);
			if (r < 0)
				break;
			total += r;
			off += r;
			len -= r;
			}
		
		return (total);
		}
	
	/**
	 * Read data from a Reader (shortcut for read(in, data, 0, data.length))
	 * @param in InputStream
	 * @param data Destination buffer
	 * @return Number of bytes read, will be less than length ONLY if EOF was reached
	 * @throws IOException on errors
	 */
	public static int read(Reader in, char[] data) throws IOException
		{
		return (read(in, data, 0, data.length));
		}
	
	/**
	 * Copy data from an InputStream to an OutputStream
	 * @param in InputStream
	 * @param out OutputStream
	 * @return Number of copied bytes
	 * @throws IOException on errors
	 */
	public static long copy(InputStream in, OutputStream out) throws IOException
		{
		final byte[] buffer = new byte[BUFFERSIZE];
		
		long ret;
		int len;
		for (ret = 0; ; ret += len)
			{
			len = in.read(buffer);
			if (len < 0)
				break;
			out.write(buffer, 0, len);
			}
		
		return (ret);
		}
	
	/**
	 * Copy data from an InputStream to an OutputStream
	 * @param in InputStream
	 * @param out OutputStream
	 * @param max Max. number of bytes to copy
	 * @param mon ProgressMonitor
	 * @return Number of copied bytes
	 * @throws IOException on errors
	 */
	public static long copy(InputStream in, OutputStream out, long max, ProgressMonitor mon) throws IOException
		{
		final byte[] buffer = new byte[BUFFERSIZE];
		
		long ret;
		int len;
		for (ret = 0; ret < max; ret += len)
			{
			if ((max - ret) >= BUFFERSIZE)
				len = in.read(buffer);
			else
				len = in.read(buffer, 0, (int) (max - ret));
			if (len < 0)
				break;
			out.write(buffer, 0, len);
			if ((mon != null) && !mon.progress(len))
				break;
			}
		
		return (ret);
		}
	
	/**
	 * Copy data from a Reader to a Writer
	 * @param in Reader
	 * @param out Writer
	 * @return Number of copied chars
	 * @throws IOException on errors
	 */
	public static long copy(Reader in, Writer out) throws IOException
		{
		final char[] buffer = new char[BUFFERSIZE];
		
		long ret;
		int len;
		for (ret = 0; ; ret += len)
			{
			len = in.read(buffer);
			if (len < 0)
				break;
			out.write(buffer, 0, len);
			}
		
		return (ret);
		}
	
	/**
	 * Copy data from a Reader to a Writer
	 * @param in Reader
	 * @param out Writer
	 * @param max Max. number of chars to copy
	 * @param mon ProgressMonitor
	 * @return Number of copied chars
	 * @throws IOException on errors
	 */
	public static long copy(Reader in, Writer out, long max, ProgressMonitor mon) throws IOException
		{
		final char[] buffer = new char[BUFFERSIZE];
		
		long ret;
		int len;
		for (ret = 0; ret < max; ret += len)
			{
			if ((max - ret) >= BUFFERSIZE)
				len = in.read(buffer);
			else
				len = in.read(buffer, 0, (int) (max - ret));
			if (len < 0)
				break;
			out.write(buffer, 0, len);
			if ((mon != null) && !mon.progress(len))
				break;
			}
		
		return (ret);
		}
	
	/**
	 * Close an InputStream and swallow any IOException
	 * @param is InputStream
	 */
	public static void closeQuietly(InputStream is)
		{
		try	{
			if (is != null)
				is.close();
			}
		catch (IOException e)
			{
			// log it?
			}
		}
	
	/**
	 * Close an OutputStream and swallow any IOException
	 * @param os OutputStream
	 */
	public static void closeQuietly(OutputStream os)
		{
		try	{
			if (os != null)
				os.close();
			}
		catch (IOException e)
			{
			// log it?
			}
		}
	
	/**
	 * Close a Reader and swallow any IOException
	 * @param r Reader
	 */
	public static void closeQuietly(Reader r)
		{
		try	{
			if (r != null)
				r.close();
			}
		catch (IOException e)
			{
			// log it?
			}
		}
	
	/**
	 * Close a Writer and swallow any IOException
	 * @param w Writer
	 */
	public static void closeQuietly(Writer w)
		{
		try	{
			if (w != null)
				w.close();
			}
		catch (IOException e)
			{
			// log it?
			}
		}
	}
