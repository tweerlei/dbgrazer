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

import java.util.Collections;
import java.util.Map;

import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.ResultOrientation;
import de.tweerlei.dbgrazer.query.model.ResultVisitor;
import de.tweerlei.dbgrazer.query.model.SubQueryResolver;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
public abstract class BaseQueryType extends NamedBase implements QueryType
	{
	private final LinkType linkType;
	private final Map<String, Class<?>> attributes;
	
	/**
	 * Constructor for query types
	 * @param name Name
	 * @param linkType LinkType
	 * @param attributes Supported attributes
	 */
	protected BaseQueryType(String name, LinkType linkType, Map<String, Class<?>> attributes)
		{
		super(name);
		this.linkType = linkType;
		this.attributes = (attributes == null) ? Collections.<String, Class<?>>emptyMap() : Collections.unmodifiableMap(attributes);
		}
	
	@Override
	public final LinkType getLinkType()
		{
		return linkType;
		}
	
	@Override
	public final Map<String, Class<?>> getSupportedAttributes()
		{
		return (attributes);
		}
	
	@Override
	public SubQueryResolver getSubQueryResolver()
		{
		return null;
		}
	
	@Override
	public ResultVisitor getPostProcessor()
		{
		return null;
		}
	
	@Override
	public boolean isScript()
		{
		return false;
		}
	
	@Override
	public boolean isManipulation()
		{
		return false;
		}
	
	@Override
	public ResultOrientation getOrientation()
		{
		return ResultOrientation.ACROSS;
		}
	
	@Override
	public boolean isExplorer()
		{
		return false;
		}
	
	@Override
	public boolean isColumnPrefixed()
		{
		return false;
		}
	
	@Override
	public boolean isSingleColumnSet()
		{
		return false;
		}
	
	@Override
	public boolean isAccumulatingResults()
		{
		return false;
		}
	}
