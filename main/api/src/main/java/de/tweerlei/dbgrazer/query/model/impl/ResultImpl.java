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
package de.tweerlei.dbgrazer.query.model.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.Result;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.RowSet;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public class ResultImpl implements Result
	{
	private final Query query;
	private final List<String> parameters;
	private final Map<String, RowSet> rowSets;
	
	/**
	 * Constructor
	 * @param query Query
	 */
	public ResultImpl(Query query)
		{
		this.query = query;
		this.rowSets = new LinkedHashMap<String, RowSet>();
		this.parameters = new ArrayList<String>();
		}
	
	@Override
	public Query getQuery()
		{
		return (query);
		}
	
	@Override
	public List<String> getParameterValues()
		{
		return (parameters);
		}
	
	@Override
	public Map<String, RowSet> getRowSets()
		{
		return rowSets;
		}
	
	@Override
	public RowSet getFirstRowSet()
		{
		if (rowSets.isEmpty())
			return (null);
		
		return (rowSets.values().iterator().next());
		}
	
	@Override
	public void accept(ResultVisitor v)
		{
		if (v.startResult(this))
			{
			for (RowSet rs : rowSets.values())
				rs.accept(v, 0);
			v.endResult(this);
			}
		}
	
	@Override
	public ResultImpl clone()
		{
		final ResultImpl ret = new ResultImpl(query);
		
		ret.getParameterValues().addAll(parameters);
		for (Map.Entry<String, RowSet> ent : rowSets.entrySet())
			ret.getRowSets().put(ent.getKey(), ent.getValue().clone());
		
		return (ret);
		}
	}
