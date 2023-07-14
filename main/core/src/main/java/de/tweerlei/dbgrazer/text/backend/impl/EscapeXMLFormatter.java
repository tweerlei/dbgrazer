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
package de.tweerlei.dbgrazer.text.backend.impl;

import de.tweerlei.dbgrazer.text.backend.XMLEncodedTextFormatter;

/**
 * Format text for XML output
 * 
 * @author Robert Wruck
 */
public class EscapeXMLFormatter extends XMLEncodedTextFormatter
	{
	/**
	 * Constructor
	 */
	public EscapeXMLFormatter()
		{
		super("EscapeXML");
		}
	
	@Override
	public String format(String value)
		{
		if (value == null)
			return (value);
		
		final int l = value.length();
		final StringBuilder sb = new StringBuilder(l);
		for (int i = 0; i < l; i++)
			{
			final char c = value.charAt(i);
			switch (c)
				{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
/*
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&apos;");
					break;
*/
				default:
					sb.append(c);
					break;
				}
			}
		
		return (sb.toString());
		}
	}
