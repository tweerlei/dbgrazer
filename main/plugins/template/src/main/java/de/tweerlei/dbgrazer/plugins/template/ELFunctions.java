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
package de.tweerlei.dbgrazer.plugins.template;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Functions available in the EL context used by TemplateHandler
 * 
 * @author Robert Wruck
 */
public class ELFunctions
	{
	private final Map<String, DateFormat> formatCache;
	
	/**
	 * Constructor
	 */
	public ELFunctions()
		{
		this.formatCache = new HashMap<String, DateFormat>();
		}
	
	/**
	 * Format an Object using String.format
	 * @param fmt Format string
	 * @param arg Object
	 * @return Formatted String or null
	 */
	public String format(String fmt, Object arg)
		{
		try	{
			return (String.format(Locale.US, fmt, arg));
			}
		catch (IllegalFormatException e)
			{
			return (null);
			}
		}
	
	/**
	 * Format a Date
	 * @param fmt Format string
	 * @param arg Object
	 * @return Formatted String or null
	 */
	public String formatDate(String fmt, Object arg)
		{
		final Date d = parseDate(arg);
		
		if (d == null)
			return (null);
		
		final DateFormat sdf = getDateFormat(fmt);
		return (sdf.format(d));
		}
	
	/**
	 * Parse a Date
	 * @param fmt Format string
	 * @param arg Object to parse
	 * @return Date or null
	 */
	public Date parseDate(String fmt, Object arg)
		{
		if (arg == null)
			return (null);
		else if (arg instanceof Date)
			return ((Date) arg);
		else if (arg instanceof Number)
			return (toDate((Number) arg));
		
		final DateFormat sdf = getDateFormat(fmt);
		try	{
			return (sdf.parse(arg.toString()));
			}
		catch (ParseException e)
			{
			return (null);
			}
		}
	
	/**
	 * Format a Date as UNIX timestamp
	 * @param arg Object
	 * @return Formatted String or null
	 */
	public Long formatDate(Object arg)
		{
		final Date d = parseDate(arg);
		
		if (d == null)
			return (null);
		
		return (d.getTime() / 1000L);
		}
	
	/**
	 * Parse a Date from UNIX timestamp
	 * @param arg Object to parse
	 * @return Date or null
	 */
	public Date parseDate(Object arg)
		{
		if (arg == null)
			return (null);
		else if (arg instanceof Date)
			return ((Date) arg);
		else if (arg instanceof Number)
			return (toDate((Number) arg));
		else
			return (toDate(arg.toString()));
		}
	
	private DateFormat getDateFormat(String fmt)
		{
		DateFormat df = formatCache.get(fmt);
		if (df == null)
			{
			df = new SimpleDateFormat(fmt, Locale.US);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			formatCache.put(fmt, df);
			}
		return (df);
		}
	
	private Date toDate(String s)
		{
		try	{
			final Long l = Long.valueOf(s);
			return (toDate(l));
			}
		catch (NumberFormatException e)
			{
			return (null);
			}
		}
	
	private Date toDate(Number n)
		{
		final long l = n.longValue();
		
		if (l < 10000000000L)
			return (new Date(l * 1000));
		else
			return (new Date(l));
		}
	}
