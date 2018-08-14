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

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import de.tweerlei.common.codec.HexCodec;

/**
 * Plain format for CSV export
 * 
 * @author Robert Wruck
 */
public class ExportDataFormatterImpl extends AbstractDataFormatter
	{
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
	public ExportDataFormatterImpl(String tsfmt, String ldfmt, String sdfmt, String ifmt, String ffmt, Locale locale, TimeZone tz, int sizeLimit)
		{
		super(tsfmt, ldfmt, sdfmt, ifmt, ffmt, locale, tz, sizeLimit);
		}
	
	@Override
	protected String formatNull()
		{
		return ("");
		}
	
	@Override
	protected String formatUnknown(Object value)
		{
		return ("<" + value.getClass().getName() + ">");
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
	protected String formatBoolean(boolean b)
		{
		return (b ? "1" : "0");
		}
	
	@Override
	protected String formatException(Object value, Exception e)
		{
		return ("<" + value.getClass().getName() + ": " + e.getMessage() + ">");
		}
	
	@Override
	protected String formatBinary(byte[] data, int size)
		{
		try	{
			final HexCodec hb = new HexCodec(false, null, 0, null);
			
			return (hb.encode(data, 0, size));
			}
		catch (IOException e)
			{
			return (formatException(data, e));
			}
		}
	
	@Override
	protected String formatSpecial(String s)
		{
		return ("<" + s + ">");
		}
	}
