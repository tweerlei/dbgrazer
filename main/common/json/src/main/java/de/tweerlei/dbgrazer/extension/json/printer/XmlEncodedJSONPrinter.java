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
package de.tweerlei.dbgrazer.extension.json.printer;

import de.tweerlei.dbgrazer.extension.json.handler.JSONPrinter;

/**
 * Print XML syntax elements with HTML syntax highlighting
 * 
 * @author Robert Wruck
 */
public class XmlEncodedJSONPrinter implements JSONPrinter
	{
	private static final String STYLE_KEY = "json-key";
	private static final String STYLE_VALUE = "json-value";
	private static final String STYLE_COMMENT = "json-comment";
	
	private boolean highlight;
	
	/**
	 * Constructor
	 * @param highlight Add syntax highlighting
	 */
	public XmlEncodedJSONPrinter(boolean highlight)
		{
		this.highlight = highlight;
		}
	
	@Override
	public String printKey(String tag)
		{
		return (printString(tag, STYLE_KEY));
		}
	
	@Override
	public String printString(String tag)
		{
		return (printString(tag, STYLE_VALUE));
		}
	
	private String printString(String tag, String style)
		{
		if (highlight)
			{
			final StringBuilder sb = new StringBuilder();
			sb.append("<span class=\"").append(style).append("\">").append(textEncode(tag)).append("</span>");
			
			return (sb.toString());
			}
		else
			return (textEncode(tag));
		}
	
	@Override
	public String printNumber(String tag)
		{
		return (printString(tag, STYLE_VALUE));
		}
	
	@Override
	public String printName(String tag)
		{
		return (printString(tag, STYLE_VALUE));
		}
	
	@Override
	public String printObjectStart()
		{
		return ("{");
		}
	
	@Override
	public String printObjectEnd()
		{
		return ("}");
		}
	
	@Override
	public String printArrayStart()
		{
		return ("[");
		}
	
	@Override
	public String printArrayEnd()
		{
		return ("]");
		}
	
	@Override
	public String printKeySeparator()
		{
		return (":");
		}
	
	@Override
	public String printValueSeparator()
		{
		return (",");
		}
	
	@Override
	public String printComment(String text)
		{
		return (printString(text, STYLE_COMMENT));
		}
	
	private String textEncode(String s)
		{
		return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\r\n", "<br/>").replace("\r", "<br/>").replace("\n", "<br/>"));
		}
	}
