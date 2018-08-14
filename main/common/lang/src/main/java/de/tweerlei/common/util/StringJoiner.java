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
 * Helper class for concatenating strings with separators
 * 
 * @author Robert Wruck
 */
public class StringJoiner
	{
	private final StringBuffer sb;
	private final String separator;
	
	/**
	 * Constructor
	 * @param separator Separator String
	 */
	public StringJoiner(String separator)
		{
		this.sb = new StringBuffer();
		this.separator = separator;
		}
	
	/**
	 * Append a String (does nothing if the passed String is null)
	 * @param s String
	 * @return this
	 */
	public StringJoiner append(String s)
		{
		if (!StringUtils.empty(s))
			{			
			if (sb.length() > 0)
				sb.append(separator);
			
			sb.append(s);
			}
		
		return (this);
		}
	
	/**
	 * Get the length of the resulting String
	 * @return Length
	 */
	public int length()
		{
		return (sb.length());
		}
	
	public String toString()
		{
		return (sb.toString());
		}
	}
