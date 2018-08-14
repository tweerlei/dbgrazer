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
package de.tweerlei.dbgrazer.common.file;

import java.util.Date;

/**
 * History entry
 * 
 * @author Robert Wruck
 */
public class HistoryEntry implements Comparable<HistoryEntry>
	{
	private final String version;
	private final Date date;
	private final String message;
	
	/**
	 * Constructor
	 * @param version Version
	 * @param date Date
	 * @param message Message
	 */
	public HistoryEntry(String version, Date date, String message)
		{
		this.version = version;
		this.date = date;
		this.message = message;
		}

	/**
	 * Get the version
	 * @return the version
	 */
	public String getVersion()
		{
		return version;
		}

	/**
	 * Get the date
	 * @return the date
	 */
	public Date getDate()
		{
		return date;
		}

	/**
	 * Get the message
	 * @return the message
	 */
	public String getMessage()
		{
		return message;
		}

	@Override
	public int compareTo(HistoryEntry o)
		{
		return (o.date.compareTo(date));
		}
	}
