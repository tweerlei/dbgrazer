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
package de.tweerlei.dbgrazer.query.backend.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.common.util.impl.NamedMap;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.query.backend.QueryPersister;
import de.tweerlei.dbgrazer.query.model.ColumnType;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ParameterTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;

/**
 * QueryLoader that loads query definitions from files
 * 
 * @author Robert Wruck
 */
@Service
public class QueryPersisterImpl implements QueryPersister
	{
	private static final String PROP_TYPE = "Type";
	private static final String PROP_GROUP = "Group";
	private static final String PROP_PARAMETERS = "Parameters";
	private static final String PROP_LINKS = "Links";
	private static final String PROP_VIEWS = "Views";
	private static final String PROP_ATTRIBUTES = "Attributes";
	
	private static final String LINE_SEPARATOR = "\n";
	private static final char HEADER_SEPARATOR = ':';
	private static final String PART_SEPARATOR = ":";
	private static final String PARAM_MARKER = "*";
	
	private final KeywordService keywordService;
	private final Map<String, QueryType> queryTypes;
	
	/**
	 * Constructor
	 * @param keywordService KeywordService
	 * @param queryTypes All known query types
	 */
	@Autowired
	public QueryPersisterImpl(KeywordService keywordService, Set<QueryType> queryTypes)
		{
		this.keywordService = keywordService;
		this.queryTypes = new NamedMap<QueryType>(queryTypes);
		}
	
	@Override
	public Query readQuery(Reader reader, String name, SchemaDef scope) throws IOException
		{
		QueryType type = null;
		String groupName = "";
		List<ParameterDef> params = null;
		Map<Integer, TargetDef> targets = null;
		List<SubQueryDef> views = null;
		Map<String, String> attributes = null;
		final StringBuilder stmt = new StringBuilder();
		boolean inStmt = false;
		
		final BufferedReader r = new BufferedReader(reader);
		for (;;)
			{
			final String line = r.readLine();
			if (line == null)
				break;
			
			if (inStmt)
				{
				if (stmt.length() > 0)
					stmt.append(LINE_SEPARATOR);
				stmt.append(line);
				continue;
				}
			
			if (line.length() == 0)
				{
				inStmt = true;
				continue;
				}
			
			final int i = line.indexOf(HEADER_SEPARATOR);
			if (i < 0)
				continue;
			
			final String key = line.substring(0, i).trim();
			final String value = line.substring(i + 1).trim();
			
			if (key.equals(PROP_TYPE))
				{
				type = queryTypes.get(value);
				if (type == null)
					throw new IOException("Invalid query type: " + value);
				}
			else if (key.equals(PROP_GROUP))
				{
				groupName = value;
				}
			else if (key.equals(PROP_PARAMETERS))
				{
				params = parseParams(value);
				}
			else if (key.equals(PROP_LINKS))
				{
				targets = parseTargets(value);
				}
			else if (key.equals(PROP_VIEWS))
				{
				views = parseViews(value);
				}
			else if (key.equals(PROP_ATTRIBUTES))
				{
				attributes = parseAttributes(value);
				}
			}
		
		if (type == null)
			throw new IOException("Missing query type");
		
		if (type.getResultType().isView())
			{
//			if (CollectionUtils.empty(views))
//				throw new IOException("Invalid view definition");
			
			return (new ViewImpl(name, scope, groupName, type, params, views, attributes));
			}
		else
			{
			final String s = stmt.toString().trim();
//			if (StringUtils.empty(s))
//				throw new IOException("Invalid query definition");
			
			return (new QueryImpl(name, scope, groupName, s, type, params, targets, attributes));
			}
		}
	
	private List<ParameterDef> parseParams(String line) throws IOException
		{
		if (StringUtils.empty(line))
			return (null);
		
		final List<ParameterDef> ret = new ArrayList<ParameterDef>();
		
		for (String s : keywordService.extractValues(line))
			{
			final String[] p = StringUtils.split(s, PART_SEPARATOR);
			
			if (p.length < 2)
				throw new IOException("Invalid parameter definition: " + s);
			
			final String name = p[0].trim();
			final ColumnType type;
			try	{
				type = ColumnType.valueOf(p[1].trim());
				}
			catch (IllegalArgumentException e)
				{
				throw new IOException("Invalid parameter type: " + p[1]);
				}
			final String query = (p.length > 2) ? p[2].trim() : "";
			
			if (StringUtils.empty(name) || type == null)
				throw new IOException("Invalid parameter definition: " + s);
			
			ret.add(new ParameterDefImpl(name, type, query));
			}
		
		return (ret);
		}
	
	private Map<Integer, TargetDef> parseTargets(String line) throws IOException
		{
		if (StringUtils.empty(line))
			return (null);
		
		final Map<Integer, TargetDef> ret = new HashMap<Integer, TargetDef>();
		
		for (String s : keywordService.extractValues(line))
			{
			final String[] p = StringUtils.split(s, PART_SEPARATOR);
			
			if (p.length < 2)
				throw new IOException("Invalid link definition: " + s);
			
			final Integer index;
			try	{
				index = Integer.valueOf(p[0].trim());
				if (index < 0)
					throw new IOException("Invalid parameter definition: " + s);
				}
			catch (NumberFormatException e)
				{
				throw new IOException("Invalid link definition: " + s);
				}
			
			final String target = p[1].trim();
			if (target.startsWith(PARAM_MARKER))
				{
				final String param = target.substring(PARAM_MARKER.length());
				if (StringUtils.empty(param))
					throw new IOException("Invalid parameter definition: " + s);
				
				ret.put(index, new ParameterTargetImpl(param));
				}
			else
				{
				if (StringUtils.empty(target))
					throw new IOException("Invalid parameter definition: " + s);
				
				ret.put(index, new QueryTargetImpl(target));
				}
			}
		
		return (ret);
		}
	
	private List<SubQueryDef> parseViews(String line) throws IOException
		{
		if (StringUtils.empty(line))
			return (null);
		
		final List<SubQueryDef> ret = new ArrayList<SubQueryDef>();
		
		for (String s : keywordService.extractValues(line))
			{
			final String[] p = StringUtils.split(s, PART_SEPARATOR);
			
			final String n = p[0];
			if (StringUtils.empty(n))
				throw new IOException("Invalid view definition: " + s);
			
			final List<String> params;
			if (p.length > 1)
				{
				params = new ArrayList<String>(p.length - 1);
				for (int i = 1; i < p.length; i++)
					params.add(p[i]);
				}
			else
				params = null;
			
			ret.add(new SubQueryDefImpl(n, params));
			}
		
		return (ret);
		}
	
	private Map<String, String> parseAttributes(String line) throws IOException
		{
		if (StringUtils.empty(line))
			return (null);
		
		final Map<String, String> ret = new HashMap<String, String>();
		
		for (String s : keywordService.extractValues(line))
			{
			final String[] p = StringUtils.split(s, PART_SEPARATOR);
			
			if (p.length != 2)
				throw new IOException("Invalid attribute definition: " + s);
			
			final String key = p[0].trim();
			final String value = p[1].trim();
			if (StringUtils.empty(key) || StringUtils.empty(value))
				throw new IOException("Invalid attribute definition: " + s);
			
			ret.put(key, value);
			}
		
		return (ret);
		}
	
	@Override
	public void writeQuery(Writer writer, Query query) throws IOException
		{
		final BufferedWriter w = new BufferedWriter(writer);
		
		w.write(PROP_TYPE);
		w.write(HEADER_SEPARATOR);
		w.write(" ");
		w.write(query.getType().getName());
		w.write(LINE_SEPARATOR);
		
		w.write(PROP_GROUP);
		w.write(HEADER_SEPARATOR);
		w.write(" ");
		w.write(sanitizeName(query.getGroupName(), true));
		w.write(LINE_SEPARATOR);
		
		writeAttributes(w, query.getAttributes());
		writeParameters(w, query.getParameters());
		
		if (query.getType().getResultType().isView())
			writeViews(w, query.getSubQueries());
		else
			{
			final String sql = query.getStatement()
					.replace("\r\n", LINE_SEPARATOR)	// Windows-style
					.replace("\r", LINE_SEPARATOR);	// Mac style
			
			writeLinks(w, query.getTargetQueries());
			w.write(LINE_SEPARATOR);
			w.write(sql);
			w.write(LINE_SEPARATOR);
			}
		
		w.flush();
		}
	
	private void writeParameters(Writer w, List<ParameterDef> params) throws IOException
		{
		final List<String> values = new ArrayList<String>(params.size());
		
		for (ParameterDef s : params)
			{
			final StringBuilder sb = new StringBuilder();
			sb.append(sanitizeParam(s.getName()));
			sb.append(PART_SEPARATOR);
			sb.append(s.getType().name());
			if (!StringUtils.empty(s.getValueQuery()))
				{
				sb.append(PART_SEPARATOR);
				sb.append(sanitizeName(s.getValueQuery(), false));
				}
			values.add(sb.toString());
			}
		
		w.write(PROP_PARAMETERS);
		w.write(HEADER_SEPARATOR);
		w.write(" ");
		w.write(keywordService.combineValues(values));
		w.write(LINE_SEPARATOR);
		}
	
	private void writeLinks(Writer w, Map<Integer, TargetDef> links) throws IOException
		{
		final List<String> values = new ArrayList<String>(links.size());
		
		for (Map.Entry<Integer, TargetDef> ent : links.entrySet())
			{
			final StringBuilder sb = new StringBuilder();
			sb.append(String.valueOf(ent.getKey()));
			sb.append(PART_SEPARATOR);
			if (ent.getValue().isParameter())
				{
				sb.append(PARAM_MARKER);
				sb.append(sanitizeName(ent.getValue().getParameterName(), false));
				}
			else
				sb.append(sanitizeName(ent.getValue().getQueryName(), false));
			values.add(sb.toString());
			}
		
		w.write(PROP_LINKS);
		w.write(HEADER_SEPARATOR);
		w.write(" ");
		w.write(keywordService.combineValues(values));
		w.write(LINE_SEPARATOR);
		}
	
	private void writeViews(Writer w, List<SubQueryDef> links) throws IOException
		{
		final List<String> values = new ArrayList<String>(links.size());
		
		for (SubQueryDef s : links)
			{
			final StringBuilder sb = new StringBuilder();
			sb.append(sanitizeName(s.getName(), false));
			for (String p : s.getParameterValues())
				{
				sb.append(PART_SEPARATOR);
				sb.append(sanitizeValue(p));
				}
			values.add(sb.toString());
			}
		
		w.write(PROP_VIEWS);
		w.write(HEADER_SEPARATOR);
		w.write(" ");
		w.write(keywordService.combineValues(values));
		w.write(LINE_SEPARATOR);
		}
	
	private void writeAttributes(Writer w, Map<String, String> attributes) throws IOException
		{
		final List<String> values = new ArrayList<String>(attributes.size());
		
		for (Map.Entry<String, String> ent : attributes.entrySet())
			{
			final StringBuilder sb = new StringBuilder();
			sb.append(sanitizeParam(ent.getKey()));
			sb.append(PART_SEPARATOR);
			sb.append(sanitizeValue(ent.getValue()));
			values.add(sb.toString());
			}
		
		w.write(PROP_ATTRIBUTES);
		w.write(HEADER_SEPARATOR);
		w.write(" ");
		w.write(keywordService.combineValues(values));
		w.write(LINE_SEPARATOR);
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
	
	private String sanitizeParam(String name) throws IOException
		{
		final String s = keywordService.normalizeParam(name);
		if (StringUtils.empty(s))
			throw new IOException("Invalid name: " + name);
		return (s);
		}
	
	private String sanitizeValue(String name)
		{
		final String s = keywordService.normalizeValue(name);
		if (StringUtils.empty(s))
			return ("");
		return (s);
		}
	}
