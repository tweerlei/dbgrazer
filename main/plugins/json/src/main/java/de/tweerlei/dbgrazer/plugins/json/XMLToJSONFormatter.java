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

import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.extension.json.handler.PrettyPrintJSONHandler;
import de.tweerlei.dbgrazer.extension.json.parser.JSONHandler;
import de.tweerlei.dbgrazer.extension.xml.parser.XMLParser;
import de.tweerlei.dbgrazer.text.backend.BaseTextFormatter;

/**
 * Format text
 * 
 * @author Robert Wruck
 */
@Service
public class XMLToJSONFormatter extends BaseTextFormatter
	{
	/**
	 * Constructor
	 */
	public XMLToJSONFormatter()
		{
		super("XMLJSON-Formatted");
		}
	
	@Override
	public String format(String value)
		{
		final JSONHandler h = new PrettyPrintJSONHandler();
		new XMLParser(new XMLToJSONHandler(h)).parse(value);
		return (h.toString());
		}
	}
