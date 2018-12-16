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
package de.tweerlei.dbgrazer.web.taglib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.math.Rational;
import de.tweerlei.dbgrazer.web.service.FrontendHelperService;

/**
 * JSP tag library
 *
 * @author Robert Wruck
 */
@Service
public class JspFunctions
	{
	// Hack to make the FrontendHelperService available to static JSP calls
	private static FrontendHelperService frontendHelper;
	
	/**
	 * Constructor 
	 * @param helper FrontendHelperService
	 */
	@Autowired
	public JspFunctions(FrontendHelperService helper)
		{
		frontendHelper = helper;
		}
	
	/**
	 * Encode a String for passing as query parameter.
	 * The String "123  456" is split into multiple parameters like:
	 * &amp;params[0]=123&amp;params[1]=456
	 * @param s Parameter string
	 * @return Query String
	 */
	public static String paramEncode(String s)
		{
		return (frontendHelper.paramEncode(s, true));
		}
	
	/**
	 * Extract a query parameter from a String.
	 * This will return the first non-whitespace word from the String,
	 * e.g. "123 Label" will become "123".
	 * @param s Parameter string
	 * @return Query String
	 */
	public static String paramExtract(String s)
		{
		return (frontendHelper.paramExtract(s));
		}
	
	/**
	 * Format a String as link title.
	 * Only the last component is returned:
	 * "123  456" will yield "456".
	 * @param s Parameter string
	 * @return Link title
	 */
	public static String getLinkTitle(String s)
		{
		return (frontendHelper.getLinkTitle(s));
		}
	
	/**
	 * Get the number of menu items per column for a total number of items
	 * @param n Item count
	 * @param r Aspect ratio
	 * @return Row count
	 */
	public static int getMenuRows(int n, Rational r)
		{
		return (frontendHelper.getMenuRows(n, r));
		}
	}
