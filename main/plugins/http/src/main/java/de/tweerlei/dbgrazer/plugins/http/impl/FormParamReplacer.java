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
package de.tweerlei.dbgrazer.plugins.http.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import de.tweerlei.common.xml.XMLWriter;
import de.tweerlei.dbgrazer.query.backend.ParamReplacer;

/**
 * Replace ?n? placeholders in the stmt with the n-th parameter and place all remaining parameters into jdbcParams
 * 
 * @author Robert Wruck
 */
public class FormParamReplacer extends ParamReplacer
	{
	private static final String URL_CHARSET = "UTF-8";
	
	/**
	 * Constructor
	 * @param params Actual parameters
	 */
	public FormParamReplacer(List<Object> params)
		{
		super(params);
		}
	
	@Override
	protected String getReplacement(int index)
		{
		final Object p = getParams().get(index);
		
		if (p == null)
			return ("");
		
		final String value;
		if (p instanceof Date)
			value = XMLWriter.printDateTime((Date) p);
		else
			value = p.toString();
		
		try	{
			return (URLEncoder.encode(value, URL_CHARSET));
			}
		catch (UnsupportedEncodingException e)
			{
			return ("");
			}
		}
	}
