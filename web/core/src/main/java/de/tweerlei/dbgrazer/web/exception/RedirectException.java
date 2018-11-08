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
package de.tweerlei.dbgrazer.web.exception;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

/**
 * Thrown from a controller to redirect the user to a different page.
 * 
 * @author Robert Wruck <wruck@tweerlei.de>
 */
public class RedirectException extends ModelAndViewDefiningException
	{
	/**
	 * Constructor
	 * @param url Target URL
	 */
	public RedirectException(String url)
		{
		super(new ModelAndView("redirect:" + url));
		}
	}
