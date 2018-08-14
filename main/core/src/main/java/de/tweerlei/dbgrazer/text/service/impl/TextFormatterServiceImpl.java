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
package de.tweerlei.dbgrazer.text.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.text.backend.TextFormatter;
import de.tweerlei.dbgrazer.text.service.TextFormatterService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class TextFormatterServiceImpl implements TextFormatterService
	{
	private final Logger logger;
	private final Map<String, TextFormatter> formats;
	
	/**
	 * Constructor
	 * @param formats TextFormatters
	 */
	@Autowired(required = false)
	public TextFormatterServiceImpl(Set<TextFormatter> formats)
		{
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.formats = Collections.unmodifiableMap(new NamedMap<TextFormatter>(formats));
		
		this.logger.log(Level.INFO, "Text formatters: " + this.formats);
		}
	
	/**
	 * Constructor used when no TextFormatter instances are available
	 */
	public TextFormatterServiceImpl()
		{
		this(Collections.<TextFormatter>emptySet());
		}
	
	@Override
	public Set<String> getSupportedTextFormats()
		{
		final Set<String> formatNames = new TreeSet<String>();
		
		for (TextFormatter c : formats.values())
			formatNames.add(c.getName());
		
		return (formatNames);
		}
	
	@Override
	public String format(String text, String format)
		{
		final TextFormatter fmt = formats.get(format);
		if (fmt == null)
			return (text);
		
		return (fmt.format(text));
		}
	
	@Override
	public boolean isXMLEncoded(String format)
		{
		final TextFormatter fmt = formats.get(format);
		if (fmt == null)
			return (false);
		
		return (fmt.isXMLEncoded());
		}
	}
