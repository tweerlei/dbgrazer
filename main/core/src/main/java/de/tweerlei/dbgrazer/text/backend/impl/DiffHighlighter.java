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

import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.text.backend.BaseTextFormatter;
import de.tweerlei.dbgrazer.text.backend.EscapeXMLFormatter;
import de.tweerlei.dbgrazer.text.backend.TextFormatter;

/**
 * Syntax highlighting for unified diffs
 * 
 * @author Robert Wruck
 */
@Service
public class DiffHighlighter extends BaseTextFormatter
	{
	private static final String STYLE_HUNK = "diff-hunk";
	private static final String STYLE_ADD = "diff-add";
	private static final String STYLE_DEL = "diff-del";
	
	private final TextFormatter xmlFormatter;
	
	/**
	 * Constructor
	 */
	public DiffHighlighter()
		{
		super("Diff");
		xmlFormatter = new EscapeXMLFormatter();
		}
	
	@Override
	public String format(String value)
		{
		final StringBuilder sb = new StringBuilder();
		
		for (String s : StringUtils.split(value, "\\n"))
			{
			if (s.startsWith("@"))
				{
				sb.append("<span class=\"").append(STYLE_HUNK).append("\">");
				sb.append(xmlFormatter.format(s));
				sb.append("</span>");
				}
			else if (s.startsWith("+"))
				{
				sb.append("<span class=\"").append(STYLE_ADD).append("\">");
				sb.append(xmlFormatter.format(s));
				sb.append("</span>");
				}
			else if (s.startsWith("-"))
				{
				sb.append("<span class=\"").append(STYLE_DEL).append("\">");
				sb.append(xmlFormatter.format(s));
				sb.append("</span>");
				}
			else
				sb.append(xmlFormatter.format(s));
			sb.append("\n");
			}
		
		return (sb.toString());
		}
	
	@Override
	public boolean isXMLEncoded()
		{
		return (true);
		}
	}
