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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.extension.xml.parser.XMLHandler;
import de.tweerlei.dbgrazer.extension.xml.printer.DefaultXMLPrinter;

/**
 * Concatenate XML tokens
 * 
 * @author Robert Wruck
 */
public class TokenListXMLHandler implements XMLHandler
	{
	private final List<String> tokens;
	private final XMLPrinter printer;
	
	/**
	 * Constructor
	 */
	public TokenListXMLHandler()
		{
		this(new DefaultXMLPrinter());
		}
	
	/**
	 * Constructor
	 * @param printer XMLPrinter
	 */
	public TokenListXMLHandler(XMLPrinter printer)
		{
		this.tokens = new ArrayList<String>();
		this.printer = printer;
		}
	
	@Override
	public void handleDeclaration(String tag, String attrs, int level)
		{
		tokens.add(printer.printDeclaration(tag, attrs));
		}
	
	@Override
	public void handleProcessingInstruction(String tag, Map<String, String> attrs, int level)
		{
		tokens.add(printer.printProcessingInstruction(tag, attrs));
		}
	
	@Override
	public void startTag(String tagName, Map<String, String> attrs, boolean empty, int level)
		{
		tokens.add(printer.printStartTag(tagName, attrs, empty));
		}
	
	@Override
	public void endTag(String tagName, int level)
		{
		tokens.add(printer.printEndTag(tagName));
		}
	
	@Override
	public void handleText(String text, int level)
		{
		tokens.add(printer.printText(text));
		}
	
	@Override
	public void handleCDATA(String text, int level)
		{
		tokens.add(printer.printCDATA(text));
		}
	
	@Override
	public void handleComment(String text, int level)
		{
		tokens.add(printer.printComment(text));
		}
	
	/**
	 * Get the tokens
	 * @return Tokens
	 */
	public List<String> getTokens()
		{
		return (tokens);
		}
	}
