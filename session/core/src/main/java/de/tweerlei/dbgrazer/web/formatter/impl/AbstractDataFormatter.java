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

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public abstract class AbstractDataFormatter implements DataFormatter
	{
	private static final Pattern NUMBER_PATTERN = Pattern.compile("([0-9]+).*");
	
	private final Calendar calendar;
	private final DateFormat timestampDateFormat;
	private final DateFormat longDateFormat;
	private final DateFormat shortDateFormat;
	private final NumberFormat integerFormat;
	private final NumberFormat floatFormat;
	private final int sizeLimit;
	
	/**
	 * Constructor
	 * @param tsfmt Timestamp date format
	 * @param ldfmt Long date format
	 * @param sdfmt Short date format
	 * @param ifmt Integer format
	 * @param ffmt Float format
	 * @param locale Locale
	 * @param tz Time zone
	 * @param sizeLimit Size limit (characters) for formatted output
	 */
	public AbstractDataFormatter(String tsfmt, String ldfmt, String sdfmt, String ifmt, String ffmt, Locale locale, TimeZone tz, int sizeLimit)
		{
		final DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance(locale);
		
		this.calendar = Calendar.getInstance(tz, locale);
		this.timestampDateFormat = new SimpleDateFormat(tsfmt, locale);
		this.timestampDateFormat.setTimeZone(tz);
		this.longDateFormat = new SimpleDateFormat(ldfmt, locale);
		this.longDateFormat.setTimeZone(tz);
		this.shortDateFormat = new SimpleDateFormat(sdfmt, locale);
		this.shortDateFormat.setTimeZone(tz);
		this.integerFormat = new DecimalFormat(ifmt, sym);
		this.floatFormat = new DecimalFormat(ffmt, sym);
		this.sizeLimit = sizeLimit;
		}
	
	@Override
	public final Calendar getCalendar()
		{
		return (calendar);
		}
	
	@Override
	public final String format(ColumnType type, Object value)
		{
		if (value == null)
			return (formatNull());
		
		try	{
			switch (type)
				{
				case INTEGER:
					{
					final Number n = (Number) value;
					return (formatInteger(n));
					}
				case FLOAT:
					{
					final Number n = (Number) value;
					return (formatFloat(n));
					}
				case STRING:
				case TEXT:
				case PATTERN:
					return (formatString(value.toString()));
				case DATE:
					{
					final Date d = (Date) value;
					return (formatDate(d));
					}
				case BOOLEAN:
					{
					final Boolean b = (Boolean) value;
					return (formatBoolean(b.booleanValue()));
					}
				case BINARY:
					{
					final byte[] cb = (byte[]) value;
					final int size = getMaxLength(cb.length);
					return (formatBinary(cb, size));
					}
				case ROWID:
					{
					final RowId r = (RowId) value;
					return (formatString(r.toString()));
					}
				case ARRAY:
					try	{
						final Array arr = (Array) value;
						return (formatSpecial(arr.getBaseTypeName()));
						}
					catch (SQLException e)
						{
						return (formatException(value, e));
						}
				case STRUCT:
					try	{
						final Struct str = (Struct) value;
						return (formatSpecial(str.getSQLTypeName()));
						}
					catch (SQLException e)
						{
						return (formatException(value, e));
						}
				case BLOB:
					try	{
						final Blob lob = (Blob) value;
						final int size = getMaxLength(lob.length());
						return (formatBinary(lob.getBytes(1, size), size));
						}
					catch (SQLException e)
						{
						return (formatException(value, e));
						}
				case CLOB:
					try	{
						final Clob lob = (Clob) value;
						final int size = getMaxLength(lob.length());
						return (formatString(lob.getSubString(1, size)));
						}
					catch (SQLException e)
						{
						return (formatException(value, e));
						}
				case XML:
					try	{
						final SQLXML xml = (SQLXML) value;
						return (formatString(xml.getString()));
						}
					catch (SQLException e)
						{
						return (formatException(value, e));
						}
				case REF:
					try	{
						final Ref ref = (Ref) value;
						return (formatSpecial(ref.getBaseTypeName()));
						}
					catch (SQLException e)
						{
						return (formatException(value, e));
						}
				default:
					return (formatString(value.toString()));
				}
			}
		catch (RuntimeException e)
			{
			return (formatException(value, e));
			}
		}
	
	private int getMaxLength(long l)
		{
		return (l > sizeLimit) ? sizeLimit : (int) l;
		}
	
	/**
	 * Format a NULL value
	 * @return String representation
	 */
	protected abstract String formatNull();
	
	private String formatString(String s)
		{
		if (StringUtils.empty(s))
			return (formatEmptyString());
		
		if (s.length() > sizeLimit)
			return (formatNonemptyString(s.substring(0, sizeLimit)));
		
		return (formatNonemptyString(s));
		}
	
	/**
	 * Format an empty string
	 * @return String representation
	 */
	protected abstract String formatEmptyString();
	
	/**
	 * Format an nonempty string
	 * @param s String
	 * @return String representation
	 */
	protected abstract String formatNonemptyString(String s);
	
	private String formatInteger(Number value)
		{
		return (integerFormat.format(value));
		}
	
	private String formatFloat(Number value)
		{
		final double d = value.doubleValue();
		if (d % 1.0 == 0.0)
			return (integerFormat.format(value));
		else
			return (floatFormat.format(value));
		}
	
	private String formatDate(Date value)
		{
		calendar.setTime(value);
		
		if (calendar.get(Calendar.MILLISECOND) != 0)
			return (timestampDateFormat.format(value));
		else if ((calendar.get(Calendar.HOUR_OF_DAY) != 0)
				|| (calendar.get(Calendar.MINUTE) != 0)
				|| (calendar.get(Calendar.SECOND) != 0))
			return (longDateFormat.format(value));
		else
			return (shortDateFormat.format(value));
		}
	
	/**
	 * Format a boolean value
	 * @param b boolean
	 * @return String representation
	 */
	protected abstract String formatBoolean(boolean b);
	
	/**
	 * Format an exception
	 * @param value Value that caused the exception
	 * @param e Exception
	 * @return String representation
	 */
	protected abstract String formatException(Object value, Exception e);
	
	/**
	 * Format a binary value
	 * @param data byte array
	 * @param size size limit
	 * @return String representation
	 */
	protected abstract String formatBinary(byte[] data, int size);
	
	/**
	 * Format a special value
	 * @param s Label
	 * @return String representation
	 */
	protected abstract String formatSpecial(String s);
	
	@Override
	public final Object parse(ColumnType type, String value)
		{
		if (value == null)
			return (null);
		
		switch (type)
			{
			case INTEGER:
			case FLOAT:
				return (parseNumber(value.trim()));
			case BOOLEAN:
				return (parseBoolean(value.trim()));
			case PATTERN:
				return ("%" + value + "%");
			case DATE:
				return (parseDate(value.trim()));
			default:
				return (value);
			}
		}
	
	private Object parseBoolean(String v)
		{
		if (v.equalsIgnoreCase("true"))
			return (Boolean.TRUE);
		if (v.equalsIgnoreCase("1"))
			return (Boolean.TRUE);
		if (v.equalsIgnoreCase("y"))
			return (Boolean.TRUE);
		if (v.equalsIgnoreCase("yes"))
			return (Boolean.TRUE);
		
		return (Boolean.FALSE);
		}
	
	private Object parseNumber(String v)
		{
		try	{
			return (floatFormat.parse(v));
			}
		catch (ParseException e)
			{
			}
		
		try	{
			return (integerFormat.parse(v));
			}
		catch (ParseException e)
			{
			}
		
		try	{
			final Matcher m = NUMBER_PATTERN.matcher(v);
			if (m.matches())
				return Long.parseLong(m.group(1));
			}
		catch (NumberFormatException e)
			{
			}
		
		return (null);
		}
	
	private Object parseDate(String v)
		{
		try	{
			final Date date = timestampDateFormat.parse(v);
			return (new java.sql.Timestamp(date.getTime()));
			}
		catch (ParseException e)
			{
			}
		
		try	{
			final Date date = longDateFormat.parse(v);
			return (new java.sql.Timestamp(date.getTime()));
			}
		catch (ParseException e)
			{
			}
		
		try	{
			final Date date = shortDateFormat.parse(v);
			return (new java.sql.Date(date.getTime()));
			}
		catch (ParseException e)
			{
			}
		
		return (null);
		}
	}
