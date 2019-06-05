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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public abstract class AbstractQueryImpl extends NamedBase implements Query
	{
	private final SchemaDef scope;
	private final String groupName;
	private final transient QueryType type;
	private final List<ParameterDef> params;
	private final Map<String, String> attributes;
	
	/**
	 * Constructor
	 * @param name Name
	 * @param scope Applicability scope
	 * @param groupName Group name
	 * @param type Query type
	 * @param params Parameters
	 * @param attributes Attributes
	 */
	public AbstractQueryImpl(String name, SchemaDef scope, String groupName, QueryType type, List<ParameterDef> params, Map<String, String> attributes)
		{
		super(name);
		this.scope = scope;
		this.groupName = groupName;
		this.type = type;
		this.params = (params == null) ? Collections.<ParameterDef>emptyList() : Collections.unmodifiableList(new ArrayList<ParameterDef>(params));
		this.attributes = (attributes == null) ? Collections.<String, String>emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<String, String>(attributes));
		}
	
	@Override
	public final SchemaDef getSourceSchema()
		{
		return (scope);
		}
	
	@Override
	public final String getGroupName()
		{
		return (groupName);
		}
	
	@Override
	public final List<ParameterDef> getParameters()
		{
		return (params);
		}
	
	@Override
	public final QueryType getType()
		{
		return (type);
		}
	
	@Override
	public final Map<String, String> getAttributes()
		{
		return (attributes);
		}
	}
