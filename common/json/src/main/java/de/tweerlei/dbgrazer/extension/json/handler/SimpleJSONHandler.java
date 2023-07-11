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

import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.json.printer.DefaultJSONPrinter;

/**
 * Concatenate XML tokens
 * 
 * @author Robert Wruck
 */
public class SimpleJSONHandler implements JSONHandler
	{
	private final Appendable sb;
	private final JSONPrinter printer;
	
	/**
	 * Constructor
	 */
	public SimpleJSONHandler()
		{
		this(new DefaultJSONPrinter(), new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param a Appendable to receive formatted output
	 */
	public SimpleJSONHandler(Appendable a)
		{
		this(new DefaultJSONPrinter(), a);
		}
	
	/**
	 * Constructor
	 * @param pr XMLPrinter
	 */
	public SimpleJSONHandler(JSONPrinter pr)
		{
		this(pr, new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param pr XMLPrinter
	 * @param a Appendable to receive formatted output
	 */
	public SimpleJSONHandler(JSONPrinter pr, Appendable a)
		{
		this.sb = a;
		this.printer = pr;
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
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
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
	public void startObject(int level)
		{
		try	{
			sb.append(printer.printObjectStart());
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
			sb.append(printer.printObjectEnd());
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
			sb.append(printer.printArrayEnd());
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
		try	{
			sb.append(text);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public String toString()
		{
		return (sb.toString());
		}
	}
