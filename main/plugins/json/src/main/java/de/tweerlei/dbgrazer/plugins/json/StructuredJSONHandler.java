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
package de.tweerlei.dbgrazer.plugins.json;

import java.util.LinkedList;

import de.tweerlei.dbgrazer.extension.json.handler.JSONPrinter;
import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.json.printer.DefaultJSONPrinter;

/**
 * Format JSON as nested HTML elements
 * 
 * @author Robert Wruck
 */
public class StructuredJSONHandler implements JSONHandler
	{
	private static final class State
		{
		public int arrayIndex;
		
		public State(int arrayIndex)
			{
			this.arrayIndex = arrayIndex;
			}
		}
	
	private final StringBuilder sb;
	private final JSONPrinter printer;
	private LinkedList<State> stack;
	
	/**
	 * Constructor
	 * @param printer XMLPrinter
	 */
	public StructuredJSONHandler(JSONPrinter printer)
		{
		this.sb = new StringBuilder();
		this.printer = printer;
		this.stack = new LinkedList<State>();
		this.stack.push(new State(-1));
		}
	
	/**
	 * Constructor
	 */
	public StructuredJSONHandler()
		{
		this(new DefaultJSONPrinter());
		}
	
	@Override
	public void handleKey(String tag, int level)
		{
		sb.append("<dt>");
		sb.append(printer.printKey(tag));
		sb.append("</dt>");
		}
	
	@Override
	public void handleString(String tag, int level)
		{
		printArrayIndex();
		
		if (level > 0)
			sb.append("<dd>");
		sb.append(printer.printString(tag));
		if (level > 0)
			sb.append("</dd>");
		}
	
	@Override
	public void handleNumber(String tag, int level)
		{
		printArrayIndex();
		
		if (level > 0)
			sb.append("<dd>");
		sb.append(printer.printNumber(tag));
		if (level > 0)
			sb.append("</dd>");
		}
	
	@Override
	public void handleName(String tag, int level)
		{
		printArrayIndex();
		
		if (level > 0)
			sb.append("<dd>");
		sb.append(printer.printName(tag));
		if (level > 0)
			sb.append("</dd>");
		}
	
	private void printArrayIndex()
		{
		final State state = stack.peek();
		if (state.arrayIndex >= 0)
			{
			sb.append("<dt>");
			sb.append(printer.printNumber(String.valueOf(state.arrayIndex)));
			sb.append("</dt>");
			}
		}
	
	@Override
	public void startObject(int level)
		{
		printArrayIndex();
		
		if (level > 0)
			sb.append("<dd>");
		sb.append("<dl class=\"json-object\">");
		
		stack.push(new State(-1));
		}
	
	@Override
	public void endObject(int level)
		{
		sb.append("</dl>");
		if (level > 0)
			sb.append("</dd>");
		
		stack.pop();
		}
	
	@Override
	public void handleKeySeparator(int level)
		{
		}
	
	@Override
	public void handleValueSeparator(int level)
		{
		final State state = stack.peek();
		if (state.arrayIndex >= 0)
			state.arrayIndex++;
		}
	
	@Override
	public void startArray(int level)
		{
		printArrayIndex();
		
		if (level > 0)
			sb.append("<dd>");
		sb.append("<dl class=\"json-array\">");
		
		stack.push(new State(0));
		}
	
	@Override
	public void endArray(int level)
		{
		sb.append("</dl>");
		if (level > 0)
			sb.append("</dd>");
		
		stack.pop();
		}
	
	@Override
	public void handleComment(String text, int level)
		{
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
