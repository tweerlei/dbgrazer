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
 * Print XML syntax elements with HTML syntax highlighting
 * 
 * @author Robert Wruck
 */
public class SyntaxHighlightXMLPrinter implements XMLPrinter
	{
	private static final String STYLE_TAG = "xml-tag";
	private static final String STYLE_ATTRIBUTE = "xml-attr";
	private static final String STYLE_VALUE = "xml-value";
	private static final String STYLE_COMMENT = "xml-comment";
	private static final String STYLE_IDENTIFIER = "identifier";
	
	@Override
	public String printDeclaration(String tag, String attrs)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("&lt;!<span class=\"").append(STYLE_TAG).append("\">").append(tag).append("</span>").append(attrs).append("&gt;");
		
		return (sb.toString());
		}
	
	@Override
	public String printProcessingInstruction(String tag, Map<String, String> attrs)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("&lt;?<span class=\"").append(STYLE_TAG).append("\">").append(tag).append("</span>");
		appendAttributes(sb, attrs);
		sb.append("?&gt;");
		
		return (sb.toString());
		}
	
	@Override
	public String printStartTag(String tagName, Map<String, String> attrs, boolean empty)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("&lt;<span class=\"").append(STYLE_TAG).append("\">").append(tagName).append("</span>");
		appendAttributes(sb, attrs);
		if (empty)
			sb.append("/&gt;");
		else
			sb.append("&gt;");
		
		return (sb.toString());
		}
	
	@Override
	public String printEndTag(String tagName)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("&lt;/<span class=\"").append(STYLE_TAG).append("\">").append(tagName).append("</span>&gt;");
		
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
		sb.append("&lt;![CDATA[").append(textEncode(text)).append("]]&gt;");
		
		return (sb.toString());
		}
	
	@Override
	public String printComment(String text)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append("<span class=\"").append(STYLE_COMMENT).append("\">&lt;!--").append(textEncode(text)).append("--&gt;</span>");
		
		return (sb.toString());
		}
	
	private void appendAttributes(StringBuilder sb, Map<String, String> attrs)
		{
		for (Map.Entry<String, String> ent : attrs.entrySet())
			{
			sb.append(" <span class=\"").append(STYLE_ATTRIBUTE).append("\">");
			sb.append(ent.getKey());
			sb.append("</span>=<span class=\"").append(STYLE_VALUE);
			if (ent.getKey().equalsIgnoreCase("id"))
				sb.append(" ").append(STYLE_IDENTIFIER);
			sb.append("\">\"");
			sb.append(attrEncode(ent.getValue()));
			sb.append("\"</span>");
			}
		}
	
	private String textEncode(String s)
		{
		return (s.replace("&", "&amp;amp;").replace("<", "&amp;lt;").replace(">", "&amp;gt;"));
		}
	
	private String attrEncode(String s)
		{
		return (s.replace("&", "&amp;amp;").replace("<", "&amp;lt;").replace(">", "&amp;gt;").replace("\"", "&amp;quot;"));
		}
	}
