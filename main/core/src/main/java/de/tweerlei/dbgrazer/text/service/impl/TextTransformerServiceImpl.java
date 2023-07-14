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

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.text.backend.TextFormatter;
import de.tweerlei.dbgrazer.text.backend.impl.EscapeXMLFormatter;
import de.tweerlei.dbgrazer.text.backend.impl.LineNumberTableFormatter;
import de.tweerlei.dbgrazer.text.service.TextFormatterService;
import de.tweerlei.dbgrazer.text.service.TextTransformerService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class TextTransformerServiceImpl implements TextTransformerService, ConfigListener
	{
	private static final char SUFFIX_SEPARATOR = '-';
	private static final String SYNTAX_COLOR_SUFFIX = "Highlight";
	private static final String FORMATTED_SUFFIX = "Formatted";
	private static final String STRUCTURED_SUFFIX = "Structured";
	
	private final ConfigService configService;
	private final TextFormatterService formatterService;
	private final Logger logger;
	private TextFormatter xmlFormatter;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param formatterService TextFormatterService
	 */
	@Autowired
	public TextTransformerServiceImpl(ConfigService configService, TextFormatterService formatterService)
		{
		this.configService = configService;
		this.formatterService = formatterService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		}
	
	/**
	 * Read attributes from the manifest
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		this.xmlFormatter = new EscapeXMLFormatter();
		}
	
	@Override
	public Set<String> getSupportedTextFormats()
		{
		final Set<String> formatNames = new TreeSet<String>();
		
		for (String c : formatterService.getSupportedTextFormats())
			{
			if (c.indexOf(SUFFIX_SEPARATOR) < 0)
				formatNames.add(c);
			}
		
		return (formatNames);
		}
	
	@Override
	public String format(String text, String format, Set<Option> options)
		{
		final boolean numbering = (options != null && options.contains(Option.LINE_NUMBERS) && !options.contains(Option.STRUCTURE));
		
		String ret = null;
		String formatterError = null;
		boolean needsEscaping = true;
		
		try	{
			final String formatName = findEffectiveFormatName(format, options);
			if (formatName != null)
				{
				ret = formatterService.format(text, formatName);
				needsEscaping = !formatterService.isXMLEncoded(formatName);
				}
			}
		catch (RuntimeException e)
			{
			logger.log(Level.INFO, "format", e);
			formatterError = xmlFormatter.format(e.getMessage());
			}
		
		// If no formatter matched, use original text
		if (ret == null)
			ret = text;
		
		if (needsEscaping)
			{
			// syntax coloring includes XML formatting while normal text formatters might output reserved characters 
			ret = xmlFormatter.format(ret);
			}
		
		// Add line numbers if requested
		if (numbering)
			{
			final TextFormatter lineFormatter = new LineNumberTableFormatter(formatterError);
			ret = lineFormatter.format(ret);
			}
		else if (formatterError != null)
			{
			ret = "[" + formatterError + "]\n" + ret;
			}
		
		return (ret);
		}
	
	private String findEffectiveFormatName(String format, Set<Option> options)
		{
		final boolean structured = (options != null && options.contains(Option.STRUCTURE));
		final boolean formatted = (options != null && options.contains(Option.FORMATTING));
		final boolean color = (options != null && options.contains(Option.SYNTAX_COLORING));
		
		final String formatPrefix = StringUtils.notNull(format);
		final Set<String> knownFormats = formatterService.getSupportedTextFormats();
		
		// If structured output requested, check for supporting formatters (with or without coloring)
		if (structured)
			{
			if (color)
				{
				final String formatName = formatPrefix + SUFFIX_SEPARATOR + STRUCTURED_SUFFIX + SUFFIX_SEPARATOR + SYNTAX_COLOR_SUFFIX;
				if (knownFormats.contains(formatName))
					return (formatName);
				}
			final String formatName = formatPrefix + SUFFIX_SEPARATOR + STRUCTURED_SUFFIX;
			if (knownFormats.contains(formatName))
				return (formatName);
			}
		
		// If formatted output requested, check for supporting formatters (with or without coloring)
		if (formatted)
			{
			if (color)
				{
				final String formatName = formatPrefix + SUFFIX_SEPARATOR + FORMATTED_SUFFIX + SUFFIX_SEPARATOR + SYNTAX_COLOR_SUFFIX;
				if (knownFormats.contains(formatName))
					return (formatName);
				}
			final String formatName = formatPrefix + SUFFIX_SEPARATOR + FORMATTED_SUFFIX;
			if (knownFormats.contains(formatName))
				return (formatName);
			}
		
		// If human readable / formatting not requested or no formatter matched, check for default formatters (with or without coloring)
		if (color)
			{
			final String formatName = formatPrefix + SUFFIX_SEPARATOR + SYNTAX_COLOR_SUFFIX;
			if (knownFormats.contains(formatName))
				return (formatName);
			}
		if (knownFormats.contains(formatPrefix))
			return (formatPrefix);
		
		return (null);
		}
	}
