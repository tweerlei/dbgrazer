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
package de.tweerlei.dbgrazer.web.formatter.impl;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Format for usage in XML files
 * 
 * @author Robert Wruck
 */
public class XMLDataFormatterImpl extends AbstractDataFormatter
	{
	// Formats for generating DBUnit XML
	private static final String XML_TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String XML_LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String XML_SHORT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String XML_INTEGER_FORMAT = "0";
	private static final String XML_FLOAT_FORMAT = "0.00######";
	
	/**
	 * Constructor
	 * @param tz Time zone
	 * @param sizeLimit Size limit (characters) for formatted output
	 */
	public XMLDataFormatterImpl(TimeZone tz, int sizeLimit)
		{
		super(XML_TIMESTAMP_DATE_FORMAT, XML_LONG_DATE_FORMAT, XML_SHORT_DATE_FORMAT, XML_INTEGER_FORMAT, XML_FLOAT_FORMAT, Locale.US, tz, sizeLimit);
		}
	
	@Override
	protected String formatNull()
		{
		return ("[NULL]");
		}
	
	@Override
	protected String formatEmptyString()
		{
		return ("");
		}
	
	@Override
	protected String formatNonemptyString(String s)
		{
		return (s);
		}
	
	@Override
	protected String formatPassword(String value)
		{
		return ("");
		}
	
	@Override
	protected String formatBoolean(boolean b)
		{
		return (b ? "true" : "false");
		}
	
	@Override
	protected String formatException(Object value, Exception e)
		{
		return ("<!-- " + value.getClass().getName() + ": " + e.getMessage() + " -->");
		}
	
	@Override
	protected String formatBinary(byte[] data, int size)
		{
		return ("<!-- " + size + " binary bytes -->");
		}
	
	@Override
	protected String formatSpecial(String s)
		{
		return ("<!-- " + s + " -->");
		}
	}
