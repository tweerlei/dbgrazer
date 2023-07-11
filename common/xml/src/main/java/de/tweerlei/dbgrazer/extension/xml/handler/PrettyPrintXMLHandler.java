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
package de.tweerlei.dbgrazer.extension.xml.handler;

import java.io.IOException;
import java.util.Map;

import de.tweerlei.dbgrazer.extension.xml.parser.XMLHandler;
import de.tweerlei.dbgrazer.extension.xml.printer.DefaultXMLPrinter;

/**
 * Format XML in multiple lines with indentation
 * 
 * @author Robert Wruck
 */
public class PrettyPrintXMLHandler implements XMLHandler
	{
	private static final String EOL = "\n";
	private static final String INDENT = "\t";
	
	private final Appendable sb;
	private final XMLPrinter printer;
	private boolean textSeen;
	
	/**
	 * Constructor
	 */
	public PrettyPrintXMLHandler()
		{
		this(new DefaultXMLPrinter(), new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param a Appendable to receive formatted output
	 */
	public PrettyPrintXMLHandler(Appendable a)
		{
		this(new DefaultXMLPrinter(), a);
		}
	
	/**
	 * Constructor
	 * @param pr XMLPrinter
	 */
	public PrettyPrintXMLHandler(XMLPrinter pr)
		{
		this(pr, new StringBuilder());
		}
	
	/**
	 * Constructor
	 * @param pr XMLPrinter
	 * @param a Appendable to receive formatted output
	 */
	public PrettyPrintXMLHandler(XMLPrinter pr, Appendable a)
		{
		this.sb = a;
		this.printer = pr;
		this.textSeen = true;
		}
	
	private void indent(int level) throws IOException
		{
		for (int i = 0; i < level; i++)
			sb.append(INDENT);
		}
	
	@Override
	public void handleDeclaration(String tag, String attrs, int level)
		{
		try	{
			if (!textSeen)
				{
				sb.append(EOL);
				indent(level);
				}
			sb.append(printer.printDeclaration(tag, attrs));
			sb.append(EOL);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleProcessingInstruction(String tag, Map<String, String> attrs, int level)
		{
		try	{
			if (!textSeen)
				{
				sb.append(EOL);
				indent(level);
				}
			sb.append(printer.printProcessingInstruction(tag, attrs));
			sb.append(EOL);
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void startTag(String tagName, Map<String, String> attrs, boolean empty, int level)
		{
		try	{
			if (!textSeen)
				{
				sb.append(EOL);
				indent(level);
				}
			sb.append(printer.printStartTag(tagName, attrs, empty));
			textSeen = false;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void endTag(String tagName, int level)
		{
		try	{
			if (!textSeen)
				{
				sb.append(EOL);
				indent(level);
				}
			sb.append(printer.printEndTag(tagName));
			textSeen = false;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleText(String text, int level)
		{
		// Ignore white space text nodes
		final String t = text.trim();
		if (t.length() == 0)
			return;
		
		try	{
			sb.append(printer.printText(t));
			textSeen = true;
			}
		catch (IOException e)
			{
			throw new RuntimeException(e);
			}
		}
	
	@Override
	public void handleCDATA(String text, int level)
		{
		try	{
			sb.append(printer.printCDATA(text));
			textSeen = true;
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
			if (!textSeen)
				{
				sb.append(EOL);
				indent(level);
				}
			sb.append(printer.printComment(text));
			textSeen = false;
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
