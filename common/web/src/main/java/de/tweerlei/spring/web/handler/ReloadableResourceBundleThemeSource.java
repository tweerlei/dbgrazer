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
package de.tweerlei.spring.web.handler;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;

/**
 * ResourceBundleThemeSource that uses ReloadableResourceBundles and thus is capable
 * of loading theme data from the file system.
 * 
 * @author Robert Wruck
 */
public class ReloadableResourceBundleThemeSource extends ResourceBundleThemeSource
	{
	private String defaultEncoding;
	private MessageSource parentMessageSource;
	private ReloadableResourceBundleMessageSource messageSource;
	
	/**
	 * Set the defaultEncoding
	 * @param defaultEncoding the defaultEncoding to set
	 */
	public void setDefaultEncoding(String defaultEncoding)
		{
		this.defaultEncoding = defaultEncoding;
		}
	
	/**
	 * Set the parentMessageSource
	 * @param parentMessageSource the parentMessageSource to set
	 */
	public void setParentMessageSource(MessageSource parentMessageSource)
		{
		this.parentMessageSource = parentMessageSource;
		}
	
	/**
	 * Reload the message source
	 */
	public void reload()
		{
		if (messageSource != null)
			messageSource.clearCache();
		}
	
	@Override
	protected MessageSource createMessageSource(String basename)
		{
		messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setFallbackToSystemLocale(false);
		if (defaultEncoding != null)
			messageSource.setDefaultEncoding(defaultEncoding);
		if (parentMessageSource != null)
			messageSource.setParentMessageSource(parentMessageSource);
		messageSource.setBasename(basename);
		return (messageSource);
		}
	}
