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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tweerlei.dbgrazer.text.backend.XMLEncodedTextFormatter;

/**
 * Add line numbers via CSS classes:
 * <pre>
 * &lt;pre class="code"&gt;
 *   &lt;span class="line"&gt;
 *     &lt;span class="no"&gt;&lt;/span&gt;
 *     &lt;code&gt;This is line 1.&lt;/code&gt;
 *   &lt;/span&gt;
 * &lt;/pre&gt;
 * </pre>
 * 
 * @author Robert Wruck
 */
public class LineNumberTableFormatter extends XMLEncodedTextFormatter
	{
	private static final Pattern PATTERN = Pattern.compile("\\r?\\n");
	
	private static final String ROW_CLASS = "line";
	private static final String NUMBER_CLASS = "no";
	private static final String ERROR_ROW_CLASS = "errline";
	private static final String ERROR_NUMBER_CLASS = "errno";
	private static final String ERROR_MSG_CLASS = "errmsg";
	
	private final String error;
	
	/**
	 * Constructor
	 * @param error Formatting error to display
	 */
	public LineNumberTableFormatter(String error)
		{
		super("CODEBLOCK");
		this.error = error;
		}
	
	/**
	 * Constructor
	 */
	public LineNumberTableFormatter()
		{
		this(null);
		}
	
	@Override
	public String format(String value)
		{
		if (value == null)
			return (value);
		
		final StringBuffer sb = new StringBuffer();
		
		if (error != null)
			{
			sb.append("<span class=\"").append(ERROR_ROW_CLASS).append("\">");
			sb.append("<span class=\"").append(ERROR_NUMBER_CLASS).append("\"></span>");
			sb.append("<span class=\"").append(ERROR_MSG_CLASS).append("\">");
			sb.append(error);
			sb.append("</span>");
			sb.append("</span>\n");
			}
		
		final Matcher m = PATTERN.matcher(value);
		while (m.find())
			{
			sb.append("<span class=\"").append(ROW_CLASS).append("\">");
			sb.append("<span class=\"").append(NUMBER_CLASS).append("\"></span>");
			sb.append("<code>");
			m.appendReplacement(sb, "");
			sb.append("</code>");
			sb.append("</span>\n");
			}
		
		sb.append("<span class=\"").append(ROW_CLASS).append("\">");
		sb.append("<span class=\"").append(NUMBER_CLASS).append("\"></span>");
		sb.append("<code>");
		m.appendTail(sb);
		sb.append("</code>");
		sb.append("</span>\n");
		
		return (sb.toString());
		}
	}
