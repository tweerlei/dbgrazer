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

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.text.backend.BaseTextFormatter;

/**
 * Format text
 * 
 * @author Robert Wruck
 */
@Service
public class HTMLFormatter extends BaseTextFormatter
	{
	private static final Pattern HTML_PATTERN = Pattern.compile("<html[\\s>]", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Constructor
	 */
	public HTMLFormatter()
		{
		super("HTML");
		}
	
	@Override
	public String format(String value)
		{
		final String body;
		
		if (value == null)
			body = "";
		else if (HTML_PATTERN.matcher(value).find())
			return (value);
		else
			body = value;
		
		return ("<html><body>" + body + "</body></html>");
		}
	
	@Override
	public boolean isXMLEncoded()
		{
		return (false);
		}
	}
