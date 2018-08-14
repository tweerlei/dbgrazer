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
package de.tweerlei.dbgrazer.extension.jdbc.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tweerlei.common.util.ProgressMonitor;
import de.tweerlei.common.util.StringUtils;
import de.tweerlei.common5.collections.StringComparators;
import de.tweerlei.common5.jdbc.model.ColumnDescription;
import de.tweerlei.common5.jdbc.model.ForeignKeyDescription;
import de.tweerlei.common5.jdbc.model.QualifiedName;
import de.tweerlei.common5.jdbc.model.TableDescription;
import de.tweerlei.dbgrazer.common.service.ConfigListener;
import de.tweerlei.dbgrazer.common.service.ConfigService;
import de.tweerlei.dbgrazer.extension.jdbc.ConfigKeys;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataLoader;
import de.tweerlei.dbgrazer.extension.jdbc.MetadataService;
import de.tweerlei.dbgrazer.link.service.LinkListener;
import de.tweerlei.dbgrazer.link.service.LinkService;
import de.tweerlei.ermtools.dialect.SQLExecutionPlan;
import de.tweerlei.spring.service.ModuleLookupService;

/**
 * Default impl.
 * 
 * @author Robert Wruck
 */
@Service
public class MetadataServiceImpl implements MetadataService, ConfigListener, LinkListener
	{
	private final ConfigService configService;
	private final LinkService linkService;
	private final ModuleLookupService moduleService;
	private final Logger logger;
	
	private final Map<String, MetadataLoader> loaders;
	private String loaderPrefix;
	private boolean cache;
	
	/**
	 * Constructor
	 * @param configService ConfigService
	 * @param linkService LinkService
	 * @param moduleService ModuleLookupService
	 */
	@Autowired
	public MetadataServiceImpl(ConfigService configService, LinkService linkService, ModuleLookupService moduleService)
		{
		this.configService = configService;
		this.linkService = linkService;
		this.moduleService = moduleService;
		this.logger = Logger.getLogger(getClass().getCanonicalName());
		this.loaders = new ConcurrentHashMap<String, MetadataLoader>();
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
		loaderPrefix = configService.get(ConfigKeys.METADATA_LOADER);
		cache = configService.get(ConfigKeys.METADATA_CACHE);
		
		logger.log(Level.INFO, "Using MetadataLoader: " + loaderPrefix + (cache ? " (cached)" : ""));
		
		flushCache(null);
		}
	
	@Override
	public void linksChanged()
		{
		flushCache(null);
		}
	
	@Override
	public void linkChanged(String link)
		{
		flushCache(link);
		}
	
	@Override
	public Map<String, Integer> getLinkStats()
		{
		final Map<String, Integer> ret = new TreeMap<String, Integer>(StringComparators.CASE_INSENSITIVE);
		
		for (Map.Entry<String, MetadataLoader> ent : loaders.entrySet())
			ret.put(ent.getKey(), 1);
		
		return (ret);
		}
	
	@Override
	public void flushCache(String link)
		{
		if (link == null)
			loaders.clear();
		else
			loaders.remove(link);
		}
	
	private MetadataLoader getMetadataLoader(String link)
		{
		MetadataLoader ret = loaders.get(link);
		if (ret == null)
			{
			try	{
				ret = moduleService.findModuleInstance(loaderPrefix + "MetadataLoader", MetadataLoader.class);
				}
			catch (RuntimeException e)
				{
				logger.log(Level.SEVERE, "findModuleInstance", e);
				ret = new DummyMetadataLoader();
				}
			if (cache)
				ret = new CachedMetadataLoader(ret);
			ret.setLink(link);
			loaders.put(link, ret);
			}
		return (ret);
		}
	
	@Override
	public Map<String, String> getDBInfo(String link)
		{
		return (getMetadataLoader(link).getDBInfo());
		}
	
	@Override
	public SortedSet<String> getCatalogs(String link)
		{
		return (getMetadataLoader(link).getCatalogs());
		}
	
	@Override
	public SortedSet<String> getSchemas(String link)
		{
		return (getMetadataLoader(link).getSchemas());
		}
	
	@Override
	public SortedMap<QualifiedName, String> getTables(String link, String catalog, String schema)
		{
		return (getTables(link, catalog, schema, null));
		}
	
	@Override
	public SortedMap<QualifiedName, String> getTables(String link, String catalog, String schema, String filter)
		{
		final SortedMap<QualifiedName, String> tables = getMetadataLoader(link).getTables(catalog, schema);
		if (!StringUtils.empty(filter))
			{
			try	{
				final Pattern p = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
				for (Iterator<QualifiedName> i = tables.keySet().iterator(); i.hasNext(); )
					{
					if (!p.matcher(i.next().getObjectName()).matches())
						i.remove();
					}
				}
			catch (RuntimeException e)
				{
				// Filter string might not be a valid RegExp
				final String flt = filter.toLowerCase();
				for (Iterator<QualifiedName> i = tables.keySet().iterator(); i.hasNext(); )
					{
					if (!i.next().getObjectName().toLowerCase().equals(flt))
						i.remove();
					}
				}
			}
		return (tables);
		}
	
	@Override
	public TableDescription getTableInfo(String link, QualifiedName table, ColumnMode mode)
		{
		return (filterColumns(getMetadataLoader(link).getTableInfo(table), mode));
		}
	
	@Override
	public Set<TableDescription> getTableInfos(String link, Set<QualifiedName> tables, Set<QualifiedName> missing, ColumnMode mode, ProgressMonitor p)
		{
		return (filterColumns(getMetadataLoader(link).getTableInfos(tables, missing, p), mode));
		}
	
	@Override
	public Set<TableDescription> getTableInfoRecursive(String link, QualifiedName table, int depth, boolean all, ColumnMode mode, ProgressMonitor p)
		{
		return (filterColumns(getMetadataLoader(link).getTableInfoRecursive(table, depth, all, true, p), mode));
		}
	
	private List<ColumnDescription> sortColumns(TableDescription info, boolean refOnly)
		{
		final SortedMap<Integer, ColumnDescription> pk = new TreeMap<Integer, ColumnDescription>();
		final SortedMap<String, ColumnDescription> notnull_fk = new TreeMap<String, ColumnDescription>();
		final SortedMap<String, ColumnDescription> notnull = new TreeMap<String, ColumnDescription>();
		final SortedMap<String, ColumnDescription> nullable_fk = new TreeMap<String, ColumnDescription>();
		final SortedMap<String, ColumnDescription> nullable = new TreeMap<String, ColumnDescription>();
		
		LOOP: for (ColumnDescription c : info.getColumns())
			{
			if (info.getPrimaryKey() != null)
				{
				final int ix = info.getPrimaryKey().getColumns().indexOf(c.getName());
				if (ix >= 0)
					{
					pk.put(ix, c);
					continue LOOP;
					}
				}
			
			for (ForeignKeyDescription fk : info.getReferencedKeys())
				{
				for (String cn : fk.getColumns().keySet())
					{
					if (c.getName().equals(cn))
						{
						if (c.isNullable())
							nullable_fk.put(c.getName(), c);
						else
							notnull_fk.put(c.getName(), c);
						continue LOOP;
						}
					}
				}
			
			for (ForeignKeyDescription fk : info.getReferencingKeys())
				{
				for (String cn : fk.getColumns().values())
					{
					if (c.getName().equals(cn))
						{
						if (c.isNullable())
							nullable_fk.put(c.getName(), c);
						else
							notnull_fk.put(c.getName(), c);
						continue LOOP;
						}
					}
				}
			
			if (c.isNullable())
				nullable.put(c.getName(), c);
			else
				notnull.put(c.getName(), c);
			}
		
		final List<ColumnDescription> cols = new ArrayList<ColumnDescription>(info.getColumns().size());
		
		cols.addAll(pk.values());
		cols.addAll(notnull_fk.values());
		if (!refOnly)
			cols.addAll(notnull.values());
		cols.addAll(nullable_fk.values());
		if (!refOnly)
			cols.addAll(nullable.values());
		
		return (cols);
		}
	
	private TableDescription filterColumns(TableDescription info, ColumnMode mode)
		{
		final List<ColumnDescription> cols;
		switch (mode)
			{
			case PK_FK:
				cols = sortColumns(info, true);
				break;
			case SORTED:
				cols = sortColumns(info, false);
				break;
			default:
				return (info);
			}
		
		return (new TableDescription(
				info.getName().getCatalogName(),
				info.getName().getSchemaName(),
				info.getName().getObjectName(),
				info.getComment(),
				info.getType(),
				info.getPrimaryKey(),
				cols,
				info.getIndices(),
				info.getReferencedKeys(),
				info.getReferencingKeys(),
				info.getPrivileges()
				));
		}
	
	private Set<TableDescription> filterColumns(Set<TableDescription> infos, ColumnMode mode)
		{
		if (mode == ColumnMode.ALL)
			return (infos);
		
		final Set<TableDescription> ret = new HashSet<TableDescription>();
		
		for (TableDescription info : infos)
			ret.add(filterColumns(info, mode));
		
		return (ret);
		}
	
	@Override
	public SQLExecutionPlan analyzeStatement(String link, String stmt, List<Object> params)
		{
		return (getMetadataLoader(link).analyzeStatement(stmt, params));
		}
	}
