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
package de.tweerlei.dbgrazer.extension.sql.handler;

import java.io.IOException;
import java.util.Arrays;

import de.tweerlei.dbgrazer.extension.sql.parser.SQLHandler;
import de.tweerlei.dbgrazer.extension.sql.printer.DefaultSQLPrinter;

/**
 * Concatenate SQL tokens, enforcing spaces where necessary
 * 
 * @author Robert Wruck
 */
public class SimpleSQLHandler implements SQLHandler
	{
	private static enum State
		{
		INITIAL,
		OPERATOR,
		LITERAL,
		NAME,
		EOL
		}
	
	private final Appendable sb;
	private final SQLPrinter printer;
	private State state;
	
	/**
	 * Constructor
	 */
	public SimpleSQLHandler()
		{
		this(new DefaultSQLPrinter(), new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param pr SQLPrinter
	 */
	public SimpleSQLHandler(SQLPrinter pr)
		{
		this(pr, new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param a Appendable to receive formatted output
	 */
	public SimpleSQLHandler(Appendable a)
		{
		this(new DefaultSQLPrinter(), a);
		}
	
	/**
	 * Constructor
	 * @param pr SQLPrinter
	 * @param a Appendable to receive formatted output
	 */
	public SimpleSQLHandler(SQLPrinter pr, Appendable a)
		{
		this.sb = a;
		this.printer = pr;
		this.state = State.INITIAL;
		}
	
	@Override
	public void handleName(String token, int level)
		{
		try	{
			if (state == State.NAME/* || state == State.OPERATOR*/)
				sb.append(" ");
			else
				{
				if (state == State.EOL)
					sb.append("\n");
				state = State.NAME;
				}
			sb.append(printer.printName(token));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleString(String token, int level)
		{
		try	{
			if (state == State.LITERAL)
				sb.append(" ");
			else
				{
				if (state == State.EOL)
					sb.append("\n");
				state = State.LITERAL;
				}
			sb.append(printer.printString(token));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleNumber(String token, int level)
		{
		try	{
			if ((state == State.LITERAL) || (state == State.NAME))
				sb.append(" ");
			else
				{
				if (state == State.EOL)
					sb.append("\n");
				state = State.LITERAL;
				}
			sb.append(printer.printNumber(token));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleOperator(String token, int level)
		{
		try	{
			if (state == State.OPERATOR)
				{
//				sb.append(" ");
				}
			else
				{
				if (state == State.EOL)
					sb.append("\n");
				state = State.OPERATOR;
				}
			sb.append(printer.printOperator(token));
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleComment(String token, int level)
		{
		try	{
			if (state == State.EOL)
				sb.append("\n");
			boolean first = true;
			for (String s : printer.printComment(Arrays.asList(token.split("\n", -1))))
				{
				if (first)
					first = false;
				else
					sb.append("\n");
				sb.append(s);
				}
			state = State.INITIAL;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleEOLComment(String token, int level)
		{
		try	{
			sb.append(printer.printEOLComment(token));
			state = State.EOL;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleSpace(String token, int level)
		{
		try	{
			sb.append(token);
			state = State.INITIAL;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void finish()
		{
		}
	
	@Override
	public String toString()
		{
		return (sb.toString());
		}
	}
