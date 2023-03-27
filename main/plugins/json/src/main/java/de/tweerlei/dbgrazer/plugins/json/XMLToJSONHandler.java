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

import java.util.Map;
import java.util.regex.Pattern;

import de.tweerlei.dbgrazer.extension.json.parser.JSONConsumer;
import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLHandler;

/**
 * Format XML in multiple lines with indentation
 * 
 * @author Robert Wruck
 */
public class XMLToJSONHandler implements XMLHandler
	{
	private static final Pattern PAT_NUMBER = Pattern.compile("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][-+]?[0-9]+)?");
	
	private final JSONConsumer sb;
	private boolean tagOpened;
	private String textContent;
	
	/**
	 * Constructor
	 * @param sb JSONHandler
	 */
	public XMLToJSONHandler(JSONHandler sb)
		{
		this.sb = new JSONConsumer(sb);
		}
	
	@Override
	public void handleDeclaration(String tag, String attrs, int level)
		{
		// ignore
		}
	
	@Override
	public void handleProcessingInstruction(String tag, Map<String, String> attrs, int level)
		{
		// ignore
		}
	
	@Override
	public void startTag(String tagName, Map<String, String> attrs, boolean empty, int level)
		{
		if (!tagOpened && (textContent == null))
			sb.startObject();
		else if (tagOpened && (textContent == null))
			sb.appendValueSeparator();
		else if (tagOpened && (textContent != null))
			{
			sb.appendValueSeparator();
			sb.appendString("text");
			sb.appendKeySeparator();
			appendValue(textContent);
			sb.appendValueSeparator();
			textContent = null;
			}
		else if (!tagOpened && (textContent != null))
			{
			sb.startObject();
			sb.appendString("text");
			sb.appendKeySeparator();
			appendValue(textContent);
			sb.appendValueSeparator();
			textContent = null;
			tagOpened = true;
			}
		
		sb.appendString(stripNamespace(tagName));
		sb.appendKeySeparator();
		boolean attrSeen = false;
		for (Map.Entry<String, String> ent : attrs.entrySet())
			{
			// Ignore name space declarations
			if (ent.getKey().startsWith("xmlns"))
				continue;
			
			// Ignore white space attribute values
			final String t = ent.getValue().trim();
			if (t.length() == 0)
				continue;
			
			if (attrSeen)
				sb.appendValueSeparator();
			else
				{
				sb.startObject();
				attrSeen = true;
				}
			
			sb.appendString(stripNamespace(ent.getKey()));
			sb.appendKeySeparator();
			appendValue(ent.getValue());
			}
		
		if (attrSeen)
			{
			if (empty)
				sb.endObject();
			tagOpened = true;
			}
		else if (empty)
			{
			appendValue(null);
			tagOpened = true;
			}
		else
			tagOpened = false;
		
		if ((level == 0) && empty)
			sb.endObject();
		}
	
	private String stripNamespace(String s)
		{
		// Strip name space prefixes
		final int ix = s.indexOf(':');
		if (ix < 0)
			return (s);
		else
			return (s.substring(ix + 1));
		}
	
	private void appendValue(String value)
		{
		if (value == null)
			sb.appendName("null");
		else if (value.equals("null"))
			sb.appendName(value);
		else if (value.equals("true"))
			sb.appendName(value);
		else if (value.equals("false"))
			sb.appendName(value);
		else if (PAT_NUMBER.matcher(value).matches())
			sb.appendNumber(value);
		else
			sb.appendString(value);
		}
	
	@Override
	public void endTag(String tagName, int level)
		{
		if (!tagOpened && (textContent == null))
			{
			sb.appendName("null");
			tagOpened = true;
			}
		else if (tagOpened && (textContent == null))
			sb.endObject();
		else if (tagOpened && (textContent != null))
			{
			sb.appendValueSeparator();
			sb.appendString("text");
			sb.appendKeySeparator();
			appendValue(textContent);
			sb.endObject();
			textContent = null;
			}
		else if (!tagOpened && (textContent != null))
			{
			appendValue(textContent);
			textContent = null;
			tagOpened = true;
			}
		
		if (level == 0)
			sb.endObject();
		}
	
	@Override
	public void handleText(String text, int level)
		{
		// Ignore white space text nodes
		final String t = text.trim();
		if (t.length() == 0)
			return;
		
		textContent = t;
		}
	
	@Override
	public void handleCDATA(String text, int level)
		{
		handleText(text, level);
		}
	
	@Override
	public void handleComment(String text, int level)
		{
		sb.appendComment(text);
		}
	
	@Override
	public String toString()
		{
		return (sb.toString());
		}
	}
