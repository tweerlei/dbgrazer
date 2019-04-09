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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.dbgrazer.common.file.HistoryEntry;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.common.service.KeywordService;
import de.tweerlei.dbgrazer.common.util.impl.NamedSet;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.model.impl.LinkDefImpl;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.dbgrazer.query.backend.QueryLoader;
import de.tweerlei.dbgrazer.query.backend.impl.DummyQueryLoader;
import de.tweerlei.dbgrazer.query.model.ParameterDef;
import de.tweerlei.dbgrazer.query.model.Query;
import de.tweerlei.dbgrazer.query.model.QueryCheckResult;
import de.tweerlei.dbgrazer.query.model.QueryErrorKeys;
import de.tweerlei.dbgrazer.query.model.QueryGroup;
import de.tweerlei.dbgrazer.query.model.QueryType;
import de.tweerlei.dbgrazer.query.model.SubQueryDef;
import de.tweerlei.dbgrazer.query.model.TargetDef;
import de.tweerlei.dbgrazer.query.model.impl.ParameterDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryGroupBuilder;
import de.tweerlei.dbgrazer.query.model.impl.QueryImpl;
import de.tweerlei.dbgrazer.query.model.impl.QueryTargetImpl;
import de.tweerlei.dbgrazer.query.model.impl.SubQueryDefImpl;
import de.tweerlei.dbgrazer.query.model.impl.ViewImpl;
import de.tweerlei.dbgrazer.query.service.QueryService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class QueryServiceImpl implements QueryService, ConfigListener, LinkListener
	{
	private final ConfigService configService;
	private final KeywordService keywordService;
	private final ModuleLookupService moduleService;
	private final LinkService linkService;
	private final Logger logger;
	private final Set<QueryType> queryTypes;
	private final Map<SchemaDef, Map<String, Query>> queriesBySchema;
	private final Map<String, Map<String, Query>> queries;
	
	private QueryLoader loader;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param keywordService KeywordService
	 * @param moduleService ModuleLookupService
	 * @param linkService LinkService
	 * @param queryTypes All known query types
	 */
	@Autowired
	public QueryServiceImpl(ConfigService configService, KeywordService keywordService, ModuleLookupService moduleService,
			LinkService linkService, Set<QueryType> queryTypes)
		{
		this.configService = configService;
		this.keywordService = keywordService;
		this.moduleService = moduleService;
		this.linkService = linkService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.queriesBySchema = new ConcurrentHashMap<SchemaDef, Map<String, Query>>();
		this.queries = new ConcurrentHashMap<String, Map<String, Query>>();
		this.queryTypes = Collections.unmodifiableSet(new NamedSet<QueryType>(queryTypes));
		
		this.logger.log(Level.INFO, "Query types: " + this.queryTypes);
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		linkService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		final String loaderPrefix = configService.get(ConfigKeys.QUERY_LOADER);
		
		logger.log(Level.INFO, "Using QueryLoader: " + loaderPrefix);
		try	{
			loader = moduleService.findModuleInstance(loaderPrefix + "QueryLoader", QueryLoader.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			loader = new DummyQueryLoader();
			}
		
		reloadQueries();
		}
	
	@Override
	public void linksChanged()
		{
		queries.clear();
		}
	
	@Override
	public void linkChanged(String link)
		{
		queries.remove(link);
		}
	
	@Override
	public void reloadQueries()
		{
		queriesBySchema.clear();
		queries.clear();
		}
	
	@Override
	public SortedMap<SchemaDef, Integer> getQueryStats()
		{
		final SortedMap<SchemaDef, Integer> ret = new TreeMap<SchemaDef, Integer>();
		
		for (Map.Entry<SchemaDef, Map<String, Query>> ent : queriesBySchema.entrySet())
			ret.put(ent.getKey(), ent.getValue().size());
		
		return (ret);
		}
	
	@Override
	public Query findQueryByName(String link, String name)
		{
		final String qn = keywordService.normalizeName(name);
		
		return (getQueries(link).get(qn));
		}
	
	@Override
	public List<Query> findQueriesByParameters(String link, List<String> params, boolean views)
		{
		final List<Query> ret = new ArrayList<Query>();
		
		for (Query q : getQueries(link).values())
			{
			if ((q.getType().getResultType().isView() != views) || (q.getParameters().size() != params.size()))
				continue;
			
			final Iterator<String> pi = params.iterator();
			final Iterator<ParameterDef> di = q.getParameters().iterator();
			boolean matched = true;
			while (pi.hasNext() && di.hasNext())
				{
				final String pn = pi.next();
				final ParameterDef def = di.next();
				
				if (!def.getName().equals(pn))
					{
					matched = false;
					break;
					}
				}
			if (matched)
				ret.add(q);
			}
		
		return (ret);
		}
	
	@Override
	public QueryGroup groupAllQueries(String link, boolean listviews, boolean valuequeries)
		{
		final QueryGroupBuilder builder = new QueryGroupBuilder(listviews, valuequeries);
		
		for (Query q : getQueries(link).values())
			builder.add(q);
		
		return (builder.build());
		}
	
	@Override
	public QueryGroup groupQueries(String link, Set<String> names, boolean listviews, boolean valuequeries)
		{
		final QueryGroupBuilder builder = new QueryGroupBuilder(listviews, valuequeries);
		
		final Map<String, Query> all = getQueries(link);
		
		for (String name : names)
			{
			final Query q = all.get(name);
			if (q != null)
				builder.add(q);
			}
		
		return (builder.build());
		}
	
	@Override
	public Set<QueryType> findSimpleQueryTypes(LinkType linkType)
		{
		final Set<QueryType> ret = new NamedSet<QueryType>();
		
		for (QueryType t : queryTypes)
			{
			if (!t.getResultType().isView() && ((t.getLinkType() == null) || (t.getLinkType() == linkType)))
				ret.add(t);
			}
		
		return (ret);
		}
	
	@Override
	public Set<QueryType> findScriptQueryTypes(LinkType linkType)
		{
		final Set<QueryType> ret = new NamedSet<QueryType>();
		
		for (QueryType t : queryTypes)
			{
			if (t.isScript() && ((t.getLinkType() == null) || (t.getLinkType() == linkType)))
				ret.add(t);
			}
		
		return (ret);
		}
	
	@Override
	public Set<QueryType> findViewQueryTypes()
		{
		final Set<QueryType> ret = new NamedSet<QueryType>();
		
		for (QueryType t : queryTypes)
			{
			if (t.getResultType().isView())
				ret.add(t);
			}
		
		return (ret);
		}
	
	@Override
	public Set<QueryType> findAllQueryTypes(LinkType linkType)
		{
		final Set<QueryType> ret = new NamedSet<QueryType>();
		
		for (QueryType t : queryTypes)
			{
			if ((t.getLinkType() == null) || (t.getLinkType() == linkType))
				ret.add(t);
			}
		
		return (ret);
		}
	
	@Override
	public QueryType findQueryType(String name)
		{
		for (QueryType t : queryTypes)
			{
			if (t.getName().equals(name))
				return (t);
			}
		
		return (null);
		}
	
	@Override
	public SortedSet<String> findAllGroupNames(String link)
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
		for (Query q : getQueries(link).values())
			ret.add(q.getGroupName());
		
		return (ret);
		}
	
	@Override
	public SortedSet<String> findSingleParameterNames(String link)
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
		for (Query q : getQueries(link).values())
			{
			if (q.getParameters().size() == 1)
				ret.add(q.getParameters().get(0).getName());
			}
		
		return (ret);
		}
	
	@Override
	public SortedSet<String> findAllParameterNames(String link)
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		
		for (Query q : getQueries(link).values())
			{
			for (ParameterDef p : q.getParameters())
				ret.add(p.getName());
			}
		
		return (ret);
		}
	
	@Override
	public QueryGroup groupQueriesByParameter(String link, String name)
		{
		final QueryGroupBuilder builder = new QueryGroupBuilder(false, true);
		
		for (Query q : getQueries(link).values())
			{
			if ((q.getParameters().size() == 1) && q.getParameters().get(0).getName().equals(name))
				builder.add(q);
			else
				builder.addRefs(q);
			}
		
		return (builder.build());
		}
	
	@Override
	public QueryGroup groupMatchingQueries(String link, String term, boolean stmt)
		{
		final QueryGroupBuilder builder = new QueryGroupBuilder(false, true);
		
		try	{
			final Pattern p = Pattern.compile(term, Pattern.CASE_INSENSITIVE);
			
			for (Query q : getQueries(link).values())
				{
				if (p.matcher(q.getName()).find() || (stmt && p.matcher(q.getStatement()).find()))
					builder.add(q);
				else
					builder.addRefs(q);
				}
			}
		catch (RuntimeException e)
			{
			// Don't rely on the user to enter a valid pattern
			}
		
		return (builder.build());
		}
	
	@Override
	public List<Query> findReferencingQueries(String link, String name)
		{
		final List<Query> ret = new ArrayList<Query>();
		
		for (Query q : getQueries(link).values())
			{
			boolean found = containsSubQuery(q, name);
			
			if (!found)
				{
				for (TargetDef t : q.getTargetQueries().values())
					{
					if (!t.isParameter() && name.equals(t.getQueryName()))
						{
						found = true;
						break;
						}
					}
				}
			
			if (!found)
				{
				for (ParameterDef p : q.getParameters())
					{
					if (name.equals(p.getValueQuery()))
						{
						found = true;
						break;
						}
					}
				}
			
			if (found)
				ret.add(q);
			}
		
		return (ret);
		}
	
	@Override
	public List<Query> findReferencedQueries(String link, String name, boolean deep)
		{
		final Map<String, Query> found = new HashMap<String, Query>();
		
		final Map<String, Query> all = getQueries(link);
		addReferenced(all.get(name), all, found, deep ? Integer.MAX_VALUE : 1);
		
		return (new ArrayList<Query>(found.values()));
		}
	
	private void addReferenced(Query query, Map<String, Query> all, Map<String, Query> found, int level)
		{
		if (query == null)
			return;
		
		if (found.put(query.getName(), query) != null)
			return;
		
		if (level <= 0)
			return;
		
		for (SubQueryDef qn : query.getSubQueries())
			addReferenced(all.get(qn.getName()), all, found, level - 1);
		
		for (TargetDef t : query.getTargetQueries().values())
			{
			if (!t.isParameter())
				addReferenced(all.get(t.getQueryName()), all, found, level - 1);
			}
		
		for (ParameterDef p : query.getParameters())
			{
			if (!StringUtils.empty(p.getValueQuery()))
				addReferenced(all.get(p.getValueQuery()), all, found, level - 1);
			}
		}
	
	@Override
	public QueryGroup groupRelatedQueries(String link, String name)
		{
		final QueryGroupBuilder builder = new QueryGroupBuilder(false, true);
		
		final Map<String, Query> all = getQueries(link);
		final Query query = all.get(name);
		
		if (query == null)
			return (builder.build());
		
		for (Query q : all.values())
			{
			boolean match = false;
			
			if (!q.getName().equals(query.getName()))
				{
				if (containsSubQuery(q, name))
					match = true;
				else if (!query.getParameters().isEmpty() && !q.getParameters().isEmpty())
					{
					final Iterator<ParameterDef> i1 = query.getParameters().iterator();
					final Iterator<ParameterDef> i2 = q.getParameters().iterator();
					
					match = true;
					while (i1.hasNext() && i2.hasNext())
						{
						final ParameterDef p1 = i1.next();
						final ParameterDef p2 = i2.next();
						
						if (!p1.getName().equals(p2.getName()) || (p1.getType() != p2.getType()))
							{
							match = false;
							break;
							}
						}
					}
				}
			
			if (match)
				builder.add(q);
			else
				builder.addRefs(q);
			}
		
		return (builder.build());
		}
	
	private boolean containsSubQuery(Query q, String n)
		{
		for (SubQueryDef sq : q.getSubQueries())
			{
			if (sq.getName().equals(n))
				return (true);
			}
		return (false);
		}
	
	private Map<String, Query> getQueries(String link)
		{
		Map<String, Query> ret = queries.get(link);
		if (ret != null)
			return (ret);
		
		final LinkDef c = linkService.getLink(link, null);
		if (c == null)
			return (Collections.emptyMap());
		
		return (loadQueries(c));
		}
	
	private synchronized Map<String, Query> loadQueries(LinkDef c)
		{
		Map<String, Query> ret = queries.get(c.getName());
		if (ret != null)
			return (ret);
		
		final SortedMap<String, Query> allQueries = new TreeMap<String, Query>();
		
		// Load queries for all query sets
		for (String qs : c.getQuerySetNames())
			allQueries.putAll(loadQueries(new SchemaDef(null, qs)));
		
		// Load queries for main schema
		if (c.getSchema().isSubschema())
			allQueries.putAll(loadQueries(c.getSchema().getUnversionedSchema()));
		
		// Load queries for subschema (or main schema if unversioned)
		allQueries.putAll(loadQueries(c.getSchema()));
		
		ret = Collections.unmodifiableMap(allQueries);
		queries.put(c.getName(), ret);
		
		return (ret);
		}
	
	private synchronized Map<String, Query> loadQueries(SchemaDef schema)
		{
		Map<String, Query> ret = queriesBySchema.get(schema);
		if (ret != null)
			return (ret);
		
		ret = loader.loadQueries(schema);
		logger.log(Level.INFO, "Loaded " + ret.size() + " queries for " + schema);
		
		for (Iterator<Query> it = ret.values().iterator(); it.hasNext(); )
			{
			final Query query = it.next();
			try	{
				validateQuery(query);
				}
			catch (BindException e)
				{
				logger.log(Level.WARNING, "Invalid query " + query.getName() + ": " + e.getMessage());
				it.remove();
				}
			}
		
		queriesBySchema.put(schema, ret);
		return (ret);
		}
	
	private synchronized void reloadQueries(String link)
		{
		final LinkDef c = linkService.getLink(link, null);
		if (c == null)
			return;
		
		for (Iterator<Map.Entry<SchemaDef, Map<String, Query>>> i = queriesBySchema.entrySet().iterator(); i.hasNext(); )
			{
			final Map.Entry<SchemaDef, Map<String, Query>> ent = i.next();
			if (ent.getKey().getName().equals(c.getSchema().getName()))
				i.remove();
			else if (ent.getKey().isQuerySet() && c.getQuerySetNames().contains(ent.getKey().getVersion()))
				i.remove();
			}
		
		queries.clear();
		}
	
	private synchronized Map<SchemaDef, Map<String, Query>> getAllQueries(String link)
		{
		final Map<SchemaDef, Map<String, Query>> ret = new HashMap<SchemaDef, Map<String, Query>>();
		
		final LinkDef c = linkService.getLink(link, null);
		if (c == null)
			return (ret);
		
		final SchemaDef mainSchema = c.getSchema().getUnversionedSchema();
		ret.put(mainSchema, loadQueries(mainSchema));
		
		for (SchemaDef subSchema : loader.getSubSchemas(mainSchema))
			ret.put(subSchema, loadQueries(subSchema));
		
		for (String qs : c.getQuerySetNames())
			{
			final SchemaDef setSchema = new SchemaDef(null, qs);
			ret.put(setSchema, loadQueries(setSchema));
			}
		
		return (ret);
		}
	
	@Override
	public Set<SchemaDef> getPossibleSchemaNames(String link)
		{
		final Set<SchemaDef> ret = new LinkedHashSet<SchemaDef>();
		
		final LinkDef c = linkService.getLink(link, null);
		if (c == null)
			return (ret);
		
		// Add main schema
		ret.add(c.getSchema().getUnversionedSchema());
		
		// Add subschema if versioned
		if (c.getSchema().isSubschema())
			ret.add(c.getSchema());
		
		// Add all associated query sets
		for (String qs : c.getQuerySetNames())
			{
			final SchemaDef setSchema = new SchemaDef(null, qs);
			ret.add(setSchema);
			}
		
		return (ret);
		}
	
	@Override
	public SortedMap<String, List<QueryCheckResult>> checkQueries(String link)
		{
		final SortedMap<String, List<QueryCheckResult>> ret = new TreeMap<String, List<QueryCheckResult>>(StringComparators.CASE_INSENSITIVE);
		
		final Map<String, Query> allQueries = getQueries(link);
		
		final QueryValidator v = new QueryValidator(allQueries);
		
		for (Map.Entry<String, Query> ent : allQueries.entrySet())
			{
			final List<QueryCheckResult> msg = v.validate(ent.getValue());
			if (!msg.isEmpty())
				ret.put(ent.getKey(), msg);
			}
		
		return (ret);
		}
	
	@Override
	public synchronized String createQuery(String link, String user, Query query) throws BindException
		{
		validateQuery(query);
		
		final Map<String, Query> allQueries = getQueries(link);
		
		final String qn = keywordService.normalizeName(query.getName());
		if (StringUtils.empty(qn))
			{
			logger.log(Level.INFO, "createQuery: Name is empty");
			return (null);
			}
		if (allQueries.containsKey(qn))
			{
			logger.log(Level.INFO, "createQuery: Not overwriting " + qn);
			return (null);
			}
		
		final SchemaDef targetSchema;
		final Set<SchemaDef> possibleSchemas = getPossibleSchemaNames(link);
		if (possibleSchemas.contains(query.getSourceSchema()))
			targetSchema = query.getSourceSchema();
		else
			{
			targetSchema = possibleSchemas.iterator().next();
			logger.log(Level.INFO, "createQuery: Invalid schema " + query.getSourceSchema() + " for " + link + ", using " + targetSchema);
			}
		
		try	{
			if (targetSchema.isSubschema())
				{
				// Also create in main schema
				final SchemaDef mainSchema = targetSchema.getUnversionedSchema();
				loader.createQuery(mainSchema, user, qn, query);
				logger.log(Level.INFO, "createQuery: Successfully created " + qn + " in " + mainSchema);
				}
			
			loader.createQuery(targetSchema, user, qn, query);
			logger.log(Level.INFO, "createQuery: Successfully created " + qn + " in " + targetSchema);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createQuery: createQuery failed " + qn + " in " + targetSchema, e);
			return (null);
			}
		
		reloadQueries(link);
		return (qn);
		}
	
	@Override
	public synchronized String updateQuery(String link, String user, String name, Query query) throws BindException
		{
		validateQuery(query);
		
		final Map<String, Query> allQueries = getQueries(link);
		
		final String qnOld = keywordService.normalizeName(name);
		final Query qOld = allQueries.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "updateQuery: Not found " + qnOld);
			return (null);
			}
		
		final String qnNew = keywordService.normalizeName(query.getName());
		if (StringUtils.empty(qnNew))
			{
			logger.log(Level.INFO, "updateQuery: Name is empty");
			return (null);
			}
		
		final Query qNew = allQueries.get(qnNew);
		if ((qNew != null) && (qOld != qNew))
			{
			logger.log(Level.INFO, "updateQuery: Not overwriting " + qnNew);
			return (null);
			}
		
		final SchemaDef currentSchema = qOld.getSourceSchema();
		final SchemaDef targetSchema;
		if (getPossibleSchemaNames(link).contains(query.getSourceSchema()))
			targetSchema = query.getSourceSchema();
		else
			{
			targetSchema = currentSchema;
			logger.log(Level.INFO, "updateQuery: Invalid schema " + query.getSourceSchema() + " for " + link + ", using " + targetSchema);
			}
		
		try	{
			if (currentSchema.equals(targetSchema))
				{
				// No schema change:
				// Update query in current schema
				loader.updateQuery(targetSchema, user, qOld.getName(), qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully changed " + qOld.getName() + "/" + qnNew + " in " + targetSchema);
				}
			else if (currentSchema.isMainSchema() && targetSchema.isSubschema())
				{
				// Main to subschema:
				// Create query in subschema
				loader.createQuery(targetSchema, user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema);
				}
			else if (currentSchema.isSubschema() && targetSchema.isMainSchema())
				{
				// Sub to main schema:
				// Update main query
				loader.updateQuery(targetSchema.getUnversionedSchema(), user, qOld.getName(), qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully changed " + qOld.getName() + "/" + qnNew + " in " + targetSchema.getUnversionedSchema());
				// Remove query from subschema
				loader.removeQuery(currentSchema, user, qOld.getName());
				}
			else if (currentSchema.isMainSchema() && targetSchema.isQuerySet())
				{
				// Main to dialect:
				// Create in dialect
				loader.createQuery(targetSchema, user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema);
				// Remove from main schema and all subschemas
				for (Map.Entry<SchemaDef, Map<String, Query>> ent : getAllQueries(link).entrySet())
					{
					if (!ent.getKey().isQuerySet())
						{
						final Query qr = ent.getValue().get(qOld.getName());
						if (qr != null)
							loader.removeQuery(ent.getKey(), user, qOld.getName());
						}
					}
				}
			else if (currentSchema.isQuerySet() && targetSchema.isMainSchema())
				{
				// Dialect to main:
				// Create query in main schema
				loader.createQuery(targetSchema, user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema);
				// Remove query from dialect
				loader.removeQuery(currentSchema, user, qOld.getName());
				}
			else if (currentSchema.isSubschema() && targetSchema.isQuerySet())
				{
				// Subschema to dialect:
				// Create in dialect
				loader.createQuery(targetSchema, user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema);
				// Remove from main schema and all subschemas
				for (Map.Entry<SchemaDef, Map<String, Query>> ent : getAllQueries(link).entrySet())
					{
					if (!ent.getKey().isQuerySet())
						{
						final Query qr = ent.getValue().get(qOld.getName());
						if (qr != null)
							loader.removeQuery(ent.getKey(), user, qOld.getName());
						}
					}
				}
			else if (currentSchema.isQuerySet() && targetSchema.isSubschema())
				{
				// Dialect to subschema:
				// Create query in main schema
				loader.createQuery(targetSchema.getUnversionedSchema(), user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema.getUnversionedSchema());
				// Create query in subschema
				loader.createQuery(targetSchema, user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema);
				// Remove query from dialect
				loader.removeQuery(currentSchema, user, qOld.getName());
				}
			else if (currentSchema.isQuerySet() && targetSchema.isQuerySet())
				{
				// Dialect to other dialect:
				// Create in dialect
				loader.createQuery(targetSchema, user, qnNew, query);
				logger.log(Level.INFO, "updateQuery: Successfully created " + qnNew + " in " + targetSchema);
				// Remove query from dialect
				loader.removeQuery(currentSchema, user, qOld.getName());
				}
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "updateQuery: updateQuery failed " + qOld.getName() + "/" + qnNew + " in " + targetSchema, e);
			return (null);
			}
		
		// If the query was renamed, also rename in all related schemas and referencing queries
		if (!qnNew.equals(qOld.getName()))
			{
			for (Map.Entry<SchemaDef, Map<String, Query>> ent : getAllQueries(link).entrySet())
				{
				// Rename in other subschemas
				final Query qr = ent.getValue().get(qOld.getName());
				if ((qr != null) && (qr != qOld))
					{
					try	{
						loader.updateQuery(ent.getKey(), user, qOld.getName(), qnNew, qr);
						logger.log(Level.INFO, "updateQuery: Successfully renamed " + qOld.getName() + "/" + qnNew + " in " + ent.getKey());
						}
					catch (IOException e)
						{
						logger.log(Level.WARNING, "updateQuery: rename failed " + qOld.getName() + "/" + qnNew + " in " + ent.getKey(), e);
						}
					}
				
				// Update references
				for (Query q : ent.getValue().values())
					{
					if ((q == qOld) || q.getName().equals(qOld.getName()))
						continue;
					
					final Query tmp;
					if (q.getType().getResultType().isView())
						{
						boolean found = false;
						
						final List<SubQueryDef> subqueries = new ArrayList<SubQueryDef>(q.getSubQueries().size());
						for (SubQueryDef s : q.getSubQueries())
							{
							if (qOld.getName().equals(s.getName()))
								{
								subqueries.add(new SubQueryDefImpl(qnNew, s.getParameterValues()));
								found = true;
								}
							else
								subqueries.add(s);
							}
						
						if (!found)
							continue;
						
						tmp = new ViewImpl(q.getName(), q.getSourceSchema(), q.getGroupName(), q.getType(), q.getParameters(), subqueries, q.getAttributes());
						}
					else
						{
						boolean found = false;
						
						final List<ParameterDef> params = new ArrayList<ParameterDef>(q.getParameters().size());
						for (ParameterDef p : q.getParameters())
							{
							if (qOld.getName().equals(p.getValueQuery()))
								{
								params.add(new ParameterDefImpl(p.getName(), p.getType(), qnNew));
								found = true;
								}
							else
								params.add(p);
							}
						
						final Map<Integer, TargetDef> targets = new TreeMap<Integer, TargetDef>();
						for (Map.Entry<Integer, TargetDef> tent : q.getTargetQueries().entrySet())
							{
							if (qOld.getName().equals(tent.getValue().getQueryName()))
								{
								targets.put(tent.getKey(), new QueryTargetImpl(qnNew));
								found = true;
								}
							else
								targets.put(tent.getKey(), tent.getValue());
							}
						
						if (!found)
							continue;
						
						tmp = new QueryImpl(q.getName(), q.getSourceSchema(), q.getGroupName(), q.getStatement(), q.getType(), params, targets, q.getAttributes());
						}
					
					try	{
						loader.updateQuery(ent.getKey(), user, tmp.getName(), tmp.getName(), tmp);
						logger.log(Level.INFO, "updateQuery: Successfully changed " + tmp.getName() + " in " + ent.getKey());
						}
					catch (IOException e)
						{
						logger.log(Level.WARNING, "updateQuery: updateQuery failed " + tmp.getName() + " in " + ent.getKey(), e);
						}
					}
				}
			}
		
		reloadQueries(link);
		return (qnNew);
		}
	
	private void validateQuery(Query query) throws BindException
		{
		final BindException errors = new BindException(query, "model");
		
		if (query.getType() == null)
			errors.reject(QueryErrorKeys.UNKNOWN_TYPE);
		else
			query.getType().validate(query, errors);
		
		if (errors.hasErrors())
			throw errors;
		}
	
	@Override
	public synchronized String renameGroup(String link, String user, String name, String newName)
		{
		final String qnOld = keywordService.normalizeName(name);
		final String qnNew = keywordService.normalizeName(newName);
		
		for (Map.Entry<SchemaDef, Map<String, Query>> ent : getAllQueries(link).entrySet())
			{
			for (Query q : ent.getValue().values())
				{
				if (!q.getGroupName().equals(qnOld))
					continue;
				
				final Query tmp;
				if (q.getType().getResultType().isView())
					tmp = new ViewImpl(q.getName(), q.getSourceSchema(), qnNew, q.getType(), q.getParameters(), q.getSubQueries(), q.getAttributes());
				else
					tmp = new QueryImpl(q.getName(), q.getSourceSchema(), qnNew, q.getStatement(), q.getType(), q.getParameters(), q.getTargetQueries(), q.getAttributes());
				
				try	{
					loader.updateQuery(ent.getKey(), user, tmp.getName(), tmp.getName(), tmp);
					logger.log(Level.INFO, "renameGroup: Successfully changed " + tmp.getName() + " in " + ent.getKey());
					}
				catch (IOException e)
					{
					logger.log(Level.WARNING, "renameGroup: updateQuery failed " + tmp.getName() + " in " + ent.getKey(), e);
					}
				}
			}
		
		reloadQueries(link);
		return (qnNew);
		}
	
	@Override
	public synchronized String renameParameter(String link, String user, String name, String newName)
		{
		final String qnOld = keywordService.normalizeName(name);
		final String qnNew = keywordService.normalizeName(newName);
		if (StringUtils.empty(qnNew))
			{
			logger.log(Level.INFO, "renameParameter: Name is empty");
			return (null);
			}
		
		for (Map.Entry<SchemaDef, Map<String, Query>> ent : getAllQueries(link).entrySet())
			{
			for (Query q : ent.getValue().values())
				{
				if (q.getParameters().isEmpty())
					continue;
				
				boolean found = false;
				
				final List<ParameterDef> params = new ArrayList<ParameterDef>(q.getParameters().size());
				for (ParameterDef p : q.getParameters())
					{
					if (p.getName().equals(qnOld))
						{
						params.add(new ParameterDefImpl(qnNew, p.getType(), p.getValueQuery()));
						found = true;
						}
					else
						params.add(p);
					}
				
				if (!found)
					continue;
				
				final Query tmp;
				if (q.getType().getResultType().isView())
					tmp = new ViewImpl(q.getName(), q.getSourceSchema(), q.getGroupName(), q.getType(), params, q.getSubQueries(), q.getAttributes());
				else
					tmp = new QueryImpl(q.getName(), q.getSourceSchema(), q.getGroupName(), q.getStatement(), q.getType(), params, q.getTargetQueries(), q.getAttributes());
				
				try	{
					loader.updateQuery(ent.getKey(), user, tmp.getName(), tmp.getName(), tmp);
					logger.log(Level.INFO, "renameParameter: Successfully changed " + tmp.getName() + " in " + ent.getKey());
					}
				catch (IOException e)
					{
					logger.log(Level.WARNING, "renameParameter: updateQuery failed " + tmp.getName() + " in " + ent.getKey(), e);
					}
				}
			}
		
		reloadQueries(link);
		return (qnNew);
		}
	
	@Override
	public synchronized void renameSchema(String user, SchemaDef oldName, SchemaDef newName) throws BindException
		{
		if ((oldName == null) || (newName == null) || oldName.equals(newName))
			return;
		
		try	{
			loader.renameSchema(user, oldName, newName);
			reloadQueries();
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "renameSchema " + oldName +  " to " + newName + " failed", e);
			return;
			}
		
		for (Map.Entry<SchemaDef, List<LinkDef>> ent : linkService.groupAllLinks().entrySet())
			{
			if (ent.getKey().getName().equals(oldName.getName()))
				{
				final SchemaDef effectiveName;
				if (ent.getKey().isSubschema())
					{
					if (ent.getKey().getVersion().equals(oldName.getVersion()))
						effectiveName = newName;
					else
						effectiveName = new SchemaDef(newName.getName(), ent.getKey().getVersion());
					}
				else
					effectiveName = newName.getUnversionedSchema();
				
				if (!ent.getKey().equals(effectiveName))
					{
					for (LinkDef def : ent.getValue())
						{
						final LinkDef conn = new LinkDefImpl(def.getType(), def.getName(), def.getDescription(), def.getDriver(), def.getUrl(), def.getUsername(), def.getPassword(),
								def.isWritable(), def.getPreDMLStatement(), def.getPostDMLStatement(), def.getGroupName(), def.getSetName(), def.getDialectName(), def.getProperties(),
								effectiveName.getName(), effectiveName.getVersion(), def.getQuerySetNames());
						linkService.updateLink(user, def.getName(), conn);
						}
					}
				}
			}
		}
	
	@Override
	public synchronized boolean removeQuery(String link, String user, String name)
		{
		final Map<String, Query> allQueries = getQueries(link);
		
		final String qnOld = keywordService.normalizeName(name);
		final Query qOld = allQueries.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "removeQuery: Not found " + qnOld);
			return (false);
			}
		
		boolean ret = false;
		
		if (qOld.getSourceSchema().isSubschema())
			{
			// delete a link specific query only from its specific subschema
			try	{
				loader.removeQuery(qOld.getSourceSchema(), user, qOld.getName());
				logger.log(Level.INFO, "removeQuery: Successfully removed " + qOld.getName() + " from " + qOld.getSourceSchema());
				ret = true;
				}
			catch (IOException e)
				{
				logger.log(Level.WARNING, "removeQuery: removeQuery failed " + qOld.getName() + " from " + qOld.getSourceSchema(), e);
				}
			
			reloadQueries(link);
			return (ret);
			}
		
		// delete a generic query and all link specific variants
		for (Map.Entry<SchemaDef, Map<String, Query>> ent : getAllQueries(link).entrySet())
			{
			// don't delete a schema specific query from query sets
			if (ent.getKey().isQuerySet() && !qOld.getSourceSchema().isQuerySet())
				continue;
			
			for (Query q : ent.getValue().values())
				{
				if (q.getName().equals(qOld.getName()))
					{
					// Remove
					try	{
						loader.removeQuery(ent.getKey(), user, q.getName());
						logger.log(Level.INFO, "removeQuery: Successfully removed " + q.getName() + " from " + ent.getKey());
						ret = true;
						}
					catch (IOException e)
						{
						logger.log(Level.WARNING, "removeQuery: removeQuery failed " + q.getName() + " from " + ent.getKey(), e);
						}
					
					continue;
					}
				
				// Remove references
				final Query tmp;
				if (q.getType().getResultType().isView())
					{
					boolean found = false;
					
					final List<SubQueryDef> subqueries = new ArrayList<SubQueryDef>(q.getSubQueries().size());
					for (SubQueryDef s : q.getSubQueries())
						{
						if (qOld.getName().equals(s.getName()))
							found = true;
						else
							subqueries.add(s);
						}
					
					if (!found)
						continue;
					
					tmp = new ViewImpl(q.getName(), q.getSourceSchema(), q.getGroupName(), q.getType(), q.getParameters(), subqueries, q.getAttributes());
					}
				else
					{
					boolean found = false;
					
					final List<ParameterDef> params = new ArrayList<ParameterDef>(q.getParameters().size());
					for (ParameterDef p : q.getParameters())
						{
						if (qOld.getName().equals(p.getValueQuery()))
							{
							params.add(new ParameterDefImpl(p.getName(), p.getType(), null));
							found = true;
							}
						else
							params.add(p);
						}
					
					final Map<Integer, TargetDef> targets = new TreeMap<Integer, TargetDef>();
					for (Map.Entry<Integer, TargetDef> qent : q.getTargetQueries().entrySet())
						{
						if (qOld.getName().equals(qent.getValue().getQueryName()))
							found = true;
						else
							targets.put(qent.getKey(), qent.getValue());
						}
					
					if (!found)
						continue;
					
					tmp = new QueryImpl(q.getName(), q.getSourceSchema(), q.getGroupName(), q.getStatement(), q.getType(), params, targets, q.getAttributes());
					}
				
				try	{
					loader.updateQuery(ent.getKey(), user, tmp.getName(), tmp.getName(), tmp);
					logger.log(Level.INFO, "removeQuery: Successfully changed " + tmp.getName() + " in " + ent.getKey());
					}
				catch (IOException e)
					{
					logger.log(Level.WARNING, "removeQuery: updateQuery failed " + tmp.getName() + " in " + ent.getKey(), e);
					}
				}
			}
		
		reloadQueries(link);
		return (ret);
		}
	
	@Override
	public List<HistoryEntry> getHistory(String link, String name, int limit)
		{
		final Map<String, Query> allQueries = getQueries(link);
		
		final String qnOld = keywordService.normalizeName(name);
		final Query qOld = allQueries.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "getHistory: Not found " + qnOld);
			return (Collections.emptyList());
			}
		
		try	{
			final List<HistoryEntry> l = loader.getHistory(qOld.getSourceSchema(), qOld.getName(), limit);
			Collections.sort(l);
			return (l);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "getHistory: getHistory failed " + qOld.getName() + " in " + qOld.getSourceSchema(), e);
			return (Collections.emptyList());
			}
		}
	
	@Override
	public Query getQueryVersion(String link, String name, String version)
		{
		final Map<String, Query> allQueries = getQueries(link);
		
		final String qnOld = keywordService.normalizeName(name);
		final Query qOld = allQueries.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "getQueryVersion: Not found " + qnOld);
			return (null);
			}
		
		try	{
			final Query q = loader.getQueryVersion(qOld.getSourceSchema(), qOld.getName(), version);
			return (q);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "getQueryVersion: getQueryVersion failed " + qOld.getName() + " in " + qOld.getSourceSchema(), e);
			return (null);
			}
		}
	}
