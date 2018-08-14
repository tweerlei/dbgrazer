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

import java.util.Date;

/**
 * Error record
 * 
 * @author Robert Wruck
 */
public class ErrorRecord
	{
	private final Date timestamp;
	private final String key;
	private final Object[] params;
	private final Object info;
	
	/**
	 * Constructor
	 * @param timestamp Timestamp
	 * @param key Message key
	 * @param params Message parameters
	 * @param info Additional info
	 */
	public ErrorRecord(Date timestamp, String key, Object[] params, Object info)
		{
		this.timestamp = timestamp;
		this.key = key;
		this.params = params;
		this.info = info;
		}

	/**
	 * Get the timestamp
	 * @return the timestamp
	 */
	public Date getTimestamp()
		{
		return timestamp;
		}

	/**
	 * Get the key
	 * @return the key
	 */
	public String getKey()
		{
		return key;
		}

	/**
	 * Get the param
	 * @return the param
	 */
	public Object[] getParams()
		{
		return params;
		}

	/**
	 * Get the additional info
	 * @return the info
	 */
	public Object getInfo()
		{
		return info;
		}
	}
