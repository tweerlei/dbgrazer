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
package de.tweerlei.dbgrazer.web.service.impl;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import de.tweerlei.dbgrazer.query.model.StatementHandler;
import de.tweerlei.dbgrazer.web.constant.MessageKeys;
import de.tweerlei.dbgrazer.web.formatter.DataFormatter;
import de.tweerlei.dbgrazer.web.formatter.SQLWriter;
import de.tweerlei.dbgrazer.web.formatter.impl.ExportDataFormatterImpl;
import de.tweerlei.dbgrazer.web.formatter.impl.SQLDataFormatterImpl;
import de.tweerlei.dbgrazer.web.formatter.impl.SQLWriterImpl;
import de.tweerlei.dbgrazer.web.formatter.impl.WebDataFormatterImpl;
import de.tweerlei.dbgrazer.web.formatter.impl.XMLDataFormatterImpl;
import de.tweerlei.dbgrazer.web.service.DataFormatterFactory;
import de.tweerlei.dbgrazer.web.session.RequestSettings;
import de.tweerlei.ermtools.dialect.SQLDialect;
import de.tweerlei.spring.config.ConfigAccessor;
import de.tweerlei.spring.config.impl.ConfigMap;

/**
 * Implementation that loads format strings from a MessageSource
 * 
 * @author Robert Wruck
 */
@Service
public class DataFormatterFactoryImpl implements DataFormatterFactory
	{
	// Default formats for web output
	private static final String DEFAULT_TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String DEFAULT_LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String DEFAULT_SHORT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DEFAULT_INTEGER_FORMAT = "0";
	private static final String DEFAULT_FLOAT_FORMAT = "#,##0.00######";
	
	private final ConfigAccessor configService;
	private final MessageSource messageSource;
	private final RequestSettings themeSettings;
	
	/**
	 * Constructor
	 * @param configService ConfigAccessor
	 * @param messageSource MessageSource
	 * @param themeSettings ThemeSettings
	 */
	@Autowired
	public DataFormatterFactoryImpl(ConfigAccessor configService, MessageSource messageSource, RequestSettings themeSettings)
		{
		this.configService = configService;
		this.messageSource = messageSource;
		this.themeSettings = themeSettings;
		}
	
	@Override
	public DataFormatter getWebFormatter()
		{
		final Locale locale = themeSettings.getLocale();
		
		final String tfmt = messageSource.getMessage(MessageKeys.TIMESTAMP_DATE_FORMAT, null, DEFAULT_TIMESTAMP_DATE_FORMAT, locale);
		final String lfmt = messageSource.getMessage(MessageKeys.LONG_DATE_FORMAT, null, DEFAULT_LONG_DATE_FORMAT, locale);
		final String sfmt = messageSource.getMessage(MessageKeys.SHORT_DATE_FORMAT, null, DEFAULT_SHORT_DATE_FORMAT, locale);
		final String ifmt = messageSource.getMessage(MessageKeys.INTEGER_FORMAT, null, DEFAULT_INTEGER_FORMAT, locale);
		final String ffmt = messageSource.getMessage(MessageKeys.FLOAT_FORMAT, null, DEFAULT_FLOAT_FORMAT, locale);
		
		return (new WebDataFormatterImpl(tfmt, lfmt, sfmt, ifmt, ffmt, locale, themeSettings.getTimeZone(), configService.get(ConfigKeys.COLUMN_SIZE_LIMIT), configService.get(ConfigKeys.HEX_COLUMN_LIMIT)));
		}
	
	@Override
	public DataFormatter getExportFormatter()
		{
		final Locale locale = themeSettings.getLocale();
		
		final String tfmt = messageSource.getMessage(MessageKeys.TIMESTAMP_DATE_FORMAT, null, DEFAULT_TIMESTAMP_DATE_FORMAT, locale);
		final String lfmt = messageSource.getMessage(MessageKeys.LONG_DATE_FORMAT, null, DEFAULT_LONG_DATE_FORMAT, locale);
		final String sfmt = messageSource.getMessage(MessageKeys.SHORT_DATE_FORMAT, null, DEFAULT_SHORT_DATE_FORMAT, locale);
		final String ifmt = messageSource.getMessage(MessageKeys.INTEGER_FORMAT, null, DEFAULT_INTEGER_FORMAT, locale);
		final String ffmt = messageSource.getMessage(MessageKeys.FLOAT_FORMAT, null, DEFAULT_FLOAT_FORMAT, locale);
		
		return (new ExportDataFormatterImpl(tfmt, lfmt, sfmt, ifmt, ffmt, locale, themeSettings.getTimeZone(), Integer.MAX_VALUE));
		}
	
	@Override
	public DataFormatter getSQLFormatter(SQLDialect dialect)
		{
		return (new SQLDataFormatterImpl(dialect.getTimestampFormat(), dialect.getDatetimeFormat(), dialect.getDateFormat(), dialect.supportsBoolean(), TimeZone.getDefault(), Integer.MAX_VALUE));
		}
	
	@Override
	public DataFormatter getXMLFormatter()
		{
		return (new XMLDataFormatterImpl(TimeZone.getDefault(), Integer.MAX_VALUE));
		}
	
	@Override
	public SQLWriter getSQLWriter(StatementHandler h, SQLDialect dialect, boolean pretty)
		{
		return (new SQLWriterImpl(h,
				dialect,
				getSQLFormatter(dialect),
				pretty
				));
		}
	
	@Override
	public TimeZone getTimeZone()
		{
		return (themeSettings.getTimeZone());
		}
	
	@Override
	public String getMessage(String key, Object... args)
		{
		return (messageSource.getMessage(key, args, "", themeSettings.getLocale()));
		}
	
	@Override
	public void doWithDefaultTheme(Runnable r)
		{
		final ConfigAccessor config = themeSettings.getConfig();
		try	{
			themeSettings.setConfig(new ConfigMap());
			
			r.run();
			}
		finally
			{
			themeSettings.setConfig(config);
			}
		}
	}
