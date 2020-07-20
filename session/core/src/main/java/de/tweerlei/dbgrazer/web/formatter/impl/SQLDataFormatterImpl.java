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
 * Format for usage in SQL statements
 * 
 * @author Robert Wruck
 */
public class SQLDataFormatterImpl extends AbstractDataFormatter
	{
	// Formats for generating SQL literals
	private static final String SQL_INTEGER_FORMAT = "0";
	private static final String SQL_FLOAT_FORMAT = "0.00######";
	
	private final boolean hasBoolean;
	
	/**
	 * Constructor
	 * @param tsfmt Timestamp date format
	 * @param ldfmt Long date format
	 * @param sdfmt Short date format
	 * @param hasBoolean Whether to use boolean literals
	 * @param tz Time zone
	 * @param sizeLimit Size limit (characters) for formatted output
	 */
	public SQLDataFormatterImpl(String tsfmt, String ldfmt, String sdfmt, boolean hasBoolean, TimeZone tz, int sizeLimit)
		{
		super(tsfmt, ldfmt, sdfmt, SQL_INTEGER_FORMAT, SQL_FLOAT_FORMAT, Locale.US, tz, sizeLimit);
		this.hasBoolean = hasBoolean;
		}
	
	@Override
	protected String formatNull()
		{
		return ("NULL");
		}
	
	@Override
	protected String formatEmptyString()
		{
		return ("''");
		}
	
	@Override
	protected String formatNonemptyString(String s)
		{
		return ("'" + s.replace("'", "''") + "'");
		}
	
	@Override
	protected String formatPassword(String value)
		{
		return ("");
		}
	
	@Override
	protected String formatBoolean(boolean b)
		{
		if (hasBoolean)
			return (b ? "true" : "false");
		else
			return (b ? "1" : "0");
		}
	
	@Override
	protected String formatException(Object value, Exception e)
		{
		return ("NULL /* " + value.getClass().getName() + ": " + e.getMessage() + " */");
		}
	
	@Override
	protected String formatBinary(byte[] data, int size)
		{
		return ("NULL /* " + size + " binary bytes */");
		}
	
	@Override
	protected String formatSpecial(String s)
		{
		return ("NULL /* " + s + " */");
		}
	}
