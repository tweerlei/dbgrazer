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
package de.tweerlei.dbgrazer.link.backend.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.link.backend.LinkPersister;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.impl.LinkDefImpl;

/**
 * Property file based impl.
 * 
 * @author Robert Wruck
 */
@Service
public class LinkPersisterImpl implements LinkPersister
	{
	private static final String PROP_TYPE = "type";
	private static final String PROP_DRIVER_CLASS = "driverClass";
	private static final String PROP_JDBC_URL = "jdbcUrl";
	private static final String PROP_USER = "user";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_WRITABLE = "writable";
	private static final String PROP_PRE_DML = "preDML";
	private static final String PROP_POST_DML = "postDML";
	private static final String PROP_SCHEMA = "schema";
	private static final String PROP_SUBSCHEMA = "subSchema";
	private static final String PROP_QUERY_SETS = "querySets";
	private static final String PROP_GROUP = "group";
	private static final String PROP_SET = "set";
	private static final String PROP_DIALECT = "dialect";
	private static final String PROP_DESCRIPTION = "description";
	
	private final KeywordService keywordService;
	private final Map<String, LinkType> linkTypes;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 * @param linkTypes All known link types
	 */
	@Autowired(required = false)
	public LinkPersisterImpl(KeywordService keywordService, Set<LinkType> linkTypes)
		{
		this.keywordService = keywordService;
		this.linkTypes = new NamedMap<LinkType>(linkTypes);
		}
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 */
	@Autowired(required = false)
	public LinkPersisterImpl(KeywordService keywordService)
		{
		this(keywordService, Collections.<LinkType>emptySet());
		}
	
	@Override
	public LinkDef readLink(Reader reader, String name) throws IOException
		{
		final Properties props = new Properties();
		props.load(reader);
		
		final String typeString = props.getProperty(PROP_TYPE);
		props.remove(PROP_TYPE);
		final LinkType type;
		if (StringUtils.empty(typeString))
			{
			// Legacy support
			if (linkTypes.isEmpty())
				type = null;
			else
				type = linkTypes.values().iterator().next();
			}
		else
			type = linkTypes.get(typeString);
		
		if (type == null)
			throw new IOException("Invalid link type: " + typeString);
		
		final String driver = props.getProperty(PROP_DRIVER_CLASS);
		props.remove(PROP_DRIVER_CLASS);
		final String url = props.getProperty(PROP_JDBC_URL);
		props.remove(PROP_JDBC_URL);
		final String username = props.getProperty(PROP_USER);
		props.remove(PROP_USER);
		final String password = props.getProperty(PROP_PASSWORD);
		props.remove(PROP_PASSWORD);
		final boolean writable = Boolean.parseBoolean(props.getProperty(PROP_WRITABLE));
		props.remove(PROP_WRITABLE);
		final String schema = props.getProperty(PROP_SCHEMA);
		props.remove(PROP_SCHEMA);
		final String subSchema = props.getProperty(PROP_SUBSCHEMA);
		props.remove(PROP_SUBSCHEMA);
		final String group = props.getProperty(PROP_GROUP);
		props.remove(PROP_GROUP);
		final String set = props.getProperty(PROP_SET);
		props.remove(PROP_SET);
		final String dialect = props.getProperty(PROP_DIALECT);
		props.remove(PROP_DIALECT);
		final String preDML = props.getProperty(PROP_PRE_DML);
		props.remove(PROP_PRE_DML);
		final String postDML = props.getProperty(PROP_POST_DML);
		props.remove(PROP_POST_DML);
		final String description = props.getProperty(PROP_DESCRIPTION);
		props.remove(PROP_DESCRIPTION);
		
		final String groups = props.getProperty(PROP_QUERY_SETS);
		props.remove(PROP_QUERY_SETS);
		final Set<String> groupNames = new HashSet<String>(keywordService.extractValues(groups));
		
		return (new LinkDefImpl(type, name, StringUtils.notNull(description), driver, url, username, password, writable,
				StringUtils.notNull(preDML), StringUtils.notNull(postDML), StringUtils.notNull(group), StringUtils.empty(set) ? name : set, StringUtils.notNull(dialect),
				props, schema, subSchema, groupNames));
		}
	
	@Override
	public void writeLink(Writer writer, LinkDef c) throws IOException
		{
		final Properties props = new Properties();
		props.putAll(c.getProperties());
		
		props.setProperty(PROP_TYPE, c.getType().getName());
		props.setProperty(PROP_DRIVER_CLASS, c.getDriver());
		props.setProperty(PROP_JDBC_URL, c.getUrl());
		props.setProperty(PROP_USER, c.getUsername());
		props.setProperty(PROP_PASSWORD, c.getPassword());
		props.setProperty(PROP_WRITABLE, Boolean.toString(c.isWritable()));
		props.setProperty(PROP_SCHEMA, sanitizeName(c.getSchema().getName(), false));
		props.setProperty(PROP_SUBSCHEMA, sanitizeName(c.getSchema().getVersion(), true));
		props.setProperty(PROP_GROUP, sanitizeGroupName(c.getGroupName()));
		props.setProperty(PROP_DIALECT, sanitizeName(c.getDialectName(), true));
		props.setProperty(PROP_PRE_DML, c.getPreDMLStatement());
		props.setProperty(PROP_POST_DML, c.getPostDMLStatement());
		// Don't sanitize description and set
		props.setProperty(PROP_DESCRIPTION, c.getDescription());
		props.setProperty(PROP_SET, c.getSetName());
		
		final Set<String> groupNames = new HashSet<String>();
		for (String g : c.getQuerySetNames())
			groupNames.add(sanitizeName(g, false));
		
		if (!groupNames.isEmpty())
			props.setProperty(PROP_QUERY_SETS, keywordService.combineValues(groupNames));
		
		props.store(writer, null);
		}
	
	private String sanitizeName(String name, boolean allowEmpty) throws IOException
		{
		final String s = keywordService.normalizeName(name);
		if (StringUtils.empty(s))
			{
			if (!allowEmpty)
				throw new IOException("Invalid name: " + name);
			return ("");
			}
		return (s);
		}
	
	private String sanitizeGroupName(String name)
		{
		final String s = keywordService.normalizeGroup(name);
		if (StringUtils.empty(s))
			return ("");
		return (s);
		}
	}
