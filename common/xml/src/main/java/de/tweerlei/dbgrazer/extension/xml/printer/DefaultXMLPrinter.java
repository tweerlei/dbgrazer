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
package de.tweerlei.dbgrazer.extension.xml.printer;

import java.util.Map;

import de.tweerlei.dbgrazer.extension.xml.handler.XMLPrinter;

/**
 * Print XML syntax elements
 * 
 * @author Robert Wruck
 */
public class DefaultXMLPrinter implements XMLPrinter
	{
	@Override
	public String printDeclaration(String tag, String attrs)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<!").append(tag).append(attrs).append(">");
		
		return (sb.toString());
		}
	
	@Override
	public String printProcessingInstruction(String tag, Map<String, String> attrs)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<?").append(tag);
		appendAttributes(sb, attrs);
		sb.append("?>");
		
		return (sb.toString());
		}
	
	@Override
	public String printStartTag(String tagName, Map<String, String> attrs, boolean empty)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<").append(tagName);
		appendAttributes(sb, attrs);
		if (empty)
			sb.append("/>");
		else
			sb.append(">");
		
		return (sb.toString());
		}
	
	@Override
	public String printEndTag(String tagName)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("</").append(tagName).append(">");
		
		return (sb.toString());
		}
	
	@Override
	public String printText(String text)
		{
		return (textEncode(text));
		}
	
	@Override
	public String printCDATA(String text)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<![CDATA[").append(text).append("]]>");
		
		return (sb.toString());
		}
	
	@Override
	public String printComment(String text)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<!--").append(text).append("-->");
		
		return (sb.toString());
		}
	
	private void appendAttributes(StringBuilder sb, Map<String, String> attrs)
		{
		for (Map.Entry<String, String> ent : attrs.entrySet())
			{
			sb.append(" ");
			sb.append(ent.getKey());
			sb.append("=\"");
			sb.append(attrEncode(ent.getValue()));
			sb.append("\"");
			}
		}
	
	private String textEncode(String s)
		{
		return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
		}
	
	private String attrEncode(String s)
		{
		return (s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"));
		}
	}
