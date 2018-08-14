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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File name
 * 
 * @author Robert Wruck
 */
public final class Filename
	{
	private static final char EXT_SEPARATOR = '.';
	private static final Pattern PATTERN = Pattern.compile("((.*?)[:/\\\\])?(([^:/\\\\]*?)(\\.([^.:/\\\\]*))?)?");
	
	private String directory;
	private String basename;
	private String extension;
	
	/**
	 * Constructor
	 */
	public Filename()
		{
		this.basename = "";
		}
	
	/**
	 * Constructor
	 * @param f File
	 */
	public Filename(File f)
		{
		this(f.getPath());
		}
	
	/**
	 * Constructor
	 * @param fn Path
	 */
	public Filename(String fn)
		{
		final Matcher m = PATTERN.matcher(fn);
		if (m.matches())
			{
			directory = m.group(2);
			basename = m.group(4);
			extension = m.group(6);
			}
		}
	
	/**
	 * Create a File object for this filename
	 * @return File
	 */
	public File getFile()
		{
		return (new File(directory, getFilename()));
		}

	/**
	 * Get the directory
	 * @return the directory
	 */
	public String getDirectory()
		{
		return directory;
		}

	/**
	 * Set the directory
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory)
		{
		this.directory = directory;
		}

	/**
	 * Get the filename
	 * @return the filename
	 */
	public String getFilename()
		{
		final StringBuffer sb = new StringBuffer();
		if (basename != null)
			sb.append(basename);
		if (extension != null)
			{
			sb.append(EXT_SEPARATOR);
			sb.append(extension);
			}
		return sb.toString();
		}

	/**
	 * Set the filename
	 * @param filename the filename to set
	 */
	public void setFilename(String filename)
		{
		if (filename == null)
			{
			this.basename = "";
			this.extension = null;
			}
		else
			{
			final int i = filename.lastIndexOf(EXT_SEPARATOR);
			if (i < 0)
				{
				this.basename = filename;
				this.extension = null;
				}
			else
				{
				this.basename = filename.substring(0, i);
				this.extension = filename.substring(i + 1);
				}
			}
		}

	/**
	 * Get the basename
	 * @return the basename, NEVER NULL
	 */
	public String getBasename()
		{
		return basename;
		}

	/**
	 * Set the basename
	 * @param basename the basename to set
	 */
	public void setBasename(String basename)
		{
		if (basename == null)
			this.basename = "";
		else
			this.basename = basename;
		}

	/**
	 * Get the extension
	 * @return the extension
	 */
	public String getExtension()
		{
		return extension;
		}

	/**
	 * Set the extension
	 * @param extension the extension to set
	 */
	public void setExtension(String extension)
		{
		this.extension = extension;
		}
	}
