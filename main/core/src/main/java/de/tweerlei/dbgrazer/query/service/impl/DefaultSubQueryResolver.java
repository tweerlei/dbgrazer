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
package de.tweerlei.dbgrazer.query.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.SubQueryInfo;
import de.tweerlei.dbgrazer.query.model.SubQueryResolver;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public class DefaultSubQueryResolver implements SubQueryResolver
	{
	/** The instance */
	public static final DefaultSubQueryResolver INSTANCE = new DefaultSubQueryResolver();
	
	private DefaultSubQueryResolver()
		{
		}
	
	@Override
	public List<SubQueryInfo> resolve(Query mainQuery, List<Object> params, List<SubQueryInfo> subQueries, List<SubQueryInfo> targetQueries)
		{
		final List<SubQueryInfo> ret = new ArrayList<SubQueryInfo>(subQueries.size());
		
		for (SubQueryInfo s : subQueries)
			{
			if (s.getCurried().isEmpty())
				{
				final int n = s.getQuery().getParameters().size();
				if (n == params.size())
					ret.add(new SubQueryInfo(s.getQuery(), s.getQuery().getName(), null, null, params));
				else
					{
					final List<Object> curried = new ArrayList<Object>(n);
					int i = 0;
					for (Object v : params)
						{
						if (i < n)
							curried.add(v);
						i++;
						}
					ret.add(new SubQueryInfo(s.getQuery(), s.getQuery().getName(), null, null, curried));
					}
				}
			else
				{
				final int n = s.getQuery().getParameters().size();
				final List<Object> curried = new ArrayList<Object>(n);
				int i = 0;
				for (String v : s.getCurried())
					{
					if (i < n)
						{
						// TODO: Translate String values to Objects
						curried.add(v);
						}
					i++;
					}
				for (Object v : params)
					{
					if (i < n)
						curried.add(v);
					i++;
					}
				ret.add(new SubQueryInfo(s.getQuery(), s.getQuery().getName(), StringUtils.join(s.getCurried().iterator(), ", "), s.getCurried(), curried));
				}
			}
		
		return (ret);
		}
	
	@Override
	public List<ParameterDef> getAdditionalParameters(Query mainQuery)
		{
		return (Collections.emptyList());
		}
	}
