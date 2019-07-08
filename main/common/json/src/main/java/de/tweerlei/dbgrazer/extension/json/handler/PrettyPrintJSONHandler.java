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
package de.tweerlei.dbgrazer.extension.json.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.json.printer.DefaultJSONPrinter;

/**
 * Format XML in multiple lines with indentation
 * 
 * @author Robert Wruck
 */
public class PrettyPrintJSONHandler implements JSONHandler
	{
	private static final String SPACE = " ";
	private static final String EOL = "\n";
	private static final String INDENT = "\t";
	
	private final Appendable sb;
	private final JSONPrinter printer;
	
	/**
	 * Constructor
	 */
	public PrettyPrintJSONHandler()
		{
		this(new DefaultJSONPrinter(), new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param a Appendable to receive formatted output
	 */
	public PrettyPrintJSONHandler(Appendable a)
		{
		this(new DefaultJSONPrinter(), a);
		}
	
	/**
	 * Constructor
	 * @param pr XMLPrinter
	 */
	public PrettyPrintJSONHandler(JSONPrinter pr)
		{
		this(pr, new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param pr XMLPrinter
	 * @param a Appendable to receive formatted output
	 */
	public PrettyPrintJSONHandler(JSONPrinter pr, Appendable a)
		{
		this.sb = a;
		this.printer = pr;
		}
	
	private void indent(int level) throws IOException
		{
		for (int i = 0; i < level; i++)
			sb.append(INDENT);
		}
	
	@Override
	public void handleName(String tag, int level)
		{
		try	{
			sb.append(printer.printName(tag));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleKey(String tag, int level)
		{
		try	{
			sb.append(printer.printKey(tag));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleString(String tag, int level)
		{
		try	{
			sb.append(printer.printString(tag));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleNumber(String tag, int level)
		{
		try	{
			sb.append(printer.printNumber(tag));
			// Hack: Try to interpret the value as a UNIX timestamp
			// If successful, print the result as comment
			final String comment = convertNumberToDate(tag);
			if (comment != null)
				sb.append(printer.printComment(comment));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	private static String convertNumberToDate(String tag)
		{
		try	{
			final double value = Double.parseDouble(tag);
			// 2001-01-01 .. 2100-01-01 as seconds
			if (value >= 978307200.0 && value <= 4102444800.0)
				return (formatDate(new Date((long) (value * 1000.0))));
			// 2001-01-01 .. 2100-01-01 as milliseconds
			else if (value >= 978307200000.0 && value <= 4102444800000.0)
				return (formatDate(new Date((long) value)));
			else
				return (null);
			}
		catch (NumberFormatException e)
			{
			return (null);
			}
		}
	
	private static String formatDate(Date d)
		{
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return (sdf.format(d));
		}
	
	@Override
	public void startObject(int level)
		{
		try	{
			sb.append(printer.printObjectStart());
			sb.append(EOL);
			indent(level + 1);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endObject(int level)
		{
		try	{
			sb.append(EOL);
			indent(level);
			sb.append(printer.printObjectEnd());
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startArray(int level)
		{
		try	{
			sb.append(printer.printArrayStart());
			sb.append(EOL);
			indent(level + 1);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endArray(int level)
		{
		try	{
			sb.append(EOL);
			indent(level);
			sb.append(printer.printArrayEnd());
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleKeySeparator(int level)
		{
		try	{
			sb.append(printer.printKeySeparator());
			sb.append(SPACE);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleValueSeparator(int level)
		{
		try	{
			sb.append(printer.printValueSeparator());
			sb.append(EOL);
			indent(level);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleComment(String text, int level)
		{
		try	{
			sb.append(printer.printComment(text));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleSpace(String text, int level)
		{
		}
	
	@Override
	public String toString()
		{
		return (sb.toString());
		}
	}
