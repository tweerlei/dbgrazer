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
 * Print XML syntax elements
 * 
 * @author Robert Wruck
 */
public class DefaultJSONPrinter implements JSONPrinter
	{
	@Override
	public String printKey(String tag)
		{
		return (printString(tag));
		}
	
	@Override
	public String printString(String tag)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append('"');
		
		for (int i = 0; i < tag.length(); i++)
			{
			final char c = tag.charAt(i);
			switch (c)
				{
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					if ((c < ' ') || (c > '~'))
						sb.append(String.format("\\u%04x", Integer.valueOf(c)));
					else
						sb.append(c);
					break;
				}
			}
		
		sb.append('"');
		
		return (sb.toString());
		}
	
	@Override
	public String printNumber(String tag)
		{
		return (tag);
		}
	
	@Override
	public String printName(String tag)
		{
		return (tag);
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
		final StringBuilder sb = new StringBuilder();
		sb.append("/*").append(text).append("*/");
		
		return (sb.toString());
		}
	}
