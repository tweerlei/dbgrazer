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
package de.tweerlei.dbgrazer.plugins.jdbc.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.query.backend.ParamReplacer;

/**
 * Replace ?n? placeholders in the stmt with the n-th parameter and place all remaining parameters into jdbcParams
 * 
 * @author Robert Wruck
 */
public class JdbcParamReplacer extends ParamReplacer
	{
	private static final String SQL_DATE_FORMAT = "'TIMESTAMP'''yyyy-MM-dd HH:mm:ss''";
	
	private final KeywordService keywordService;
	private final Set<Integer> usedParams;
	private final DateFormat format;
	
	/**
	 * Constructor
	 * @param params Actual parameters
	 * @param keywordService KeywordService
	 */
	public JdbcParamReplacer(List<Object> params, KeywordService keywordService)
		{
		super(params);
		this.usedParams = new HashSet<Integer>();
		this.keywordService = keywordService;
		this.format = new SimpleDateFormat(SQL_DATE_FORMAT);
		}
	
	@Override
	protected String getReplacement(int index)
		{
		usedParams.add(index);
		
		final Object p = getParams().get(index);
		if (p == null)
			return ("");
		else if (p instanceof Date)
			return (format.format(p));
		else
			return (keywordService.normalizeWord(p.toString()));
		}
	
	/**
	 * Get the parameters that were not replaced
	 * @return Remaining parameters
	 */
	public List<Object> getRemainingParams()
		{
		final List<Object> jdbcParams = new ArrayList<Object>(getParams().size());
		int i = 0;
		for (Object p : getParams())
			{
			if (!usedParams.contains(i))
				jdbcParams.add(p);
			i++;
			}
		return (jdbcParams);
		}
	}
