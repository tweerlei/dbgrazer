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
package de.tweerlei.dbgrazer.link.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import de.tweerlei.dbgrazer.link.backend.LinkLoader;
import de.tweerlei.dbgrazer.link.backend.impl.DummyLinkLoader;
import de.tweerlei.dbgrazer.link.model.LinkDef;
import de.tweerlei.dbgrazer.link.model.LinkErrorKeys;
import de.tweerlei.dbgrazer.link.model.LinkType;
import de.tweerlei.dbgrazer.link.model.SchemaDef;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkManager;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class LinkServiceImpl implements LinkService, ConfigListener
	{
	private final ConfigService configService;
	private final KeywordService keywordService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	private final List<LinkListener> listeners;
	private final List<LinkManager> managers;
	private final Set<LinkType> linkTypes;
	
	private LinkLoader loader;
	private Map<String, LinkDef> links;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param keywordService KeywordService
	 * @param moduleService ModuleLookupService
	 * @param linkTypes All known link types
	 */
	@Autowired(required = false)
	public LinkServiceImpl(ConfigService configService, KeywordService keywordService,
			ModuleLookupService moduleService, Set<LinkType> linkTypes)
		{
		this.configService = configService;
		this.keywordService = keywordService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.listeners = new LinkedList<LinkListener>();
		this.managers = new LinkedList<LinkManager>();;
		this.linkTypes = Collections.unmodifiableSet(new NamedSet<LinkType>(linkTypes));
		
		this.logger.log(Level.INFO, "Link types: " + this.linkTypes);
		}
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param keywordService KeywordService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired(required = false)
	public LinkServiceImpl(ConfigService configService, KeywordService keywordService,
			ModuleLookupService moduleService)
		{
		this(configService, keywordService, moduleService, Collections.<LinkType>emptySet());
		}
	
	/**
	 * Register for config changes
	 */
	@PostConstruct
	public void init()
		{
		configService.addListener(this);
		configChanged();
		}
	
	@Override
	public void configChanged()
		{
		final String loaderPrefix = configService.get(ConfigKeys.LINK_LOADER);
		
		logger.log(Level.INFO, "Using LinkLoader: " + loaderPrefix);
		try	{
			loader = moduleService.findModuleInstance(loaderPrefix + "LinkLoader", LinkLoader.class);
			}
		catch (RuntimeException e)
			{
			logger.log(Level.SEVERE, "findModuleInstance", e);
			loader = new DummyLinkLoader();
			}
		
		reloadLinks(true);
		}
	
	@Override
	public void reloadLinks()
		{
		reloadLinks(true);
		}
	
	private synchronized void reloadLinks(boolean notify)
		{
		final Map<String, LinkDef> map = loader.loadLinks();
		
		logger.log(Level.INFO, "Loaded " + map.size() + " links");
		
		for (Iterator<LinkDef> it = map.values().iterator(); it.hasNext(); )
			{
			final LinkDef def = it.next();
			try	{
				validateLink(def);
				}
			catch (BindException e)
				{
				logger.log(Level.WARNING, "Invalid link " + def.getName() + ": " + e.getMessage());
				it.remove();
				}
			}
		
		links = Collections.unmodifiableMap(map);
		
		if (notify)
			fireLinksChanged();
		}
	
	@Override
	public void addListener(LinkListener listener)
		{
		this.listeners.add(listener);
		}
	
	private void fireLinksChanged()
		{
		for (LinkListener l : listeners)
			l.linksChanged();
		}
	
	private void fireLinkChanged(String link)
		{
		for (LinkListener l : listeners)
			l.linkChanged(link);
		}
	
	@Override
	public void addManager(LinkManager manager)
		{
		this.managers.add(manager);
		}
	
	private boolean isInGroups(String groupName, Set<String> groups)
		{
		return ((groups == null) || StringUtils.empty(groupName) || groups.contains(groupName));
		}
	
	private boolean matchesSchema(SchemaDef def, String schema, String version)
		{
		if (schema == null)
			return (true);
		if (!def.getName().equals(schema))
			return (false);
		if (version == null)
			return (true);
		return (def.getVersion().equals(version));
		}
	
	@Override
	public Set<LinkType> findAllLinkTypes()
		{
		final Set<LinkType> ret = new NamedSet<LinkType>(linkTypes);
		
		return (ret);
		}
	
	@Override
	public LinkType findLinkType(String name)
		{
		for (LinkType t : linkTypes)
			{
			if (t.getName().equals(name))
				return (t);
			}
		
		return (null);
		}
	
	@Override
	public SortedMap<String, String> findAllLinkNames(Set<String> groups, String schema, String version)
		{
		final SortedMap<String, String> ret = new TreeMap<String, String>(StringComparators.CASE_INSENSITIVE);
		for (Map.Entry<String, LinkDef> ent : links.entrySet())
			{
			if (isInGroups(ent.getValue().getGroupName(), groups) && matchesSchema(ent.getValue().getSchema(), schema, version))
				ret.put(ent.getValue().getFullDescription(), ent.getKey());
			}
		return (ret);
		}
	
	@Override
	public SortedMap<String, Map<String, String>> findAllLinkSets(Set<String> groups, String schema, String version)
		{
		final SortedMap<String, Map<String, String>> ret = new TreeMap<String, Map<String, String>>(StringComparators.CASE_INSENSITIVE);
		for (Map.Entry<String, LinkDef> ent : links.entrySet())
			{
			if (isInGroups(ent.getValue().getGroupName(), groups) && matchesSchema(ent.getValue().getSchema(), schema, version))
				{
				final String key = ent.getValue().getSetDescription();
				Map<String, String> map = ret.get(key);
				if (map == null)
					{
					map = new TreeMap<String, String>(StringComparators.CASE_INSENSITIVE);
					ret.put(key, map);
					}
				map.put(ent.getValue().getFullDescription(), ent.getKey());
				}
			}
		return (ret);
		}
	
	@Override
	public LinkDef getLink(String name, Set<String> groups)
		{
		if (name == null)
			return (null);
		
		final LinkDef def = links.get(name);
		if (def == null)
			return (null);
		
		if (isInGroups(def.getGroupName(), groups))
			return (def);
		
		return (null);
		}
	
	@Override
	public SortedSet<String> findAllLinkGroups()
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		for (LinkDef def : links.values())
			{
			if (!StringUtils.empty(def.getGroupName()))
				ret.add(def.getGroupName());
			}
		return (ret);
		}
	
	@Override
	public SortedSet<String> findAllLinkSets()
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		for (LinkDef def : links.values())
			{
			if (!StringUtils.empty(def.getSetName()))
				ret.add(def.getSetName());
			}
		return (ret);
		}
	
	@Override
	public SortedSet<String> findAllQuerySets()
		{
		final SortedSet<String> ret = new TreeSet<String>(StringComparators.CASE_INSENSITIVE);
		for (LinkDef def : links.values())
			ret.addAll(def.getQuerySetNames());
		return (ret);
		}
	
	@Override
	public SchemaDef getSchema(String c)
		{
		// Java bug: TreeMap does not support get(null)
		if (c == null)
			return (null);
		
		final LinkDef def = links.get(c);
		if (def == null)
			return (null);
		
		return (def.getSchema());
		}
	
	@Override
	public SortedMap<SchemaDef, List<LinkDef>> groupAllLinks()
		{
		final SortedMap<SchemaDef, List<LinkDef>> ret = new TreeMap<SchemaDef, List<LinkDef>>();
		
		for (LinkDef def : links.values())
			{
			List<LinkDef> l = ret.get(def.getSchema());
			if (l == null)
				{
				l = new LinkedList<LinkDef>();
				ret.put(def.getSchema(), l);
				}
			l.add(def);
			}
		
		return (ret);
		}
	
	@Override
	public List<LinkDef> findLinksByType(String type)
		{
		final List<LinkDef> ret = new ArrayList<LinkDef>(links.size());
		
		for (LinkDef def : links.values())
			{
			if (def.getType().getName().equals(type))
				ret.add(def);
			}
		
		return (ret);
		}
	
	@Override
	public List<LinkDef> findLinksBySetName(String type, String name)
		{
		final List<LinkDef> ret = new ArrayList<LinkDef>(links.size());
		
		for (LinkDef def : links.values())
			{
			final String key = StringUtils.empty(def.getSetName()) ? def.getDescription() : def.getSetName();
			if (def.getType().getName().equals(type) && key.equals(name))
				ret.add(def);
			}
		
		return (ret);
		}
	
	@Override
	public List<LinkDef> findLinksByGroupName(String type, String name)
		{
		final List<LinkDef> ret = new ArrayList<LinkDef>(links.size());
		
		for (LinkDef def : links.values())
			{
			if (def.getType().getName().equals(type) && !StringUtils.empty(def.getGroupName()) && def.getGroupName().equals(name))
				ret.add(def);
			}
		
		return (ret);
		}
	
	@Override
	public List<LinkDef> findLinksByUrl(String type, String url)
		{
		final List<LinkDef> ret = new ArrayList<LinkDef>(links.size());
		
		for (LinkDef def : links.values())
			{
			if (def.getType().getName().equals(type) && def.getUrl().equals(url))
				ret.add(def);
			}
		
		return (ret);
		}
	
	@Override
	public LinkDef findLinkByName(String name)
		{
		// Java bug: TreeMap does not support get(null)
		if (name == null)
			return (null);
		
		final String qn = keywordService.normalizeName(name);
		
		return (links.get(qn));
		}
	
	private void validateLink(LinkDef conn) throws BindException
		{
		final BindException errors = new BindException(conn, "model");
		
		if (conn.getType() == null)
			errors.reject(LinkErrorKeys.UNKNOWN_TYPE);
		else
			conn.getType().validate(conn, errors);
		
		if (errors.hasErrors())
			throw errors;
		}
	
	@Override
	public synchronized String createLink(String user, LinkDef conn) throws BindException
		{
		validateLink(conn);
		
		final String qn = keywordService.normalizeName(conn.getName());
		if (StringUtils.empty(qn))
			{
			logger.log(Level.INFO, "createLink: Name is empty");
			return (null);
			}
		if (links.containsKey(qn))
			{
			logger.log(Level.INFO, "createLink: Not overwriting " + qn);
			return (null);
			}
		
		try	{
			loader.createLink(user, qn, conn);
			logger.log(Level.INFO, "createLink: Successfully created " + qn);
			reloadLinks(false);
			fireLinkChanged(qn);
			return (qn);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "createLink: createLink failed " + qn, e);
			return (null);
			}
		}
	
	@Override
	public synchronized String updateLink(String user, String name, LinkDef conn) throws BindException
		{
		validateLink(conn);
		
		final String qnOld = keywordService.normalizeName(name);
		final LinkDef qOld = links.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "updateLink: Not found " + qnOld);
			return (null);
			}
		
		final String qnNew = keywordService.normalizeName(conn.getName());
		if (StringUtils.empty(qnNew))
			{
			logger.log(Level.INFO, "updateLink: Name is empty");
			return (null);
			}
		
		final LinkDef qNew = links.get(qnNew);
		if ((qNew != null) && (qOld != qNew))
			{
			logger.log(Level.INFO, "updateLink: Not overwriting " + qnNew);
			return (null);
			}
		
		try	{
			loader.updateLink(user, qOld.getName(), qnNew, conn);
			logger.log(Level.INFO, "updateLink: Successfully changed " + qOld.getName() + "/" + qnNew);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "updateLink: updateLink failed " + qOld.getName() + "/" + qnNew, e);
			return (null);
			}
		
		reloadLinks(false);
		fireLinkChanged(qnNew);
		return (qnNew);
		}
	
	@Override
	public synchronized boolean removeLink(String user, String name)
		{
		final String qnOld = keywordService.normalizeName(name);
		final LinkDef qOld = links.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "removeLink: Not found " + qnOld);
			return (false);
			}
		
		try	{
			loader.removeLink(user, qOld.getName());
			logger.log(Level.INFO, "removeLink: Successfully removed " + qOld.getName());
			reloadLinks(false);
			fireLinkChanged(qOld.getName());
			return (true);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "removeLink: removeLink failed", e);
			return (false);
			}
		}
	
	@Override
	public List<HistoryEntry> getHistory(String name, int limit)
		{
		final String qnOld = keywordService.normalizeName(name);
		final LinkDef qOld = links.get(qnOld);
		if (qOld == null)
			{
			logger.log(Level.INFO, "getHistory: Not found " + qnOld);
			return (Collections.emptyList());
			}
		
		try	{
			final List<HistoryEntry> l = loader.getHistory(qOld.getName(), limit);
			if (l.size() > 1)
				Collections.sort(l);
			return (l);
			}
		catch (IOException e)
			{
			logger.log(Level.WARNING, "getHistory: getHistory failed", e);
			return (Collections.emptyList());
			}
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (LinkManager m : managers)
			ret.putAll(m.getLinkStats());
		
		return (ret);
		}
	}
