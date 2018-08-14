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
package de.tweerlei.spring.web.view;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Return a fixed view instance for view names matching a pattern
 * 
 * @author Robert Wruck
 */
public class PatternViewResolver implements ViewResolver, Ordered
	{
	private Pattern pattern;
	private View view;
	private int order;
	
	public PatternViewResolver()
		{
		pattern = Pattern.compile(".*");
		view = null;
		order = 0;
		}
	
	public void setViewNamePattern(String p)
		{
		pattern = Pattern.compile(p);
		}
	
	public void setView(View v)
		{
		view = v;
		}
	
	public void setOrder(int o)
		{
		order = o;
		}
	
	public int getOrder()
		{
		return (order);
		}
	
	public View resolveViewName(String viewName, Locale locale) throws Exception
		{
		if (pattern.matcher(viewName).matches())
			return (view);
		
		return (null);
		}
	}
