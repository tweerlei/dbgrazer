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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public class ViewImpl extends AbstractQueryImpl
	{
	private final List<SubQueryDef> subqueries;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param scope Applicability scope
	 * @param groupName Group name
	 * @param type Query type
	 * @param params Parameters
	 * @param subqueries Subqueries
	 * @param attributes Attributes
	 */
	public ViewImpl(String name, SchemaDef scope, String groupName, QueryType type, List<ParameterDef> params, List<SubQueryDef> subqueries, Map<String, String> attributes)
		{
		super(name, scope, groupName, type, params, attributes);
		this.subqueries = (subqueries == null) ? Collections.<SubQueryDef>emptyList() : Collections.unmodifiableList(new ArrayList<SubQueryDef>(subqueries));
		}
	
	@Override
	public String getStatement()
		{
		return ("");
		}
	
	@Override
	public Map<Integer, TargetDef> getTargetQueries()
		{
		return (Collections.emptyMap());
		}
	
	@Override
	public List<SubQueryDef> getSubQueries()
		{
		return (subqueries);
		}
	}
