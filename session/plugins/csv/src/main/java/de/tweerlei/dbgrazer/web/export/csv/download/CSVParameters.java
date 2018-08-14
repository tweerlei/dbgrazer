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
package de.tweerlei.dbgrazer.web.export.csv.download;

/**
 * CSV format parameters
 * 
 * @author Robert Wruck
 */
public class CSVParameters
	{
	private final String fieldSeparator;
	private final String textDelimiter;
	private final String delimiterEscape;
	private final String recordSeparator;
	
	/**
	 * Constructor
	 * @param fieldSeparator String for separating Fields
	 * @param textDelimiter String for delimiting text fields
	 * @param delimiterEscape String for escaping the textDelimiter inside text fields
	 * @param recordSeparator Line terminator
	 */
	public CSVParameters(String fieldSeparator, String textDelimiter, String delimiterEscape, String recordSeparator)
		{
		this.fieldSeparator = fieldSeparator;
		this.textDelimiter = textDelimiter;
		this.delimiterEscape = delimiterEscape;
		this.recordSeparator = recordSeparator;
		}
	
	/**
	 * @return the fieldSeparator
	 */
	public String getFieldSeparator()
		{
		return fieldSeparator;
		}

	/**
	 * @return the textDelimiter
	 */
	public String getTextDelimiter()
		{
		return textDelimiter;
		}
	
	/**
	 * @return the delimiterEscape
	 */
	public String getDelimiterEscape()
		{
		return delimiterEscape;
		}
	
	/**
	 * @return the eol
	 */
	public String getRecordSeparator()
		{
		return recordSeparator;
		}
	}
