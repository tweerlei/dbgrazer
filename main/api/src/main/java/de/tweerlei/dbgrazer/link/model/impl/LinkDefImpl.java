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
package de.tweerlei.dbgrazer.link.model.impl;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.util.impl.NamedBase;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.SchemaDef;

/**
 * Link definition
 * 
 * @author Robert Wruck
 */
public class LinkDefImpl extends NamedBase implements LinkDef
	{
	private final transient LinkType type;
	private final String description;
	private final String driver;
	private final String url;
	private final String username;
	private final String password;
	private final boolean writable;
	private final SchemaDef schema;
	private final Set<String> querySetNames;
	private final String groupName;
	private final String setName;
	private final String dialectName;
	private final String preDMLStatement;
	private final String postDMLStatement;
	private final Properties properties;
	
	/**
	 * Constructor
	 * @param type Link type
	 * @param name Link name
	 * @param description Description
	 * @param driver Driver class name
	 * @param url JDBC URL
	 * @param username Username
	 * @param password Password
	 * @param writable Writable flag
	 * @param preDMLStatement Pre DML statement
	 * @param postDMLStatement Post DML statement
	 * @param groupName Group name
	 * @param setName Set name
	 * @param dialectName Dialect name
	 * @param properties Additional properties passed to the JDBC driver
	 * @param schemaName Schema name
	 * @param subSchemaName SubSchema name
	 * @param querySetNames Query set names
	 */
	public LinkDefImpl(LinkType type, String name, String description, String driver, String url, String username, String password,
			boolean writable, String preDMLStatement, String postDMLStatement, String groupName, String setName, String dialectName,
			Properties properties, String schemaName, String subSchemaName, Set<String> querySetNames)
		{
		super(name);
		this.type = type;
		this.description = description;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.writable = writable;
		this.schema = new SchemaDef(schemaName, subSchemaName);
		this.querySetNames = (querySetNames == null) ? Collections.<String>emptySet() : Collections.unmodifiableSet(new TreeSet<String>(querySetNames));
		this.groupName = groupName;
		this.setName = setName;
		this.dialectName = dialectName;
		this.preDMLStatement = preDMLStatement;
		this.postDMLStatement = postDMLStatement;
		this.properties = properties;
		}

	@Override
	public LinkType getType()
		{
		return type;
		}

	@Override
	public String getDescription()
		{
		return description;
		}

	@Override
	public String getFullDescription()
		{
		return (formatDescription(!StringUtils.empty(description)));
		}
	
	@Override
	public String getSetDescription()
		{
		return (formatDescription(false));
		}
	
	private String formatDescription(boolean full)
		{
		final StringBuilder sb = new StringBuilder();
		sb.append(schema.getName());
		sb.append(" - ");
		sb.append(setName);
		if (full)
			{
			sb.append(" - ");
			sb.append(description);
			}
		
		return (sb.toString());
		}
	
	@Override
	public String getDriver()
		{
		return driver;
		}

	@Override
	public String getUrl()
		{
		return url;
		}

	@Override
	public String getUsername()
		{
		return username;
		}

	@Override
	public String getPassword()
		{
		return password;
		}

	@Override
	public SchemaDef getSchema()
		{
		return schema;
		}

	@Override
	public Set<String> getQuerySetNames()
		{
		return (querySetNames);
		}
	
	@Override
	public String getGroupName()
		{
		return groupName;
		}
	
	@Override
	public String getSetName()
		{
		return setName;
		}
	
	@Override
	public String getDialectName()
		{
		return dialectName;
		}
	
	@Override
	public String getPreDMLStatement()
		{
		return (preDMLStatement);
		}
	
	@Override
	public String getPostDMLStatement()
		{
		return (postDMLStatement);
		}
	
	@Override
	public Properties getProperties()
		{
		return properties;
		}
	
	@Override
	public boolean isWritable()
		{
		return writable;
		}
	}
