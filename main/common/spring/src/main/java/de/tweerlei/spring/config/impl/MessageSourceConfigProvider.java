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
package de.tweerlei.spring.config.impl;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import de.tweerlei.spring.config.ConfigProvider;

/**
 * ConfigProvider that uses a Spring MessageSource
 * 
 * @author Robert Wruck
 */
public class MessageSourceConfigProvider implements ConfigProvider
	{
	private final MessageSource messageSource;
	private final Locale locale;
	
	/**
	 * Constructor
	 * @param messageSource MessageSource
	 * @param locale Locale for obtaining messages
	 */
	public MessageSourceConfigProvider(MessageSource messageSource, Locale locale)
		{
		this.messageSource = messageSource;
		this.locale = locale;
		}
	
	public String get(String key)
		{
		try	{
			return (messageSource.getMessage(key, null, locale));
			}
		catch (NoSuchMessageException e)
			{
			return (null);
			}
		}
	
	public Map<String, String> list()
		{
		return (Collections.emptyMap());
		}
	}
