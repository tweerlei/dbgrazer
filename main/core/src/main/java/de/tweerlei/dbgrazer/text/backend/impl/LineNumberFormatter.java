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

import de.tweerlei.dbgrazer.text.backend.BaseTextFormatter;

/**
 * Format text
 * 
 * @author Robert Wruck
 */
public class LineNumberFormatter extends BaseTextFormatter
	{
	private static final Pattern PATTERN = Pattern.compile("\\r?\\n");
	
	private final String lineNumberFormat;
	
	/**
	 * Constructor
	 * @param lineNumberFormat Line number format pattern (suitable for String.format)
	 */
	public LineNumberFormatter(String lineNumberFormat)
		{
		super("NR");
		this.lineNumberFormat = lineNumberFormat;
		}
	
	@Override
	public String format(String value)
		{
		if (value == null)
			return (value);
		
		final StringBuffer sb = new StringBuffer();
		final Matcher m = PATTERN.matcher(value);
		int line;
		for (line = 1; m.find(); line++)
			{
			sb.append(String.format(lineNumberFormat, line));
			m.appendReplacement(sb, "\n");
			}
		
		sb.append(String.format(lineNumberFormat, line));
		m.appendTail(sb);
		
		return (sb.toString());
		}
	}
